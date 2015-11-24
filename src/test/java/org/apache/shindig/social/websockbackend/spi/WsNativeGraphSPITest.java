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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.shindig.social.opensocial.model.Group;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.opensocial.spi.UserId.Type;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.hofuniversity.iisys.neo4j.websock.queries.IQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.queries.TestQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.query.EQueryType;
import de.hofuniversity.iisys.neo4j.websock.query.WebsockQuery;
import de.hofuniversity.iisys.neo4j.websock.result.ListResult;
import de.hofuniversity.iisys.neo4j.websock.shindig.ShindigNativeQueries;

/**
 * Test for the IGraphService implementation of the websocket back-end.
 */
public class WsNativeGraphSPITest {
  private static final String JOHN_ID = "john.doe", JANE_ID = "jane.doe", JACK_ID = "jack.doe",
          HORST_ID = "horst", HANS_ID = "hans", HANNO_ID = "hanno", BOGUS_ID = "bogus";

  private static final String DOE_FAM_ID = "fam.doe.group", DENKER_ID = "denker",
          POKER_ID = "poker";

  private Map<String, Object> fFamGroup, fDenkGroup, fPokerGroup;
  private Map<String, Object> fJohn, fJane, fJack, fHorst, fHans, fHanno, fBogus;

  /**
   * Sets up some test data.
   */
  @Before
  public void setupData() {
    // groups
    this.fFamGroup = new HashMap<String, Object>();
    this.fFamGroup.put(Group.Field.ID.toString(), WsNativeGraphSPITest.DOE_FAM_ID);

    this.fDenkGroup = new HashMap<String, Object>();
    this.fDenkGroup.put(Group.Field.ID.toString(), WsNativeGraphSPITest.DENKER_ID);

    this.fPokerGroup = new HashMap<String, Object>();
    this.fPokerGroup.put(Group.Field.ID.toString(), WsNativeGraphSPITest.POKER_ID);

    // people
    this.fJohn = new HashMap<String, Object>();
    this.fJohn.put(Person.Field.ID.toString(), WsNativeGraphSPITest.JOHN_ID);

    this.fJane = new HashMap<String, Object>();
    this.fJane.put(Person.Field.ID.toString(), WsNativeGraphSPITest.JANE_ID);

    this.fJack = new HashMap<String, Object>();
    this.fJack.put(Person.Field.ID.toString(), WsNativeGraphSPITest.JACK_ID);

    this.fHorst = new HashMap<String, Object>();
    this.fHorst.put(Person.Field.ID.toString(), WsNativeGraphSPITest.HORST_ID);

    this.fHans = new HashMap<String, Object>();
    this.fHans.put(Person.Field.ID.toString(), WsNativeGraphSPITest.HANS_ID);

    this.fHanno = new HashMap<String, Object>();
    this.fHanno.put(Person.Field.ID.toString(), WsNativeGraphSPITest.HANNO_ID);

    this.fBogus = new HashMap<String, Object>();
    this.fBogus.put(Person.Field.ID.toString(), WsNativeGraphSPITest.BOGUS_ID);
  }

  /**
   * Tests the retrieval of friends of friends up to a certain depth.
   *
   * @throws Exception
   *           if the test fails
   */
  @Test
  public void friendsOfFriendsTest() throws Exception {
    // construct expected query
    WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.GET_FOFS_QUERY);

    exQuery.setParameter(ShindigNativeQueries.FOF_DEPTH, 10);
    exQuery.setParameter(ShindigNativeQueries.FOF_UNKNOWN, false);

    List<String> idList = new ArrayList<String>();
    idList.add(WsNativeGraphSPITest.HANNO_ID);
    exQuery.setParameter(ShindigNativeQueries.USER_ID_LIST, idList);

    // construct expected result
    List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
    resList.add(this.fJohn);
    resList.add(this.fJane);
    resList.add(this.fJack);
    resList.add(this.fHorst);
    resList.add(this.fHans);
    ListResult exResult = new ListResult(resList);

    // create single-use handler and service
    IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    WsNativeGraphSPI graphSPI = new WsNativeGraphSPI(qHandler);

    // execute
    final Set<UserId> userIds = new HashSet<UserId>();
    List<Person> results = null;
    final Set<String> foundIds = new HashSet<String>();

    // full traversal with max depth
    userIds.add(new UserId(Type.userId, WsNativeGraphSPITest.HANNO_ID));
    results = graphSPI.getFriendsOfFriends(userIds, 10, false, new CollectionOptions(), null, null)
            .get().getList();

    // add IDs
    for (final Person p : results) {
      foundIds.add(p.getId());
    }

    Assert.assertEquals(5, results.size());
    Assert.assertTrue(foundIds.contains(WsNativeGraphSPITest.JOHN_ID));
    Assert.assertTrue(foundIds.contains(WsNativeGraphSPITest.JANE_ID));
    Assert.assertTrue(foundIds.contains(WsNativeGraphSPITest.JACK_ID));
    Assert.assertTrue(foundIds.contains(WsNativeGraphSPITest.HORST_ID));
    Assert.assertTrue(foundIds.contains(WsNativeGraphSPITest.HANS_ID));

    // multiple users, unknown people
    // construct expected query
    exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.GET_FOFS_QUERY);

    exQuery.setParameter(ShindigNativeQueries.FOF_DEPTH, 2);
    exQuery.setParameter(ShindigNativeQueries.FOF_UNKNOWN, true);

    idList = new ArrayList<String>();
    idList.add(WsNativeGraphSPITest.JANE_ID);
    idList.add(WsNativeGraphSPITest.JACK_ID);
    idList.add(WsNativeGraphSPITest.JOHN_ID);
    exQuery.setParameter(ShindigNativeQueries.USER_ID_LIST, idList);

    // construct expected result
    resList = new ArrayList<Map<String, Object>>();
    resList.add(this.fHans);
    exResult = new ListResult(resList);

    // create single-use handler and service
    qHandler = new TestQueryHandler(exQuery, exResult);
    graphSPI = new WsNativeGraphSPI(qHandler);

    // execute
    userIds.clear();
    userIds.add(new UserId(Type.userId, WsNativeGraphSPITest.JANE_ID));
    userIds.add(new UserId(Type.userId, WsNativeGraphSPITest.JACK_ID));
    userIds.add(new UserId(Type.userId, WsNativeGraphSPITest.JOHN_ID));
    results = graphSPI.getFriendsOfFriends(userIds, 2, true, new CollectionOptions(), null, null)
            .get().getList();

    // add IDs
    foundIds.clear();
    for (final Person p : results) {
      foundIds.add(p.getId());
    }

    Assert.assertEquals(1, results.size());
    Assert.assertTrue(foundIds.contains(WsNativeGraphSPITest.HANS_ID));
  }

  /**
   * Tests the search for a shortest path between two people.
   *
   * @throws Exception
   *           if the test fails
   */
  @Test
  public void shortestPathTest() throws Exception {
    // construct expected query
    WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.GET_SHORTEST_PATH_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeGraphSPITest.JOHN_ID);
    exQuery.setParameter(ShindigNativeQueries.TARGET_USER_ID, WsNativeGraphSPITest.HANNO_ID);

    // construct expected result
    final List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
    resList.add(this.fJohn);
    resList.add(this.fJane);
    resList.add(this.fHorst);
    resList.add(this.fHans);
    resList.add(this.fHanno);
    ListResult exResult = new ListResult(resList);

    // create single-use handler and service
    IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    WsNativeGraphSPI graphSPI = new WsNativeGraphSPI(qHandler);

    // execute
    List<Person> results = null;

    // full path test
    final UserId source = new UserId(Type.userId, WsNativeGraphSPITest.JOHN_ID);
    UserId target = new UserId(Type.userId, WsNativeGraphSPITest.HANNO_ID);

    results = graphSPI.getShortestPath(source, target, new CollectionOptions(), null, null).get()
            .getList();

    Assert.assertEquals(5, results.size());

    // check for correct path
    Assert.assertTrue(results.get(0).getId().equals(WsNativeGraphSPITest.JOHN_ID));
    Assert.assertTrue(results.get(1).getId().equals(WsNativeGraphSPITest.JANE_ID));
    Assert.assertTrue(results.get(2).getId().equals(WsNativeGraphSPITest.HORST_ID));
    Assert.assertTrue(results.get(3).getId().equals(WsNativeGraphSPITest.HANS_ID));
    Assert.assertTrue(results.get(4).getId().equals(WsNativeGraphSPITest.HANNO_ID));

    // no path
    // construct expected query
    exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.GET_SHORTEST_PATH_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeGraphSPITest.JOHN_ID);
    exQuery.setParameter(ShindigNativeQueries.TARGET_USER_ID, WsNativeGraphSPITest.BOGUS_ID);

    // construct expected result
    exResult = new ListResult((List<?>) null);

    // create single-use handler and service
    qHandler = new TestQueryHandler(exQuery, exResult);
    graphSPI = new WsNativeGraphSPI(qHandler);

    // execute
    target = new UserId(Type.userId, WsNativeGraphSPITest.BOGUS_ID);
    results = graphSPI.getShortestPath(source, target, new CollectionOptions(), null, null).get()
            .getList();

    Assert.assertTrue(results == null || results.isEmpty());
  }

  /**
   * Tests the group suggestion method based on friends' memberships.
   *
   * @throws Exception
   *           if the test fails
   */
  @Test
  public void groupSuggestionTest() throws Exception {
    // construct expected query
    WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.RECOMMEND_GROUP_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeGraphSPITest.JANE_ID);
    exQuery.setParameter(ShindigNativeQueries.MIN_FRIENDS_IN_GROUP, 1);

    // construct expected result
    final List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
    resList.add(this.fDenkGroup);
    resList.add(this.fPokerGroup);
    ListResult exResult = new ListResult(resList);

    // create single-use handler and service
    IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    WsNativeGraphSPI graphSPI = new WsNativeGraphSPI(qHandler);

    // execute
    final Set<String> foundIds = new HashSet<String>();
    List<Group> results = null;

    // most
    UserId userId = new UserId(Type.userId, WsNativeGraphSPITest.JANE_ID);
    results = graphSPI.getGroupRecommendation(userId, 1, new CollectionOptions(), null, null).get()
            .getList();

    for (final Group g : results) {
      foundIds.add(g.getId());
    }

    Assert.assertEquals(2, results.size());
    Assert.assertTrue(foundIds.contains(WsNativeGraphSPITest.DENKER_ID));
    Assert.assertTrue(foundIds.contains(WsNativeGraphSPITest.POKER_ID));

    // none
    // construct expected query
    exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.RECOMMEND_GROUP_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeGraphSPITest.HANS_ID);
    exQuery.setParameter(ShindigNativeQueries.MIN_FRIENDS_IN_GROUP, 1);

    // construct expected result
    exResult = new ListResult((List<?>) null);

    // create single-use handler and service
    qHandler = new TestQueryHandler(exQuery, exResult);
    graphSPI = new WsNativeGraphSPI(qHandler);

    // execute
    userId = new UserId(Type.userId, WsNativeGraphSPITest.HANS_ID);
    results = graphSPI.getGroupRecommendation(userId, 1, new CollectionOptions(), null, null).get()
            .getList();

    Assert.assertEquals(0, results.size());
  }

  /**
   * Tests the friend suggestion method based on friends' friends.
   *
   * @throws Exception
   *           if the test fails
   */
  @Test
  public void friendSuggestionTest() throws Exception {
    // construct expected query
    WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.RECOMMEND_FRIEND_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeGraphSPITest.JANE_ID);
    exQuery.setParameter(ShindigNativeQueries.MIN_COMMON_FRIENDS, 1);

    // construct expected result
    final List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
    resList.add(this.fHans);
    ListResult exResult = new ListResult(resList);

    // create single-use handler and service
    IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    WsNativeGraphSPI graphSPI = new WsNativeGraphSPI(qHandler);

    // execute
    List<Person> results = null;

    // only one connected person
    UserId userId = new UserId(Type.userId, WsNativeGraphSPITest.JANE_ID);
    results = graphSPI.getFriendRecommendation(userId, 1, new CollectionOptions(), null, null)
            .get().getList();

    Assert.assertEquals(1, results.size());
    Assert.assertEquals(WsNativeGraphSPITest.HANS_ID, results.get(0).getId());

    // no suggestions possible
    // construct expected query
    exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.RECOMMEND_FRIEND_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeGraphSPITest.BOGUS_ID);
    exQuery.setParameter(ShindigNativeQueries.MIN_COMMON_FRIENDS, 1);

    // construct expected result
    exResult = new ListResult((List<?>) null);

    // create single-use handler and service
    qHandler = new TestQueryHandler(exQuery, exResult);
    graphSPI = new WsNativeGraphSPI(qHandler);

    // execute
    userId = new UserId(Type.userId, WsNativeGraphSPITest.BOGUS_ID);
    results = graphSPI.getFriendRecommendation(userId, 1, new CollectionOptions(), null, null)
            .get().getList();

    Assert.assertEquals(0, results.size());
  }
}
