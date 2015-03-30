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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.apache.shindig.common.util.DateUtil;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.core.model.ActivityEntryImpl;
import org.apache.shindig.social.core.model.ActivityObjectImpl;
import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.model.ActivityObject;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.websockbackend.model.dto.ActivityEntryDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.hofuniversity.iisys.neo4j.websock.queries.IQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.queries.TestQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.query.EQueryType;
import de.hofuniversity.iisys.neo4j.websock.query.WebsockQuery;
import de.hofuniversity.iisys.neo4j.websock.result.ListResult;
import de.hofuniversity.iisys.neo4j.websock.result.SingleResult;
import de.hofuniversity.iisys.neo4j.websock.shindig.ShindigNativeQueries;

/**
 * Test for the ActivityStreamService implementation of the websocket back-end.
 */
public class WsNativeActivityStreamSPITest {
  private static final String JOHN_ID = "john.doe", JANE_ID = "jane.doe", HORST_ID = "horst";

  private Map<String, Map<String, Object>> fActsById;
  private List<Map<String, Object>> fJohnActs, fJaneActs, fHorstActs;

  /**
   * Sets up some test data.
   */
  @Before
  public void setupData() {
    this.fActsById = new HashMap<String, Map<String, Object>>();

    // people
    final Map<String, Object> johndoe = new HashMap<String, Object>();
    johndoe.put(Person.Field.ID.toString(), WsNativeActivityStreamSPITest.JOHN_ID);
    this.fJohnActs = new ArrayList<Map<String, Object>>();

    final Map<String, Object> janedoe = new HashMap<String, Object>();
    janedoe.put(Person.Field.ID.toString(), WsNativeActivityStreamSPITest.JANE_ID);
    this.fJaneActs = new ArrayList<Map<String, Object>>();

    final Map<String, Object> horst = new HashMap<String, Object>();
    horst.put(Person.Field.ID.toString(), WsNativeActivityStreamSPITest.HORST_ID);
    this.fHorstActs = new ArrayList<Map<String, Object>>();

    // activity objects
    final Map<String, Object> johnObject = new HashMap<String, Object>();
    johnObject.put(ActivityObject.Field.ID.toString(), WsNativeActivityStreamSPITest.JOHN_ID);
    johnObject.put(ActivityObject.Field.DISPLAY_NAME.toString(), "John Doe");

    final Map<String, Object> janeObject = new HashMap<String, Object>();
    janeObject.put(ActivityObject.Field.ID.toString(), WsNativeActivityStreamSPITest.JANE_ID);
    janeObject.put(ActivityObject.Field.DISPLAY_NAME.toString(), "Jane Doe");

    final Map<String, Object> horstObject = new HashMap<String, Object>();
    horstObject.put(ActivityObject.Field.ID.toString(), WsNativeActivityStreamSPITest.HORST_ID);
    horstObject.put(ActivityObject.Field.DISPLAY_NAME.toString(), "Horst");

    final Map<String, Object> vacObject = new HashMap<String, Object>();
    vacObject.put(ActivityObject.Field.ID.toString(), "vacancy");
    vacObject.put(ActivityObject.Field.DISPLAY_NAME.toString(), "Stellenanzeige");

    final Map<String, Object> appObject = new HashMap<String, Object>();
    appObject.put(ActivityObject.Field.ID.toString(), "application");
    appObject.put(ActivityObject.Field.DISPLAY_NAME.toString(), "Bewerbung");

    // activity entries
    final Map<String, Object> hireJaneAct = new HashMap<String, Object>();
    hireJaneAct.put(ActivityEntry.Field.ID.toString(), "1");
    hireJaneAct.put(ActivityEntry.Field.TITLE.toString(), "Einstellung");
    hireJaneAct.put(ActivityEntry.Field.VERB.toString(), "hat eingestellt");

    hireJaneAct.put(ActivityEntry.Field.ACTOR.toString(), johnObject);
    hireJaneAct.put(ActivityEntry.Field.TARGET.toString(), janeObject);

    this.fJohnActs.add(hireJaneAct);
    this.fActsById.put("1", hireJaneAct);

    final Map<String, Object> postVacAct = new HashMap<String, Object>();
    postVacAct.put(ActivityEntry.Field.ID.toString(), "2");
    postVacAct.put(ActivityEntry.Field.TITLE.toString(), "neue Stellenanzeige");
    postVacAct.put(ActivityEntry.Field.VERB.toString(), "erstellte");

    postVacAct.put(ActivityEntry.Field.ACTOR.toString(), janeObject);
    postVacAct.put(ActivityEntry.Field.TARGET.toString(), vacObject);

    this.fJaneActs.add(postVacAct);
    this.fActsById.put("2", postVacAct);

    final Map<String, Object> applicAct = new HashMap<String, Object>();
    applicAct.put(ActivityEntry.Field.ID.toString(), "3");
    applicAct.put(ActivityEntry.Field.TITLE.toString(), "Bewerbung");
    applicAct.put(ActivityEntry.Field.VERB.toString(), "versendete");

    applicAct.put(ActivityEntry.Field.ACTOR.toString(), horstObject);
    applicAct.put(ActivityEntry.Field.OBJECT.toString(), appObject);
    applicAct.put(ActivityEntry.Field.TARGET.toString(), janeObject);

    this.fHorstActs.add(applicAct);
    this.fActsById.put("3", applicAct);

    final Map<String, Object> hireHorstAct = new HashMap<String, Object>();
    hireHorstAct.put(ActivityEntry.Field.ID.toString(), "4");
    hireHorstAct.put(ActivityEntry.Field.TITLE.toString(), "Einstellung");
    hireHorstAct.put(ActivityEntry.Field.VERB.toString(), "hat eingestellt");

    hireHorstAct.put(ActivityEntry.Field.ACTOR.toString(), janeObject);
    hireHorstAct.put(ActivityEntry.Field.TARGET.toString(), horstObject);

    this.fJaneActs.add(hireHorstAct);
    this.fActsById.put("4", hireHorstAct);

    final Map<String, Object> welcomeAct = new HashMap<String, Object>();
    welcomeAct.put(ActivityEntry.Field.ID.toString(), "5");
    welcomeAct.put(ActivityEntry.Field.TITLE.toString(), "Neuer Mitarbeiter");
    welcomeAct.put(ActivityEntry.Field.VERB.toString(), "heißt wilkommen");

    welcomeAct.put(ActivityEntry.Field.ACTOR.toString(), johnObject);
    welcomeAct.put(ActivityEntry.Field.TARGET.toString(), horstObject);

    this.fJohnActs.add(welcomeAct);
    this.fActsById.put("5", welcomeAct);

    final Map<String, Object> welcomeAct2 = new HashMap<String, Object>();
    welcomeAct2.put(ActivityEntry.Field.ID.toString(), "6");
    welcomeAct2.put(ActivityEntry.Field.TITLE.toString(), "Neuer Mitarbeiter");
    welcomeAct2.put(ActivityEntry.Field.VERB.toString(), "heißt wilkommen");

    welcomeAct2.put(ActivityEntry.Field.ACTOR.toString(), janeObject);
    welcomeAct2.put(ActivityEntry.Field.TARGET.toString(), horstObject);

    this.fJaneActs.add(welcomeAct2);
    this.fActsById.put("6", welcomeAct2);
  }

  /**
   * Tests retrieval of all activity entries for people.
   *
   * @throws Exception
   *           if an exception occurs
   */
  @Test
  public void allRetrievalTest() throws Exception {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.GET_ACT_ENTRIES_QUERY);

    final List<String> idList = new ArrayList<String>();
    idList.add(WsNativeActivityStreamSPITest.JOHN_ID);
    idList.add(WsNativeActivityStreamSPITest.HORST_ID);
    exQuery.setParameter(ShindigNativeQueries.USER_ID_LIST, idList);

    // construct expected result
    final List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
    resList.add(this.fActsById.get("1"));
    resList.add(this.fActsById.get("3"));
    resList.add(this.fActsById.get("5"));
    final ListResult exResult = new ListResult(resList);

    // create single-use handler and service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    final WsNativeActivityStreamSPI actSPI = new WsNativeActivityStreamSPI(qHandler);

    // retrieve and check
    final Set<UserId> userIds = new HashSet<UserId>();
    userIds.add(new UserId(UserId.Type.userId, WsNativeActivityStreamSPITest.JOHN_ID));
    userIds.add(new UserId(UserId.Type.userId, WsNativeActivityStreamSPITest.HORST_ID));

    final Future<RestfulCollection<ActivityEntry>> entryColl = actSPI.getActivityEntries(userIds,
            null, null, null, new CollectionOptions(), null);

    final List<ActivityEntry> actEntries = entryColl.get().getList();

    Assert.assertEquals(3, actEntries.size());

    boolean oneFound = false;
    boolean threeFound = false;
    boolean fiveFound = false;

    String id = null;
    for (final ActivityEntry entry : actEntries) {
      id = entry.getId();

      if (id.equals("1")) {
        oneFound = true;
      } else if (id.equals("3")) {
        threeFound = true;
      } else if (id.equals("5")) {
        fiveFound = true;
      } else {
        throw new Exception("unexpected activity entry");
      }
    }
    Assert.assertTrue(oneFound && threeFound && fiveFound);
  }

  /**
   * Tests retrieval of certain activity entries for a person.
   *
   * @throws Exception
   *           if an exception occurs
   */
  @Test
  public void idSetRetrievalTest() throws Exception {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.GET_ACT_ENTRIES_BY_ID_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeActivityStreamSPITest.JANE_ID);

    final List<String> idList = new ArrayList<String>();
    idList.add("2");
    idList.add("4");
    exQuery.setParameter(ShindigNativeQueries.ACTIVITY_IDS, idList);

    // construct expected result
    final List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
    resList.add(this.fActsById.get("2"));
    resList.add(this.fActsById.get("4"));
    final ListResult exResult = new ListResult(resList);

    // create single-use handler and service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    final WsNativeActivityStreamSPI actSPI = new WsNativeActivityStreamSPI(qHandler);

    // retrieve and check
    final UserId userId = new UserId(UserId.Type.userId, WsNativeActivityStreamSPITest.JANE_ID);
    final Set<String> actIds = new HashSet<String>();
    actIds.add("2");
    actIds.add("4");

    final Future<RestfulCollection<ActivityEntry>> entryColl = actSPI.getActivityEntries(userId,
            null, null, null, new CollectionOptions(), actIds, null);

    final List<ActivityEntry> actEntries = entryColl.get().getList();

    Assert.assertEquals(2, actEntries.size());

    boolean twoFound = false;
    boolean fourFound = false;

    String id = null;
    for (final ActivityEntry entry : actEntries) {
      id = entry.getId();

      if (id.equals("2")) {
        twoFound = true;
      } else if (id.equals("4")) {
        fourFound = true;
      } else {
        throw new Exception("unexpected activity entry");
      }
    }
    Assert.assertTrue(twoFound && fourFound);
  }

  /**
   * Tests retrieval of certain activity entries for a person. Also checks for proper conversion.
   *
   * @throws Exception
   *           if an exception occurs
   */
  @Test
  public void singleRetrievalTest() throws Exception {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.GET_ACT_ENTRY_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeActivityStreamSPITest.JOHN_ID);
    exQuery.setParameter(ShindigNativeQueries.ACTIVITY_ID, "5");

    // construct expected result
    final SingleResult exResult = new SingleResult(this.fActsById.get("5"));

    // create single-use handler and service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    final WsNativeActivityStreamSPI actSPI = new WsNativeActivityStreamSPI(qHandler);

    // retrieve and check
    final UserId userId = new UserId(UserId.Type.userId, WsNativeActivityStreamSPITest.JOHN_ID);

    final Future<ActivityEntry> entryFut = actSPI.getActivityEntry(userId, null, null, null, "5",
            null);
    final ActivityEntry entry = entryFut.get();
    Assert.assertNotNull(entry);
    Assert.assertEquals("5", entry.getId());

    Assert.assertNotNull(entry.getActor());
    Assert.assertNotNull(entry.getTarget());
  }

  /**
   * Tests the creation of activity entries for a person. Also checks for proper conversion.
   *
   * @throws Exception
   *           if an exception occurs
   */
  @Test
  public void creationTest() throws Exception {
    // create sample
    final UserId userId = new UserId(UserId.Type.userId, WsNativeActivityStreamSPITest.JANE_ID);

    ActivityEntry activity = new ActivityEntryImpl();
    activity.setVerb("create");

    ActivityObject object = new ActivityObjectImpl();
    object.setId(WsNativeActivityStreamSPITest.JANE_ID);
    object.setDisplayName("Jane Doe");
    object.setObjectType("person");
    activity.setActor(object);

    object = new ActivityObjectImpl();
    object.setId("testobject");
    object.setDisplayName("Testdatei");
    object.setObjectType("file");
    activity.setObject(object);

    object = new ActivityObjectImpl();
    object.setId("testapp");
    object.setDisplayName("Testanwendung");
    object.setObjectType("application");
    activity.setTarget(object);
    activity.setGenerator(object);

    activity.setPublished(DateUtil.formatIso8601Date(System.currentTimeMillis()));

    // construct expected query
    final Map<String, Object> actMap = new HashMap<String, Object>();
    new ActivityEntryDTO(actMap).setData(activity);

    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.CREATE_ACT_ENTRY_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeActivityStreamSPITest.JANE_ID);
    exQuery.setParameter(ShindigNativeQueries.APP_ID, "testapp");
    exQuery.setParameter(ShindigNativeQueries.ACTIVITY_ENTRY_OBJECT, actMap);

    // construct expected result
    final SingleResult exResult = new SingleResult(actMap);

    // create single-use handler and service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    final WsNativeActivityStreamSPI actSPI = new WsNativeActivityStreamSPI(qHandler);

    // execute
    final Future<ActivityEntry> entryFut = actSPI.createActivityEntry(userId, null, "testapp",
            null, activity, null);

    // check result
    activity = entryFut.get();
    Assert.assertEquals("create", activity.getVerb());

    object = activity.getActor();
    Assert.assertEquals(WsNativeActivityStreamSPITest.JANE_ID, object.getId());

    object = activity.getObject();
    Assert.assertEquals("testobject", object.getId());

    object = activity.getTarget();
    Assert.assertEquals("testapp", object.getId());

    object = activity.getGenerator();
    Assert.assertEquals("testapp", object.getId());
  }

  /**
   * Tests updating an activity for a person.
   */
  @Test
  public void updateTest() throws Exception {
    // create sample
    final UserId userId = new UserId(UserId.Type.userId, WsNativeActivityStreamSPITest.JANE_ID);

    final ActivityEntry activity = new ActivityEntryImpl();
    activity.setId("42");
    activity.setVerb("create");

    ActivityObject object = new ActivityObjectImpl();
    object.setId(WsNativeActivityStreamSPITest.JANE_ID);
    object.setDisplayName("Jane Doe");
    object.setObjectType("person");
    activity.setActor(object);

    object = new ActivityObjectImpl();
    object.setId("testobject");
    object.setDisplayName("Testdatei");
    object.setObjectType("file");
    activity.setObject(object);

    object = new ActivityObjectImpl();
    object.setId("testapp");
    object.setDisplayName("Testanwendung");
    object.setObjectType("application");
    activity.setTarget(object);
    activity.setGenerator(object);

    // construct expected query
    final Map<String, Object> actMap = new HashMap<String, Object>();
    new ActivityEntryDTO(actMap).setData(activity);

    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.UPDATE_ACT_ENTRY_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeActivityStreamSPITest.JANE_ID);
    exQuery.setParameter(ShindigNativeQueries.APP_ID, "testapp");
    exQuery.setParameter(ShindigNativeQueries.ACTIVITY_ID, "42");
    exQuery.setParameter(ShindigNativeQueries.ACTIVITY_ENTRY_OBJECT, actMap);

    // construct expected result
    final SingleResult exResult = new SingleResult(actMap);

    // create single-use handler and service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    final WsNativeActivityStreamSPI actSPI = new WsNativeActivityStreamSPI(qHandler);

    // execute and check result
    final ActivityEntry entry = actSPI.updateActivityEntry(userId, null, "testapp", null, activity,
            "42", null).get();

    Assert.assertEquals("42", entry.getId());
    Assert.assertEquals("create", entry.getVerb());
  }

  /**
   * Tests deletion of certain activity entries for a person.
   *
   * @throws Exception
   *           if an exception occurs
   */
  @Test
  public void deletionTest() throws Exception {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.DELETE_ACT_ENTRIES_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeActivityStreamSPITest.JANE_ID);
    exQuery.setParameter(ShindigNativeQueries.APP_ID, "testapp");

    final List<String> idList = new ArrayList<String>();
    idList.add("42");
    exQuery.setParameter(ShindigNativeQueries.ACTIVITY_IDS, idList);

    // create single-use handler and service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, new SingleResult(null));
    final WsNativeActivityStreamSPI actSPI = new WsNativeActivityStreamSPI(qHandler);

    // delete entry
    final UserId userId = new UserId(UserId.Type.userId, WsNativeActivityStreamSPITest.JANE_ID);
    final Set<String> actIDs = new HashSet<String>();
    actIDs.add("42");

    actSPI.deleteActivityEntries(userId, null, "testapp", actIDs, null);
  }
}
