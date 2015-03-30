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
import org.apache.shindig.social.opensocial.model.Name;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.websockbackend.model.dto.PersonDTO;
import org.apache.shindig.social.websockbackend.util.CollOptsConverter;

import com.google.common.util.concurrent.Futures;
import com.google.inject.Inject;

import de.hofuniversity.iisys.neo4j.websock.queries.IQueryCallback;
import de.hofuniversity.iisys.neo4j.websock.queries.IQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.query.EQueryType;
import de.hofuniversity.iisys.neo4j.websock.query.WebsockQuery;
import de.hofuniversity.iisys.neo4j.websock.result.ListResult;
import de.hofuniversity.iisys.neo4j.websock.shindig.ShindigNativeQueries;

/**
 * Neo4j over websocket implementation for the friendship service.
 */
public class WsNativeFriendSPI implements IFriendService {
  private static final String NAME_FIELD = Person.Field.NAME.toString();
  private static final String FORMATTED_FIELD = Name.Field.FORMATTED.toString();

  private final IQueryHandler fQueryHandler;
  private final Logger fLogger;

  /**
   * Creates a friendship service using the given query handler to dispatch queries to a remote
   * server.
   *
   * @param qHandler
   *          query handler to use
   */
  @Inject
  public WsNativeFriendSPI(IQueryHandler qHandler) {
    if (qHandler == null) {
      throw new NullPointerException("query handler was null");
    }

    this.fQueryHandler = qHandler;
    this.fLogger = Logger.getLogger(this.getClass().getName());
  }

  @Override
  public Future<RestfulCollection<Person>> getRequests(UserId userId,
          CollectionOptions collectionOptions, Set<String> fields, final SecurityToken token) {
    final List<Person> personList = new ArrayList<Person>();

    final String sortField = collectionOptions.getSortBy();
    if (sortField == null || sortField.equals(WsNativeFriendSPI.NAME_FIELD)) {
      collectionOptions.setSortBy(WsNativeFriendSPI.FORMATTED_FIELD);
    }

    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.GET_FRIEND_REQUESTS_QUERY);

    // set collection options
    CollOptsConverter.convert(collectionOptions, query);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));

    if (fields != null) {
      final List<String> fieldList = new ArrayList<String>(fields);
      query.setParameter(ShindigNativeQueries.FIELD_LIST, fieldList);
    }

    final IQueryCallback callback = this.fQueryHandler.sendQuery(query);
    ListResult result = null;

    try {
      result = (ListResult) callback.get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not retrieve results", e);
    }

    @SuppressWarnings("unchecked")
    final List<Map<String, Object>> mapList = (List<Map<String, Object>>) result.getResults();

    if (mapList != null) {
      String id = null;
      PersonDTO tmpPerson = null;
      for (final Map<String, Object> persMap : mapList) {
        tmpPerson = new PersonDTO(persMap);
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

        personList.add(tmpPerson);
      }
    }

    // return search query information
    final RestfulCollection<Person> people = new RestfulCollection<Person>(personList);
    people.setItemsPerPage(result.getMax());
    people.setStartIndex(result.getFirst());
    people.setTotalResults(result.getTotal());

    return Futures.immediateFuture(people);
  }

  @Override
  public Future<Void> requestFriendship(UserId userId, Person target, SecurityToken token) {
    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.REQUEST_FRIENDSHIP_QUERY);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));
    query.setParameter(ShindigNativeQueries.TARGET_USER_ID, target.getId());

    try {
      this.fQueryHandler.sendQuery(query).get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not request or confirm friendship", e);
    }

    return Futures.immediateFuture(null);
  }

  @Override
  public Future<Void> denyFriendship(UserId userId, Person target, SecurityToken token) {
    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.DENY_FRIENDSHIP_QUERY);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));
    query.setParameter(ShindigNativeQueries.TARGET_USER_ID, target.getId());

    try {
      this.fQueryHandler.sendQuery(query).get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not deny or revoke friendship", e);
    }

    return Futures.immediateFuture(null);
  }
}
