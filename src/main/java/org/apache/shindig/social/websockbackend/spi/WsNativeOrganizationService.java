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
package org.apache.shindig.social.websockbackend.spi;

import java.util.ArrayList;
import java.util.LinkedList;
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
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.websockbackend.model.dto.RelationshipDTO;

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
 * Implementation of the IOrganizationService interface retrieving person data from a remote Neo4j
 * graph database over a websocket.
 */
@Singleton
public class WsNativeOrganizationService implements IOrganizationService {
  public static final String MANAGER_OF_ID = "@manager_of";
  public static final String MANAGED_BY_ID = "@managed_by";

  private final IQueryHandler fQueryHandler;

  private final IExtPersonService fPeople;

  private final Logger fLogger;

  /**
   * Creates a graph organization service using the given query handler to dispatch queries to a
   * remote server and converts person objects using the given person service. Throws a
   * NullPointerException if the given query handler or person service are null.
   *
   * @param qHandler
   *          query handler to use
   * @param people
   *          person service to use
   */
  @Inject
  public WsNativeOrganizationService(IQueryHandler qHandler, IExtPersonService people) {
    if (qHandler == null) {
      throw new NullPointerException("query handler was null");
    }
    if (people == null) {
      throw new NullPointerException("person service was null");
    }

    this.fQueryHandler = qHandler;
    this.fPeople = people;

    this.fLogger = Logger.getLogger(this.getClass().getName());
  }

  @Override
  public Future<RestfulCollection<Object>> getHierarchyPath(UserId userId, String target,
          Set<String> fields, SecurityToken token) {
    final List<Object> pathList = new LinkedList<Object>();

    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.GET_HIERARCHY_PATH_QUERY);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));
    query.setParameter(ShindigNativeQueries.TARGET_USER_ID, target);

    if (fields != null) {
      final List<String> fieldList = new ArrayList<String>(fields);
      query.setParameter(ShindigNativeQueries.FIELD_LIST, fieldList);
    }

    // execute query
    final IQueryCallback result = this.fQueryHandler.sendQuery(query);

    // extract results
    ListResult resultList = null;
    try {
      resultList = (ListResult) result.get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not retrieve results", e);
    }

    // convert results
    @SuppressWarnings("unchecked")
    final List<Map<String, Object>> mapList = (List<Map<String, Object>>) resultList.getResults();

    String id = null;
    for (final Map<String, Object> entity : mapList) {
      // should be made up of person nodes and "manager" relations
      // -> convert to generic objects that will be serialized through reflection
      id = entity.get("id").toString();
      if (id.equals(WsNativeOrganizationService.MANAGED_BY_ID)
              || id.equals(WsNativeOrganizationService.MANAGER_OF_ID)) {
        // relation
        pathList.add(new RelationshipDTO(id));
      } else {
        // person
        pathList.add(this.fPeople.convertPerson(entity, fields, token));
      }
    }

    // wrap result
    final RestfulCollection<Object> resColl = new RestfulCollection<Object>(pathList);
    resColl.setItemsPerPage(resultList.getMax());
    resColl.setStartIndex(resultList.getFirst());
    resColl.setTotalResults(resultList.getTotal());
    return Futures.immediateFuture(resColl);
  }

}
