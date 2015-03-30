/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shindig.social.websockbackend.spi;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import de.hofuniversity.iisys.neo4j.websock.result.ListResult;
import de.hofuniversity.iisys.neo4j.websock.result.SingleResult;
import de.hofuniversity.iisys.neo4j.websock.shindig.ShindigNativeQueries;

/**
 * Implementation of the MessageService interface retrieving message and message collection data
 * from a remote Neo4j graph database over a websocket.
 */
@Singleton
public class WsNativeMessageSPI implements MessageService {
  private static final String ID_FIELD = MessageCollection.Field.ID.toString();
  private static final String TITLE_FIELD = MessageCollection.Field.TITLE.toString();

  private final IQueryHandler fQueryHandler;
  private final Logger fLogger;

  /**
   * Creates a graph person service using the given query handler to dispatch queries to a remote
   * server. Throws a NullPointerException if the given query handler is null.
   *
   * @param qHandler
   *          query handler to use
   */
  @Inject
  public WsNativeMessageSPI(IQueryHandler qHandler) {
    if (qHandler == null) {
      throw new NullPointerException("query handler was null");
    }

    this.fQueryHandler = qHandler;
    this.fLogger = Logger.getLogger(this.getClass().getName());
  }

  @Override
  public Future<RestfulCollection<MessageCollection>> getMessageCollections(UserId userId,
          Set<String> fields, CollectionOptions options, SecurityToken token)
          throws ProtocolException {
    final List<MessageCollection> collections = new ArrayList<MessageCollection>();

    final String sortField = options.getSortBy();
    if (sortField == null) {
      options.setSortBy(WsNativeMessageSPI.TITLE_FIELD);
    }

    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.GET_MESSAGE_COLLECTIONS_QUERY);

    // set options
    CollOptsConverter.convert(options, query);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));

    if (fields != null) {
      final List<String> fieldList = new ArrayList<String>(fields);
      query.setParameter(ShindigNativeQueries.FIELD_LIST, fieldList);
    }

    final IQueryCallback result = this.fQueryHandler.sendQuery(query);

    ListResult resultList = null;

    try {
      resultList = (ListResult) result.get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not retrieve results", e);
    }

    @SuppressWarnings("unchecked")
    final List<Map<String, Object>> mapList = (List<Map<String, Object>>) resultList.getResults();

    // convert the items requested
    if (mapList != null) {
      MessageCollectionDTO dto = null;
      for (final Map<String, Object> mcMap : mapList) {
        dto = new MessageCollectionDTO(mcMap);
        collections.add(dto);
      }
    }

    final RestfulCollection<MessageCollection> collColl = new RestfulCollection<MessageCollection>(
            collections);
    collColl.setItemsPerPage(resultList.getMax());
    collColl.setStartIndex(resultList.getFirst());
    collColl.setTotalResults(resultList.getTotal());
    return Futures.immediateFuture(collColl);
  }

  @Override
  public Future<MessageCollection> createMessageCollection(UserId userId,
          MessageCollection msgCollection, SecurityToken token) throws ProtocolException {
    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.CREATE_MESS_COLL_QUERY);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));

    final Map<String, Object> collMap = new HashMap<String, Object>();
    MessageCollectionDTO dto = new MessageCollectionDTO(collMap);
    dto.setData(msgCollection);
    dto.stripNullValues();
    query.setParameter(ShindigNativeQueries.MESSAGE_COLLECTION_OBJECT, collMap);

    // execute
    final IQueryCallback result = this.fQueryHandler.sendQuery(query);

    SingleResult sResult = null;

    try {
      sResult = (SingleResult) result.get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not retrieve result", e);
    }

    @SuppressWarnings("unchecked")
    final Map<String, Object> map = (Map<String, Object>) sResult.getResults();
    dto = new MessageCollectionDTO(map);

    return Futures.immediateFuture((MessageCollection) dto);
  }

  @Override
  public Future<Void> modifyMessageCollection(UserId userId, MessageCollection msgCollection,
          SecurityToken token) throws ProtocolException {
    // set time stamp
    msgCollection.setUpdated(new Date(System.currentTimeMillis()));

    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.MODIFY_MESS_COLL_QUERY);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));

    final Map<String, Object> collMap = new HashMap<String, Object>();
    final MessageCollectionDTO dto = new MessageCollectionDTO(collMap);
    dto.setData(msgCollection);
    dto.stripNullValues();
    query.setParameter(ShindigNativeQueries.MESSAGE_COLLECTION_OBJECT, collMap);

    // execute
    final IQueryCallback result = this.fQueryHandler.sendQuery(query);

    try {
      result.get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "failed to execute query", e);
    }

    return Futures.immediateFuture(null);
  }

  @Override
  public Future<Void> deleteMessageCollection(UserId userId, String msgCollId, SecurityToken token)
          throws ProtocolException {
    // TODO: block deletion of inbox and outbox?

    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.DELETE_MESS_COLL_QUERY);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));
    query.setParameter(ShindigNativeQueries.MESSAGE_COLLECTION_ID, msgCollId);

    // execute
    final IQueryCallback result = this.fQueryHandler.sendQuery(query);

    try {
      result.get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "failed to execute query", e);
    }

    return Futures.immediateFuture(null);
  }

  @Override
  public Future<RestfulCollection<Message>> getMessages(UserId userId, String msgCollId,
          Set<String> fields, List<String> msgIds, CollectionOptions options, SecurityToken token)
          throws ProtocolException {
    final List<Message> messages = new ArrayList<Message>();

    final String sortField = options.getSortBy();
    if (sortField == null) {
      options.setSortBy(WsNativeMessageSPI.ID_FIELD);
    }

    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.GET_MESSAGES_QUERY);

    // set options
    CollOptsConverter.convert(options, query);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));
    query.setParameter(ShindigNativeQueries.MESSAGE_COLLECTION_ID, msgCollId);

    if (msgIds != null && !msgIds.isEmpty()) {
      query.setParameter(ShindigNativeQueries.MESSAGE_ID_LIST, msgIds);
    }

    if (fields != null) {
      final List<String> fieldList = new ArrayList<String>(fields);
      query.setParameter(ShindigNativeQueries.FIELD_LIST, fieldList);
    }

    final IQueryCallback result = this.fQueryHandler.sendQuery(query);

    ListResult resultList = null;

    try {
      resultList = (ListResult) result.get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not retrieve results", e);
    }

    @SuppressWarnings("unchecked")
    final List<Map<String, Object>> mapList = (List<Map<String, Object>>) resultList.getResults();

    // convert the items requested
    MessageDTO dto = null;
    for (final Map<String, Object> messMap : mapList) {
      dto = new MessageDTO(messMap);
      messages.add(dto);
    }

    final RestfulCollection<Message> messageColl = new RestfulCollection<Message>(messages);
    messageColl.setItemsPerPage(resultList.getMax());
    messageColl.setStartIndex(resultList.getFirst());
    messageColl.setTotalResults(resultList.getTotal());
    return Futures.immediateFuture(messageColl);
  }

  @Override
  public Future<Void> createMessage(UserId userId, String appId, String msgCollId, Message message,
          SecurityToken token) throws ProtocolException {
    final Date time = new Date(System.currentTimeMillis());
    message.setTimeSent(time);
    message.setUpdated(time);

    final Map<String, Object> msgMap = new HashMap<String, Object>();
    final MessageDTO dto = new MessageDTO(msgMap);
    dto.setData(message);
    dto.stripNullValues();

    if (msgCollId == null || msgCollId.isEmpty()) {
      msgCollId = MessageCollection.OUTBOX;
    }

    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.CREATE_MESSAGE_QUERY);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));
    query.setParameter(ShindigNativeQueries.APP_ID, appId);
    query.setParameter(ShindigNativeQueries.MESSAGE_COLLECTION_ID, msgCollId);
    query.setParameter(ShindigNativeQueries.MESSAGE_OBJECT, msgMap);

    try {
      this.fQueryHandler.sendQuery(query).get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not create message", e);
    }

    return Futures.immediateFuture(null);
  }

  @Override
  public Future<Void> deleteMessages(UserId userId, String msgCollId, List<String> ids,
          SecurityToken token) throws ProtocolException {
    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.DELETE_MESSAGES_QUERY);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));
    query.setParameter(ShindigNativeQueries.MESSAGE_COLLECTION_ID, msgCollId);
    query.setParameter(ShindigNativeQueries.MESSAGE_ID_LIST, ids);

    try {
      this.fQueryHandler.sendQuery(query).get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not delete messages", e);
    }

    return Futures.immediateFuture(null);
  }

  @Override
  public Future<Void> modifyMessage(UserId userId, String msgCollId, String messageId,
          Message message, SecurityToken token) throws ProtocolException {
    // set time stamp
    message.setUpdated(new Date(System.currentTimeMillis()));

    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.MODIFY_MESSAGE_QUERY);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));
    query.setParameter(ShindigNativeQueries.MESSAGE_COLLECTION_ID, msgCollId);
    query.setParameter(ShindigNativeQueries.MESSAGE_ID, messageId);

    final Map<String, Object> msgMap = new HashMap<String, Object>();
    final MessageDTO dto = new MessageDTO(msgMap);
    dto.setData(message);
    dto.stripNullValues();
    query.setParameter(ShindigNativeQueries.MESSAGE_OBJECT, msgMap);

    // execute
    try {
      this.fQueryHandler.sendQuery(query).get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not modify message", e);
    }

    return Futures.immediateCheckedFuture(null);
  }
}
