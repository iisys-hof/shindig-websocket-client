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
import java.util.concurrent.Future;

import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.opensocial.model.Group;
import org.apache.shindig.social.opensocial.model.Name;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.UserId;
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
 * Test for the GroupService implementation of the websocket back-end.
 */
public class WsNativeGroupSPITest {
  private static final String JOHN_ID = "john.doe", JANE_ID = "jane.doe", HORST_ID = "horst";

  private static final String DOE_FAM_ID = "fam.doe.group", DENKER_ID = "denker";

  private Map<String, Object> fFamGroup, fDenkGroup;

  /**
   * Sets up an some test data.
   */
  @Before
  public void setupData() {
    // groups
    this.fFamGroup = new HashMap<String, Object>();
    this.fFamGroup.put(Group.Field.ID.toString(), WsNativeGroupSPITest.DOE_FAM_ID);
    this.fFamGroup.put(Group.Field.DESCRIPTION.toString(),
            "private group for members of the Doe family");
    this.fFamGroup.put(Group.Field.TITLE.toString(), "Doe family");

    this.fDenkGroup = new HashMap<String, Object>();
    this.fDenkGroup.put(Group.Field.ID.toString(), WsNativeGroupSPITest.DENKER_ID);
    this.fDenkGroup.put(Group.Field.TITLE.toString(), "Club der Denker");

    // people
    final Map<String, Object> johndoe = new HashMap<String, Object>();
    johndoe.put(Person.Field.ID.toString(), WsNativeGroupSPITest.JOHN_ID);
    johndoe.put(Name.Field.FORMATTED.toString(), "John Doe");
    johndoe.put(Name.Field.GIVEN_NAME.toString(), "John");
    johndoe.put(Name.Field.FAMILY_NAME.toString(), "Doe");

    final Map<String, Object> janedoe = new HashMap<String, Object>();
    janedoe.put(Person.Field.ID.toString(), WsNativeGroupSPITest.JANE_ID);
    janedoe.put(Name.Field.FORMATTED.toString(), "Jane Doe");
    janedoe.put(Name.Field.GIVEN_NAME.toString(), "Jane");
    janedoe.put(Name.Field.FAMILY_NAME.toString(), "Doe");

    final Map<String, Object> horst = new HashMap<String, Object>();
    horst.put(Person.Field.ID.toString(), WsNativeGroupSPITest.HORST_ID);
    horst.put(Name.Field.FORMATTED.toString(), "Horst");
    horst.put(Name.Field.GIVEN_NAME.toString(), "Horst");
    horst.put(Name.Field.FAMILY_NAME.toString(), "Horstsen");
  }

  /**
   * Tests group retrieval for single people.
   *
   * @throws Exception
   *           if an exception occurs
   */
  @Test
  public void groupRetrievalTest() throws Exception {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.GET_GROUPS_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeGroupSPITest.JANE_ID);

    // construct expected result
    final List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
    resList.add(this.fFamGroup);
    resList.add(this.fDenkGroup);
    ListResult exResult = new ListResult(resList);

    // create single-use handler and service
    IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    WsNativeGroupSPI groupSPI = new WsNativeGroupSPI(qHandler);

    // Jane - member in both groups
    Future<RestfulCollection<Group>> groupColl = groupSPI.getGroups(new UserId(UserId.Type.userId,
            WsNativeGroupSPITest.JANE_ID), new CollectionOptions(), null, null);
    List<Group> groups = groupColl.get().getList();

    Assert.assertEquals(2, groups.size());

    boolean famFound = false;
    boolean denkFound = false;

    String id = null;
    for (final Group group : groups) {
      id = group.getId();

      if (id.equals(WsNativeGroupSPITest.DOE_FAM_ID)) {
        famFound = true;

        Assert.assertEquals("Doe family", group.getTitle());
      } else if (id.equals(WsNativeGroupSPITest.DENKER_ID)) {
        denkFound = true;

        Assert.assertEquals("Club der Denker", group.getTitle());
      } else {
        throw new Exception("unexpected group");
      }
    }
    Assert.assertTrue(famFound && denkFound);

    // Horst - not in any group
    // construct expected query
    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeGroupSPITest.HORST_ID);

    // construct expected result
    exResult = new ListResult((List<?>) null);

    // create single-use handler and service
    qHandler = new TestQueryHandler(exQuery, exResult);
    groupSPI = new WsNativeGroupSPI(qHandler);

    // execute
    groupColl = groupSPI.getGroups(new UserId(UserId.Type.userId, WsNativeGroupSPITest.HORST_ID),
            new CollectionOptions(), null, null);
    groups = groupColl.get().getList();

    Assert.assertEquals(0, groups.size());
  }
}
