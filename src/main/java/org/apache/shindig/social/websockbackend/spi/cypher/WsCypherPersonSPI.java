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
import org.apache.shindig.social.opensocial.model.Organization;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.GroupId.Type;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.websockbackend.Constants;
import org.apache.shindig.social.websockbackend.model.IExtOrganization;
import org.apache.shindig.social.websockbackend.model.dto.PersonDTO;
import org.apache.shindig.social.websockbackend.spi.IExtPersonService;
import org.apache.shindig.social.websockbackend.util.CollOptsConverter;

import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.Futures;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.hofuniversity.iisys.neo4j.websock.queries.IQueryCallback;
import de.hofuniversity.iisys.neo4j.websock.queries.IQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.query.EQueryType;
import de.hofuniversity.iisys.neo4j.websock.query.WebsockQuery;
import de.hofuniversity.iisys.neo4j.websock.result.TableResult;
import de.hofuniversity.iisys.neo4j.websock.service.Neo4jServiceQueries;

/**
 * Implementation of the IExtPersonService interface retrieving person data from a remote Neo4j
 * graph database over a websocket using Cypher.
 */
@Singleton
public class WsCypherPersonSPI implements IExtPersonService {
  private static final String END_DATE_FIELD = Organization.Field.END_DATE.toString();
  private static final String SALARY_FIELD = Organization.Field.SALARY.toString();
  private static final String START_DATE_FIELD = Organization.Field.START_DATE.toString();
  private static final String TITLE_FIELD = Organization.Field.TITLE.toString();
  private static final String PRIMARY_FIELD = Organization.Field.PRIMARY.toString();

  private static final String ADDRESSES_FIELD = Person.Field.ADDRESSES.toString();
  private static final String ORGS_FIELD = Person.Field.ORGANIZATIONS.toString();

  private static final String ORG_ADD_FIELD = Organization.Field.ADDRESS.toString();

  private static final String EMAILS_FIELD = Person.Field.EMAILS.toString();
  private static final String PHONES_FIELD = Person.Field.PHONE_NUMBERS.toString();

  private final IQueryHandler fQueryHandler;

  private final Logger fLogger;

  /**
   * Creates a new Cypher person service using the given query handler to retrieve data from a Neo4j
   * instance over a websocket using Cypher. The given query handler must not be null.
   *
   * @param qHandler
   *          query handler to use
   */
  @Inject
  public WsCypherPersonSPI(IQueryHandler qHandler) {
    if (qHandler == null) {
      throw new NullPointerException("Query handler was null");
    }

    this.fQueryHandler = qHandler;
    this.fLogger = Logger.getLogger(this.getClass().getName());

    // initialize stored procedures
    // none yet
  }

  private TableResult query(Set<UserId> idSet, GroupId gid, final Set<String> fields,
          CollectionOptions opts, final SecurityToken token) {
    final WebsockQuery wsQuery = new WebsockQuery(EQueryType.DIRECT_CYPHER);
    final Set<String> toReturn = new HashSet<String>();
    final StringBuffer query = new StringBuffer();

    if (opts != null) {
      CollOptsConverter.convert(opts, wsQuery);
    }

    if (gid == null) {
      gid = new GroupId(Type.self, "@self");
    }

    // build request start string
    if (gid.getType() == Type.objectId) {
      query.append("START group=node:" + Constants.GROUP_NODES + "(id = {id})\n");
      wsQuery.setParameter("id", gid.getObjectId().toString());
    } else if (idSet != null && !idSet.isEmpty()) {
      query.append("START person=node:" + Constants.PERSON_NODES + "({idLookup})\n");

      String idLookup = "id:(";
      for (final UserId userId : idSet) {
        idLookup += userId.getUserId(token) + " ";
      }
      idLookup += ")";
      wsQuery.setParameter("idLookup", idLookup);
    } else {
      query.append("START person=node:" + Constants.PERSON_NODES + "('id:(*)')\n");
    }

    // determine the entity to retrieve data for
    boolean match = false;
    String entity = null;
    switch (gid.getType()) {
    case self:
      entity = "person";
      break;

    case friends:
      query.append("MATCH person-[:FRIEND_OF]->friend");
      match = true;
      entity = "friend";
      break;

    case objectId:
      query.append("MATCH group<-[:MEMBER_OF]-person");
      match = true;
      entity = "person";
      break;
    }

    toReturn.add(entity + " as person");

    // add clauses for external data in other nodes
    handleRelations(query, match, entity, fields, toReturn);

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

  private void handleRelations(final StringBuffer query, boolean match, final String entity,
          final Set<String> fields, final Set<String> toReturn) {
    // TODO: IMs, current location, ?

    if (fields == null || fields.isEmpty()) {
      match = handleMatch(match, query);

      query.append(entity);
      query.append("-[aff?:AFFILIATED]->org-[?:LOCATED_AT]->org_add");
      // query.append(entity);
      // query.append("-[?:LOCATED_AT]->add, ");
      // query.append(entity);
      // query.append("-[?:EMAILS]->emails, ");
      // query.append(entity);
      // query.append("-[?:PHONE_NUMS]->phones");
      //
      toReturn.add("collect(aff) as affs");
      toReturn.add("collect(org) as orgs");
      toReturn.add("collect(org_add) as org_adds");
      // toReturn.add("collect(add) as adds");
      // toReturn.add("emails");
      // toReturn.add("phones");

      toReturn.add("collect(distinct extract(p in " + entity
              + "-[:LOCATED_AT]->() : last(p))) as adds");
      toReturn.add("extract(p in " + entity + "-[:EMAILS]->() : last(p)) as emails");
      toReturn.add("extract(p in " + entity + "-[:PHONE_NUMS]->() : last(p)) as phones");
    } else {
      if (fields.contains(WsCypherPersonSPI.ORGS_FIELD)) {
        match = handleMatch(match, query);

        query.append(entity);
        query.append("-[aff?:AFFILIATED]->org-[?:LOCATED_AT]->org_add");
        toReturn.add("collect(aff) as affs");
        toReturn.add("collect(org) as orgs");
        toReturn.add("collect(org_add) as org_adds");
      }

      if (fields.contains(WsCypherPersonSPI.ADDRESSES_FIELD)) {
        // match = handleMatch(match, query);
        //
        // query.append(entity);
        // query.append("-[?:LOCATED_AT]->add");
        // toReturn.add("collect(add) as adds");

        toReturn.add("collect(extract(p in " + entity + "-[:LOCATED_AT]->() : last(p))) as adds");
      }

      if (fields.contains(WsCypherPersonSPI.EMAILS_FIELD)) {
        // match = handleMatch(match, query);
        //
        // query.append(entity);
        // query.append("-[?:EMAILS]->emails");
        // toReturn.add("emails");

        toReturn.add("extract(p in " + entity + "-[:EMAILS]->() : last(p)) as emails");
      }

      if (fields.contains(WsCypherPersonSPI.PHONES_FIELD)) {
        match = handleMatch(match, query);

        query.append(entity);
        query.append("-[?:PHONE_NUMS]->phones");
        toReturn.add("phones");

        toReturn.add("extract(p in " + entity + "-[:PHONE_NUMS]->() : last(p) as phones");
      }
    }
  }

  private boolean handleMatch(boolean match, final StringBuffer query) {
    // if there is no match clause yet, add it
    if (!match) {
      query.append("MATCH ");
      match = true;
    }
    // if there is one, add a separator
    else {
      query.append(", ");
    }

    return match;
  }

  @SuppressWarnings("unchecked")
  private Future<RestfulCollection<Person>> convertTable(final TableResult resultTable) {
    final List<Person> people = new ArrayList<Person>();
    final List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();

    // convert to maps for DTOs
    final List<List<Object>> listList = resultTable.getResults();

    final int nodeIndex = resultTable.getColumnIndex("person");
    final int affsIndex = resultTable.getColumnIndex("affs");
    final int orgsIndex = resultTable.getColumnIndex("orgs");
    final int orgAddsIndex = resultTable.getColumnIndex("org_adds");
    final int addsIndex = resultTable.getColumnIndex("adds");
    final int emailsIndex = resultTable.getColumnIndex("emails");
    final int phonesIndex = resultTable.getColumnIndex("phones");

    // collect root nodes
    Map<String, Object> nodeMap = null;
    for (final List<Object> pList : listList) {
      nodeMap = (Map<String, Object>) pList.get(nodeIndex);

      if (addsIndex >= 0) {
        final List<?> addL = (List<?>) pList.get(addsIndex);

        if (!addL.isEmpty()) {
          nodeMap.put(WsCypherPersonSPI.ADDRESSES_FIELD, addL.get(0));
        }
      }

      if (emailsIndex >= 0) {
        final List<?> mailL = (List<?>) pList.get(emailsIndex);

        if (!mailL.isEmpty()) {
          nodeMap.put(WsCypherPersonSPI.EMAILS_FIELD, mailL.get(0));
        }
      }

      if (phonesIndex >= 0) {
        final List<?> phL = (List<?>) pList.get(phonesIndex);

        if (!phL.isEmpty()) {
          nodeMap.put(WsCypherPersonSPI.PHONES_FIELD, phL.get(0));
        }
      }

      maps.add(nodeMap);
    }

    // collect subordinate maps
    copyOrgs(maps, listList, affsIndex, orgsIndex, orgAddsIndex);

    // wrap maps
    for (final Map<String, Object> pMap : maps) {
      people.add(new PersonDTO(pMap));
    }

    final RestfulCollection<Person> rColl = new RestfulCollection<Person>(people);
    rColl.setStartIndex(resultTable.getFirst());
    rColl.setTotalResults(resultTable.getTotal());
    rColl.setItemsPerPage(resultTable.getMax());
    return Futures.immediateFuture(rColl);
  }

  @SuppressWarnings("unchecked")
  private void copyOrgs(final List<Map<String, Object>> mapList, final List<List<Object>> listList,
          final int affsIndex, final int orgsIndex, final int orgAddsIndex) {
    if (orgsIndex < 0) {
      return;
    }

    final Iterator<List<Object>> listIt = listList.iterator();
    List<Object> colList = null;
    List<Map<String, Object>> nodeMapList = null;
    List<Map<String, Object>> affMapList = null;
    List<Map<String, Object>> addMapList = null;
    for (final Map<String, Object> pMap : mapList) {
      colList = listIt.next();
      nodeMapList = (List<Map<String, Object>>) colList.get(orgsIndex);

      affMapList = (List<Map<String, Object>>) colList.get(affsIndex);
      addMapList = (List<Map<String, Object>>) colList.get(orgAddsIndex);

      getOrgs(nodeMapList, affMapList.iterator(), addMapList.iterator());
      pMap.put(WsCypherPersonSPI.ORGS_FIELD, nodeMapList);
    }
  }

  private void getOrgs(final List<Map<String, Object>> orgMaps,
          final Iterator<Map<String, Object>> affIt, final Iterator<Map<String, Object>> orgAddIt) {
    Map<String, Object> attMap = null;

    for (final Map<String, Object> nodeMap : orgMaps) {
      if (nodeMap == null) {
        continue;
      }

      // affiliation data
      if (affIt != null && affIt.hasNext()) {
        attMap = affIt.next();
        nodeMap.putAll(attMap);
      }

      // organization address
      if (orgAddIt != null && orgAddIt.hasNext()) {
        attMap = orgAddIt.next();
        nodeMap.put(WsCypherPersonSPI.ORG_ADD_FIELD, attMap);
      }
    }
  }

  @Override
  public Future<RestfulCollection<Person>> getPeople(Set<UserId> userIds, GroupId groupId,
          CollectionOptions collectionOptions, Set<String> fields, SecurityToken token)
          throws ProtocolException {
    final TableResult result = query(userIds, groupId, fields, collectionOptions, token);
    return convertTable(result);
  }

  @Override
  public Future<Person> getPerson(UserId id, Set<String> fields, SecurityToken token)
          throws ProtocolException {
    final TableResult result = query(ImmutableSet.of(id), null, fields, null, token);
    final Future<RestfulCollection<Person>> collection = convertTable(result);

    // extract single result
    Person p = null;
    try {
      p = collection.get().getList().get(0);
    } catch (final Exception e) {
      e.printStackTrace();
    }

    // TODO: exception if not found

    return Futures.immediateFuture(p);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Future<Person> updatePerson(UserId id, Person person, SecurityToken token)
          throws ProtocolException {
    // TODO: retrieve current values to create delta?

    // create map with new data
    final Map<String, Object> personMap = new HashMap<String, Object>();
    new PersonDTO(personMap).setData(person);

    // extract external data
    final List<Map<String, Object>> orgData = (List<Map<String, Object>>) personMap
            .remove(WsCypherPersonSPI.ORGS_FIELD);

    final List<Map<String, Object>> affData = new ArrayList<Map<String, Object>>();
    final List<Map<String, Object>> orgAddData = new ArrayList<Map<String, Object>>();

    Map<String, Object> aff = null;
    for (final Map<String, Object> orgMap : orgData) {
      aff = new HashMap<String, Object>();

      aff.put(WsCypherPersonSPI.SALARY_FIELD, orgMap.remove(WsCypherPersonSPI.SALARY_FIELD));
      aff.put(WsCypherPersonSPI.START_DATE_FIELD, orgMap.remove(WsCypherPersonSPI.START_DATE_FIELD));
      aff.put(WsCypherPersonSPI.END_DATE_FIELD, orgMap.remove(WsCypherPersonSPI.END_DATE_FIELD));
      aff.put(WsCypherPersonSPI.TITLE_FIELD, orgMap.remove(WsCypherPersonSPI.TITLE_FIELD));
      aff.put(WsCypherPersonSPI.PRIMARY_FIELD, orgMap.remove(WsCypherPersonSPI.PRIMARY_FIELD));

      aff.put(IExtOrganization.DEPARTMENT_FIELD, orgMap.remove(IExtOrganization.DEPARTMENT_FIELD));
      aff.put(IExtOrganization.DEPARTMENT_HEAD_FIELD,
              orgMap.remove(IExtOrganization.DEPARTMENT_HEAD_FIELD));
      aff.put(IExtOrganization.MANAGER_ID_FIELD, orgMap.remove(IExtOrganization.MANAGER_ID_FIELD));
      aff.put(IExtOrganization.SECRETARY_ID_FIELD,
              orgMap.remove(IExtOrganization.SECRETARY_ID_FIELD));

      affData.add(aff);

      orgAddData.add((Map<String, Object>) orgMap.get(WsCypherPersonSPI.ORG_ADD_FIELD));
    }

    final List<Map<String, Object>> addData = (List<Map<String, Object>>) personMap
            .remove(WsCypherPersonSPI.ADDRESSES_FIELD);
    final Map<String, Object> phonesMap = (Map<String, Object>) personMap
            .remove(WsCypherPersonSPI.PHONES_FIELD);
    final Map<String, Object> emailsMap = (Map<String, Object>) personMap
            .remove(WsCypherPersonSPI.PHONES_FIELD);

    // TODO: actually set values on external nodes

    final WebsockQuery wsQuery = new WebsockQuery(EQueryType.DIRECT_CYPHER);
    final List<String> toReturn = new ArrayList<String>();
    final StringBuffer query = new StringBuffer();

    query.append("START person=node:" + Constants.PERSON_NODES + "(id = {id})\n");

    wsQuery.setParameter("properties", personMap);
    query.append("SET person={properties}");
    toReturn.add("person");

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
    RestfulCollection<Person> coll = null;
    TableResult result = null;

    try {
      result = (TableResult) callback.get();
      coll = convertTable(result).get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not retrieve result", e);
    }

    final Person resultP = coll.getList().get(0);
    return Futures.immediateFuture(resultP);
  }

  // extended functionality
  @Override
  public Future<Void> deletePerson(UserId id, SecurityToken token) throws ProtocolException {
    return null;
  }

  @Override
  public Future<RestfulCollection<Person>> getAllPeople(CollectionOptions collectionOptions,
          Set<String> fields, SecurityToken token) throws ProtocolException {
    final TableResult result = query(null, null, fields, collectionOptions, token);
    return convertTable(result);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Future<Person> createPerson(Person person, SecurityToken token) throws ProtocolException {
    // TODO: check whether ID is missing or already used

    // create map with new data
    final Map<String, Object> personMap = new HashMap<String, Object>();
    new PersonDTO(personMap).setData(person);

    // extract external data
    final List<Map<String, Object>> orgData = (List<Map<String, Object>>) personMap
            .remove(WsCypherPersonSPI.ORGS_FIELD);

    final List<Map<String, Object>> affData = new ArrayList<Map<String, Object>>();
    final List<Map<String, Object>> orgAddData = new ArrayList<Map<String, Object>>();

    Map<String, Object> aff = null;
    for (final Map<String, Object> orgMap : orgData) {
      aff = new HashMap<String, Object>();

      aff.put(WsCypherPersonSPI.SALARY_FIELD, orgMap.remove(WsCypherPersonSPI.SALARY_FIELD));
      aff.put(WsCypherPersonSPI.START_DATE_FIELD, orgMap.remove(WsCypherPersonSPI.START_DATE_FIELD));
      aff.put(WsCypherPersonSPI.END_DATE_FIELD, orgMap.remove(WsCypherPersonSPI.END_DATE_FIELD));
      aff.put(WsCypherPersonSPI.TITLE_FIELD, orgMap.remove(WsCypherPersonSPI.TITLE_FIELD));
      aff.put(WsCypherPersonSPI.PRIMARY_FIELD, orgMap.remove(WsCypherPersonSPI.PRIMARY_FIELD));

      aff.put(IExtOrganization.DEPARTMENT_FIELD, orgMap.remove(IExtOrganization.DEPARTMENT_FIELD));
      aff.put(IExtOrganization.DEPARTMENT_HEAD_FIELD,
              orgMap.remove(IExtOrganization.DEPARTMENT_HEAD_FIELD));
      aff.put(IExtOrganization.MANAGER_ID_FIELD, orgMap.remove(IExtOrganization.MANAGER_ID_FIELD));
      aff.put(IExtOrganization.SECRETARY_ID_FIELD,
              orgMap.remove(IExtOrganization.SECRETARY_ID_FIELD));

      affData.add(aff);

      orgAddData.add((Map<String, Object>) orgMap.get(WsCypherPersonSPI.ORG_ADD_FIELD));
    }

    final List<Map<String, Object>> addData = (List<Map<String, Object>>) personMap
            .remove(WsCypherPersonSPI.ADDRESSES_FIELD);
    final Map<String, Object> phonesMap = (Map<String, Object>) personMap
            .remove(WsCypherPersonSPI.PHONES_FIELD);
    final Map<String, Object> emailsMap = (Map<String, Object>) personMap
            .remove(WsCypherPersonSPI.PHONES_FIELD);

    // TODO: actually set values on external nodes

    WebsockQuery wsQuery = new WebsockQuery(EQueryType.DIRECT_CYPHER);
    final List<String> toReturn = new ArrayList<String>();
    final StringBuffer query = new StringBuffer();

    wsQuery.setParameter("properties", personMap);
    query.append("CREATE person={properties}");
    toReturn.add("ID(person)");

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
    IQueryCallback callback = this.fQueryHandler.sendQuery(wsQuery);
    TableResult result = null;

    try {
      result = (TableResult) callback.get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not retrieve result", e);
    }

    final String nodeId = result.getResults().get(0).get(0).toString();

    // create index entry
    wsQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    wsQuery.setPayload(Neo4jServiceQueries.CREATE_INDEX_ENTRY_QUERY);
    wsQuery.setParameter(Neo4jServiceQueries.INDEX, Constants.PERSON_NODES);
    wsQuery.setParameter(Neo4jServiceQueries.NODE_ID, Long.parseLong(nodeId));
    wsQuery.setParameter(Neo4jServiceQueries.KEY, "id");
    wsQuery.setParameter(Neo4jServiceQueries.VALUE, person.getId());

    callback = this.fQueryHandler.sendQuery(wsQuery);
    try {
      callback.get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not create index entry", e);
    }

    // retrieve stored person
    return getPerson(new UserId(UserId.Type.userId, person.getId()), null, token);
  }

  @Override
  public PersonDTO convertPerson(Map<String, Object> person, Set<String> fields, SecurityToken token) {
    // not used in this implementation
    return null;
  }
}
