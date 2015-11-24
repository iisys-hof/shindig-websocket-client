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
import java.util.Iterator;
import java.util.LinkedHashSet;
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
import org.apache.shindig.social.opensocial.model.Organization;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.PersonService;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.opensocial.spi.UserId.Type;
import org.apache.shindig.social.websockbackend.Constants;
import org.apache.shindig.social.websockbackend.model.dto.GroupDTO;
import org.apache.shindig.social.websockbackend.model.dto.PersonDTO;
import org.apache.shindig.social.websockbackend.spi.IGraphService;
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
 * Implementation of the IGraphService interface retrieving graph data from a remote Neo4j graph
 * database over a websocket using Cypher.
 */
@Singleton
public class WsCypherGraphSPI implements IGraphService {
  private static final String ADDRESSES_FIELD = Person.Field.ADDRESSES.toString();
  private static final String ORGS_FIELD = Person.Field.ORGANIZATIONS.toString();

  private static final String EMAILS_FIELD = Person.Field.EMAILS.toString();
  private static final String PHONES_FIELD = Person.Field.PHONE_NUMBERS.toString();

  private static final String ORG_ADD_FIELD = Organization.Field.ADDRESS.toString();

  private static final String SPATH_QUERY_NAME = "cSPath";
  private static final String SPATH_QUERY = "START source=node:" + Constants.PERSON_NODES
          + "(id = {id})," + "target=node:" + Constants.PERSON_NODES + "(id = {targetId})\n"
          + "MATCH path=shortestPath(source-[:FRIEND_OF*..10]->target)\n"
          // only return people's IDs since the actual query can' be chained
          + "RETURN EXTRACT(x IN NODES(path): x.id) as idList";

  private static final String SGROUP_QUERY_NAME = "cSGroup";
  private static final String SGROUP_QUERY = "START person=node:"
          + Constants.PERSON_NODES
          + "(id = {id})\n"
          // follow friends' memberships, exclude groups the person is already in
          + "MATCH person-[:FRIEND_OF]->()-[:MEMBER_OF]->group\n"
          + "WHERE not (group<-[:MEMBER_OF]-person)\n" + "WITH group, COUNT(*) as number\n"
          + "WHERE number >= {num}\n" + "RETURN group, number\n"
          // sort by number of friends in group
          + "ORDER BY number DESC\n";

  private final IQueryHandler fQueryHandler;
  private final PersonService fPersonSPI;

  private final Logger fLogger;

  /**
   * Creates a new Cypher graph service using the given query handler and person service to retrieve
   * data from a Neo4j instance over a websocket using Cypher. The given query handler and person
   * service must not be null.
   *
   * @param qHandler
   *          query handler to use
   * @param personSPI
   *          person service to use
   */
  @Inject
  public WsCypherGraphSPI(IQueryHandler qHandler, PersonService personSPI) {
    if (qHandler == null) {
      throw new NullPointerException("Query handler was null");
    }
    if (personSPI == null) {
      throw new NullPointerException("person service was null");
    }

    this.fQueryHandler = qHandler;
    this.fPersonSPI = personSPI;
    this.fLogger = Logger.getLogger(this.getClass().getName());

    // initialize stored procedures
    // shortest path query
    WebsockQuery wsQuery = new WebsockQuery(EQueryType.STORE_PROCEDURE);
    wsQuery.setPayload(WsCypherGraphSPI.SPATH_QUERY);
    wsQuery.setParameter(WebsockConstants.PROCEDURE_NAME, WsCypherGraphSPI.SPATH_QUERY_NAME);
    try {
      this.fQueryHandler.sendMessage(wsQuery).get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new RuntimeException("could not store procedure \""
              + wsQuery.getParameter(WebsockConstants.PROCEDURE_NAME), e);
    }

    // group suggestion query
    wsQuery = new WebsockQuery(EQueryType.STORE_PROCEDURE);
    wsQuery.setPayload(WsCypherGraphSPI.SGROUP_QUERY);
    wsQuery.setParameter(WebsockConstants.PROCEDURE_NAME, WsCypherGraphSPI.SGROUP_QUERY_NAME);
    try {
      this.fQueryHandler.sendMessage(wsQuery).get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new RuntimeException("could not store procedure \""
              + wsQuery.getParameter(WebsockConstants.PROCEDURE_NAME), e);
    }
  }

  private TableResult queryFof(Set<UserId> userIds, int depth, boolean unknown,
          final Set<String> fields, CollectionOptions opts, final SecurityToken token) {
    final WebsockQuery wsQuery = new WebsockQuery(EQueryType.DIRECT_CYPHER);
    final List<String> toReturn = new ArrayList<String>();
    final StringBuffer query = new StringBuffer();

    if (opts != null) {
      CollOptsConverter.convert(opts, wsQuery);
    }

    String idLookup = "id:(";
    for (final UserId userId : userIds) {
      idLookup += userId.getUserId(token) + " ";
    }
    idLookup += ")";
    wsQuery.setParameter("idLookup", idLookup);

    query.append("START person=node:" + Constants.PERSON_NODES + "({idLookup})\n");

    if (unknown) {
      query.append("MATCH person-[:FRIEND_OF]->()-[:FRIEND_OF*1.." + (depth - 1) + "]->fof");
    } else {
      query.append("MATCH person-[:FRIEND_OF*1.." + depth + "]->fof");
    }
    toReturn.add("distinct fof as person");

    handleRelations(query, true, "fof", fields, toReturn);

    if (unknown) {
      query.append("\nWHERE not (fof<-[:FRIEND_OF]-person)");
    }

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
          final Set<String> fields, final List<String> toReturn) {
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

      toReturn.add("collect(extract(p in " + entity + "-[:LOCATED_AT]->() : last(p))) as adds");
      toReturn.add("extract(p in " + entity + "-[:EMAILS]->() : last(p)) as emails");
      toReturn.add("extract(p in " + entity + "-[:PHONE_NUMS]->() : last(p)) as phones");
    } else {
      if (fields.contains(WsCypherGraphSPI.ORGS_FIELD)) {
        match = handleMatch(match, query);

        query.append(entity);
        query.append("-[aff?:AFFILIATED]->org-[?:LOCATED_AT]->org_add");
        toReturn.add("collect(aff) as affs");
        toReturn.add("collect(org) as orgs");
        toReturn.add("collect(org_add) as org_adds");
      }

      if (fields.contains(WsCypherGraphSPI.ADDRESSES_FIELD)) {
        // match = handleMatch(match, query);
        //
        // query.append(entity);
        // query.append("-[?:LOCATED_AT]->add");
        // toReturn.add("collect(add) as adds");

        toReturn.add("collect(extract(p in " + entity + "-[:LOCATED_AT]->() : last(p))) as adds");
      }

      if (fields.contains(WsCypherGraphSPI.EMAILS_FIELD)) {
        // match = handleMatch(match, query);
        //
        // query.append(entity);
        // query.append("-[?:EMAILS]->emails");
        // toReturn.add("emails");

        toReturn.add("extract(p in " + entity + "-[:EMAILS]->() : last(p)) as emails");
      }

      if (fields.contains(WsCypherGraphSPI.PHONES_FIELD)) {
        match = handleMatch(match, query);

        query.append(entity);
        query.append("-[?:PHONE_NUMS]->phones");
        toReturn.add("phones");

        toReturn.add("extract(p in " + entity + "-[:PHONE_NUMS]->() : last(p)) as phones");
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
  private Future<RestfulCollection<Person>> convertPeople(final TableResult resultTable) {
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
        nodeMap.put(WsCypherGraphSPI.ADDRESSES_FIELD, pList.get(addsIndex));
      }

      if (emailsIndex >= 0) {
        nodeMap.put(WsCypherGraphSPI.EMAILS_FIELD, pList.get(emailsIndex));
      }

      if (phonesIndex >= 0) {
        nodeMap.put(WsCypherGraphSPI.PHONES_FIELD, pList.get(phonesIndex));
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
      pMap.put(WsCypherGraphSPI.ORGS_FIELD, nodeMapList);
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
        nodeMap.put(WsCypherGraphSPI.ORG_ADD_FIELD, attMap);
      }
    }
  }

  @Override
  public Future<RestfulCollection<Person>> getFriendsOfFriends(Set<UserId> userIds, int depth,
          boolean unknown, CollectionOptions collectionOptions, Set<String> fields,
          SecurityToken token) {
    final TableResult result = queryFof(userIds, depth, unknown, fields, collectionOptions, token);
    return convertPeople(result);
  }

  @SuppressWarnings("unchecked")
  private Future<RestfulCollection<Person>> querySPath(String source, String target,
          final Set<String> fields, CollectionOptions opts, SecurityToken token) {
    final WebsockQuery wsQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);

    if (opts != null) {
      CollOptsConverter.convert(opts, wsQuery);
    }

    // set parameters
    wsQuery.setPayload(WsCypherGraphSPI.SPATH_QUERY_NAME);
    wsQuery.setParameter("id", source);
    wsQuery.setParameter("targetId", target);

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

    // only request people if there is a path
    List<String> idList = null;
    final List<List<Object>> results = result.getResults();

    if (!results.isEmpty() && !results.get(0).isEmpty()) {
      idList = (List<String>) results.get(0).get(0);

      // use person service to retrieve people in order
      final Set<UserId> idSet = new LinkedHashSet<UserId>();
      for (final String id : idList) {
        idSet.add(new UserId(Type.userId, id));
      }

      return this.fPersonSPI.getPeople(idSet, null, new CollectionOptions(), fields, token);
    }

    // if there is no path, return an empty collection
    return Futures.immediateFuture(new RestfulCollection<Person>(new ArrayList<Person>()));
  }

  @Override
  public Future<RestfulCollection<Person>> getShortestPath(UserId userId, UserId targetId,
          CollectionOptions collectionOptions, Set<String> fields, SecurityToken token) {
    return querySPath(userId.getUserId(token), targetId.getUserId(token), fields,
            collectionOptions, token);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Future<RestfulCollection<Group>> getGroupRecommendation(UserId userId, int number,
          CollectionOptions collectionOptions, Set<String> fields, SecurityToken token) {
    final List<Group> groupList = new ArrayList<Group>();
    final WebsockQuery wsQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);

    if (collectionOptions != null) {
      CollOptsConverter.convert(collectionOptions, wsQuery);
    }

    // set parameters
    wsQuery.setPayload(WsCypherGraphSPI.SGROUP_QUERY_NAME);
    wsQuery.setParameter("id", userId.getUserId(token));
    wsQuery.setParameter("num", number);

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

  private TableResult querySFriends(String userId, int number, final Set<String> fields,
          CollectionOptions opts) {
    final WebsockQuery wsQuery = new WebsockQuery(EQueryType.DIRECT_CYPHER);
    final List<String> toReturn = new ArrayList<String>();
    final StringBuffer query = new StringBuffer();

    if (opts != null) {
      CollOptsConverter.convert(opts, wsQuery);
    }

    final String idLookup = "id:(" + userId + ")";
    wsQuery.setParameter("idLookup", idLookup);

    query.append("START person=node:" + Constants.PERSON_NODES + "({idLookup})\n");

    query.append("MATCH person-[:FRIEND_OF]->()-[:FRIEND_OF]");
    query.append("->friend_of_friend\n");
    toReturn.add("friend_of_friend as person");

    handleRelations(query, true, "friend_of_friend", fields, toReturn);

    query.append("\nWHERE not (friend_of_friend<-[:FRIEND_OF]-person)");

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

  @Override
  public Future<RestfulCollection<Person>> getFriendRecommendation(UserId userId, int number,
          CollectionOptions collectionOptions, Set<String> fields, SecurityToken token) {
    final TableResult result = querySFriends(userId.getUserId(token), number, fields,
            collectionOptions);
    return convertPeople(result);
  }
}
