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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.core.model.PersonImpl;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.opensocial.spi.UserId.Type;
import org.junit.Assert;
import org.junit.Test;

import de.hofuniversity.iisys.neo4j.websock.queries.IQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.queries.TestQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.query.EQueryType;
import de.hofuniversity.iisys.neo4j.websock.query.WebsockQuery;
import de.hofuniversity.iisys.neo4j.websock.result.ListResult;
import de.hofuniversity.iisys.neo4j.websock.shindig.ShindigNativeQueries;

/**
 * Test routine to check whether the friendship service implementation is working correctly.
 */
public class WsNativeFriendSPITest {
  private static final String JOHN_ID = "john.doe", JANE_ID = "jane.doe", JACK_ID = "jack.doe",
          HORST_ID = "horst", FRED_ID = "FRED";

  /**
   * Test routine for the retrieval of people from whom the requesting user has pending friend
   * requests.
   *
   * @throws Exception
   *           if a test fails
   */
  @Test
  public void getRequestsTest() throws Exception {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.GET_FRIEND_REQUESTS_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeFriendSPITest.JOHN_ID);

    // construct expected result
    ListResult exResult = new ListResult((List<?>) null);

    // create single-use handler and service
    IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    WsNativeFriendSPI friendSPI = new WsNativeFriendSPI(qHandler);

    // execute
    final CollectionOptions collOpts = new CollectionOptions();

    Future<RestfulCollection<Person>> peopleColl = null;
    List<Person> people = null;

    // no requests
    peopleColl = friendSPI.getRequests(new UserId(Type.userId, WsNativeFriendSPITest.JOHN_ID),
            collOpts, null, null);
    people = peopleColl.get().getList();

    Assert.assertTrue(people.isEmpty());

    // construct expected query
    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeFriendSPITest.HORST_ID);

    // construct expected result
    final List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
    final Map<String, Object> janedoe = new HashMap<String, Object>();
    janedoe.put(Person.Field.ID.toString(), WsNativeFriendSPITest.JANE_ID);
    resList.add(janedoe);
    final Map<String, Object> jackdoe = new HashMap<String, Object>();
    jackdoe.put(Person.Field.ID.toString(), WsNativeFriendSPITest.JACK_ID);
    resList.add(jackdoe);
    exResult = new ListResult(resList);

    // create single-use handler and service
    qHandler = new TestQueryHandler(exQuery, exResult);
    friendSPI = new WsNativeFriendSPI(qHandler);

    // existing requests
    peopleColl = friendSPI.getRequests(new UserId(Type.userId, WsNativeFriendSPITest.HORST_ID),
            collOpts, null, null);
    people = peopleColl.get().getList();

    Assert.assertEquals(2, people.size());

    boolean janeFound = false;
    boolean jackFound = false;
    for (final Person p : people) {
      if (p.getId().equals(WsNativeFriendSPITest.JANE_ID)) {
        janeFound = true;
      } else if (p.getId().equals(WsNativeFriendSPITest.JACK_ID)) {
        jackFound = true;
      }
    }
    Assert.assertTrue(janeFound);
    Assert.assertTrue(jackFound);
  }

  /**
   * Test routine for making and accepting friendship requests.
   *
   * @throws Exception
   *           if a test fails
   */
  @Test
  public void requestTest() throws Exception {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.REQUEST_FRIENDSHIP_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeFriendSPITest.HORST_ID);
    exQuery.setParameter(ShindigNativeQueries.TARGET_USER_ID, WsNativeFriendSPITest.FRED_ID);

    // construct expected result
    final ListResult exResult = new ListResult((List<?>) null);

    // create single-use handler and service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    final WsNativeFriendSPI friendSPI = new WsNativeFriendSPI(qHandler);

    // execute
    final UserId horstId = new UserId(Type.userId, WsNativeFriendSPITest.HORST_ID);

    final Person fred = new PersonImpl();
    fred.setId(WsNativeFriendSPITest.FRED_ID);

    // create request
    friendSPI.requestFriendship(horstId, fred, null);
  }

  /**
   * Test routine for denying and deleting friendships.
   *
   * @throws Exception
   *           if a test fails
   */
  @Test
  public void denyTest() throws Exception {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.DENY_FRIENDSHIP_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeFriendSPITest.HORST_ID);
    exQuery.setParameter(ShindigNativeQueries.TARGET_USER_ID, WsNativeFriendSPITest.JANE_ID);

    // construct expected result
    final ListResult exResult = new ListResult((List<?>) null);

    // create single-use handler and service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    final WsNativeFriendSPI friendSPI = new WsNativeFriendSPI(qHandler);

    // execute
    final UserId horstId = new UserId(Type.userId, WsNativeFriendSPITest.HORST_ID);

    final Person jane = new PersonImpl();
    jane.setId(WsNativeFriendSPITest.JANE_ID);

    // deny request
    friendSPI.denyFriendship(horstId, jane, null);
  }
}
