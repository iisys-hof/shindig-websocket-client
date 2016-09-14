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
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.websockbackend.WebsockConfig;
import org.apache.shindig.social.websockbackend.events.BasicEvent;
import org.apache.shindig.social.websockbackend.events.ShindigEventBus;
import org.apache.shindig.social.websockbackend.events.ShindigEventType;
import org.apache.shindig.social.websockbackend.model.ISkillSet;
import org.apache.shindig.social.websockbackend.model.dto.SkillSetDTO;
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
 * Implementation of the skill service retrieving skill data from a remote Neo4j graph database over
 * a websocket.
 */
public class WsNativeSkillSPI implements ISkillService {
  private static final String EVENTS_ENABLED = "shindig.events.enabled";

  private static final String NAME_FIELD = "name";

  private final IQueryHandler fQueryHandler;
  
  private final ShindigEventBus fEventBus;
  
  private final Logger fLogger;

  private final boolean fFireEvents;

  /**
   * Creates a graph skill service using the given query handler to dispatch queries to a remote
   * server. Throws a NullPointerException if the given query handler ise null.
   *
   * @param qHandler
   *          query handler to use
   */
  @Inject
  public WsNativeSkillSPI(IQueryHandler qHandler, WebsockConfig config,
      ShindigEventBus eventBus) {
    if (qHandler == null) {
      throw new NullPointerException("query handler was null");
    }
    if (config == null) {
      throw new NullPointerException("configuration object was null");
    }
    if (eventBus == null) {
      throw new NullPointerException("event bus was null");
    }

    this.fQueryHandler = qHandler;
    this.fEventBus = eventBus;
    this.fLogger = Logger.getLogger(this.getClass().getName());

    this.fFireEvents = Boolean.parseBoolean(config.getProperty(WsNativeSkillSPI.EVENTS_ENABLED));
  }

  @Override
  public Future<RestfulCollection<String>> getSkillAutocomp(String fragment,
          CollectionOptions options, SecurityToken token) throws ProtocolException {
    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.GET_SKILL_AUTOCOMPLETION_QUERY);

    // set options
    CollOptsConverter.convert(options, query);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.AUTOCOMPLETE_FRAGMENT, fragment);

    // execute query
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
    final List<String> skills = (List<String>) resultList.getResults();

    // wrap result
    final RestfulCollection<String> skillColl = new RestfulCollection<String>(skills);
    skillColl.setItemsPerPage(resultList.getMax());
    skillColl.setStartIndex(resultList.getFirst());
    skillColl.setTotalResults(resultList.getTotal());
    return Futures.immediateFuture(skillColl);
  }

  @Override
  public Future<RestfulCollection<ISkillSet>> getSkills(UserId userId, CollectionOptions options,
          SecurityToken token) throws ProtocolException {

    final List<ISkillSet> skills = new ArrayList<ISkillSet>();

    final String sortField = options.getSortBy();
    if (sortField == null) {
      options.setSortBy(WsNativeSkillSPI.NAME_FIELD);
    }

    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.GET_SKILLS_QUERY);

    // set options
    CollOptsConverter.convert(options, query);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));

    // execute query
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
      SkillSetDTO dto = null;
      for (final Map<String, Object> sMap : mapList) {
        dto = new SkillSetDTO(sMap);
        skills.add(dto);
      }
    }

    // wrap result
    final RestfulCollection<ISkillSet> skillColl = new RestfulCollection<ISkillSet>(skills);
    skillColl.setItemsPerPage(resultList.getMax());
    skillColl.setStartIndex(resultList.getFirst());
    skillColl.setTotalResults(resultList.getTotal());
    return Futures.immediateFuture(skillColl);
  }

  @Override
  public Future<Void> addSkill(UserId userId, String skill, SecurityToken token)
          throws ProtocolException {

    // check linking person parameter
    if (token == null || token.getViewerId() == null || token.getViewerId().isEmpty()) {
      throw new ProtocolException(HttpServletResponse.SC_BAD_REQUEST,
              "viewer ID from security token is required");
    }

    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.ADD_SKILL_QUERY);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));
    query.setParameter(ShindigNativeQueries.SKILL_LINKER, token.getViewerId());
    query.setParameter(ShindigNativeQueries.SKILL, skill);

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
    
    // fire event
    if (this.fFireEvents) {
      try {
        // fire event
        final BasicEvent event = new BasicEvent(ShindigEventType.SKILL_ADDED);
        
        String[] params = {userId.getUserId(token), skill};
        event.setPayload(params);

        event.setToken(token);
        this.fEventBus.fireEvent(event);
      } catch (final Exception e) {
        this.fLogger.log(Level.WARNING, "failed to send event", e);
      }
    }

    return Futures.immediateFuture(null);
  }

  @Override
  public Future<Void> removeSkill(UserId userId, String skill, SecurityToken token)
          throws ProtocolException {

    // check linking person parameter
    if (token == null || token.getViewerId() == null || token.getViewerId().isEmpty()) {
      throw new ProtocolException(HttpServletResponse.SC_BAD_REQUEST,
              "viewer ID from security token is required");
    }

    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.REMOVE_SKILL_QUERY);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));
    query.setParameter(ShindigNativeQueries.SKILL_LINKER, token.getViewerId());
    query.setParameter(ShindigNativeQueries.SKILL, skill);

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

    // fire event
    if (this.fFireEvents) {
      try {
        // fire event
        final BasicEvent event = new BasicEvent(ShindigEventType.SKILL_REMOVED);
        
        String[] params = {userId.getUserId(token), skill};
        event.setPayload(params);

        event.setToken(token);
        this.fEventBus.fireEvent(event);
      } catch (final Exception e) {
        this.fLogger.log(Level.WARNING, "failed to send event", e);
      }
    }

    return Futures.immediateFuture(null);
  }
}
