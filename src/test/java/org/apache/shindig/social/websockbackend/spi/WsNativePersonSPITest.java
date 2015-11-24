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
import java.util.concurrent.Future;

import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.core.model.NameImpl;
import org.apache.shindig.social.core.model.PersonImpl;
import org.apache.shindig.social.opensocial.model.Name;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.websockbackend.WebsockConfig;
import org.apache.shindig.social.websockbackend.events.ShindigEventBus;
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
 * Test for the PersonService implementation of the websocket back-end.
 */
public class WsNativePersonSPITest {
  private static final String JOHN_ID = "john.doe", JANE_ID = "jane.doe", JACK_ID = "jack.doe",
          HORST_ID = "horst", FRED_ID = "FRED";

  private static final String DOE_FAM_ID = "fam.doe.group";

  private Map<String, Object> fJohn, fJane, fJack, fHorst;

  /**
   * Sets up some test data.
   */
  @Before
  public void setupData() {
    // people
    this.fJohn = new HashMap<String, Object>();
    this.fJohn.put(Person.Field.ID.toString(), WsNativePersonSPITest.JOHN_ID);
    this.fJohn.put(Name.Field.FORMATTED.toString(), "John Doe");
    this.fJohn.put(Name.Field.GIVEN_NAME.toString(), "John");
    this.fJohn.put(Name.Field.FAMILY_NAME.toString(), "Doe");

    this.fJane = new HashMap<String, Object>();
    this.fJane.put(Person.Field.ID.toString(), WsNativePersonSPITest.JANE_ID);
    this.fJane.put(Name.Field.FORMATTED.toString(), "Jane Doe");
    this.fJane.put(Name.Field.GIVEN_NAME.toString(), "Jane");
    this.fJane.put(Name.Field.FAMILY_NAME.toString(), "Doe");

    this.fJack = new HashMap<String, Object>();
    this.fJack.put(Person.Field.ID.toString(), WsNativePersonSPITest.JACK_ID);
    this.fJack.put(Name.Field.FORMATTED.toString(), "Jack Doe");
    this.fJack.put(Name.Field.GIVEN_NAME.toString(), "Jack");
    this.fJack.put(Name.Field.FAMILY_NAME.toString(), "Doe");
    this.fJack.put(Person.Field.BIRTHDAY.toString(), new Long(0));

    this.fHorst = new HashMap<String, Object>();
    this.fHorst.put(Person.Field.ID.toString(), WsNativePersonSPITest.HORST_ID);
    this.fHorst.put(Name.Field.FORMATTED.toString(), "Horst");
    this.fHorst.put(Name.Field.GIVEN_NAME.toString(), "Horst");
    this.fHorst.put(Name.Field.FAMILY_NAME.toString(), "Horstsen");
    this.fHorst.put(Person.Field.AGE.toString(), new Integer(60));
  }

  /**
   * Tests single person retrieval. Checks some relevant fields representing categories in the
   * result.
   *
   * @throws Exception
   *           if an exception occurs
   */
  @Test
  public void personRetrievalTest() throws Exception {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.GET_PERSON_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativePersonSPITest.HORST_ID);

    // construct expected result
    final SingleResult exResult = new SingleResult(this.fHorst);

    // create single-use handler and service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    final WebsockConfig config = new WebsockConfig(true);
    final WsNativePersonSPI personSPI = new WsNativePersonSPI(qHandler, config,
            new ShindigEventBus(config));

    // execute
    final Future<Person> horstFut = personSPI.getPerson(new UserId(UserId.Type.userId,
            WsNativePersonSPITest.HORST_ID), null, null);

    final Person horst = horstFut.get();
    Assert.assertEquals(WsNativePersonSPITest.HORST_ID, horst.getId());
    Assert.assertEquals(new Integer(60), horst.getAge());
  }

  /**
   * Tests the retrieval of multiple people, via IDs, relations and groups. Checks the validity of
   * the collections returned.
   *
   * @throws Exception
   *           if an exception occurs
   */
  @Test
  public void peopleRetrievalTest() throws Exception {
    // construct expected query
    WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.GET_PEOPLE_QUERY);

    exQuery.setParameter(ShindigNativeQueries.GROUP_ID, "@self");

    List<String> uidList = new ArrayList<String>();
    uidList.add(WsNativePersonSPITest.JANE_ID);
    uidList.add(WsNativePersonSPITest.JACK_ID);
    uidList.add(WsNativePersonSPITest.HORST_ID);
    exQuery.setParameter(ShindigNativeQueries.USER_ID_LIST, uidList);

    // construct expected result
    List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
    resList.add(this.fJane);
    resList.add(this.fJack);
    resList.add(this.fHorst);
    ListResult exResult = new ListResult(resList);

    // create single-use handler and service
    IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    final WebsockConfig config = new WebsockConfig(true);
    WsNativePersonSPI personSPI = new WsNativePersonSPI(qHandler, config, new ShindigEventBus(
            config));

    // execute
    // IDs (self)
    final Set<UserId> idSet = new HashSet<UserId>();
    idSet.add(new UserId(UserId.Type.userId, WsNativePersonSPITest.JANE_ID));
    idSet.add(new UserId(UserId.Type.userId, WsNativePersonSPITest.JACK_ID));
    idSet.add(new UserId(UserId.Type.userId, WsNativePersonSPITest.HORST_ID));

    GroupId gId = new GroupId(GroupId.Type.self, null);

    Future<RestfulCollection<Person>> peopleColl = personSPI.getPeople(idSet, gId,
            new CollectionOptions(), null, null);
    List<Person> people = peopleColl.get().getList();

    Assert.assertEquals(3, people.size());

    boolean johnFound = false;
    boolean janeFound = false;
    boolean jackFound = false;
    boolean horstFound = false;

    String id = null;
    for (final Person person : people) {
      id = person.getId();
      if (id.equals(WsNativePersonSPITest.JANE_ID)) {
        janeFound = true;
      } else if (id.equals(WsNativePersonSPITest.JACK_ID)) {
        jackFound = true;
      } else if (id.equals(WsNativePersonSPITest.HORST_ID)) {
        horstFound = true;
      } else {
        throw new Exception("unexpected person");
      }
    }
    Assert.assertTrue(janeFound && jackFound && horstFound);

    // friends
    // construct expected query
    exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.GET_PEOPLE_QUERY);

    exQuery.setParameter(ShindigNativeQueries.GROUP_ID, "@friends");

    uidList = new ArrayList<String>();
    uidList.add(WsNativePersonSPITest.JANE_ID);
    exQuery.setParameter(ShindigNativeQueries.USER_ID_LIST, uidList);

    // construct expected result
    resList = new ArrayList<Map<String, Object>>();
    resList.add(this.fJohn);
    resList.add(this.fJack);
    exResult = new ListResult(resList);

    // create single-use handler and service
    qHandler = new TestQueryHandler(exQuery, exResult);
    personSPI = new WsNativePersonSPI(qHandler, config, new ShindigEventBus(config));

    // execute
    idSet.clear();
    idSet.add(new UserId(UserId.Type.userId, WsNativePersonSPITest.JANE_ID));

    gId = new GroupId(GroupId.Type.friends, null);

    peopleColl = personSPI.getPeople(idSet, gId, new CollectionOptions(), null, null);
    people = peopleColl.get().getList();

    Assert.assertEquals(2, people.size());

    johnFound = false;
    jackFound = false;

    for (final Person person : people) {
      id = person.getId();
      if (id.equals(WsNativePersonSPITest.JOHN_ID)) {
        johnFound = true;
      } else if (id.equals(WsNativePersonSPITest.JACK_ID)) {
        jackFound = true;
      } else {
        throw new Exception("unexpected person");
      }
    }
    Assert.assertTrue(johnFound && jackFound);

    // all relations
    // construct expected query
    exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.GET_PEOPLE_QUERY);

    exQuery.setParameter(ShindigNativeQueries.GROUP_ID, "@all");

    uidList = new ArrayList<String>();
    uidList.add(WsNativePersonSPITest.JANE_ID);
    exQuery.setParameter(ShindigNativeQueries.USER_ID_LIST, uidList);

    // construct expected result
    resList = new ArrayList<Map<String, Object>>();
    resList.add(this.fJohn);
    resList.add(this.fJack);
    resList.add(this.fHorst);
    exResult = new ListResult(resList);

    // create single-use handler and service
    qHandler = new TestQueryHandler(exQuery, exResult);
    personSPI = new WsNativePersonSPI(qHandler, config, new ShindigEventBus(config));

    // execute
    gId = new GroupId(GroupId.Type.all, null);

    peopleColl = personSPI.getPeople(idSet, gId, new CollectionOptions(), null, null);
    people = peopleColl.get().getList();

    Assert.assertEquals(3, people.size());

    johnFound = false;
    jackFound = false;
    horstFound = false;

    for (final Person person : people) {
      id = person.getId();
      if (id.equals(WsNativePersonSPITest.JOHN_ID)) {
        johnFound = true;
      } else if (id.equals(WsNativePersonSPITest.JACK_ID)) {
        jackFound = true;
      } else if (id.equals(WsNativePersonSPITest.HORST_ID)) {
        horstFound = true;
      } else {
        throw new Exception("unexpected person");
      }
    }
    Assert.assertTrue(johnFound && jackFound && horstFound);

    // groups
    // construct expected query
    exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.GET_PEOPLE_QUERY);

    exQuery.setParameter(ShindigNativeQueries.GROUP_ID, WsNativePersonSPITest.DOE_FAM_ID);

    uidList = new ArrayList<String>();
    uidList.add(WsNativePersonSPITest.JOHN_ID);
    exQuery.setParameter(ShindigNativeQueries.USER_ID_LIST, uidList);

    // construct expected result
    resList = new ArrayList<Map<String, Object>>();
    resList.add(this.fJohn);
    resList.add(this.fJane);
    resList.add(this.fJack);
    exResult = new ListResult(resList);

    // create single-use handler and service
    qHandler = new TestQueryHandler(exQuery, exResult);
    personSPI = new WsNativePersonSPI(qHandler, config, new ShindigEventBus(config));

    // execute
    idSet.clear();
    idSet.add(new UserId(UserId.Type.userId, WsNativePersonSPITest.JOHN_ID));

    gId = new GroupId(GroupId.Type.objectId, WsNativePersonSPITest.DOE_FAM_ID);

    peopleColl = personSPI.getPeople(idSet, gId, new CollectionOptions(), null, null);
    people = peopleColl.get().getList();

    Assert.assertEquals(3, people.size());

    johnFound = false;
    janeFound = false;
    jackFound = false;

    for (final Person person : people) {
      id = person.getId();
      if (id.equals(WsNativePersonSPITest.JOHN_ID)) {
        johnFound = true;
      } else if (id.equals(WsNativePersonSPITest.JANE_ID)) {
        janeFound = true;
      } else if (id.equals(WsNativePersonSPITest.JACK_ID)) {
        jackFound = true;
      } else {
        throw new Exception("unexpected person");
      }
    }
    Assert.assertTrue(johnFound && janeFound && jackFound);
  }

  /**
   * Tests updating a person.
   */
  @Test
  public void updateTest() throws Exception {
    final Map<String, Object> personMap = new HashMap<String, Object>();
    personMap.put(Person.Field.ID.toString(), WsNativePersonSPITest.HORST_ID);
    personMap.put(Person.Field.NICKNAME.toString(), "horsty");

    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.UPDATE_PERSON_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativePersonSPITest.HORST_ID);
    exQuery.setParameter(ShindigNativeQueries.PERSON_OBJECT, personMap);

    // construct expected response
    final SingleResult exResult = new SingleResult(personMap);

    // create single-use handler and service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    final WebsockConfig config = new WebsockConfig(true);
    final WsNativePersonSPI personSPI = new WsNativePersonSPI(qHandler, config,
            new ShindigEventBus(config));

    // execute
    final Person person = new PersonImpl();
    person.setId(WsNativePersonSPITest.HORST_ID);
    person.setNickname("horsty");

    final Person result = personSPI.updatePerson(
            new UserId(UserId.Type.userId, WsNativePersonSPITest.HORST_ID), person, null).get();

    // check
    Assert.assertEquals(WsNativePersonSPITest.HORST_ID, result.getId());
    Assert.assertEquals("horsty", result.getNickname());
  }

  // extended functionality

  /**
   * Tests the creation of users. Checks the validity of the person returned and whether it can be
   * found.
   *
   * @throws Exception
   *           if an exception occurs
   */
  @Test
  public void personCreationTest() throws Exception {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.CREATE_PERSON_QUERY);

    final Map<String, Object> fred = new HashMap<String, Object>();
    fred.put(Person.Field.ID.toString(), WsNativePersonSPITest.FRED_ID);
    fred.put(Name.Field.GIVEN_NAME.toString(), "Fred");
    fred.put(Name.Field.FAMILY_NAME.toString(), "Edison");
    fred.put(Name.Field.FORMATTED.toString(), "Dr. Fred Edison");
    exQuery.setParameter(ShindigNativeQueries.PERSON_OBJECT, fred);

    // construct expected result
    final SingleResult exResult = new SingleResult(fred);

    // create single-use handler and service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    final WebsockConfig config = new WebsockConfig(true);
    final WsNativePersonSPI personSPI = new WsNativePersonSPI(qHandler, config,
            new ShindigEventBus(config));

    final Person p = new PersonImpl();
    p.setId(WsNativePersonSPITest.FRED_ID);
    final Name name = new NameImpl();
    name.setGivenName("Fred");
    name.setFamilyName("Edison");
    name.setFormatted("Dr. Fred Edison");
    p.setName(name);

    personSPI.createPerson(p, null);
  }

  /**
   * Tests the retrieval of all people.
   */
  @Test
  public void allRetrievalTest() throws Exception {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.GET_ALL_PEOPLE_QUERY);

    // construct expected result
    final List<Map<String, Object>> exResList = new ArrayList<Map<String, Object>>();
    Map<String, Object> persMap = new HashMap<String, Object>();
    persMap.put(Person.Field.ID.toString(), WsNativePersonSPITest.JOHN_ID);
    exResList.add(persMap);

    persMap = new HashMap<String, Object>();
    persMap.put(Person.Field.ID.toString(), WsNativePersonSPITest.JANE_ID);
    exResList.add(persMap);

    persMap = new HashMap<String, Object>();
    persMap.put(Person.Field.ID.toString(), WsNativePersonSPITest.HORST_ID);
    exResList.add(persMap);

    final ListResult exResult = new ListResult(exResList);

    // create single-use handler and service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    final WebsockConfig config = new WebsockConfig(true);
    final WsNativePersonSPI personSPI = new WsNativePersonSPI(qHandler, config,
            new ShindigEventBus(config));

    // execute
    final Future<RestfulCollection<Person>> resFut = personSPI.getAllPeople(
            new CollectionOptions(), null, null);
    final List<Person> resList = resFut.get().getList();

    // check
    Assert.assertEquals(3, resList.size());

    final List<String> foundIds = new ArrayList<String>();
    for (final Person p : resList) {
      foundIds.add(p.getId());
    }

    Assert.assertTrue(foundIds.contains(WsNativePersonSPITest.JOHN_ID));
    Assert.assertTrue(foundIds.contains(WsNativePersonSPITest.JANE_ID));
    Assert.assertTrue(foundIds.contains(WsNativePersonSPITest.HORST_ID));
  }

  /**
   * Tests the deletion of a person.
   */
  @Test
  public void deletionTest() {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.DELETE_PERSON_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativePersonSPITest.HORST_ID);

    // create single-use handler and service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, new WebsockQuery(
            EQueryType.SUCCESS));
    final WebsockConfig config = new WebsockConfig(true);
    final WsNativePersonSPI personSPI = new WsNativePersonSPI(qHandler, config,
            new ShindigEventBus(config));

    // execute
    personSPI.deletePerson(new UserId(UserId.Type.userId, WsNativePersonSPITest.HORST_ID), null);
  }
}
