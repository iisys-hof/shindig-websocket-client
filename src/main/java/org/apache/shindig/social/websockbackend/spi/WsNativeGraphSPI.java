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
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.opensocial.model.Group;
import org.apache.shindig.social.opensocial.model.Name;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.websockbackend.model.dto.GroupDTO;
import org.apache.shindig.social.websockbackend.model.dto.PersonDTO;
import org.apache.shindig.social.websockbackend.util.CollOptsConverter;

import com.google.common.util.concurrent.Futures;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.hofuniversity.iisys.neo4j.websock.queries.IQueryCallback;
import de.hofuniversity.iisys.neo4j.websock.queries.IQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.query.EQueryType;
import de.hofuniversity.iisys.neo4j.websock.query.WebsockQuery;
import de.hofuniversity.iisys.neo4j.websock.result.ListResult;
import de.hofuniversity.iisys.neo4j.websock.shindig.ShindigNativeQueries;

/**
 * Neo4j-related Implementation of the graph service interface using the data from a remote Neo4j
 * graph database over a websocket.
 */
@Singleton
public class WsNativeGraphSPI implements IGraphService {
  private final IQueryHandler fQueryHandler;
  private final Logger fLogger;

  /**
   * Creates a GraphSPI using the given query handler to dispatch queries to a remote server. Throws
   * a NullPointerException if the given query handler is null.
   *
   * @param qHandler
   *          query handler to use
   */
  @Inject
  public WsNativeGraphSPI(IQueryHandler qHandler) {
    if (qHandler == null) {
      throw new NullPointerException("query handler was null");
    }

    this.fQueryHandler = qHandler;
    this.fLogger = Logger.getLogger(this.getClass().getName());
  }

  private Future<RestfulCollection<Person>> convertRequested(IQueryCallback result,
          final Set<String> fields, final SecurityToken token) throws ProtocolException {
    final List<Person> dtos = new ArrayList<Person>();

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

    // convert the items received
    if (mapList != null) {
      String id = null;
      PersonDTO tmpPerson = null;
      for (final Map<String, Object> personMap : mapList) {
        tmpPerson = new PersonDTO(personMap);
        id = tmpPerson.getId();

        // determine whether the person is viewer or owner
        if (token != null) {
          if (id.equals(token.getViewerId())) {
            tmpPerson.setIsViewer(true);
          }
          if (id.equals(token.getOwnerId())) {
            tmpPerson.setIsOwner(true);
          }
        }

        // TODO: generate profile URLs?

        dtos.add(tmpPerson);
      }
    }

    final RestfulCollection<Person> peopleColl = new RestfulCollection<Person>(dtos);
    peopleColl.setItemsPerPage(resultList.getMax());
    peopleColl.setStartIndex(resultList.getFirst());
    peopleColl.setTotalResults(resultList.getTotal());
    return Futures.immediateFuture(peopleColl);
  }

  @Override
  public Future<RestfulCollection<Person>> getFriendsOfFriends(Set<UserId> userIds, int depth,
          boolean unknown, CollectionOptions options, Set<String> fields, SecurityToken token) {
    // sort as defined by parameters
    final String sortField = options.getSortBy();
    if (sortField == null || sortField.equals(Person.Field.NAME.toString())) {
      options.setSortBy(Name.Field.FORMATTED.toString());
    }

    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.GET_FOFS_QUERY);

    // set options
    CollOptsConverter.convert(options, query);

    // set parameters for method
    final List<String> idList = new ArrayList<String>();
    for (final UserId userId : userIds) {
      idList.add(userId.getUserId(token));
    }
    query.setParameter(ShindigNativeQueries.USER_ID_LIST, idList);

    query.setParameter(ShindigNativeQueries.FOF_DEPTH, depth);
    query.setParameter(ShindigNativeQueries.FOF_UNKNOWN, unknown);

    if (fields != null) {
      final List<String> fieldList = new ArrayList<String>(fields);
      query.setParameter(ShindigNativeQueries.FIELD_LIST, fieldList);
    }

    final IQueryCallback result = this.fQueryHandler.sendQuery(query);

    // convert all requested
    return convertRequested(result, fields, token);
  }

  @Override
  public Future<RestfulCollection<Person>> getShortestPath(UserId userId, UserId targetId,
          CollectionOptions options, Set<String> fields, SecurityToken token) {
    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.GET_SHORTEST_PATH_QUERY);

    // set options
    CollOptsConverter.convert(options, query);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));
    query.setParameter(ShindigNativeQueries.TARGET_USER_ID, targetId.getUserId(token));

    if (fields != null) {
      final List<String> fieldList = new ArrayList<String>(fields);
      query.setParameter(ShindigNativeQueries.FIELD_LIST, fieldList);
    }

    final IQueryCallback result = this.fQueryHandler.sendQuery(query);

    // convert all requested
    return convertRequested(result, fields, token);
  }

  @Override
  public Future<RestfulCollection<Group>> getGroupRecommendation(UserId userId, int number,
          CollectionOptions options, Set<String> fields, SecurityToken token) {
    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.RECOMMEND_GROUP_QUERY);

    // set options
    CollOptsConverter.convert(options, query);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));
    query.setParameter(ShindigNativeQueries.MIN_FRIENDS_IN_GROUP, number);

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
    final List<Group> groupList = new ArrayList<Group>();
    if (mapList != null) {
      GroupDTO dto = null;
      for (final Map<String, Object> groupMap : mapList) {
        dto = new GroupDTO(groupMap);
        groupList.add(dto);
      }
    }

    final RestfulCollection<Group> groupColl = new RestfulCollection<Group>(groupList);
    groupColl.setItemsPerPage(resultList.getMax());
    groupColl.setStartIndex(resultList.getFirst());
    groupColl.setTotalResults(resultList.getTotal());
    return Futures.immediateFuture(groupColl);
  }

  @Override
  public Future<RestfulCollection<Person>> getFriendRecommendation(UserId userId, int number,
          CollectionOptions options, Set<String> fields, SecurityToken token) {
    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.RECOMMEND_FRIEND_QUERY);

    // set options
    CollOptsConverter.convert(options, query);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));
    query.setParameter(ShindigNativeQueries.MIN_COMMON_FRIENDS, number);

    if (fields != null) {
      final List<String> fieldList = new ArrayList<String>(fields);
      query.setParameter(ShindigNativeQueries.FIELD_LIST, fieldList);
    }

    final IQueryCallback result = this.fQueryHandler.sendQuery(query);

    return convertRequested(result, fields, token);
  }
}
