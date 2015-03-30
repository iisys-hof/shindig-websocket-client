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
import org.apache.shindig.social.opensocial.model.MessageCollection;
import org.apache.shindig.social.opensocial.model.Url;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the message collection converter class.
 */
public class MessageCollectionDTOTest {
  private static final String ID = "collection ID";
  private static final String TITLE = "collection title";
  private static final Long UPDATED = System.currentTimeMillis();
  private static final String[] URLS = { "URL1", "URL2", "URL3" };

  private static final Integer UNREAD = 2;
  private static final Integer TOTAL = 3;

  private Map<String, Object> fCollNode;

  /**
   * Sets up some test data.
   */
  @Before
  public void setupData() {
    this.fCollNode = new HashMap<String, Object>();

    // properties
    this.fCollNode.put(MessageCollection.Field.ID.toString(), MessageCollectionDTOTest.ID);
    this.fCollNode.put(MessageCollection.Field.TITLE.toString(), MessageCollectionDTOTest.TITLE);
    this.fCollNode
            .put(MessageCollection.Field.UPDATED.toString(), MessageCollectionDTOTest.UPDATED);
    this.fCollNode.put(Message.Field.URLS.toString(), MessageCollectionDTOTest.URLS);

    // 2 unread, 1 undefined (emulated)
    this.fCollNode.put(MessageCollection.Field.UNREAD.toString(), MessageCollectionDTOTest.UNREAD);
    this.fCollNode.put(MessageCollection.Field.TOTAL.toString(), MessageCollectionDTOTest.TOTAL);
  }

  /**
   * Test for conversion of existing data.
   */
  @Test
  public void conversionTest() {
    final MessageCollection coll = new MessageCollectionDTO(this.fCollNode);

    Assert.assertEquals(MessageCollectionDTOTest.ID, coll.getId());
    Assert.assertEquals(MessageCollectionDTOTest.TITLE, coll.getTitle());
    Assert.assertEquals(new Date(MessageCollectionDTOTest.UPDATED), coll.getUpdated());

    boolean url1 = false, url2 = false, url3 = false;
    final List<Url> urls = coll.getUrls();
    Assert.assertEquals(3, urls.size());

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

    Assert.assertEquals(new Integer(2), coll.getUnread());
    Assert.assertEquals(new Integer(3), coll.getTotal());
  }
}
