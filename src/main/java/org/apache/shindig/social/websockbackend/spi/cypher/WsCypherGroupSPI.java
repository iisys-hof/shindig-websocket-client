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
import org.apache.shindig.social.websockbackend.Constants;
import org.apache.shindig.social.websockbackend.model.dto.GroupDTO;
import org.apache.shindig.social.websockbackend.util.CollOptsConverter;

import com.google.common.util.concurrent.Futures;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.hofuniversity.iisys.neo4j.websock.queries.IQueryCallback;
import de.hofuniversity.iisys.neo4j.websock.queries.IQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.query.EQueryType;
import de.hofuniversity.iisys.neo4j.websock.query.WebsockQuery;
import de.hofuniversity.iisys.neo4j.websock.result.TableResult;
import de.hofuniversity.iisys.neo4j.websock.session.WebsockConstants;

/**
 * Implementation of the GroupService interface retrieving group data from a remote Neo4j graph
 * database over a websocket using Cypher.
 */
@Singleton
public class WsCypherGroupSPI implements GroupService {
  private static final String GROUPS_QUERY_NAME = "cGroups";
  private static final String GROUPS_QUERY = "START person=node:" + Constants.PERSON_NODES
          + "(id = {id})\n" + "MATCH person-[:MEMBER_OF]->group\n" + "RETURN group";

  private final IQueryHandler fQueryHandler;

  private final Logger fLogger;

  /**
   * Creates a new Cypher group service using the given query handler to retrieve data from a Neo4j
   * instance over a websocket using Cypher. The given query handler must not be null.
   *
   * @param qHandler
   *          query handler to use
   */
  @Inject
  public WsCypherGroupSPI(IQueryHandler qHandler) {
    if (qHandler == null) {
      throw new NullPointerException("Query handler was null");
    }

    this.fQueryHandler = qHandler;
    this.fLogger = Logger.getLogger(this.getClass().getName());

    // initialize stored procedures
    // delete query
    final WebsockQuery wsQuery = new WebsockQuery(EQueryType.STORE_PROCEDURE);
    wsQuery.setPayload(WsCypherGroupSPI.GROUPS_QUERY);
    wsQuery.setParameter(WebsockConstants.PROCEDURE_NAME, WsCypherGroupSPI.GROUPS_QUERY_NAME);

    try {
      this.fQueryHandler.sendMessage(wsQuery).get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new RuntimeException("could not store procedure \""
              + wsQuery.getParameter(WebsockConstants.PROCEDURE_NAME), e);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public Future<RestfulCollection<Group>> getGroups(UserId userId, CollectionOptions options,
          Set<String> fields, SecurityToken token) throws ProtocolException {
    final List<Group> groupList = new ArrayList<Group>();
    final WebsockQuery wsQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);

    if (options != null) {
      CollOptsConverter.convert(options, wsQuery);
    }

    // set parameters
    wsQuery.setPayload(WsCypherGroupSPI.GROUPS_QUERY_NAME);
    wsQuery.setParameter("id", userId.getUserId(token));

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

    // wrap results
    Map<String, Object> gMap = null;
    for (final List<Object> gList : result.getResults()) {
      gMap = (Map<String, Object>) gList.get(0);
      groupList.add(new GroupDTO(gMap));
    }

    final RestfulCollection<Group> rColl = new RestfulCollection<Group>(groupList);
    rColl.setStartIndex(result.getFirst());
    rColl.setTotalResults(result.getTotal());
    rColl.setItemsPerPage(result.getMax());
    return Futures.immediateFuture(rColl);
  }
}
