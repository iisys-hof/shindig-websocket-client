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
import org.apache.shindig.social.core.model.MessageCollectionImpl;
import org.apache.shindig.social.core.model.MessageImpl;
import org.apache.shindig.social.opensocial.model.Message;
import org.apache.shindig.social.opensocial.model.MessageCollection;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.websockbackend.Constants;
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
 * Test for the MessageService implementation of the websocket back-end.
 */
public class WsNativeMessageSPITest {
  private static final String JOHN_ID = "john.doe", JANE_ID = "jane.doe", HORST_ID = "horst";

  private Map<String, Object> fMess1, fMess2, fMess3, fMess4;

  /**
   * Sets up an some test data.
   */
  @Before
  public void setupData() {
    // messages
    this.fMess1 = new HashMap<String, Object>();
    this.fMess1.put(Message.Field.ID.toString(), "1");
    this.fMess1.put(Message.Field.TITLE.toString(), "Hallo Horst");

    this.fMess2 = new HashMap<String, Object>();
    this.fMess2.put(Message.Field.ID.toString(), "2");
    this.fMess2.put(Message.Field.TITLE.toString(), "Hallo Familie");

    this.fMess3 = new HashMap<String, Object>();
    this.fMess3.put(Message.Field.ID.toString(), "3");
    this.fMess3.put(Message.Field.TITLE.toString(), "Liebe Kollegen");

    this.fMess4 = new HashMap<String, Object>();
    this.fMess4.put(Message.Field.ID.toString(), "4");
    this.fMess4.put(Message.Field.TITLE.toString(), "Hallo Mutti");
  }

  /**
   * Tests the retrieval of message collections.
   *
   * @throws Exception
   *           if an exception occurs
   */
  @Test
  public void collectionRetrievalTest() throws Exception {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.GET_MESSAGE_COLLECTIONS_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeMessageSPITest.JOHN_ID);

    // construct expected result
    final List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
    final Map<String, Object> johnIn = new HashMap<String, Object>();
    johnIn.put(MessageCollection.Field.ID.toString(), Constants.INBOX_NAME);
    johnIn.put(MessageCollection.Field.TITLE.toString(), "John's inbox");
    resList.add(johnIn);

    final Map<String, Object> johnOut = new HashMap<String, Object>();
    johnOut.put(MessageCollection.Field.ID.toString(), MessageCollection.OUTBOX);
    johnOut.put(MessageCollection.Field.TITLE.toString(), "John's outbox");
    resList.add(johnOut);

    final ListResult exResult = new ListResult(resList);

    // create single-use handler and service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    final WebsockConfig config = new WebsockConfig(true);
    final WsNativeMessageSPI messageSPI = new WsNativeMessageSPI(qHandler, config,
            new ShindigEventBus(config));

    // execute
    final Future<RestfulCollection<MessageCollection>> colls = messageSPI.getMessageCollections(
            new UserId(UserId.Type.userId, WsNativeMessageSPITest.JOHN_ID), null,
            new CollectionOptions(), null);

    final List<MessageCollection> messColls = colls.get().getList();

    Assert.assertEquals(2, messColls.size());

    boolean inFound = false;
    boolean outFound = false;

    String name = null;
    for (final MessageCollection c : messColls) {
      name = c.getTitle();

      if (name.equals("John's inbox")) {
        inFound = true;
      } else if (name.equals("John's outbox")) {
        outFound = true;
      } else {
        throw new Exception("unexpected collection");
      }
    }
    Assert.assertTrue(inFound && outFound);
  }

  /**
   * Tests the creation of a message collection.
   */
  @Test
  public void collectionCreationTest() throws Exception {
    final Map<String, Object> messCollMap = new HashMap<String, Object>();
    messCollMap.put(MessageCollection.Field.ID.toString(), "newId");
    messCollMap.put(MessageCollection.Field.TITLE.toString(), "newTitle");

    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.CREATE_MESS_COLL_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeMessageSPITest.JOHN_ID);
    exQuery.setParameter(ShindigNativeQueries.MESSAGE_COLLECTION_OBJECT, messCollMap);

    // construct expected result
    final SingleResult exResult = new SingleResult(messCollMap);

    // create single-use handler and service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    final WebsockConfig config = new WebsockConfig(true);
    final WsNativeMessageSPI messageSPI = new WsNativeMessageSPI(qHandler, config,
            new ShindigEventBus(config));

    // execute
    final MessageCollection msgCollection = new MessageCollectionImpl();
    msgCollection.setId("newId");
    msgCollection.setTitle("newTitle");

    final MessageCollection result = messageSPI.createMessageCollection(
            new UserId(UserId.Type.userId, WsNativeMessageSPITest.JOHN_ID), msgCollection, null)
            .get();

    // check result
    Assert.assertEquals("newId", result.getId());
    Assert.assertEquals("newTitle", result.getTitle());
  }

  /**
   * Tests the creation of a message collection.
   */
  @Test
  public void collectionModificationTest() {
    final Map<String, Object> messCollMap = new HashMap<String, Object>();
    messCollMap.put(MessageCollection.Field.ID.toString(), "newId");
    messCollMap.put(MessageCollection.Field.TITLE.toString(), "newTitle");

    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.MODIFY_MESS_COLL_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeMessageSPITest.JOHN_ID);
    exQuery.setParameter(ShindigNativeQueries.MESSAGE_COLLECTION_OBJECT, messCollMap);

    // create single-use handler and service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, new WebsockQuery(
            EQueryType.SUCCESS));
    final WebsockConfig config = new WebsockConfig(true);
    final WsNativeMessageSPI messageSPI = new WsNativeMessageSPI(qHandler, config,
            new ShindigEventBus(config));

    // execute
    final MessageCollection msgCollection = new MessageCollectionImpl();
    msgCollection.setId("newId");
    msgCollection.setTitle("newTitle");

    messageSPI.modifyMessageCollection(new UserId(UserId.Type.userId,
            WsNativeMessageSPITest.JOHN_ID), msgCollection, null);
  }

  /**
   * Tests the deletion of a message collection.
   */
  @Test
  public void collectionDeletionTest() {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.DELETE_MESS_COLL_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeMessageSPITest.JOHN_ID);
    exQuery.setParameter(ShindigNativeQueries.MESSAGE_COLLECTION_ID, "newId");

    // create single-use handler and service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, new WebsockQuery(
            EQueryType.SUCCESS));
    final WebsockConfig config = new WebsockConfig(true);
    final WsNativeMessageSPI messageSPI = new WsNativeMessageSPI(qHandler, config,
            new ShindigEventBus(config));

    // execute
    messageSPI.deleteMessageCollection(new UserId(UserId.Type.userId,
            WsNativeMessageSPITest.JOHN_ID), "newId", null);
  }

  /**
   * Tests the retrieval of message collections.
   *
   * @throws Exception
   *           if an exception occurs
   */
  @Test
  public void messageRetrievalTest() throws Exception {
    // construct expected query
    WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.GET_MESSAGES_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeMessageSPITest.JANE_ID);
    exQuery.setParameter(ShindigNativeQueries.MESSAGE_COLLECTION_ID, MessageCollection.ALL);

    // construct expected result
    List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
    resList.add(this.fMess1);
    resList.add(this.fMess2);
    resList.add(this.fMess3);
    resList.add(this.fMess4);
    ListResult exResult = new ListResult(resList);

    // create single-use handler and service
    IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    final WebsockConfig config = new WebsockConfig(true);
    WsNativeMessageSPI messageSPI = new WsNativeMessageSPI(qHandler, config, new ShindigEventBus(
            config));

    // retrieve all
    Future<RestfulCollection<Message>> messColl = messageSPI.getMessages(new UserId(
            UserId.Type.userId, WsNativeMessageSPITest.JANE_ID), MessageCollection.ALL, null, null,
            new CollectionOptions(), null);

    List<Message> messages = messColl.get().getList();

    Assert.assertEquals(4, messages.size());

    final boolean found[] = { false, false, false, false };
    for (final Message m : messages) {
      if (m.getId().equals("1")) {
        found[0] = true;
      } else if (m.getId().equals("2")) {
        found[1] = true;
      } else if (m.getId().equals("3")) {
        found[2] = true;
      } else if (m.getId().equals("4")) {
        found[3] = true;
      }
    }
    Assert.assertTrue(found[0] && found[1] && found[2] && found[3]);

    // by collection
    // construct expected query
    exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.GET_MESSAGES_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeMessageSPITest.JANE_ID);
    exQuery.setParameter(ShindigNativeQueries.MESSAGE_COLLECTION_ID, Constants.INBOX_NAME);

    // construct expected result
    resList = new ArrayList<Map<String, Object>>();
    resList.add(this.fMess2);
    resList.add(this.fMess4);
    exResult = new ListResult(resList);

    // create single-use handler and service
    qHandler = new TestQueryHandler(exQuery, exResult);
    messageSPI = new WsNativeMessageSPI(qHandler, config, new ShindigEventBus(config));

    // execute
    messColl = messageSPI.getMessages(
            new UserId(UserId.Type.userId, WsNativeMessageSPITest.JANE_ID), Constants.INBOX_NAME,
            null, null, new CollectionOptions(), null);

    messages = messColl.get().getList();

    Assert.assertEquals(2, messages.size());

    found[1] = false;
    found[3] = false;
    for (final Message m : messages) {
      if (m.getId().equals("2")) {
        found[1] = true;
      } else if (m.getId().equals("4")) {
        found[3] = true;
      } else {
        throw new Exception("unexpected message " + m.getId());
      }
    }
    Assert.assertTrue(found[1] && found[3]);

    // by id
    // construct expected query
    exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.GET_MESSAGES_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeMessageSPITest.JANE_ID);
    exQuery.setParameter(ShindigNativeQueries.MESSAGE_COLLECTION_ID, MessageCollection.ALL);

    final List<String> messIds = new ArrayList<String>();
    messIds.add("1");
    messIds.add("4");
    exQuery.setParameter(ShindigNativeQueries.MESSAGE_ID_LIST, messIds);

    // construct expected result
    resList = new ArrayList<Map<String, Object>>();
    resList.add(this.fMess1);
    resList.add(this.fMess4);
    exResult = new ListResult(resList);

    // create single-use handler and service
    qHandler = new TestQueryHandler(exQuery, exResult);
    messageSPI = new WsNativeMessageSPI(qHandler, config, new ShindigEventBus(config));

    // execute

    messColl = messageSPI.getMessages(
            new UserId(UserId.Type.userId, WsNativeMessageSPITest.JANE_ID), MessageCollection.ALL,
            null, messIds, new CollectionOptions(), null);

    messages = messColl.get().getList();

    Assert.assertEquals(2, messages.size());

    found[0] = false;
    found[3] = false;
    for (final Message m : messages) {
      if (m.getId().equals("1")) {
        found[0] = true;
      } else if (m.getId().equals("4")) {
        found[3] = true;
      } else {
        throw new Exception("unexpected message " + m.getId());
      }
    }
    Assert.assertTrue(found[0] && found[3]);
  }

  /**
   * Tests the creation/sending of new messages to one or more recipients.
   *
   * @throws Exception
   *           if an exception occurs
   */
  @Test
  public void messageSendTest() throws Exception {
    // construct expected query
    WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.CREATE_MESSAGE_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeMessageSPITest.JANE_ID);
    exQuery.setParameter(ShindigNativeQueries.MESSAGE_COLLECTION_ID, MessageCollection.OUTBOX);

    Map<String, Object> msgObj = new HashMap<String, Object>();
    msgObj.put(Message.Field.SENDER_ID.toString(), WsNativeMessageSPITest.JANE_ID);
    msgObj.put(Message.Field.TITLE.toString(), "testmessage1");
    msgObj.put(Message.Field.RECIPIENTS.toString(),
            new String[] { WsNativeMessageSPITest.HORST_ID });
    exQuery.setParameter(ShindigNativeQueries.MESSAGE_OBJECT, msgObj);

    // construct expected result
    final SingleResult exResult = new SingleResult(msgObj);

    // create single-use handler and service
    IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    final WebsockConfig config = new WebsockConfig(true);
    WsNativeMessageSPI messageSPI = new WsNativeMessageSPI(qHandler, config, new ShindigEventBus(
            config));

    // sending to single person
    List<String> recipients = new ArrayList<String>();
    recipients.add(WsNativeMessageSPITest.HORST_ID);

    Message testMsg = new MessageImpl();
    testMsg.setTitle("testmessage1");
    testMsg.setSenderId(WsNativeMessageSPITest.JANE_ID);
    testMsg.setRecipients(recipients);

    messageSPI.createMessage(new UserId(UserId.Type.userId, WsNativeMessageSPITest.JANE_ID), null,
            MessageCollection.OUTBOX, testMsg, null);

    // sending to multiple people
    // construct expected query
    exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.CREATE_MESSAGE_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeMessageSPITest.JANE_ID);
    exQuery.setParameter(ShindigNativeQueries.MESSAGE_COLLECTION_ID, MessageCollection.OUTBOX);

    msgObj = new HashMap<String, Object>();
    msgObj.put(Message.Field.SENDER_ID.toString(), WsNativeMessageSPITest.JANE_ID);
    msgObj.put(Message.Field.TITLE.toString(), "testmessage2");
    msgObj.put(Message.Field.RECIPIENTS.toString(), new String[] { WsNativeMessageSPITest.HORST_ID,
            WsNativeMessageSPITest.JOHN_ID });
    exQuery.setParameter(ShindigNativeQueries.MESSAGE_OBJECT, msgObj);

    // create single-use handler and service
    qHandler = new TestQueryHandler(exQuery, exResult);
    messageSPI = new WsNativeMessageSPI(qHandler, config, new ShindigEventBus(config));

    // execute
    recipients = new ArrayList<String>();
    recipients.add(WsNativeMessageSPITest.HORST_ID);
    recipients.add(WsNativeMessageSPITest.JOHN_ID);

    testMsg = new MessageImpl();
    testMsg.setTitle("testmessage2");
    testMsg.setSenderId(WsNativeMessageSPITest.JANE_ID);
    testMsg.setRecipients(recipients);

    messageSPI.createMessage(new UserId(UserId.Type.userId, WsNativeMessageSPITest.JANE_ID), null,
            MessageCollection.OUTBOX, testMsg, null);

  }

  /**
   * Tests the modification of a message.
   */
  @Test
  public void messageModificationTest() {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.CREATE_MESSAGE_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeMessageSPITest.JANE_ID);
    exQuery.setParameter(ShindigNativeQueries.MESSAGE_COLLECTION_ID, "collId");

    final Map<String, Object> msgObj = new HashMap<String, Object>();
    msgObj.put(Message.Field.ID.toString(), "newId");
    msgObj.put(Message.Field.TITLE.toString(), "testmessage1");
    msgObj.put(Message.Field.COLLECTION_IDS.toString(), new String[] { "collId" });
    exQuery.setParameter(ShindigNativeQueries.MESSAGE_OBJECT, msgObj);

    // create single-use handler and service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, new WebsockQuery(
            EQueryType.SUCCESS));
    final WebsockConfig config = new WebsockConfig(true);
    final WsNativeMessageSPI messageSPI = new WsNativeMessageSPI(qHandler, config,
            new ShindigEventBus(config));

    // execute
    final Message message = new MessageImpl();
    message.setId("newId");
    message.setTitle("testmessage1");
    final List<String> collIds = new ArrayList<String>();
    collIds.add("collId");
    message.setCollectionIds(collIds);

    messageSPI.modifyMessage(new UserId(UserId.Type.userId, WsNativeMessageSPITest.JANE_ID),
            "collId", "newId", message, null);
  }

  /**
   * Tests the deletion of new messages to one or more recipients.
   *
   * @throws Exception
   *           if an exception occurs
   */
  @Test
  public void messageDeletionTest() throws Exception {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.DELETE_MESSAGES_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeMessageSPITest.JANE_ID);
    exQuery.setParameter(ShindigNativeQueries.MESSAGE_COLLECTION_ID, MessageCollection.OUTBOX);

    final List<String> mIds = new ArrayList<String>();
    mIds.add("1");
    exQuery.setParameter(ShindigNativeQueries.MESSAGE_ID_LIST, mIds);

    // construct expected result
    final SingleResult exResult = new SingleResult(null);

    // create single-use handler and service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    final WebsockConfig config = new WebsockConfig(true);
    final WsNativeMessageSPI messageSPI = new WsNativeMessageSPI(qHandler, config,
            new ShindigEventBus(config));

    // delete from out box
    messageSPI.deleteMessages(new UserId(UserId.Type.userId, WsNativeMessageSPITest.JANE_ID),
            MessageCollection.OUTBOX, mIds, null);
  }
}
