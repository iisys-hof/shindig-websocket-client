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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.apache.shindig.auth.AnonymousSecurityToken;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.core.model.NameImpl;
import org.apache.shindig.social.opensocial.model.Name;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.websockbackend.Constants;
import org.apache.shindig.social.websockbackend.WebsockConfig;
import org.apache.shindig.social.websockbackend.events.BasicEvent;
import org.apache.shindig.social.websockbackend.events.ShindigEventBus;
import org.apache.shindig.social.websockbackend.events.ShindigEventType;
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
import de.hofuniversity.iisys.neo4j.websock.result.SingleResult;
import de.hofuniversity.iisys.neo4j.websock.shindig.ShindigNativeQueries;

/**
 * Implementation of the PersonService interface retrieving person data from a remote Neo4j graph
 * database over a websocket.
 */
@Singleton
public class WsNativePersonSPI implements IExtPersonService {
  private static final String EVENTS_ENABLED = "shindig.events.enabled";
  private static final String PROFILE_URL_PROP = "people.profileurl";
  private static final String INFO_URL_PROP = "people.infourl";
  private static final String ID_VAR = "${ID}";

  private static final String ID_FIELD = Person.Field.ID.toString();
  private static final String NAME_FIELD = Person.Field.NAME.toString();
  private static final String FORMATTED_FIELD = Name.Field.FORMATTED.toString();
  private static final String PROFILE_URL_FIELD = Person.Field.PROFILE_URL.toString();
  private static final String INFO_URL_FIELD = "infoUrl";

  private static final String BY_SKILLS_FILTER = "@skills";

  private final IQueryHandler fQueryHandler;

  private final ShindigEventBus fEventBus;

  private final Logger fLogger;

  private final String fProfileUrl, fInfoUrl;

  private final boolean fFireEvents;

  /**
   * Creates a graph person service using the given query handler to dispatch queries to a remote
   * server and generates context-sensitive data according to the given configuration object. Throws
   * a NullPointerException if the given query handler or configuration are null.
   *
   * @param qHandler
   *          query handler to use
   * @param config
   *          configuration object to use
   * @param eventBus
   *          event bus to fire events to
   */
  @Inject
  public WsNativePersonSPI(IQueryHandler qHandler, WebsockConfig config, ShindigEventBus eventBus) {
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

    this.fProfileUrl = config.getProperty(WsNativePersonSPI.PROFILE_URL_PROP);
    this.fInfoUrl = config.getProperty(WsNativePersonSPI.INFO_URL_PROP);

    this.fFireEvents = Boolean.parseBoolean(config.getProperty(WsNativePersonSPI.EVENTS_ENABLED));
  }

  private PersonDTO convertPerson(Map<String, Object> person, Set<String> fields,
          SecurityToken token) {
    final String id = person.get(WsNativePersonSPI.ID_FIELD).toString();
    final PersonDTO dto = new PersonDTO(person);

    // generate profile URL if requested
    if ((fields == null || fields.isEmpty() || fields.contains(WsNativePersonSPI.PROFILE_URL_FIELD))
            && this.fProfileUrl != null) {
      dto.setProfileUrl(this.fProfileUrl.replace(WsNativePersonSPI.ID_VAR, id));
    }
    // generate URL to JSON person information if requested
    else if (fields != null && fields.contains(WsNativePersonSPI.INFO_URL_FIELD)
            && this.fInfoUrl != null) {
      dto.setProfileUrl(this.fInfoUrl.replace(WsNativePersonSPI.ID_VAR, id));
    }

    // determine whether the person is viewer or owner
    if (token != null) {
      if (id.equals(token.getViewerId())) {
        dto.setIsViewer(true);
      }
      if (id.equals(token.getOwnerId())) {
        dto.setIsOwner(true);
      }
    }

    return dto;
  }

  private Future<RestfulCollection<Person>> convertList(IQueryCallback result,
          final Set<String> fields, final SecurityToken token) throws ProtocolException {
    final List<Person> people = new ArrayList<Person>();

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

    PersonDTO tmpPerson = null;
    for (final Map<String, Object> persMap : mapList) {
      tmpPerson = convertPerson(persMap, fields, token);
      people.add(tmpPerson);
    }

    final RestfulCollection<Person> rColl = new RestfulCollection<Person>(people);
    rColl.setStartIndex(resultList.getFirst());
    rColl.setTotalResults(resultList.getTotal());
    rColl.setItemsPerPage(resultList.getMax());
    return Futures.immediateFuture(rColl);
  }

  @Override
  public Future<RestfulCollection<Person>> getPeople(Set<UserId> userIds, GroupId groupId,
          CollectionOptions options, Set<String> fields, SecurityToken token)
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

    final String sortField = options.getSortBy();
    if (sortField == null || sortField.equals(WsNativePersonSPI.NAME_FIELD)) {
      options.setSortBy(WsNativePersonSPI.FORMATTED_FIELD);
    }

    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.GET_PEOPLE_QUERY);

    // set options
    CollOptsConverter.convert(options, query);

    // set parameters for method
    final List<String> idList = new ArrayList<String>();
    for (final UserId userId : userIds) {
      idList.add(userId.getUserId(token));
    }
    query.setParameter(ShindigNativeQueries.USER_ID_LIST, idList);

    query.setParameter(ShindigNativeQueries.GROUP_ID, group);

    if (fields != null) {
      final List<String> fieldList = new ArrayList<String>(fields);
      query.setParameter(ShindigNativeQueries.FIELD_LIST, fieldList);
    }

    final IQueryCallback result = this.fQueryHandler.sendQuery(query);

    return convertList(result, fields, token);
  }

  @Override
  public Future<Person> getPerson(UserId id, Set<String> fields, SecurityToken token)
          throws ProtocolException {
    PersonDTO person = null;

    // check if it's the anonymous user
    if (id != null && AnonymousSecurityToken.ANONYMOUS_ID.equals(id.getUserId())) {
      person = new PersonDTO();
      person.setId(AnonymousSecurityToken.ANONYMOUS_ID);
      person.setName(new NameImpl(Constants.ANONYMOUS_NAME));
      person.setNickname(Constants.ANONYMOUS_NAME);

      return Futures.immediateFuture((Person) person);
    }

    // check database for user with this ID
    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.GET_PERSON_QUERY);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, id.getUserId(token));

    if (fields != null) {
      final List<String> fieldList = new ArrayList<String>(fields);
      query.setParameter(ShindigNativeQueries.FIELD_LIST, fieldList);
    }

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
    final Map<String, Object> personMap = (Map<String, Object>) sResult.getResults();

    person = convertPerson(personMap, fields, token);
    return Futures.immediateFuture((Person) person);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Future<Person> updatePerson(UserId id, Person person, SecurityToken token)
          throws ProtocolException {
    // TODO: authorization?
    // String viewer = token.getViewerId(); // viewer
    // String user = id.getUserId(token); // person to update
    //
    // //check permissions
    // if(!viewer.equals(user))
    // {
    // throw new ProtocolException(HttpServletResponse.SC_FORBIDDEN,
    // "User '" + viewer + "' does not have enough privileges " +
    // "to update person '" + user + "'");
    // }

    // set time stamp
    person.setUpdated(new Date(System.currentTimeMillis()));

    // convert to map
    Map<String, Object> personMap = new HashMap<String, Object>();
    final PersonDTO gPerson = new PersonDTO(personMap);
    gPerson.setData(person);
    gPerson.stripNullValues();

    // write changes
    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.UPDATE_PERSON_QUERY);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, id.getUserId(token));
    query.setParameter(ShindigNativeQueries.PERSON_OBJECT, personMap);

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

    personMap = (Map<String, Object>) sResult.getResults();

    final PersonDTO resultPerson = convertPerson(personMap, null, token);

    // fire event
    if (this.fFireEvents) {
      try {
        // fire event
        final BasicEvent event = new BasicEvent(ShindigEventType.PROFILE_UPDATED);
        event.setPayload(resultPerson);
        event.setToken(token);
        this.fEventBus.fireEvent(event);
      } catch (final Exception e) {
        this.fLogger.log(Level.WARNING, "failed to send event", e);
      }
    }

    return Futures.immediateFuture((Person) resultPerson);
  }

  // additional methods

  @Override
  public Future<RestfulCollection<Person>> getAllPeople(CollectionOptions options,
          Set<String> fields, SecurityToken token) throws ProtocolException {
    // TODO: visibility?

    final String sortField = options.getSortBy();
    if (sortField == null || sortField.equals(WsNativePersonSPI.NAME_FIELD)) {
      options.setSortBy(WsNativePersonSPI.FORMATTED_FIELD);
    }

    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);

    // special case: get people by skill
    if (WsNativePersonSPI.BY_SKILLS_FILTER.equals(options.getFilter())) {
      query.setPayload(ShindigNativeQueries.GET_PEOPLE_BY_SKILL_QUERY);
      query.setParameter(ShindigNativeQueries.SKILL, options.getFilterValue());
    } else {
      query.setPayload(ShindigNativeQueries.GET_ALL_PEOPLE_QUERY);
    }

    // set options
    CollOptsConverter.convert(options, query);

    // set parameters for method
    if (fields != null) {
      final List<String> fieldList = new ArrayList<String>(fields);
      query.setParameter(ShindigNativeQueries.FIELD_LIST, fieldList);
    }

    final IQueryCallback result = this.fQueryHandler.sendQuery(query);

    return convertList(result, fields, token);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Future<Person> createPerson(Person person, SecurityToken token) throws ProtocolException {
    // set time stamp
    person.setUpdated(new Date(System.currentTimeMillis()));

    // convert to map
    Map<String, Object> personMap = new HashMap<String, Object>();
    final PersonDTO gPerson = new PersonDTO(personMap);
    gPerson.setData(person);
    gPerson.stripNullValues();

    // write changes
    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.CREATE_PERSON_QUERY);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.PERSON_OBJECT, personMap);

    final IQueryCallback result = this.fQueryHandler.sendQuery(query);

    SingleResult sResult = null;
    try {
      sResult = (SingleResult) result.get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not create person", e);
    }

    personMap = (Map<String, Object>) sResult.getResults();

    final PersonDTO resultPerson = convertPerson(personMap, null, token);

    // fire event
    if (this.fFireEvents) {
      try {
        final BasicEvent event = new BasicEvent(ShindigEventType.PROFILE_CREATED);
        event.setPayload(resultPerson);
        event.setToken(token);
        this.fEventBus.fireEvent(event);
      } catch (final Exception e) {
        this.fLogger.log(Level.WARNING, "failed to send event", e);
      }
    }

    return Futures.immediateFuture((Person) resultPerson);
  }

  @Override
  public Future<Void> deletePerson(UserId id, SecurityToken token) throws ProtocolException {
    // TODO: check authorization
    // String viewer = token.getViewerId();
    // if()
    // {
    // throw new ProtocolException(HttpServletResponse.SC_FORBIDDEN,
    // "User '" + viewer + "' does not have enough privileges "
    // + "to delete this user");
    // }

    // get person for event before it is deleted
    Future<Person> oldPerson = null;
    if (this.fFireEvents) {
      try {
        oldPerson = this.getPerson(id, null, token);
      } catch (final Exception e) {
        // nop
      }
    }

    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.DELETE_PERSON_QUERY);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, id.getUserId(token));

    final IQueryCallback result = this.fQueryHandler.sendQuery(query);

    try {
      result.get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not delete person", e);
    }

    // fire event
    if (this.fFireEvents && oldPerson != null) {
      try {
        final BasicEvent event = new BasicEvent(ShindigEventType.PROFILE_DELETED);
        event.setPayload(oldPerson.get());
        event.setToken(token);
        this.fEventBus.fireEvent(event);
      } catch (final Exception e) {
        this.fLogger.log(Level.WARNING, "failed to send event", e);
      }
    }

    return Futures.immediateFuture(null);
  }
}
