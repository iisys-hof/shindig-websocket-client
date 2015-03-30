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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.util.DateUtil;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.spi.ActivityStreamService;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.websockbackend.model.dto.ActivityEntryDTO;
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
 * Implementation of the ActivityStreamService interface retrieving activity stream data from a
 * remote Neo4j graph database over a websocket.
 */
@Singleton
public class WsNativeActivityStreamSPI implements ActivityStreamService {
  private static final String PUBLISHED_FIELD = ActivityEntry.Field.PUBLISHED.toString();

  private final IQueryHandler fQueryHandler;

  private final Logger fLogger;

  /**
   * Creates a websocket activity stream service using the given query handler to dispatch queries
   * to a remote server. Throws a NullPointerException if the given query handler is null.
   *
   * @param qHandler
   *          query handler to use
   */
  @Inject
  public WsNativeActivityStreamSPI(IQueryHandler qHandler) {
    if (qHandler == null) {
      throw new NullPointerException("Query handler was null");
    }

    this.fQueryHandler = qHandler;
    this.fLogger = Logger.getLogger(this.getClass().getName());
  }

  private Future<RestfulCollection<ActivityEntry>> convertList(IQueryCallback result,
          final Set<String> fields) throws ProtocolException {
    final List<ActivityEntry> entries = new ArrayList<ActivityEntry>();

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

    for (final Map<String, Object> actMap : mapList) {
      entries.add(new ActivityEntryDTO(actMap));
    }

    final RestfulCollection<ActivityEntry> rColl = new RestfulCollection<ActivityEntry>(entries);
    rColl.setStartIndex(resultList.getFirst());
    rColl.setTotalResults(resultList.getTotal());
    rColl.setItemsPerPage(resultList.getMax());
    return Futures.immediateFuture((rColl));
  }

  private Future<ActivityEntry> convertSingle(IQueryCallback result, final Set<String> fields)
          throws ProtocolException {
    SingleResult sResult = null;

    try {
      sResult = (SingleResult) result.get();

      // TODO: proper not found exception
      if (sResult == null) {
        return null;
      }
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not retrieve result", e);
    }

    @SuppressWarnings("unchecked")
    final Map<String, Object> map = (Map<String, Object>) sResult.getResults();
    final ActivityEntry entry = new ActivityEntryDTO(map);

    return Futures.immediateFuture((entry));
  }

  @Override
  public Future<RestfulCollection<ActivityEntry>> getActivityEntries(Set<UserId> userIds,
          GroupId groupId, String appId, Set<String> fields, CollectionOptions options,
          SecurityToken token) throws ProtocolException {
    String group = null;

    if (groupId != null) {
      // actual group
      if (groupId.getType() == GroupId.Type.objectId) {
        group = groupId.getObjectId().toString();
      }
      // group type
      else {
        group = '@' + groupId.getType().toString();
      }
    }

    final String sortField = options.getSortBy();
    if (sortField == null) {
      options.setSortBy(WsNativeActivityStreamSPI.PUBLISHED_FIELD);
    }

    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.GET_ACT_ENTRIES_QUERY);

    // set options
    CollOptsConverter.convert(options, query);

    // set parameters for method
    final List<String> idList = new ArrayList<String>();
    for (final UserId userId : userIds) {
      idList.add(userId.getUserId(token));
    }
    query.setParameter(ShindigNativeQueries.USER_ID_LIST, idList);

    query.setParameter(ShindigNativeQueries.GROUP_ID, group);
    query.setParameter(ShindigNativeQueries.APP_ID, appId);

    if (fields != null) {
      final List<String> fieldList = new ArrayList<String>(fields);
      query.setParameter(ShindigNativeQueries.FIELD_LIST, fieldList);
    }

    final IQueryCallback result = this.fQueryHandler.sendQuery(query);
    return convertList(result, fields);
  }

  @Override
  public Future<RestfulCollection<ActivityEntry>> getActivityEntries(UserId userId,
          GroupId groupId, String appId, Set<String> fields, CollectionOptions options,
          Set<String> activityIds, SecurityToken token) throws ProtocolException {
    String group = null;

    if (groupId != null) {
      // actual group
      if (groupId.getType() == GroupId.Type.objectId) {
        group = groupId.getObjectId().toString();
      }
      // group type
      else {
        group = '@' + groupId.getType().toString();
      }
    }

    final String sortField = options.getSortBy();
    if (sortField == null) {
      options.setSortBy(WsNativeActivityStreamSPI.PUBLISHED_FIELD);
    }

    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.GET_ACT_ENTRIES_BY_ID_QUERY);

    // set options
    CollOptsConverter.convert(options, query);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));
    query.setParameter(ShindigNativeQueries.GROUP_ID, group);
    query.setParameter(ShindigNativeQueries.APP_ID, appId);

    if (fields != null) {
      final List<String> fieldList = new ArrayList<String>(fields);
      query.setParameter(ShindigNativeQueries.FIELD_LIST, fieldList);
    }

    if (activityIds != null) {
      final List<String> idList = new ArrayList<String>(activityIds);
      query.setParameter(ShindigNativeQueries.ACTIVITY_IDS, idList);
    }

    final IQueryCallback result = this.fQueryHandler.sendQuery(query);
    return convertList(result, fields);
  }

  @Override
  public Future<ActivityEntry> getActivityEntry(UserId userId, GroupId groupId, String appId,
          Set<String> fields, String activityId, SecurityToken token) throws ProtocolException {
    String group = null;

    if (groupId != null) {
      // actual group
      if (groupId.getType() == GroupId.Type.objectId) {
        group = groupId.getObjectId().toString();
      }
      // group type
      else {
        group = '@' + groupId.getType().toString();
      }
    }

    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.GET_ACT_ENTRY_QUERY);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));
    query.setParameter(ShindigNativeQueries.GROUP_ID, group);
    query.setParameter(ShindigNativeQueries.APP_ID, appId);

    if (fields != null) {
      final List<String> fieldList = new ArrayList<String>(fields);
      query.setParameter(ShindigNativeQueries.FIELD_LIST, fieldList);
    }

    query.setParameter(ShindigNativeQueries.ACTIVITY_ID, activityId);

    final IQueryCallback result = this.fQueryHandler.sendQuery(query);
    return convertSingle(result, fields);
  }

  @Override
  public Future<Void> deleteActivityEntries(UserId userId, GroupId groupId, String appId,
          Set<String> activityIds, SecurityToken token) throws ProtocolException {
    String group = null;

    if (groupId != null) {
      // actual group
      if (groupId.getType() == GroupId.Type.objectId) {
        group = groupId.getObjectId().toString();
      }
      // group type
      else {
        group = '@' + groupId.getType().toString();
      }
    }

    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.DELETE_ACT_ENTRIES_QUERY);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));
    query.setParameter(ShindigNativeQueries.GROUP_ID, group);
    query.setParameter(ShindigNativeQueries.APP_ID, appId);

    final List<String> idList = new ArrayList<String>(activityIds);
    query.setParameter(ShindigNativeQueries.ACTIVITY_IDS, idList);

    try {
      this.fQueryHandler.sendQuery(query).get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not delete activity entries", e);
    }

    return Futures.immediateFuture(null);
  }

  @Override
  public Future<ActivityEntry> updateActivityEntry(UserId userId, GroupId groupId, String appId,
          Set<String> fields, ActivityEntry activity, String activityId, SecurityToken token)
          throws ProtocolException {

    String group = null;

    if (groupId != null) {
      // actual group
      if (groupId.getType() == GroupId.Type.objectId) {
        group = groupId.getObjectId().toString();
      }
      // group type
      else {
        group = '@' + groupId.getType().toString();
      }
    }

    // set time stamp
    if (activity.getPublished() == null || activity.getPublished().isEmpty()) {
      activity.setUpdated(DateUtil.formatIso8601Date(System.currentTimeMillis()));
    }

    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.UPDATE_ACT_ENTRY_QUERY);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));
    query.setParameter(ShindigNativeQueries.GROUP_ID, group);
    query.setParameter(ShindigNativeQueries.APP_ID, appId);

    if (fields != null) {
      final List<String> fieldList = new ArrayList<String>(fields);
      query.setParameter(ShindigNativeQueries.FIELD_LIST, fieldList);
    }

    final Map<String, Object> actMap = new HashMap<String, Object>();
    final ActivityEntryDTO gAct = new ActivityEntryDTO(actMap);
    gAct.setData(activity);
    gAct.stripNullValues();
    query.setParameter(ShindigNativeQueries.ACTIVITY_ENTRY_OBJECT, actMap);

    query.setParameter(ShindigNativeQueries.ACTIVITY_ID, activityId);

    // execute query
    final IQueryCallback result = this.fQueryHandler.sendQuery(query);
    return convertSingle(result, fields);
  }

  @Override
  public Future<ActivityEntry> createActivityEntry(UserId userId, GroupId groupId, String appId,
          Set<String> fields, ActivityEntry activity, SecurityToken token) throws ProtocolException {
    String group = null;

    if (groupId != null) {
      // actual group
      if (groupId.getType() == GroupId.Type.objectId) {
        group = groupId.getObjectId().toString();
      }
      // group type
      else {
        group = '@' + groupId.getType().toString();
      }
    }

    // set time stamp
    if (activity.getPublished() == null || activity.getPublished().isEmpty()) {
      activity.setPublished(DateUtil.formatIso8601Date(System.currentTimeMillis()));
    }

    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.CREATE_ACT_ENTRY_QUERY);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));
    query.setParameter(ShindigNativeQueries.GROUP_ID, group);
    query.setParameter(ShindigNativeQueries.APP_ID, appId);

    final Map<String, Object> actMap = new HashMap<String, Object>();
    final ActivityEntryDTO gAct = new ActivityEntryDTO(actMap);
    gAct.setData(activity);
    gAct.stripNullValues();
    query.setParameter(ShindigNativeQueries.ACTIVITY_ENTRY_OBJECT, actMap);

    if (fields != null) {
      final List<String> fieldList = new ArrayList<String>(fields);
      query.setParameter(ShindigNativeQueries.FIELD_LIST, fieldList);
    }

    final IQueryCallback result = this.fQueryHandler.sendQuery(query);
    return convertSingle(result, fields);
  }
}
