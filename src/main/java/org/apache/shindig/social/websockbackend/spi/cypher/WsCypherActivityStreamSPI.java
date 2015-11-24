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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.spi.ActivityStreamService;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.GroupId.Type;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.websockbackend.Constants;
import org.apache.shindig.social.websockbackend.model.dto.ActivityEntryDTO;
import org.apache.shindig.social.websockbackend.util.CollOptsConverter;

import com.google.common.collect.ImmutableSet;
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
 * Implementation of the ActivityStreamService interface retrieving activity stream data from a
 * remote Neo4j graph database over a websocket using Cypher.
 */
@Singleton
public class WsCypherActivityStreamSPI implements ActivityStreamService {
  private static final String ACTOR_FIELD = ActivityEntry.Field.ACTOR.toString();
  private static final String GENERATOR_FIELD = ActivityEntry.Field.GENERATOR.toString();
  private static final String OBJECT_FIELD = ActivityEntry.Field.OBJECT.toString();
  private static final String PROVIDER_FIELD = ActivityEntry.Field.PROVIDER.toString();
  private static final String TARGET_FIELD = ActivityEntry.Field.TARGET.toString();

  private static final String DEL_ENTRIES_PROCEDURE = "cDeleteActivities";
  private static final String DEL_ENTRIES_QUERY = "person=node:" + Constants.PERSON_NODES
          + "({idLookup})\n" + "MATCH person-[:ACTED]->activity-[rel]->obj\n"
          + "WHERE activity.id IN {actIds}\n" + "DELETE rel, entry\n" + "WITH obj as object\n"
          + "WHERE NOT (object--())\n" + "DELETE obj;";

  private final IQueryHandler fQueryHandler;

  private final Logger fLogger;

  /**
   * Creates a new Cypher activity stream service using the given query handler to retrieve data
   * from a Neo4j instance over a websocket using Cypher. The given query handler must not be null.
   *
   * @param qHandler
   *          query handler to use
   */
  @Inject
  public WsCypherActivityStreamSPI(IQueryHandler qHandler) {
    if (qHandler == null) {
      throw new NullPointerException("Query handler was null");
    }

    this.fQueryHandler = qHandler;
    this.fLogger = Logger.getLogger(this.getClass().getName());

    // initialize stored procedures
    // delete query
    final WebsockQuery wsQuery = new WebsockQuery(EQueryType.STORE_PROCEDURE);
    wsQuery.setPayload(WsCypherActivityStreamSPI.DEL_ENTRIES_QUERY);
    wsQuery.setParameter(WebsockConstants.PROCEDURE_NAME,
            WsCypherActivityStreamSPI.DEL_ENTRIES_PROCEDURE);
    try {
      this.fQueryHandler.sendMessage(wsQuery).get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new RuntimeException("could not store procedure \""
              + wsQuery.getParameter(WebsockConstants.PROCEDURE_NAME), e);
    }
  }

  private TableResult query(Set<UserId> idSet, GroupId groupId, Set<String> actIds,
          final Set<String> fields, CollectionOptions opts) {
    final WebsockQuery wsQuery = new WebsockQuery(EQueryType.DIRECT_CYPHER);

    final Set<String> toReturn = new HashSet<String>();
    final StringBuffer query = new StringBuffer();

    if (opts != null) {
      CollOptsConverter.convert(opts, wsQuery);
    }

    if (groupId == null) {
      groupId = new GroupId(Type.self, "@self");
    }

    // build request start string
    if (groupId.getType() == Type.objectId) {
      query.append("START group=node:" + Constants.GROUP_NODES + "(id = {id})\n");
      wsQuery.setParameter("id", groupId.getObjectId());
    } else {
      query.append("START person=node:" + Constants.PERSON_NODES + "({idLookup})\n");

      String idLookup = "id:(";
      for (final UserId userId : idSet) {
        idLookup += userId.getUserId() + " ";
      }
      idLookup += ")";
      wsQuery.setParameter("idLookup", idLookup);
    }

    // for whom
    switch (groupId.getType()) {
    case self:
      query.append("MATCH person-[:ACTED]->activity");
      break;

    case friends:
      query.append("MATCH person-[:FRIEND_OF]->()-[:ACTED]->activity");
      break;

    case objectId:
      query.append("MATCH group<-[:MEMBER_OF]-()-[:ACTED]->activity");
      break;
    }
    toReturn.add("activity");

    // attached objects
    handleExternal(query, fields, toReturn);

    // TODO: application ID?

    // selective
    if (actIds != null && !actIds.isEmpty()) {
      query.append("\nWHERE has(activity.id) AND activity.id IN {actIds}");
      wsQuery.setParameter("actIds", actIds.toArray());
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

  private void handleExternal(final StringBuffer query, final Set<String> fields,
          final Set<String> toReturn) {
    if (fields == null || fields.isEmpty()) {
      // old, unoptimized but more intuitive
      // query.append(", activity-[?:ACTOR]->actor");
      // query.append(", activity-[?:OBJECT]->object");
      // query.append(", activity-[?:TARGET]->target");
      // query.append(", activity-[?:GENERATOR]->generator");
      // query.append(", activity-[?:PROVIDER]->provider");
      //
      // toReturn.add("actor");
      // toReturn.add("object");
      // toReturn.add("target");
      // toReturn.add("generator");
      // toReturn.add("provider");

      toReturn.add("extract(p in activity-[:ACTOR]->() : last(p)) " + "as actor");
      toReturn.add("extract(p in activity-[:OBJECT]->() : last(p)) " + "as object");
      toReturn.add("extract(p in activity-[:TARGET]->() : last(p)) " + "as target");
      toReturn.add("extract(p in activity-[:GENERATOR]->() : last(p)) " + "as generator");
      toReturn.add("extract(p in activity-[:PROVIDER]->() : last(p)) " + "as provider");
    } else {
      if (fields.contains(WsCypherActivityStreamSPI.ACTOR_FIELD)) {
        // query.append(", activity-[?:ACTOR]->actor");
        // toReturn.add("actor");

        toReturn.add("extract(p in activity-[:ACTOR]->() : last(p)) " + "as actor");
      }

      if (fields.contains(WsCypherActivityStreamSPI.OBJECT_FIELD)) {
        // query.append(", activity-[?:OBJECT]->object");
        // toReturn.add("object");

        toReturn.add("extract(p in activity-[:OBJECT]->() : last(p)) " + "as object");
      }

      if (fields.contains(WsCypherActivityStreamSPI.TARGET_FIELD)) {
        // query.append(", activity-[?:TARGET]->target");
        // toReturn.add("target");

        toReturn.add("extract(p in activity-[:TARGET]->() : last(p)) " + "as target");
      }

      if (fields.contains(WsCypherActivityStreamSPI.GENERATOR_FIELD)) {
        // query.append(", activity-[?:GENERATOR]->generator");
        // toReturn.add("generator");

        toReturn.add("extract(p in activity-[:GENERATOR]->() : " + "last(p)) as generator");
      }

      if (fields.contains(WsCypherActivityStreamSPI.PROVIDER_FIELD)) {
        // query.append(", provider-[?:PROVIDER]->provider");
        // toReturn.add("provider");

        toReturn.add("extract(p in activity-[:PROVIDER]->() : last(p))" + " as provider");
      }
    }
  }

  @SuppressWarnings("unchecked")
  private Future<RestfulCollection<ActivityEntry>> convertTable(final TableResult resultTable) {
    final List<ActivityEntry> entries = new ArrayList<ActivityEntry>();
    final List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();

    // convert to maps for DTOs
    final List<List<Object>> listList = resultTable.getResults();

    final int nodeIndex = resultTable.getColumnIndex("activity");
    final int actorIndex = resultTable.getColumnIndex("actor");
    final int objectIndex = resultTable.getColumnIndex("object");
    final int targetIndex = resultTable.getColumnIndex("target");
    final int providerIndex = resultTable.getColumnIndex("provider");
    final int generatorIndex = resultTable.getColumnIndex("generator");

    // collect root nodes
    for (final List<Object> actList : listList) {
      maps.add((Map<String, Object>) actList.get(nodeIndex));
    }

    // collect subordinate maps
    addAll(maps, listList, WsCypherActivityStreamSPI.ACTOR_FIELD, actorIndex);
    addAll(maps, listList, WsCypherActivityStreamSPI.OBJECT_FIELD, objectIndex);
    addAll(maps, listList, WsCypherActivityStreamSPI.TARGET_FIELD, targetIndex);
    addAll(maps, listList, WsCypherActivityStreamSPI.PROVIDER_FIELD, providerIndex);
    addAll(maps, listList, WsCypherActivityStreamSPI.GENERATOR_FIELD, generatorIndex);

    // wrap maps
    for (final Map<String, Object> actMap : maps) {
      entries.add(new ActivityEntryDTO(actMap));
    }

    final RestfulCollection<ActivityEntry> rColl = new RestfulCollection<ActivityEntry>(entries);
    rColl.setStartIndex(resultTable.getFirst());
    rColl.setTotalResults(resultTable.getTotal());
    rColl.setItemsPerPage(resultTable.getMax());
    return Futures.immediateFuture(rColl);
  }

  private void addAll(final List<Map<String, Object>> entries, final List<List<Object>> listList,
          final String field, final int index) {
    if (index < 0) {
      return;
    }

    final Iterator<List<Object>> listIt = listList.iterator();
    List<?> actObjList = null;
    for (final Map<String, Object> actMap : entries) {
      actObjList = (List<?>) listIt.next().get(index);

      if (actObjList != null && actObjList.size() > 0) {
        actMap.put(field, actObjList.get(0));
      }
    }
  }

  @Override
  public Future<RestfulCollection<ActivityEntry>> getActivityEntries(Set<UserId> userIds,
          GroupId groupId, String appId, Set<String> fields, CollectionOptions options,
          SecurityToken token) throws ProtocolException {
    final TableResult result = query(userIds, groupId, null, fields, options);
    return convertTable(result);
  }

  @Override
  public Future<RestfulCollection<ActivityEntry>> getActivityEntries(UserId userId,
          GroupId groupId, String appId, Set<String> fields, CollectionOptions options,
          Set<String> activityIds, SecurityToken token) throws ProtocolException {
    final Set<UserId> userIds = ImmutableSet.of(userId);
    final TableResult result = query(userIds, groupId, activityIds, fields, options);
    return convertTable(result);
  }

  @Override
  public Future<ActivityEntry> getActivityEntry(UserId userId, GroupId groupId, String appId,
          Set<String> fields, String activityId, SecurityToken token) throws ProtocolException {
    final Set<UserId> userIds = ImmutableSet.of(userId);
    final Set<String> activityIds = ImmutableSet.of(activityId);
    final TableResult result = query(userIds, groupId, activityIds, fields, null);

    ActivityEntry entry = null;
    try {
      final RestfulCollection<ActivityEntry> coll = convertTable(result).get();
      final int size = coll.getList().size();

      if (size == 1) {
        entry = coll.getList().get(0);
      } else if (size > 1) {
        throw new RuntimeException("ID not unique");
      } else {
        throw new RuntimeException("message not found");
      }
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage(), e);
    }

    return Futures.immediateFuture(entry);
  }

  @Override
  public Future<Void> deleteActivityEntries(UserId userId, GroupId groupId, String appId,
          Set<String> activityIds, SecurityToken token) throws ProtocolException {
    final WebsockQuery wsQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    wsQuery.setPayload(WsCypherActivityStreamSPI.DEL_ENTRIES_PROCEDURE);

    final String idLookup = "id:(" + userId.getUserId() + ")";
    wsQuery.setParameter("idLookup", idLookup);
    wsQuery.setParameter("actIds", activityIds.toArray());

    final IQueryCallback callback = this.fQueryHandler.sendQuery(wsQuery);

    try {
      callback.get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not retrieve results", e);
    }

    return Futures.immediateFuture(null);
  }

  @Override
  public Future<ActivityEntry> updateActivityEntry(UserId userId, GroupId groupId, String appId,
          Set<String> fields, ActivityEntry activity, String activityId, SecurityToken token)
          throws ProtocolException {
    // TODO Auto-generated method stub
    return null;
  }

  @SuppressWarnings("unchecked")
  private TableResult creationQuery(final String userId, final String appId,
          final Map<String, Object> entProps) {
    TableResult result = null;
    final WebsockQuery wsQuery = new WebsockQuery(EQueryType.DIRECT_CYPHER);
    final Set<String> toReturn = new HashSet<String>();

    final StringBuffer buffer = new StringBuffer();
    buffer.append("START person=node:" + Constants.PERSON_NODES);
    wsQuery.setParameter("uid", userId);
    buffer.append("(id = {uid})");

    // TODO: create applications?
    if (appId != null && !appId.isEmpty()) {
      buffer.append(", app=node:" + Constants.APP_NODES);
      wsQuery.setParameter("appId", appId);
      buffer.append("(id = {appId})");
      buffer.append("\n");
    }

    buffer.append("CREATE person-[:ACTED]->(activity {props})\n");

    if (appId != null && !appId.isEmpty()) {
      buffer.append("CREATE activity-[:CAME_FROM]->app\n");
    }

    final Map<String, Object> actProps = (Map<String, Object>) entProps
            .get(WsCypherActivityStreamSPI.ACTOR_FIELD);
    final Map<String, Object> objProps = (Map<String, Object>) entProps
            .get(WsCypherActivityStreamSPI.OBJECT_FIELD);
    final Map<String, Object> tarProps = (Map<String, Object>) entProps
            .get(WsCypherActivityStreamSPI.TARGET_FIELD);
    final Map<String, Object> provProps = (Map<String, Object>) entProps
            .get(WsCypherActivityStreamSPI.PROVIDER_FIELD);
    final Map<String, Object> genProps = (Map<String, Object>) entProps
            .get(WsCypherActivityStreamSPI.GENERATOR_FIELD);

    if (actProps != null) {
      wsQuery.setParameter("actProps", actProps);
      buffer.append("CREATE activity-[:ACTOR]->(actor {actProps})\n");
      toReturn.add("actor");
    }
    if (objProps != null) {
      wsQuery.setParameter("objProps", objProps);
      buffer.append("CREATE activity-[:OBJECT]->(object {objProps})\n");
      toReturn.add("object");
    }
    if (tarProps != null) {
      wsQuery.setParameter("tarProps", tarProps);
      buffer.append("CREATE activity-[:TARGET]->(target {tarProps})\n");
      toReturn.add("target");
    }
    if (provProps != null) {
      wsQuery.setParameter("provProps", provProps);
      buffer.append("CREATE activity-[:PROVIDER]->(target {provProps})\n");
      toReturn.add("provider");
    }
    if (genProps != null) {
      wsQuery.setParameter("genProps", genProps);
      buffer.append("CREATE activity-[:GENERATOR]->(generator {genProps})\n");
      toReturn.add("generator");
    }

    // strip external parameters and set
    // TODO: complete?
    entProps.remove(WsCypherActivityStreamSPI.ACTOR_FIELD);
    entProps.remove(WsCypherActivityStreamSPI.OBJECT_FIELD);
    entProps.remove(WsCypherActivityStreamSPI.TARGET_FIELD);
    entProps.remove(WsCypherActivityStreamSPI.PROVIDER_FIELD);
    entProps.remove(WsCypherActivityStreamSPI.GENERATOR_FIELD);
    wsQuery.setParameter("props", entProps);
    toReturn.add("activity");

    buffer.append("RETURN ");
    final Iterator<String> rets = toReturn.iterator();
    while (rets.hasNext()) {
      buffer.append(rets.next());

      if (rets.hasNext()) {
        buffer.append(", ");
      }
    }
    buffer.append(";");

    // execute
    final IQueryCallback callback = this.fQueryHandler.sendQuery(wsQuery);

    try {
      result = (TableResult) callback.get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not retrieve results", e);
    }

    return result;
  }

  @Override
  public Future<ActivityEntry> createActivityEntry(UserId userId, GroupId groupId, String appId,
          Set<String> fields, ActivityEntry activity, SecurityToken token) throws ProtocolException {
    // generate unique ID
    final WebsockQuery wsQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    wsQuery.setPayload(Neo4jServiceQueries.GET_UID_QUERY);
    wsQuery.setParameter(Neo4jServiceQueries.TYPE, Constants.ACTIVITY_ENTRY_NODES);

    final IQueryCallback callback = this.fQueryHandler.sendQuery(wsQuery);

    try {
      final SingleResult idRes = (SingleResult) callback.get();
      activity.setId(idRes.getResults().get("id").toString());
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not retrieve unique ID", e);
    }

    // convert activity and subordinate objects
    // TODO: map implementation
    final Map<String, Object> entProps = new HashMap<String, Object>();
    new ActivityEntryDTO(entProps).setData(activity);

    ActivityEntry entry = null;
    final TableResult result = creationQuery(userId.getUserId(token), appId, entProps);
    try {
      final RestfulCollection<ActivityEntry> coll = convertTable(result).get();
      entry = coll.getList().get(0);
    } catch (final Exception e) {
      // not applicable
    }

    return Futures.immediateFuture(entry);
  }
}
