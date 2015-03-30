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

import org.apache.shindig.protocol.DataCollection;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.opensocial.spi.UserId.Type;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import de.hofuniversity.iisys.neo4j.websock.queries.IQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.queries.TestQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.query.EQueryType;
import de.hofuniversity.iisys.neo4j.websock.query.WebsockQuery;
import de.hofuniversity.iisys.neo4j.websock.result.SingleResult;
import de.hofuniversity.iisys.neo4j.websock.shindig.ShindigNativeQueries;

/**
 * Test for the AppDataService implementation of the websocket back-end.
 */
public class WsNativeAppDataSPITest {
  private static final String JOHN_ID = "john.doe", JANE_ID = "jane.doe", JACK_ID = "jack.doe",
          HORST_ID = "horst";

  private static final String APP1_ID = "app1", APP2_ID = "app2", APP3_ID = "app3";

  private static final String ATT1_ID = "attribute1", ATT2_ID = "attribute2",
          ATT3_ID = "attribute3", ATT4_ID = "attribute4";

  private Map<String, Map<String, Map<String, Object>>> fDataByUser;

  /**
   * Sets up some test data.
   */
  @Before
  public void setupData() {
    this.fDataByUser = new HashMap<String, Map<String, Map<String, Object>>>();

    // people
    final Map<String, Map<String, Object>> johndoe = new HashMap<String, Map<String, Object>>();
    this.fDataByUser.put(WsNativeAppDataSPITest.JOHN_ID, johndoe);

    final Map<String, Map<String, Object>> janedoe = new HashMap<String, Map<String, Object>>();
    this.fDataByUser.put(WsNativeAppDataSPITest.JANE_ID, janedoe);

    final Map<String, Map<String, Object>> jackdoe = new HashMap<String, Map<String, Object>>();
    this.fDataByUser.put(WsNativeAppDataSPITest.JACK_ID, jackdoe);

    final Map<String, Map<String, Object>> horst = new HashMap<String, Map<String, Object>>();
    this.fDataByUser.put(WsNativeAppDataSPITest.HORST_ID, horst);

    // application data
    final Map<String, Object> johnApp1Data = new HashMap<String, Object>();
    johnApp1Data.put(WsNativeAppDataSPITest.ATT1_ID, "johndoe");
    johnApp1Data.put(WsNativeAppDataSPITest.ATT2_ID, "father");
    johndoe.put(WsNativeAppDataSPITest.APP1_ID, johnApp1Data);

    final Map<String, Object> johnApp2Data = new HashMap<String, Object>();
    johnApp2Data.put(WsNativeAppDataSPITest.ATT1_ID, "john.doe");
    johnApp2Data.put(WsNativeAppDataSPITest.ATT2_ID, "admin");
    johndoe.put(WsNativeAppDataSPITest.APP2_ID, johnApp2Data);

    final Map<String, Object> janeApp1Data = new HashMap<String, Object>();
    janeApp1Data.put(WsNativeAppDataSPITest.ATT1_ID, "janedoe");
    janeApp1Data.put(WsNativeAppDataSPITest.ATT2_ID, "mother");
    janedoe.put(WsNativeAppDataSPITest.APP1_ID, janeApp1Data);

    final Map<String, Object> janeApp2Data = new HashMap<String, Object>();
    janeApp2Data.put(WsNativeAppDataSPITest.ATT1_ID, "jane.doe");
    janeApp2Data.put(WsNativeAppDataSPITest.ATT2_ID, "manager");
    janedoe.put(WsNativeAppDataSPITest.APP2_ID, janeApp2Data);

    final Map<String, Object> jackApp1Data = new HashMap<String, Object>();
    jackApp1Data.put(WsNativeAppDataSPITest.ATT1_ID, "jackdoe");
    jackApp1Data.put(WsNativeAppDataSPITest.ATT2_ID, "son");
    jackdoe.put(WsNativeAppDataSPITest.APP1_ID, jackApp1Data);

    final Map<String, Object> jackApp2Data = new HashMap<String, Object>();
    jackApp2Data.put(WsNativeAppDataSPITest.ATT1_ID, "jack.doe");
    jackApp2Data.put(WsNativeAppDataSPITest.ATT2_ID, "user");
    jackdoe.put(WsNativeAppDataSPITest.APP2_ID, jackApp2Data);

    final Map<String, Object> horstApp1Data = new HashMap<String, Object>();
    horstApp1Data.put(WsNativeAppDataSPITest.ATT1_ID, "horst");
    horstApp1Data.put(WsNativeAppDataSPITest.ATT2_ID, "unrelated");
    horst.put(WsNativeAppDataSPITest.APP1_ID, horstApp1Data);

    final Map<String, Object> horstApp2Data = new HashMap<String, Object>();
    horstApp2Data.put(WsNativeAppDataSPITest.ATT1_ID, "horst");
    horstApp2Data.put(WsNativeAppDataSPITest.ATT2_ID, "viewer");
    horst.put(WsNativeAppDataSPITest.APP2_ID, horstApp2Data);
  }

  /**
   * Test for the retrieval of application data.
   *
   * @throws Exception
   *           if the test fails
   */
  @Test
  public void retrievalTest() throws Exception {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.GET_APP_DATA_QUERY);

    exQuery.setParameter(ShindigNativeQueries.APP_ID, WsNativeAppDataSPITest.APP1_ID);

    final List<String> uidList = new ArrayList<String>();
    uidList.add(WsNativeAppDataSPITest.JOHN_ID);
    exQuery.setParameter(ShindigNativeQueries.USER_ID_LIST, uidList);

    // construct expected result
    Map<String, Map<String, Object>> appDataMap = new HashMap<String, Map<String, Object>>();
    appDataMap.put(WsNativeAppDataSPITest.JOHN_ID,
            this.fDataByUser.get(WsNativeAppDataSPITest.JOHN_ID)
                    .get(WsNativeAppDataSPITest.APP1_ID));
    SingleResult exResult = new SingleResult(appDataMap);

    // create single-use handler and service
    IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    WsNativeAppDataSPI appDataSPI = new WsNativeAppDataSPI(qHandler);

    // execute
    final Set<UserId> userIds = new HashSet<UserId>();
    DataCollection result = null;
    Map<String, Map<String, Object>> data = null;
    Map<String, Object> appData = null;

    // single user
    userIds.add(new UserId(Type.userId, WsNativeAppDataSPITest.JOHN_ID));

    result = appDataSPI.getPersonData(userIds, null, WsNativeAppDataSPITest.APP1_ID, null, null)
            .get();
    data = result.getEntry();
    appData = data.get(WsNativeAppDataSPITest.JOHN_ID);

    Assert.assertEquals(1, data.size());
    Assert.assertEquals(2, appData.size());

    Assert.assertEquals("johndoe", appData.get(WsNativeAppDataSPITest.ATT1_ID));
    Assert.assertEquals("father", appData.get(WsNativeAppDataSPITest.ATT2_ID));

    // multiple users
    // construct expected query
    exQuery.setParameter(ShindigNativeQueries.APP_ID, WsNativeAppDataSPITest.APP2_ID);

    uidList.add(WsNativeAppDataSPITest.JANE_ID);
    uidList.add(WsNativeAppDataSPITest.HORST_ID);
    exQuery.setParameter(ShindigNativeQueries.USER_ID_LIST, uidList);

    // construct expected result
    appDataMap = new HashMap<String, Map<String, Object>>();
    appDataMap.put(WsNativeAppDataSPITest.JOHN_ID,
            this.fDataByUser.get(WsNativeAppDataSPITest.JOHN_ID)
                    .get(WsNativeAppDataSPITest.APP2_ID));
    appDataMap.put(WsNativeAppDataSPITest.JANE_ID,
            this.fDataByUser.get(WsNativeAppDataSPITest.JANE_ID)
                    .get(WsNativeAppDataSPITest.APP2_ID));
    appDataMap.put(
            WsNativeAppDataSPITest.HORST_ID,
            this.fDataByUser.get(WsNativeAppDataSPITest.HORST_ID).get(
                    WsNativeAppDataSPITest.APP2_ID));
    exResult = new SingleResult(appDataMap);

    // create single-use handler and service
    qHandler = new TestQueryHandler(exQuery, exResult);
    appDataSPI = new WsNativeAppDataSPI(qHandler);

    // execute
    userIds.add(new UserId(Type.userId, WsNativeAppDataSPITest.JANE_ID));
    userIds.add(new UserId(Type.userId, WsNativeAppDataSPITest.HORST_ID));

    result = appDataSPI.getPersonData(userIds, null, WsNativeAppDataSPITest.APP2_ID, null, null)
            .get();
    data = result.getEntry();

    Assert.assertEquals(3, data.size());

    appData = data.get(WsNativeAppDataSPITest.JOHN_ID);
    Assert.assertEquals(2, appData.size());
    Assert.assertEquals("john.doe", appData.get(WsNativeAppDataSPITest.ATT1_ID));
    Assert.assertEquals("admin", appData.get(WsNativeAppDataSPITest.ATT2_ID));

    appData = data.get(WsNativeAppDataSPITest.JANE_ID);
    Assert.assertEquals(2, appData.size());
    Assert.assertEquals("jane.doe", appData.get(WsNativeAppDataSPITest.ATT1_ID));
    Assert.assertEquals("manager", appData.get(WsNativeAppDataSPITest.ATT2_ID));

    appData = data.get(WsNativeAppDataSPITest.HORST_ID);
    Assert.assertEquals(2, appData.size());
    Assert.assertEquals("horst", appData.get(WsNativeAppDataSPITest.ATT1_ID));
    Assert.assertEquals("viewer", appData.get(WsNativeAppDataSPITest.ATT2_ID));
  }

  /**
   * Test for the storage of application data.
   *
   * @throws Exception
   *           if the test fails
   */
  @Test
  public void storageTest() throws Exception {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.UPDATE_APP_DATA_QUERY);

    exQuery.setParameter(ShindigNativeQueries.APP_ID, WsNativeAppDataSPITest.APP3_ID);
    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeAppDataSPITest.HORST_ID);

    final Map<String, Object> data = new HashMap<String, Object>();
    data.put(WsNativeAppDataSPITest.ATT3_ID, "firstValue");
    data.put(WsNativeAppDataSPITest.ATT4_ID, "firstValue");
    exQuery.setParameter(ShindigNativeQueries.APP_DATA, data);

    // construct expected result
    final SingleResult exResult = new SingleResult(null);

    // create single-use handler and service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    final WsNativeAppDataSPI appDataSPI = new WsNativeAppDataSPI(qHandler);

    // execute
    final UserId horstId = new UserId(Type.userId, WsNativeAppDataSPITest.HORST_ID);

    // non-existent attributes
    final Map<String, Object> values = new HashMap<String, Object>();
    values.put(WsNativeAppDataSPITest.ATT3_ID, "firstValue");
    values.put(WsNativeAppDataSPITest.ATT4_ID, "firstValue");

    appDataSPI.updatePersonData(horstId, null, WsNativeAppDataSPITest.APP3_ID, null, values, null);
  }

  /**
   * Test for the deletion of application data.
   *
   * @throws Exception
   *           if the test fails
   */
  @Test
  public void deletionTest() throws Exception {
    // construct expected query
    WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.DELETE_APP_DATA_QUERY);

    exQuery.setParameter(ShindigNativeQueries.APP_ID, WsNativeAppDataSPITest.APP3_ID);
    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeAppDataSPITest.HORST_ID);

    final List<String> fieldList = new ArrayList<String>();
    fieldList.add(WsNativeAppDataSPITest.ATT3_ID);
    exQuery.setParameter(ShindigNativeQueries.FIELD_LIST, fieldList);

    // construct expected result
    final SingleResult exResult = new SingleResult(null);

    // create single-use handler and service
    IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    WsNativeAppDataSPI appDataSPI = new WsNativeAppDataSPI(qHandler);

    // execute
    final UserId horstId = new UserId(Type.userId, WsNativeAppDataSPITest.HORST_ID);

    // partial deletion
    final Set<String> fields = ImmutableSet.of(WsNativeAppDataSPITest.ATT3_ID);
    appDataSPI.deletePersonData(horstId, null, WsNativeAppDataSPITest.APP3_ID, fields, null);

    // full deletion
    // construct expected query
    exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.DELETE_APP_DATA_QUERY);

    exQuery.setParameter(ShindigNativeQueries.APP_ID, WsNativeAppDataSPITest.APP3_ID);
    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeAppDataSPITest.HORST_ID);

    // create single-use handler and service
    qHandler = new TestQueryHandler(exQuery, exResult);
    appDataSPI = new WsNativeAppDataSPI(qHandler);

    // execute
    appDataSPI.deletePersonData(horstId, null, WsNativeAppDataSPITest.APP3_ID, null, null);
  }
}
