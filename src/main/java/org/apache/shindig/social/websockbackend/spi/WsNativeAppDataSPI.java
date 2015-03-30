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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.DataCollection;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.social.opensocial.spi.AppDataService;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.UserId;

import com.google.common.util.concurrent.Futures;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.hofuniversity.iisys.neo4j.websock.queries.IQueryCallback;
import de.hofuniversity.iisys.neo4j.websock.queries.IQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.query.EQueryType;
import de.hofuniversity.iisys.neo4j.websock.query.WebsockQuery;
import de.hofuniversity.iisys.neo4j.websock.result.SingleResult;
import de.hofuniversity.iisys.neo4j.websock.shindig.ShindigNativeQueries;

/**
 * Implementation of the ActivityService interface retrieving application data from a remote Neo4j
 * graph database over a websocket.
 */
@Singleton
public class WsNativeAppDataSPI implements AppDataService {
  private final IQueryHandler fQueryHandler;
  private final Logger fLogger;

  /**
   * Creates a graph application data service using the given query handler to dispatch queries to a
   * remote server. Throws a NullPointerException if a parameter is null.
   *
   * @param qHandler
   *          query handler to use
   */
  @Inject
  public WsNativeAppDataSPI(IQueryHandler qHandler) {
    if (qHandler == null) {
      throw new NullPointerException("query handler was null");
    }

    this.fQueryHandler = qHandler;
    this.fLogger = Logger.getLogger(this.getClass().getName());
  }

  @Override
  public Future<DataCollection> getPersonData(Set<UserId> userIds, GroupId groupId, String appId,
          Set<String> fields, SecurityToken token) throws ProtocolException {
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
    query.setPayload(ShindigNativeQueries.GET_APP_DATA_QUERY);

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

    // execute and convert result
    final IQueryCallback callback = this.fQueryHandler.sendQuery(query);
    SingleResult result = null;

    try {
      result = (SingleResult) callback.get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not retrieve result", e);
    }

    @SuppressWarnings("unchecked")
    final DataCollection dataColl = new DataCollection(
            (Map<String, Map<String, Object>>) result.getResults());

    return Futures.immediateFuture(dataColl);
  }

  @Override
  public Future<Void> deletePersonData(UserId userId, GroupId groupId, String appId,
          Set<String> fields, SecurityToken token) throws ProtocolException {
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
    query.setPayload(ShindigNativeQueries.DELETE_APP_DATA_QUERY);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));
    query.setParameter(ShindigNativeQueries.GROUP_ID, group);
    query.setParameter(ShindigNativeQueries.APP_ID, appId);

    if (fields != null) {
      final List<String> fieldList = new ArrayList<String>(fields);
      query.setParameter(ShindigNativeQueries.FIELD_LIST, fieldList);
    }

    // execute query
    try {
      this.fQueryHandler.sendQuery(query).get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not delete application data", e);
    }

    return Futures.immediateFuture(null);
  }

  @Override
  public Future<Void> updatePersonData(UserId userId, GroupId groupId, String appId,
          Set<String> fields, Map<String, Object> values, SecurityToken token)
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

    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.UPDATE_APP_DATA_QUERY);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));
    query.setParameter(ShindigNativeQueries.GROUP_ID, group);
    query.setParameter(ShindigNativeQueries.APP_ID, appId);

    query.setParameter(ShindigNativeQueries.APP_DATA, values);

    // execute query
    try {
      this.fQueryHandler.sendQuery(query).get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not delete application data", e);
    }

    return Futures.immediateFuture(null);
  }
}
