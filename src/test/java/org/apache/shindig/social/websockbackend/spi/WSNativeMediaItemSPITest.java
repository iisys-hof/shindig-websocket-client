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
import org.apache.shindig.social.core.model.MediaItemImpl;
import org.apache.shindig.social.opensocial.model.MediaItem;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.UserId;
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

public class WSNativeMediaItemSPITest {
  private static final String ID_FIELD = MediaItem.Field.ID.toString();
  private static final String TITLE_FIELD = MediaItem.Field.TITLE.toString();
  private static final String TYPE_FIELD = MediaItem.Field.TYPE.toString();
  private static final String ALBUM_ID_FIELD = MediaItem.Field.ALBUM_ID.toString();

  private static final String JOHN_ID = "john.doe", JANE_ID = "jane.doe", HORST_ID = "horst";

  private static final String JOHN_ALB_1_ID = "john1";
  private static final String JOHN_ALB_2_ID = "john2";
  private static final String JANE_ALB_1_ID = "jane1";
  private static final String HORST_ALB_ID = "horst";

  private static final String MEDIA_ITEM_1_ID = "1";
  private static final String MEDIA_ITEM_2_ID = "2";
  private static final String MEDIA_ITEM_3_ID = "3";
  private static final String MEDIA_ITEM_4_ID = "4";
  private static final String MEDIA_ITEM_5_ID = "5";
  private static final String MEDIA_ITEM_6_ID = "6";
  private static final String MEDIA_ITEM_7_ID = "7";

  private static final String MEDIA_ITEM_1_TITLE = "title 1";
  private static final String MEDIA_ITEM_2_TITLE = "title 2";
  private static final String MEDIA_ITEM_3_TITLE = "title 3";
  private static final String MEDIA_ITEM_4_TITLE = "title 4";
  private static final String MEDIA_ITEM_5_TITLE = "title 5";
  private static final String MEDIA_ITEM_6_TITLE = "title 6";
  private static final String MEDIA_ITEM_7_TITLE = "title 7";

  private static final String MEDIA_ITEM_1_TYPE = MediaItem.Type.AUDIO.name();
  private static final String MEDIA_ITEM_2_TYPE = MediaItem.Type.VIDEO.name();
  private static final String MEDIA_ITEM_3_TYPE = MediaItem.Type.IMAGE.name();
  private static final String MEDIA_ITEM_4_TYPE = MediaItem.Type.AUDIO.name();
  private static final String MEDIA_ITEM_5_TYPE = MediaItem.Type.VIDEO.name();
  private static final String MEDIA_ITEM_6_TYPE = MediaItem.Type.IMAGE.name();
  private static final String MEDIA_ITEM_7_TYPE = MediaItem.Type.AUDIO.name();

  private List<Map<String, Object>> fJohnIts, fJaneIts, fHorstIts;

  /**
   * Sets up some test data.
   */
  @Before
  public void setupData() {
    // people
    final Map<String, Object> johndoe = new HashMap<String, Object>();
    johndoe.put(Person.Field.ID.toString(), WSNativeMediaItemSPITest.JOHN_ID);
    this.fJohnIts = new ArrayList<Map<String, Object>>();

    final Map<String, Object> janedoe = new HashMap<String, Object>();
    janedoe.put(Person.Field.ID.toString(), WSNativeMediaItemSPITest.JANE_ID);
    this.fJaneIts = new ArrayList<Map<String, Object>>();

    final Map<String, Object> horst = new HashMap<String, Object>();
    horst.put(Person.Field.ID.toString(), WSNativeMediaItemSPITest.HORST_ID);
    this.fHorstIts = new ArrayList<Map<String, Object>>();

    // media items
    Map<String, Object> item = new HashMap<String, Object>();
    item.put(WSNativeMediaItemSPITest.ID_FIELD, WSNativeMediaItemSPITest.MEDIA_ITEM_1_ID);
    item.put(WSNativeMediaItemSPITest.TITLE_FIELD, WSNativeMediaItemSPITest.MEDIA_ITEM_1_TITLE);
    item.put(WSNativeMediaItemSPITest.TYPE_FIELD, WSNativeMediaItemSPITest.MEDIA_ITEM_1_TYPE);
    item.put(WSNativeMediaItemSPITest.ALBUM_ID_FIELD, WSNativeMediaItemSPITest.JOHN_ALB_1_ID);
    this.fJohnIts.add(item);

    item = new HashMap<String, Object>();
    item.put(WSNativeMediaItemSPITest.ID_FIELD, WSNativeMediaItemSPITest.MEDIA_ITEM_2_ID);
    item.put(WSNativeMediaItemSPITest.TITLE_FIELD, WSNativeMediaItemSPITest.MEDIA_ITEM_2_TITLE);
    item.put(WSNativeMediaItemSPITest.TYPE_FIELD, WSNativeMediaItemSPITest.MEDIA_ITEM_2_TYPE);
    item.put(WSNativeMediaItemSPITest.ALBUM_ID_FIELD, WSNativeMediaItemSPITest.JOHN_ALB_1_ID);
    this.fJohnIts.add(item);

    item = new HashMap<String, Object>();
    item.put(WSNativeMediaItemSPITest.ID_FIELD, WSNativeMediaItemSPITest.MEDIA_ITEM_3_ID);
    item.put(WSNativeMediaItemSPITest.TITLE_FIELD, WSNativeMediaItemSPITest.MEDIA_ITEM_3_TITLE);
    item.put(WSNativeMediaItemSPITest.TYPE_FIELD, WSNativeMediaItemSPITest.MEDIA_ITEM_3_TYPE);
    item.put(WSNativeMediaItemSPITest.ALBUM_ID_FIELD, WSNativeMediaItemSPITest.JOHN_ALB_2_ID);
    this.fJohnIts.add(item);

    item = new HashMap<String, Object>();
    item.put(WSNativeMediaItemSPITest.ID_FIELD, WSNativeMediaItemSPITest.MEDIA_ITEM_4_ID);
    item.put(WSNativeMediaItemSPITest.TITLE_FIELD, WSNativeMediaItemSPITest.MEDIA_ITEM_4_TITLE);
    item.put(WSNativeMediaItemSPITest.TYPE_FIELD, WSNativeMediaItemSPITest.MEDIA_ITEM_4_TYPE);
    item.put(WSNativeMediaItemSPITest.ALBUM_ID_FIELD, WSNativeMediaItemSPITest.JANE_ALB_1_ID);
    this.fJaneIts.add(item);

    item = new HashMap<String, Object>();
    item.put(WSNativeMediaItemSPITest.ID_FIELD, WSNativeMediaItemSPITest.MEDIA_ITEM_5_ID);
    item.put(WSNativeMediaItemSPITest.TITLE_FIELD, WSNativeMediaItemSPITest.MEDIA_ITEM_5_TITLE);
    item.put(WSNativeMediaItemSPITest.TYPE_FIELD, WSNativeMediaItemSPITest.MEDIA_ITEM_5_TYPE);
    item.put(WSNativeMediaItemSPITest.ALBUM_ID_FIELD, WSNativeMediaItemSPITest.JANE_ALB_1_ID);
    this.fJaneIts.add(item);

    item = new HashMap<String, Object>();
    item.put(WSNativeMediaItemSPITest.ID_FIELD, WSNativeMediaItemSPITest.MEDIA_ITEM_6_ID);
    item.put(WSNativeMediaItemSPITest.TITLE_FIELD, WSNativeMediaItemSPITest.MEDIA_ITEM_6_TITLE);
    item.put(WSNativeMediaItemSPITest.TYPE_FIELD, WSNativeMediaItemSPITest.MEDIA_ITEM_6_TYPE);
    item.put(WSNativeMediaItemSPITest.ALBUM_ID_FIELD, WSNativeMediaItemSPITest.JANE_ALB_1_ID);
    this.fJaneIts.add(item);

    item = new HashMap<String, Object>();
    item.put(WSNativeMediaItemSPITest.ID_FIELD, WSNativeMediaItemSPITest.MEDIA_ITEM_7_ID);
    item.put(WSNativeMediaItemSPITest.TITLE_FIELD, WSNativeMediaItemSPITest.MEDIA_ITEM_7_TITLE);
    item.put(WSNativeMediaItemSPITest.TYPE_FIELD, WSNativeMediaItemSPITest.MEDIA_ITEM_7_TYPE);
    item.put(WSNativeMediaItemSPITest.ALBUM_ID_FIELD, WSNativeMediaItemSPITest.HORST_ALB_ID);
    this.fHorstIts.add(item);
  }

  /**
   * Tests the retrieval of a single media item by its ID.
   */
  @Test
  public void singleRetrievalTest() throws Exception {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.GET_MEDIA_ITEM_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WSNativeMediaItemSPITest.JOHN_ID);
    exQuery.setParameter(ShindigNativeQueries.ALBUM_ID, WSNativeMediaItemSPITest.JOHN_ALB_1_ID);
    exQuery.setParameter(ShindigNativeQueries.MEDIA_ITEM_ID,
            WSNativeMediaItemSPITest.MEDIA_ITEM_1_ID);

    // construct expected result
    final SingleResult exResult = new SingleResult(this.fJohnIts.get(0));

    // create single use handler and album service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    final WsNativeMediaItemSPI mediaItemSPI = new WsNativeMediaItemSPI(qHandler);

    // retrieve and check
    final MediaItem mediaItem = mediaItemSPI.getMediaItem(
            new UserId(UserId.Type.userId, WSNativeMediaItemSPITest.JOHN_ID), null,
            WSNativeMediaItemSPITest.JOHN_ALB_1_ID, WSNativeMediaItemSPITest.MEDIA_ITEM_1_ID, null,
            null).get();

    Assert.assertEquals(WSNativeMediaItemSPITest.MEDIA_ITEM_1_ID, mediaItem.getId());
    Assert.assertEquals(WSNativeMediaItemSPITest.MEDIA_ITEM_1_TITLE, mediaItem.getTitle());
    Assert.assertEquals(WSNativeMediaItemSPITest.MEDIA_ITEM_1_TYPE, mediaItem.getType().name());
    Assert.assertEquals(WSNativeMediaItemSPITest.JOHN_ALB_1_ID, mediaItem.getAlbumId());
  }

  /**
   * Tests the retrieval of a list of media items by their IDs.
   */
  @Test
  public void listRetrievalTest() throws Exception {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.GET_MEDIA_ITEMS_BY_ID_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WSNativeMediaItemSPITest.JANE_ID);
    exQuery.setParameter(ShindigNativeQueries.ALBUM_ID, WSNativeMediaItemSPITest.JANE_ALB_1_ID);

    // construct expected result
    final List<Map<String, Object>> exResList = new ArrayList<Map<String, Object>>();
    exResList.add(this.fJaneIts.get(0));
    exResList.add(this.fJaneIts.get(2));

    final ListResult exResult = new ListResult(exResList);

    // create single use handler and album service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    final WsNativeMediaItemSPI mediaItemSPI = new WsNativeMediaItemSPI(qHandler);

    // retrieve and check
    final Set<String> mediaItemIds = new HashSet<String>();

    final Future<RestfulCollection<MediaItem>> resFut = mediaItemSPI.getMediaItems(new UserId(
            UserId.Type.userId, WSNativeMediaItemSPITest.JANE_ID), null,
            WSNativeMediaItemSPITest.JANE_ALB_1_ID, mediaItemIds, null, new CollectionOptions(),
            null);

    final List<MediaItem> mediaItems = resFut.get().getList();

    Assert.assertEquals(2, mediaItems.size());

    boolean found4 = false;
    boolean found6 = false;

    for (final MediaItem item : mediaItems) {
      if (WSNativeMediaItemSPITest.MEDIA_ITEM_4_ID.equals(item.getId())) {
        Assert.assertEquals(WSNativeMediaItemSPITest.MEDIA_ITEM_4_TITLE, item.getTitle());
        found4 = true;
      } else if (WSNativeMediaItemSPITest.MEDIA_ITEM_6_ID.equals(item.getId())) {
        Assert.assertEquals(WSNativeMediaItemSPITest.MEDIA_ITEM_6_TITLE, item.getTitle());
        found6 = true;
      } else {
        throw new RuntimeException("unexpected ID: " + item.getId());
      }
    }

    Assert.assertTrue(found4);
    Assert.assertTrue(found6);
  }

  /**
   * Tests the retrieval of all media items for a group of people.
   */
  @Test
  public void allRetrievalTest() throws Exception {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.GET_MEDIA_ITEMS_QUERY);

    final List<String> idList = new ArrayList<String>();
    idList.add(WSNativeMediaItemSPITest.JOHN_ID);

    exQuery.setParameter(ShindigNativeQueries.USER_ID_LIST, idList);
    exQuery.setParameter(ShindigNativeQueries.GROUP_ID, "@friends");

    // construct expected result
    final List<Map<String, Object>> exResList = new ArrayList<Map<String, Object>>();
    exResList.add(this.fJaneIts.get(0));
    exResList.add(this.fJaneIts.get(1));
    exResList.add(this.fJaneIts.get(2));
    exResList.add(this.fHorstIts.get(0));

    final ListResult exResult = new ListResult(exResList);

    // create single use handler and album service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    final WsNativeMediaItemSPI mediaItemSPI = new WsNativeMediaItemSPI(qHandler);

    // retrieve and check
    final Set<UserId> uidSet = new HashSet<UserId>();
    uidSet.add(new UserId(UserId.Type.userId, WSNativeMediaItemSPITest.JOHN_ID));

    final Future<RestfulCollection<MediaItem>> resFut = mediaItemSPI.getMediaItems(uidSet,
            new GroupId(GroupId.Type.friends, "@friends"), null, null, new CollectionOptions(),
            null);

    final List<MediaItem> mediaItems = resFut.get().getList();

    boolean found4 = false;
    boolean found5 = false;
    boolean found6 = false;
    boolean found7 = false;

    String id = null;
    for (final MediaItem item : mediaItems) {
      id = item.getId();

      switch (id) {
      case MEDIA_ITEM_4_ID:
        found4 = true;
        break;

      case MEDIA_ITEM_5_ID:
        found5 = true;
        break;

      case MEDIA_ITEM_6_ID:
        found6 = true;
        break;

      case MEDIA_ITEM_7_ID:
        found7 = true;
        break;

      default:
        throw new RuntimeException("unexpected ID: " + id);
      }
    }

    Assert.assertTrue(found4);
    Assert.assertTrue(found5);
    Assert.assertTrue(found6);
    Assert.assertTrue(found7);
  }

  /**
   * Tests the creation of a media item.
   */
  @Test
  public void creationTest() {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.CREATE_MEDIA_ITEM_QUERY);

    final Map<String, Object> itemMap = new HashMap<String, Object>();
    itemMap.put(WSNativeMediaItemSPITest.ID_FIELD, WSNativeMediaItemSPITest.MEDIA_ITEM_7_ID);
    itemMap.put(WSNativeMediaItemSPITest.TITLE_FIELD, WSNativeMediaItemSPITest.MEDIA_ITEM_7_TITLE);
    itemMap.put(WSNativeMediaItemSPITest.TYPE_FIELD, WSNativeMediaItemSPITest.MEDIA_ITEM_7_TYPE);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WSNativeMediaItemSPITest.HORST_ID);
    exQuery.setParameter(ShindigNativeQueries.ALBUM_ID, WSNativeMediaItemSPITest.HORST_ALB_ID);
    exQuery.setParameter(ShindigNativeQueries.MEDIA_ITEM_OBJECT, itemMap);

    // create single use handler and album service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, new WebsockQuery(
            EQueryType.SUCCESS));
    final WsNativeMediaItemSPI mediaItemSPI = new WsNativeMediaItemSPI(qHandler);

    // execute, check only possible through successful return
    final MediaItem item = new MediaItemImpl();
    item.setId(WSNativeMediaItemSPITest.MEDIA_ITEM_7_ID);
    item.setTitle(WSNativeMediaItemSPITest.MEDIA_ITEM_7_TITLE);
    item.setType(MediaItem.Type.valueOf(WSNativeMediaItemSPITest.MEDIA_ITEM_7_TYPE));

    mediaItemSPI.createMediaItem(new UserId(UserId.Type.userId, WSNativeMediaItemSPITest.HORST_ID),
            null, WSNativeMediaItemSPITest.HORST_ALB_ID, item, null);
  }

  /**
   * Tests updating a media item.
   */
  @Test
  public void updateTest() {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.UPDATE_MEDIA_ITEM_QUERY);

    final Map<String, Object> itemMap = new HashMap<String, Object>();
    itemMap.put(WSNativeMediaItemSPITest.ID_FIELD, WSNativeMediaItemSPITest.MEDIA_ITEM_7_ID);
    itemMap.put(WSNativeMediaItemSPITest.TITLE_FIELD, WSNativeMediaItemSPITest.MEDIA_ITEM_7_TITLE);
    itemMap.put(WSNativeMediaItemSPITest.TYPE_FIELD, WSNativeMediaItemSPITest.MEDIA_ITEM_7_TYPE);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WSNativeMediaItemSPITest.HORST_ID);
    exQuery.setParameter(ShindigNativeQueries.ALBUM_ID, WSNativeMediaItemSPITest.HORST_ALB_ID);
    exQuery.setParameter(ShindigNativeQueries.MEDIA_ITEM_ID,
            WSNativeMediaItemSPITest.MEDIA_ITEM_7_ID);
    exQuery.setParameter(ShindigNativeQueries.MEDIA_ITEM_OBJECT, itemMap);

    // create single use handler and album service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, new WebsockQuery(
            EQueryType.SUCCESS));
    final WsNativeMediaItemSPI mediaItemSPI = new WsNativeMediaItemSPI(qHandler);

    // execute, check only possible through successful return
    final MediaItem item = new MediaItemImpl();
    item.setId(WSNativeMediaItemSPITest.MEDIA_ITEM_7_ID);
    item.setTitle(WSNativeMediaItemSPITest.MEDIA_ITEM_7_TITLE);
    item.setType(MediaItem.Type.valueOf(WSNativeMediaItemSPITest.MEDIA_ITEM_7_TYPE));

    mediaItemSPI.updateMediaItem(new UserId(UserId.Type.userId, WSNativeMediaItemSPITest.HORST_ID),
            null, WSNativeMediaItemSPITest.HORST_ALB_ID, WSNativeMediaItemSPITest.MEDIA_ITEM_7_ID,
            item, null);
  }

  /**
   * Tests the deletion of a media item.
   */
  @Test
  public void deletionTest() {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.DELETE_MEDIA_ITEM_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WSNativeMediaItemSPITest.HORST_ID);
    exQuery.setParameter(ShindigNativeQueries.ALBUM_ID, WSNativeMediaItemSPITest.HORST_ALB_ID);
    exQuery.setParameter(ShindigNativeQueries.MEDIA_ITEM_ID,
            WSNativeMediaItemSPITest.MEDIA_ITEM_7_ID);

    // create single use handler and album service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, new WebsockQuery(
            EQueryType.SUCCESS));
    final WsNativeMediaItemSPI mediaItemSPI = new WsNativeMediaItemSPI(qHandler);

    // execute, check only possible through successful return
    mediaItemSPI.deleteMediaItem(new UserId(UserId.Type.userId, WSNativeMediaItemSPITest.HORST_ID),
            null, WSNativeMediaItemSPITest.HORST_ALB_ID, WSNativeMediaItemSPITest.MEDIA_ITEM_7_ID,
            null);
  }
}
