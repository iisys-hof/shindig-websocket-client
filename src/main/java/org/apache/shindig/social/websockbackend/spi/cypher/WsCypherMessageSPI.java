/*
 *  Copyright 2015 Institute of Information Systems, Hof University
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.apache.shindig.social.websockbackend.spi.cypher;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.opensocial.model.Message;
import org.apache.shindig.social.opensocial.model.MessageCollection;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.MessageService;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.websockbackend.Constants;
import org.apache.shindig.social.websockbackend.model.dto.MessageCollectionDTO;
import org.apache.shindig.social.websockbackend.model.dto.MessageDTO;
import org.apache.shindig.social.websockbackend.util.CollOptsConverter;

import com.google.common.util.concurrent.Futures;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.hofuniversity.iisys.neo4j.websock.queries.IQueryCallback;
import de.hofuniversity.iisys.neo4j.websock.queries.IQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.query.EQueryType;
import de.hofuniversity.iisys.neo4j.websock.query.WebsockQuery;
import de.hofuniversity.iisys.neo4j.websock.result.SingleResult;
import de.hofuniversity.iisys.neo4j.websock.result.TableResult;
import de.hofuniversity.iisys.neo4j.websock.service.Neo4jServiceQueries;
import de.hofuniversity.iisys.neo4j.websock.session.WebsockConstants;

/**
 * Implementation of the MessageService interface retrieving message data from a remote Neo4j graph
 * database over a websocket using Cypher.
 */
@Singleton
public class WsCypherMessageSPI implements MessageService {
  private static final String TOTAL_FIELD = MessageCollection.Field.TOTAL.toString();
  private static final String UNREAD_FIELD = MessageCollection.Field.UNREAD.toString();

  private static final String STATUS_FIELD = Message.Field.STATUS.toString();
  private static final String NEW_STATUS = Message.Status.NEW.name();
  private static final String REPLIES_FIELD = Message.Field.REPLIES.toString();

  private static final String REPLY_FIELD = Message.Field.IN_REPLY_TO.toString();

  private static final String SEND_MESSAGE_QUERY_NAME = "cSendMessage";
  private static final String SEND_MESSAGE_QUERY = "START sender=node:" + Constants.PERSON_NODES
          + "(id = {id}), recipient=node:" + Constants.PERSON_NODES + "({idLookup})\n"
          + "CREATE sender-[:SENT]->(message {props})" + ", message-[:SENT_TO]->recipient\n"
          + "WITH message, sender, recipient\n" + "MATCH sender-[:OWNS]->outbox\n"
          + "WHERE outbox.id = '@outbox'\n" + "CREATE outbox-[:CONTAINS]->message\n"
          + "WITH message, recipient\n" + "MATCH recipient-[:OWNS]->inbox\n"
          + "WHERE inbox.id = 'inbox'\n" + "CREATE inbox-[:CONTAINS {status: 'NEW'}]->message\n"
          + "RETURN ID(message)";

  private static final String REPLY_QUERY_NAME = "cReply";
  private static final String REPLY_QUERY = "START message=node({messId}), parent=node:"
          + Constants.MESSAGE_NODES + "(id = {parId})\n" + "CREATE message-[:REPLY_TO]->parent\n"
          + "RETURN ID(parent)";

  private static final String DEL_MESS_QUERY_NAME = "cDeleteMessage";
  private static final String DEL_MESS_QUERY = "START person=node:" + Constants.PERSON_NODES
          + "(id = {id})" + ", message=node:" + Constants.MESSAGE_NODES + "({messLookup})\n"
          + "MATCH message<-[con:CONTAINS]-()<-[:OWNS]-person\n" + "DELETE con\n"
          + "WITH message\n" + "WHERE NOT (message--())\n" + "DELETE message\n";

  private final IQueryHandler fQueryHandler;

  private final Logger fLogger;

  /**
   * Creates a new Cypher message service using the given query handler to retrieve data from a
   * Neo4j instance over a websocket using Cypher. The given query handler must not be null.
   *
   * @param qHandler
   *          query handler to use
   */
  @Inject
  public WsCypherMessageSPI(IQueryHandler qHandler) {
    if (qHandler == null) {
      throw new NullPointerException("Query handler was null");
    }

    this.fQueryHandler = qHandler;
    this.fLogger = Logger.getLogger(this.getClass().getName());

    // initialize stored procedures
    // basic message sending
    WebsockQuery wsQuery = new WebsockQuery(EQueryType.STORE_PROCEDURE);
    wsQuery.setPayload(WsCypherMessageSPI.SEND_MESSAGE_QUERY);
    wsQuery.setParameter(WebsockConstants.PROCEDURE_NAME,
            WsCypherMessageSPI.SEND_MESSAGE_QUERY_NAME);
    try {
      this.fQueryHandler.sendMessage(wsQuery).get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new RuntimeException("could not store procedure \""
              + wsQuery.getParameter(WebsockConstants.PROCEDURE_NAME), e);
    }

    // reply link query
    wsQuery = new WebsockQuery(EQueryType.STORE_PROCEDURE);
    wsQuery.setPayload(WsCypherMessageSPI.REPLY_QUERY);
    wsQuery.setParameter(WebsockConstants.PROCEDURE_NAME, WsCypherMessageSPI.REPLY_QUERY_NAME);

    try {
      this.fQueryHandler.sendMessage(wsQuery).get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new RuntimeException("could not store procedure \""
              + wsQuery.getParameter(WebsockConstants.PROCEDURE_NAME), e);
    }

    // delete message query
    wsQuery = new WebsockQuery(EQueryType.STORE_PROCEDURE);
    wsQuery.setPayload(WsCypherMessageSPI.DEL_MESS_QUERY);
    wsQuery.setParameter(WebsockConstants.PROCEDURE_NAME, WsCypherMessageSPI.DEL_MESS_QUERY_NAME);

    try {
      this.fQueryHandler.sendMessage(wsQuery).get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new RuntimeException("could not store procedure \""
              + wsQuery.getParameter(WebsockConstants.PROCEDURE_NAME), e);
    }
  }

  private TableResult queryMessColls(String userId, final Set<String> fields, CollectionOptions opts) {
    final WebsockQuery wsQuery = new WebsockQuery(EQueryType.DIRECT_CYPHER);
    final StringBuffer query = new StringBuffer();
    final Set<String> toReturn = new LinkedHashSet<String>();

    if (opts != null) {
      CollOptsConverter.convert(opts, wsQuery);
    }

    query.append("START person=node:" + Constants.PERSON_NODES + "(id = {id})\n"
            + "MATCH person-[:OWNS]->collection");
    wsQuery.setParameter("id", userId);
    toReturn.add("DISTINCT collection");

    // total message relationships
    if (fields == null || fields.isEmpty() || fields.contains(WsCypherMessageSPI.TOTAL_FIELD)
            || fields.contains(WsCypherMessageSPI.UNREAD_FIELD)) {
      toReturn.add("COLLECT(EXTRACT(" + "p IN collection-[:CONTAINS]->(): RELATIONSHIPS(p)))"
              + " as conrels");
    }

    query.append("\nRETURN ");
    final Iterator<String> rets = toReturn.iterator();
    while (rets.hasNext()) {
      query.append(rets.next());

      if (rets.hasNext()) {
        query.append(", ");
      }
    }

    wsQuery.setPayload(query.toString());

    // execute
    final IQueryCallback callback = this.fQueryHandler.sendQuery(wsQuery);
    TableResult result = null;

    try {
      result = (TableResult) callback.get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not retrieve result", e);
    }

    return result;
  }

  @SuppressWarnings("unchecked")
  private Future<RestfulCollection<MessageCollection>> convertColls(TableResult resultTable) {
    final List<MessageCollection> colls = new ArrayList<MessageCollection>();

    // convert to maps for DTOs
    final List<List<Object>> listList = resultTable.getResults();

    final int nodeIndex = resultTable.getColumnIndex("collection");
    final int consIndex = resultTable.getColumnIndex("conrels");

    // collect root nodes and wrap them
    Map<String, Object> collMap = null;
    for (final List<Object> pList : listList) {
      collMap = (Map<String, Object>) pList.get(nodeIndex);

      if (consIndex > 0) {
        int total = 0;
        int unread = 0;

        final List<List<List<Map<String, Object>>>> cons = (List<List<List<Map<String, Object>>>>) pList
                .get(consIndex);

        // store total and unread messages
        for (final List<Map<String, Object>> relList : cons.get(0)) {
          ++total;

          if (WsCypherMessageSPI.NEW_STATUS.equals(relList.get(0).get(
                  WsCypherMessageSPI.STATUS_FIELD))) {
            ++unread;
          }
        }

        collMap.put(WsCypherMessageSPI.TOTAL_FIELD, total);
        collMap.put(WsCypherMessageSPI.UNREAD_FIELD, unread);
      }

      colls.add(new MessageCollectionDTO(collMap));
    }

    final RestfulCollection<MessageCollection> rColl = new RestfulCollection<MessageCollection>(
            colls);
    rColl.setStartIndex(resultTable.getFirst());
    rColl.setTotalResults(resultTable.getTotal());
    rColl.setItemsPerPage(resultTable.getMax());
    return Futures.immediateFuture(rColl);
  }

  @Override
  public Future<RestfulCollection<MessageCollection>> getMessageCollections(UserId userId,
          Set<String> fields, CollectionOptions options, SecurityToken token)
          throws ProtocolException {
    final TableResult result = queryMessColls(userId.getUserId(token), fields, options);
    return convertColls(result);
  }

  @Override
  public Future<MessageCollection> createMessageCollection(UserId userId,
          MessageCollection msgCollection, SecurityToken token) throws ProtocolException {
    return null;
  }

  @Override
  public Future<Void> modifyMessageCollection(UserId userId, MessageCollection msgCollection,
          SecurityToken token) throws ProtocolException {
    return null;
  }

  @Override
  public Future<Void> deleteMessageCollection(UserId userId, String msgCollId, SecurityToken token)
          throws ProtocolException {
    return null;
  }

  private TableResult queryMsgs(String userId, final List<String> msgIds, String collId,
          final Set<String> fields, CollectionOptions opts) {
    final WebsockQuery wsQuery = new WebsockQuery(EQueryType.DIRECT_CYPHER);
    final StringBuffer query = new StringBuffer();
    final Set<String> wheres = new HashSet<String>();
    final Set<String> toReturn = new HashSet<String>();

    if (opts != null) {
      CollOptsConverter.convert(opts, wsQuery);
    }

    query.append("START person=node:" + Constants.PERSON_NODES + "(id = {id})\n");
    wsQuery.setParameter("id", userId);
    query.append("MATCH person-[:OWNS]->collection-[rel:CONTAINS]->msg");

    toReturn.add("rel.status? as status");
    toReturn.add("msg");

    // from collection
    if (collId != null && !collId.isEmpty()) {
      wheres.add("collection.id = {collId}");
      wsQuery.setParameter("collId", collId);
    }

    // selective
    if (msgIds != null && !msgIds.isEmpty()) {
      wheres.add("has(msg.id) AND msg.id IN {msgIds}");
      wsQuery.setParameter("msgIds", msgIds.toArray());
    }

    // reply IDs
    if (fields == null || fields.isEmpty() || fields.contains(WsCypherMessageSPI.REPLY_FIELD)) {
      query.append(", msg<-[?:REPLY_TO]-reply");
      toReturn.add("collect(reply.id) as replies");
    }

    query.append("\nWHERE ");
    final Iterator<String> conditions = wheres.iterator();
    while (conditions.hasNext()) {
      query.append(conditions.next());

      if (conditions.hasNext()) {
        query.append(", ");
      }
    }

    query.append("\nRETURN ");
    final Iterator<String> rets = toReturn.iterator();
    while (rets.hasNext()) {
      query.append(rets.next());

      if (rets.hasNext()) {
        query.append(", ");
      }
    }

    wsQuery.setPayload(query.toString());

    // execute
    final IQueryCallback callback = this.fQueryHandler.sendQuery(wsQuery);
    TableResult result = null;

    try {
      result = (TableResult) callback.get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not retrieve result", e);
    }

    return result;
  }

  @SuppressWarnings("unchecked")
  private Future<RestfulCollection<Message>> convertMsgs(final TableResult resultTable) {
    final List<Message> messages = new ArrayList<Message>();

    // convert to maps for DTOs
    final List<List<Object>> listList = resultTable.getResults();

    final int nodeIndex = resultTable.getColumnIndex("msg");
    final int statusIndex = resultTable.getColumnIndex("status");
    final int repliesIndex = resultTable.getColumnIndex("replies");

    // collect root nodes and wrap
    Map<String, Object> mMap = null;
    for (final List<Object> pList : listList) {
      mMap = (Map<String, Object>) pList.get(nodeIndex);

      if (statusIndex > 0) {
        mMap.put(WsCypherMessageSPI.STATUS_FIELD, pList.get(statusIndex));
      }

      if (repliesIndex > 0) {
        mMap.put(WsCypherMessageSPI.REPLIES_FIELD, pList.get(statusIndex));
      }

      messages.add(new MessageDTO(mMap));
    }

    final RestfulCollection<Message> rColl = new RestfulCollection<Message>(messages);
    rColl.setStartIndex(resultTable.getFirst());
    rColl.setTotalResults(resultTable.getTotal());
    rColl.setItemsPerPage(resultTable.getMax());
    return Futures.immediateFuture(rColl);
  }

  @Override
  public Future<RestfulCollection<Message>> getMessages(UserId userId, String msgCollId,
          Set<String> fields, List<String> msgIds, CollectionOptions options, SecurityToken token)
          throws ProtocolException {
    final TableResult result = queryMsgs(userId.getUserId(token), msgIds, msgCollId, fields,
            options);
    return convertMsgs(result);
  }

  @Override
  public Future<Void> createMessage(UserId userId, String appId, String msgCollId, Message message,
          SecurityToken token) throws ProtocolException {
    // TODO: sanity check

    // request unique ID for new message
    WebsockQuery wsQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    wsQuery.setPayload(Neo4jServiceQueries.GET_UID_QUERY);
    wsQuery.setParameter(Neo4jServiceQueries.TYPE, Constants.MESSAGE_NODES);

    IQueryCallback callback = this.fQueryHandler.sendQuery(wsQuery);

    try {
      final SingleResult idRes = (SingleResult) callback.get();
      message.setId(idRes.getResults().get("id").toString());
      message.setTimeSent(new Date(System.currentTimeMillis()));
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not retrieve unique ID", e);
    }

    // TODO: only store draft, don't send?

    // create message
    // create property map and strip external parameters
    final Map<String, Object> mMap = new HashMap<String, Object>();
    new MessageDTO(mMap).setData(message);
    // TODO: complete?

    wsQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    wsQuery.setPayload(WsCypherMessageSPI.SEND_MESSAGE_QUERY_NAME);
    wsQuery.setParameter("id", userId.getUserId(token));
    wsQuery.setParameter("props", mMap);

    // TODO: no recipients?
    String idLookup = "id:(";
    for (final String id : message.getRecipients()) {
      idLookup += id + " ";
    }
    idLookup += ")";
    wsQuery.setParameter("idLookup", idLookup);

    // execute
    callback = this.fQueryHandler.sendQuery(wsQuery);
    TableResult result = null;

    try {
      result = (TableResult) callback.get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not retrieve result", e);
    }

    final String msgId = result.getResults().get(0).get(0).toString();

    // create index entry
    wsQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    wsQuery.setPayload(Neo4jServiceQueries.CREATE_INDEX_ENTRY_QUERY);
    wsQuery.setParameter(Neo4jServiceQueries.INDEX, Constants.MESSAGE_NODES);
    wsQuery.setParameter(Neo4jServiceQueries.NODE_ID, Long.parseLong(msgId));
    wsQuery.setParameter(Neo4jServiceQueries.KEY, "id");
    wsQuery.setParameter(Neo4jServiceQueries.VALUE, message.getId());

    callback = this.fQueryHandler.sendQuery(wsQuery);
    try {
      callback.get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not create index entry", e);
    }

    // reply to relationship
    if (message.getInReplyTo() != null) {
      wsQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
      wsQuery.setPayload(WsCypherMessageSPI.REPLY_QUERY_NAME);
      wsQuery.setParameter("messId", msgId);
      wsQuery.setParameter("parId", message.getInReplyTo());

      callback = this.fQueryHandler.sendQuery(wsQuery);
      try {
        callback.get();
      } catch (final Exception e) {
        e.printStackTrace();
        this.fLogger.log(Level.SEVERE, "server error", e);
        throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "could not retrieve result", e);
      }
    }

    return Futures.immediateFuture(null);
  }

  @Override
  public Future<Void> deleteMessages(UserId userId, String msgCollId, List<String> ids,
          SecurityToken token) throws ProtocolException {
    final WebsockQuery wsQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    wsQuery.setPayload(WsCypherMessageSPI.DEL_MESS_QUERY_NAME);
    wsQuery.setParameter("id", userId.getUserId());

    String messLookup = "id:(";
    for (final String id : ids) {
      messLookup += id + " ";
    }
    messLookup += ")";
    wsQuery.setParameter("messLookup", messLookup);

    // execute
    final IQueryCallback callback = this.fQueryHandler.sendQuery(wsQuery);

    try {
      callback.get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not retrieve unique ID", e);
    }

    return Futures.immediateFuture(null);
  }

  @Override
  public Future<Void> modifyMessage(UserId userId, String msgCollId, String messageId,
          Message message, SecurityToken token) throws ProtocolException {
    return null;
  }
}
