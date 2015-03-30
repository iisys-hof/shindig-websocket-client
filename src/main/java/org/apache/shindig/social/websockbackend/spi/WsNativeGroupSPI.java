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
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupService;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.websockbackend.model.dto.GroupDTO;
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
 * Implementation of the GroupService interface retrieving group data from a remote Neo4j graph
 * database over a websocket.
 */
@Singleton
public class WsNativeGroupSPI implements GroupService {
  private static final String TITLE_FIELD = Group.Field.TITLE.toString();

  private final IQueryHandler fQueryHandler;
  private final Logger fLogger;

  /**
   * Creates a websocket group service using the given query handler to dispatch queries to a remote
   * server. Throws a NullPointerException if the given query handler is null.
   *
   * @param qHandler
   *          query handler to use
   */
  @Inject
  public WsNativeGroupSPI(IQueryHandler qHandler) {
    if (qHandler == null) {
      throw new NullPointerException("query handler was null");
    }

    this.fQueryHandler = qHandler;
    this.fLogger = Logger.getLogger(this.getClass().getName());
  }

  @Override
  public Future<RestfulCollection<Group>> getGroups(UserId userId, CollectionOptions options,
          Set<String> fields, SecurityToken token) throws ProtocolException {
    final List<Group> groupList = new ArrayList<Group>();

    final String sortField = options.getSortBy();
    if (sortField == null) {
      options.setSortBy(WsNativeGroupSPI.TITLE_FIELD);
    }

    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.GET_GROUPS_QUERY);

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
    GroupDTO dto = null;

    if (mapList != null) {
      for (final Map<String, Object> groupMap : mapList) {
        dto = new GroupDTO(groupMap);
        groupList.add(dto);
      }
    }

    final RestfulCollection<Group> groups = new RestfulCollection<Group>(groupList);
    groups.setItemsPerPage(resultList.getMax());
    groups.setStartIndex(resultList.getFirst());
    groups.setTotalResults(resultList.getTotal());
    return Futures.immediateFuture(groups);
  }
}
