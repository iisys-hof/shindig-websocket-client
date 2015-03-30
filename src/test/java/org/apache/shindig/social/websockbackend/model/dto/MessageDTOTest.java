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
package org.apache.shindig.social.websockbackend.model.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shindig.social.opensocial.model.Message;
import org.apache.shindig.social.opensocial.model.Url;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the message converter class.
 */
public class MessageDTOTest {
  private static final String APP_URL = "application URL";
  private static final String BODY = "message body";
  private static final String BODY_ID = "message body ID";
  private static final String ID = "message ID";
  private static final String REPLY_TO = "previous message ID";
  private static final String SENDER_ID = "sender ID";
  private static final String TITLE = "message title";
  private static final String TITLE_ID = "message title ID";
  private static final String TYPE = Message.Type.PRIVATE_MESSAGE.name();
  private static final Long TIME = System.currentTimeMillis();
  private static final Long UPDATED = System.currentTimeMillis();
  private static final String[] RECIPIENTS = { "ID1", "ID2", "ID3" };
  private static final String[] URLS = { "URL1", "URL2", "URL3" };

  private static final String COLL1_ID = "collection 1", COLL2_ID = "collection 2";
  private static final String STATUS = Message.Status.NEW.name();

  private static final String REPLY1_ID = "reply 1", REPLY2_ID = "reply 2";

  private Map<String, Object> fMessageNode;

  /**
   * Sets up some test data.
   */
  @Before
  public void setupData() {
    this.fMessageNode = new HashMap<String, Object>();

    // properties
    this.fMessageNode.put(Message.Field.APP_URL.toString(), MessageDTOTest.APP_URL);
    this.fMessageNode.put(Message.Field.BODY.toString(), MessageDTOTest.BODY);
    this.fMessageNode.put(Message.Field.BODY_ID.toString(), MessageDTOTest.BODY_ID);
    this.fMessageNode.put(Message.Field.ID.toString(), MessageDTOTest.ID);
    this.fMessageNode.put(Message.Field.IN_REPLY_TO.toString(), MessageDTOTest.REPLY_TO);
    this.fMessageNode.put(Message.Field.SENDER_ID.toString(), MessageDTOTest.SENDER_ID);
    this.fMessageNode.put(Message.Field.TITLE.toString(), MessageDTOTest.TITLE);
    this.fMessageNode.put(Message.Field.TITLE_ID.toString(), MessageDTOTest.TITLE_ID);
    this.fMessageNode.put(Message.Field.TYPE.toString(), MessageDTOTest.TYPE);
    this.fMessageNode.put(Message.Field.TIME_SENT.toString(), MessageDTOTest.TIME);
    this.fMessageNode.put(Message.Field.UPDATED.toString(), MessageDTOTest.UPDATED);
    this.fMessageNode.put(Message.Field.RECIPIENTS.toString(), MessageDTOTest.RECIPIENTS);
    this.fMessageNode.put(Message.Field.URLS.toString(), MessageDTOTest.URLS);

    // collections
    this.fMessageNode.put(Message.Field.STATUS.toString(), MessageDTOTest.STATUS);

    final String[] collIds = { MessageDTOTest.COLL1_ID, MessageDTOTest.COLL2_ID };
    this.fMessageNode.put(Message.Field.COLLECTION_IDS.toString(), collIds);

    // replies
    final String[] replies = { MessageDTOTest.REPLY1_ID, MessageDTOTest.REPLY2_ID };
    this.fMessageNode.put(Message.Field.REPLIES.toString(), replies);
  }

  /**
   * Test for conversion of existing data.
   */
  @Test
  public void conversionTest() {
    final Message m = new MessageDTO(this.fMessageNode);

    Assert.assertEquals(MessageDTOTest.APP_URL, m.getAppUrl());
    Assert.assertEquals(MessageDTOTest.BODY, m.getBody());
    Assert.assertEquals(MessageDTOTest.BODY_ID, m.getBodyId());
    Assert.assertEquals(MessageDTOTest.ID, m.getId());
    Assert.assertEquals(MessageDTOTest.REPLY_TO, m.getInReplyTo());
    Assert.assertEquals(MessageDTOTest.SENDER_ID, m.getSenderId());
    Assert.assertEquals(MessageDTOTest.STATUS, m.getStatus().name());
    Assert.assertEquals(MessageDTOTest.TITLE, m.getTitle());
    Assert.assertEquals(MessageDTOTest.TITLE_ID, m.getTitleId());
    Assert.assertEquals(MessageDTOTest.TYPE, m.getType().name());
    Assert.assertEquals(new Date(MessageDTOTest.TIME), m.getTimeSent());
    Assert.assertEquals(new Date(MessageDTOTest.UPDATED), m.getUpdated());

    final List<String> recipients = m.getRecipients();
    Assert.assertEquals(3, recipients.size());
    for (final String rec : MessageDTOTest.RECIPIENTS) {
      Assert.assertTrue(recipients.contains(rec));
    }

    boolean url1 = false;
    boolean url2 = false;
    boolean url3 = false;
    final List<Url> urls = m.getUrls();
    String value = null;

    for (final Url url : urls) {
      value = url.getValue();

      if (value.equals("URL1")) {
        url1 = true;
      } else if (value.equals("URL2")) {
        url2 = true;
      } else if (value.equals("URL3")) {
        url3 = true;
      }
    }

    Assert.assertTrue(url1 && url2 && url3);

    final List<String> colls = m.getCollectionIds();
    Assert.assertTrue(colls.contains(MessageDTOTest.COLL1_ID));
    Assert.assertTrue(colls.contains(MessageDTOTest.COLL2_ID));

    final List<String> replies = m.getReplies();
    Assert.assertTrue(replies.contains(MessageDTOTest.REPLY1_ID));
    Assert.assertTrue(replies.contains(MessageDTOTest.REPLY2_ID));
  }

  /**
   * Test for value storing capabilities.
   */
  @Test
  public void storageTest() {
    final Message m = new MessageDTO(this.fMessageNode);

    final Map<String, Object> newMess = new HashMap<String, Object>();
    final MessageDTO gMess = new MessageDTO(newMess);
    gMess.setData(m);

    Assert.assertEquals(MessageDTOTest.APP_URL, newMess.get(Message.Field.APP_URL.toString()));
    Assert.assertEquals(MessageDTOTest.BODY, newMess.get(Message.Field.BODY.toString()));
    Assert.assertEquals(MessageDTOTest.BODY_ID, newMess.get(Message.Field.BODY_ID.toString()));
    Assert.assertEquals(MessageDTOTest.REPLY_TO, newMess.get(Message.Field.IN_REPLY_TO.toString()));
    Assert.assertEquals(MessageDTOTest.SENDER_ID, newMess.get(Message.Field.SENDER_ID.toString()));
    Assert.assertEquals(MessageDTOTest.TITLE, newMess.get(Message.Field.TITLE.toString()));
    Assert.assertEquals(MessageDTOTest.TITLE_ID, newMess.get(Message.Field.TITLE_ID.toString()));
    Assert.assertEquals(MessageDTOTest.TYPE, newMess.get(Message.Field.TYPE.toString()));
    Assert.assertEquals(MessageDTOTest.TIME, newMess.get(Message.Field.TIME_SENT.toString()));
    Assert.assertEquals(MessageDTOTest.UPDATED, newMess.get(Message.Field.UPDATED.toString()));

    final String[] recipients = (String[]) newMess.get(Message.Field.RECIPIENTS.toString());
    Assert.assertArrayEquals(MessageDTOTest.RECIPIENTS, recipients);

    final String[] urls = (String[]) newMess.get(Message.Field.URLS.toString());
    Assert.assertArrayEquals(MessageDTOTest.URLS, urls);
  }
}
