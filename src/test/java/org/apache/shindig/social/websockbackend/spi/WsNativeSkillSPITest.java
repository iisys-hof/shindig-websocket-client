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
import java.util.List;
import java.util.Map;

import org.apache.shindig.common.testing.FakeGadgetToken;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.opensocial.spi.UserId.Type;
import org.apache.shindig.social.websockbackend.WebsockConfig;
import org.apache.shindig.social.websockbackend.events.ShindigEventBus;
import org.apache.shindig.social.websockbackend.model.ISkillSet;
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
 * Test for the ISkillService implementation of the websocket back-end.
 */
public class WsNativeSkillSPITest {
  private static final String JOHN_ID = "john.doe", JANE_ID = "jane.doe";

  private static final String NEO_SKILL = "Neo4j", JAVA_SKILL = "Java Programming";

  private Map<String, Object> fJohn, fJane;
  private Map<String, Object> fNeo, fJava;

  /**
   * Sets up some test data.
   */
  @Before
  public void setupData() {
    // people
    this.fJohn = new HashMap<String, Object>();
    this.fJohn.put(Person.Field.ID.toString(), WsNativeSkillSPITest.JOHN_ID);

    this.fJane = new HashMap<String, Object>();
    this.fJane.put(Person.Field.ID.toString(), WsNativeSkillSPITest.JANE_ID);

    // skills
    this.fNeo = new HashMap<String, Object>();
    this.fNeo.put("name", WsNativeSkillSPITest.NEO_SKILL);
    this.fNeo.put("confirmed", false);
    List<Map<String, Object>> people = new ArrayList<Map<String, Object>>();
    people.add(this.fJane);
    this.fNeo.put("people", people);

    this.fJava = new HashMap<String, Object>();
    this.fJava.put("name", WsNativeSkillSPITest.JAVA_SKILL);
    this.fJava.put("confirmed", true);
    people = new ArrayList<Map<String, Object>>();
    people.add(this.fJohn);
    people.add(this.fJane);
    this.fJava.put("people", people);
  }

  /**
   * Tests skill autocompletion requests.
   *
   * @throws Exception
   *           if the test fails
   */
  @Test
  public void autocompletionTest() throws Exception {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.GET_SKILL_AUTOCOMPLETION_QUERY);
    exQuery.setParameter(ShindigNativeQueries.AUTOCOMPLETE_FRAGMENT, "a p");

    // construct expected result
    final List<String> autocompRes = new ArrayList<String>();
    autocompRes.add(WsNativeSkillSPITest.JAVA_SKILL);
    final ListResult exResult = new ListResult(autocompRes);

    // create single-use handler and service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    final WebsockConfig config = new WebsockConfig(true);
    final WsNativeSkillSPI skillSPI = new WsNativeSkillSPI(qHandler, config,
            new ShindigEventBus(config));

    // execute
    final List<String> results = skillSPI.getSkillAutocomp("a p", new CollectionOptions(), null)
            .get().getList();

    Assert.assertEquals(1, results.size());
    Assert.assertEquals(WsNativeSkillSPITest.JAVA_SKILL, results.get(0));
  }

  /**
   * Tests skill retrieval requests for people.
   *
   * @throws Exception
   *           if the test fails
   */
  @Test
  public void skillRetrievalTest() throws Exception {

    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.GET_SKILLS_QUERY);
    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeSkillSPITest.JOHN_ID);

    // construct expected result
    final List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
    resList.add(this.fJava);
    resList.add(this.fNeo);
    final ListResult exResult = new ListResult(resList);

    // create single-use handler and service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    final WebsockConfig config = new WebsockConfig(true);
    final WsNativeSkillSPI skillSPI = new WsNativeSkillSPI(qHandler, config,
            new ShindigEventBus(config));

    // execute
    final List<ISkillSet> result = skillSPI
            .getSkills(new UserId(Type.userId, WsNativeSkillSPITest.JOHN_ID),
                    new CollectionOptions(), null).get().getList();

    Assert.assertEquals(2, result.size());

    ISkillSet skillSet = result.get(0);
    Assert.assertEquals(WsNativeSkillSPITest.JAVA_SKILL, skillSet.getName());
    List<? extends Person> linkers = skillSet.getPeople();
    Assert.assertEquals(2, linkers.size());
    Person linker = linkers.get(0);
    Assert.assertEquals(WsNativeSkillSPITest.JOHN_ID, linker.getId());
    linker = linkers.get(1);
    Assert.assertEquals(WsNativeSkillSPITest.JANE_ID, linker.getId());
    Assert.assertTrue(skillSet.getConfirmed());

    skillSet = result.get(1);
    Assert.assertEquals(WsNativeSkillSPITest.NEO_SKILL, skillSet.getName());
    linkers = skillSet.getPeople();
    Assert.assertEquals(1, linkers.size());
    linker = linkers.get(0);
    Assert.assertEquals(WsNativeSkillSPITest.JANE_ID, linker.getId());
    Assert.assertFalse(skillSet.getConfirmed());
  }

  /**
   * Tests requests to add skills to people.
   *
   * @throws Exception
   *           if the test fails
   */
  @Test
  public void skillAddingTest() throws Exception {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.ADD_SKILL_QUERY);
    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeSkillSPITest.JOHN_ID);
    exQuery.setParameter(ShindigNativeQueries.SKILL_LINKER, WsNativeSkillSPITest.JOHN_ID);
    exQuery.setParameter(ShindigNativeQueries.SKILL, WsNativeSkillSPITest.NEO_SKILL);

    final FakeGadgetToken token = new FakeGadgetToken();
    token.setViewerId(WsNativeSkillSPITest.JOHN_ID);

    // construct expected result
    final ListResult exResult = null;

    // create single-use handler and service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    final WebsockConfig config = new WebsockConfig(true);
    final WsNativeSkillSPI skillSPI = new WsNativeSkillSPI(qHandler, config,
            new ShindigEventBus(config));

    // execute
    skillSPI.addSkill(new UserId(Type.userId, WsNativeSkillSPITest.JOHN_ID),
            WsNativeSkillSPITest.NEO_SKILL, token).get();
  }

  /**
   * Tests requests to remove skills from people.
   *
   * @throws Exception
   *           if the test fails
   */
  @Test
  public void skillRemovalTest() throws Exception {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.ADD_SKILL_QUERY);
    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeSkillSPITest.JOHN_ID);
    exQuery.setParameter(ShindigNativeQueries.SKILL_LINKER, WsNativeSkillSPITest.JANE_ID);
    exQuery.setParameter(ShindigNativeQueries.SKILL, WsNativeSkillSPITest.NEO_SKILL);

    final FakeGadgetToken token = new FakeGadgetToken();
    token.setViewerId(WsNativeSkillSPITest.JANE_ID);

    // construct expected result
    final ListResult exResult = null;

    // create single-use handler and service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    final WebsockConfig config = new WebsockConfig(true);
    final WsNativeSkillSPI skillSPI = new WsNativeSkillSPI(qHandler, config,
            new ShindigEventBus(config));

    // execute
    skillSPI.removeSkill(new UserId(Type.userId, WsNativeSkillSPITest.JOHN_ID),
            WsNativeSkillSPITest.NEO_SKILL, token).get();
  }
}
