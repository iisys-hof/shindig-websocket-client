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
import org.apache.shindig.social.core.model.AlbumImpl;
import org.apache.shindig.social.opensocial.model.Album;
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

/**
 * Test for the AppDataService implementation of the websocket back-end.
 */
public class WsNativeAlbumSPITest {
  private static final String ID_FIELD = Album.Field.ID.toString();
  private static final String TITLE_FIELD = Album.Field.TITLE.toString();
  private static final String MEDIA_ITEM_COUNT_FIELD = Album.Field.MEDIA_ITEM_COUNT.toString();
  private static final String MEDIA_TYPE_FIELD = Album.Field.MEDIA_TYPE.toString();

  private static final String JOHN_ID = "john.doe", JANE_ID = "jane.doe", HORST_ID = "horst";

  private static final String JOHN_ALB_1_ID = "john1";
  private static final String JOHN_ALB_2_ID = "john2";
  private static final String JANE_ALB_1_ID = "jane1";
  private static final String JANE_ALB_2_ID = "jane2";
  private static final String JANE_ALB_3_ID = "jane3";
  private static final String HORST_ALB_ID = "horst";

  private static final String JOHN_ALB_1_TITLE = "john's album 1";
  private static final String JOHN_ALB_2_TITLE = "john's album 2";
  private static final String JANE_ALB_1_TITLE = "jane's album 1";
  private static final String JANE_ALB_2_TITLE = "jane's album 2";
  private static final String JANE_ALB_3_TITLE = "jane's album 3";
  private static final String HORST_ALB_TITLE = "horst's album";

  private static final Integer JOHN_ALB_1_COUNT = 1;
  private static final Integer JOHN_ALB_2_COUNT = 3;
  private static final Integer JANE_ALB_1_COUNT = 2;
  private static final Integer JANE_ALB_2_COUNT = 4;
  private static final Integer JANE_ALB_3_COUNT = 6;
  private static final Integer HORST_ALB_COUNT = 42;

  private final List<String> JOHN_ALB_1_TYPES = new ArrayList<String>();
  private final List<String> JOHN_ALB_2_TYPES = new ArrayList<String>();
  private final List<String> JANE_ALB_1_TYPES = new ArrayList<String>();
  private List<Map<String, Object>> fJohnAlbs, fJaneAlbs, fHorstAlbs;

  /**
   * Sets up some test data.
   */
  @Before
  public void setupData() {
    // people
    final Map<String, Object> johndoe = new HashMap<String, Object>();
    johndoe.put(Person.Field.ID.toString(), WsNativeAlbumSPITest.JOHN_ID);
    this.fJohnAlbs = new ArrayList<Map<String, Object>>();

    final Map<String, Object> janedoe = new HashMap<String, Object>();
    janedoe.put(Person.Field.ID.toString(), WsNativeAlbumSPITest.JANE_ID);
    this.fJaneAlbs = new ArrayList<Map<String, Object>>();

    final Map<String, Object> horst = new HashMap<String, Object>();
    horst.put(Person.Field.ID.toString(), WsNativeAlbumSPITest.HORST_ID);
    this.fHorstAlbs = new ArrayList<Map<String, Object>>();

    // albums
    Map<String, Object> album = new HashMap<String, Object>();
    album.put(WsNativeAlbumSPITest.ID_FIELD, WsNativeAlbumSPITest.JOHN_ALB_1_ID);
    album.put(WsNativeAlbumSPITest.TITLE_FIELD, WsNativeAlbumSPITest.JOHN_ALB_1_TITLE);
    album.put(WsNativeAlbumSPITest.MEDIA_ITEM_COUNT_FIELD, WsNativeAlbumSPITest.JOHN_ALB_1_COUNT);
    this.JOHN_ALB_1_TYPES.add(MediaItem.Type.AUDIO.name());
    this.JOHN_ALB_1_TYPES.add(MediaItem.Type.VIDEO.name());
    album.put(WsNativeAlbumSPITest.MEDIA_TYPE_FIELD, this.JOHN_ALB_1_TYPES);
    this.fJohnAlbs.add(album);

    album = new HashMap<String, Object>();
    album.put(WsNativeAlbumSPITest.ID_FIELD, WsNativeAlbumSPITest.JOHN_ALB_2_ID);
    album.put(WsNativeAlbumSPITest.TITLE_FIELD, WsNativeAlbumSPITest.JOHN_ALB_2_TITLE);
    album.put(WsNativeAlbumSPITest.MEDIA_ITEM_COUNT_FIELD, WsNativeAlbumSPITest.JOHN_ALB_2_COUNT);
    album.put(WsNativeAlbumSPITest.MEDIA_TYPE_FIELD, this.JOHN_ALB_2_TYPES);
    this.fJohnAlbs.add(album);

    album = new HashMap<String, Object>();
    album.put(WsNativeAlbumSPITest.ID_FIELD, WsNativeAlbumSPITest.JANE_ALB_1_ID);
    album.put(WsNativeAlbumSPITest.TITLE_FIELD, WsNativeAlbumSPITest.JANE_ALB_1_TITLE);
    album.put(WsNativeAlbumSPITest.MEDIA_ITEM_COUNT_FIELD, WsNativeAlbumSPITest.JANE_ALB_1_COUNT);
    this.JANE_ALB_1_TYPES.add(MediaItem.Type.AUDIO.name());
    this.JANE_ALB_1_TYPES.add(MediaItem.Type.VIDEO.name());
    this.JANE_ALB_1_TYPES.add(MediaItem.Type.IMAGE.name());
    album.put(WsNativeAlbumSPITest.MEDIA_TYPE_FIELD, this.JANE_ALB_1_TYPES);
    this.fJaneAlbs.add(album);

    album = new HashMap<String, Object>();
    album.put(WsNativeAlbumSPITest.ID_FIELD, WsNativeAlbumSPITest.JANE_ALB_2_ID);
    album.put(WsNativeAlbumSPITest.TITLE_FIELD, WsNativeAlbumSPITest.JANE_ALB_2_TITLE);
    album.put(WsNativeAlbumSPITest.MEDIA_ITEM_COUNT_FIELD, WsNativeAlbumSPITest.JANE_ALB_2_COUNT);
    this.fJaneAlbs.add(album);

    album = new HashMap<String, Object>();
    album.put(WsNativeAlbumSPITest.ID_FIELD, WsNativeAlbumSPITest.JANE_ALB_3_ID);
    album.put(WsNativeAlbumSPITest.TITLE_FIELD, WsNativeAlbumSPITest.JANE_ALB_3_TITLE);
    album.put(WsNativeAlbumSPITest.MEDIA_ITEM_COUNT_FIELD, WsNativeAlbumSPITest.JANE_ALB_3_COUNT);
    this.JANE_ALB_1_TYPES.add(MediaItem.Type.IMAGE.name());
    album.put(WsNativeAlbumSPITest.MEDIA_TYPE_FIELD, this.JANE_ALB_1_TYPES);
    this.fJaneAlbs.add(album);

    album = new HashMap<String, Object>();
    album.put(WsNativeAlbumSPITest.ID_FIELD, WsNativeAlbumSPITest.HORST_ALB_ID);
    album.put(WsNativeAlbumSPITest.TITLE_FIELD, WsNativeAlbumSPITest.HORST_ALB_TITLE);
    album.put(WsNativeAlbumSPITest.MEDIA_ITEM_COUNT_FIELD, WsNativeAlbumSPITest.HORST_ALB_COUNT);
    this.fHorstAlbs.add(album);
  }

  /**
   * Tests the retrieval of a single single and conversion.
   */
  @Test
  public void singleRetrievalTest() throws Exception {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.GET_ALBUM_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeAlbumSPITest.JOHN_ID);
    exQuery.setParameter(ShindigNativeQueries.ALBUM_ID, WsNativeAlbumSPITest.JOHN_ALB_1_ID);

    // construct expected result
    final SingleResult exResult = new SingleResult(this.fJohnAlbs.get(0));

    // create single use handler and album service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    final WsNativeAlbumSPI albumSPI = new WsNativeAlbumSPI(qHandler);

    // retrieve and check
    final Album resAlbum = albumSPI.getAlbum(
            new UserId(UserId.Type.userId, WsNativeAlbumSPITest.JOHN_ID), null, null,
            WsNativeAlbumSPITest.JOHN_ALB_1_ID, null).get();

    Assert.assertEquals(WsNativeAlbumSPITest.JOHN_ALB_1_ID, resAlbum.getId());
    Assert.assertEquals(WsNativeAlbumSPITest.JOHN_ALB_1_TITLE, resAlbum.getTitle());
    Assert.assertEquals(WsNativeAlbumSPITest.JOHN_ALB_1_COUNT, resAlbum.getMediaItemCount());

    final List<String> foundTypes = new ArrayList<String>();
    for (final MediaItem.Type type : resAlbum.getMediaType()) {
      foundTypes.add(type.name());
    }
    Assert.assertTrue(foundTypes.containsAll(this.JOHN_ALB_1_TYPES));
  }

  /**
   * Tests the retrieval of multiple albums by their IDs.
   */
  @Test
  public void listRetrievalTest() throws Exception {
    // TODO: check for actual "by ID" query

    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.GET_ALBUMS_QUERY);

    final List<String> idList = new ArrayList<String>();
    idList.add(WsNativeAlbumSPITest.JANE_ALB_1_ID);
    idList.add(WsNativeAlbumSPITest.JANE_ALB_3_ID);
    idList.add(WsNativeAlbumSPITest.HORST_ALB_ID);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeAlbumSPITest.JANE_ID);
    exQuery.setParameter(ShindigNativeQueries.ALBUM_ID_LIST, idList);

    // construct expected result
    final List<Map<String, Object>> exResList = new ArrayList<Map<String, Object>>();
    exResList.add(this.fJaneAlbs.get(0));
    exResList.add(this.fJaneAlbs.get(2));
    final ListResult exResult = new ListResult(exResList);

    // create single use handler and album service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    final WsNativeAlbumSPI albumSPI = new WsNativeAlbumSPI(qHandler);

    // retrieve and check
    final Set<String> albumIds = new HashSet<String>();
    albumIds.add(WsNativeAlbumSPITest.JANE_ALB_1_ID);
    albumIds.add(WsNativeAlbumSPITest.JANE_ALB_3_ID);
    albumIds.add(WsNativeAlbumSPITest.HORST_ALB_ID);

    final Future<RestfulCollection<Album>> resFut = albumSPI.getAlbums(new UserId(
            UserId.Type.userId, WsNativeAlbumSPITest.JANE_ID), null, null, new CollectionOptions(),
            albumIds, null);
    final List<Album> resList = resFut.get().getList();

    Assert.assertEquals(2, resList.size());

    boolean found1 = false;
    boolean found3 = false;

    for (final Album album : resList) {
      if (WsNativeAlbumSPITest.JANE_ALB_1_ID.equals(album.getId())) {
        Assert.assertEquals(WsNativeAlbumSPITest.JANE_ALB_1_TITLE, album.getTitle());
        found1 = true;
      } else if (WsNativeAlbumSPITest.JANE_ALB_3_ID.equals(album.getId())) {
        Assert.assertEquals(WsNativeAlbumSPITest.JANE_ALB_3_TITLE, album.getTitle());
        found3 = true;
      } else {
        throw new RuntimeException("unexpected ID: " + album.getId());
      }
    }

    Assert.assertTrue(found1);
    Assert.assertTrue(found3);
  }

  /**
   * Tests the retrieval of albums for a group of people.
   */
  @Test
  public void allRetrievalTest() throws Exception {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.GET_GROUP_ALBUMS_QUERY);

    final List<String> idList = new ArrayList<String>();
    idList.add(WsNativeAlbumSPITest.JOHN_ID);

    exQuery.setParameter(ShindigNativeQueries.USER_ID_LIST, idList);
    exQuery.setParameter(ShindigNativeQueries.GROUP_ID, "@friends");

    // construct expected result
    final List<Map<String, Object>> exResList = new ArrayList<Map<String, Object>>();
    exResList.add(this.fJaneAlbs.get(0));
    exResList.add(this.fJaneAlbs.get(1));
    exResList.add(this.fJaneAlbs.get(2));
    exResList.add(this.fHorstAlbs.get(0));

    final ListResult exResult = new ListResult(exResList);

    // create single use handler and album service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, exResult);
    final WsNativeAlbumSPI albumSPI = new WsNativeAlbumSPI(qHandler);

    // retrieve and check
    final Set<UserId> uidSet = new HashSet<UserId>();
    uidSet.add(new UserId(UserId.Type.userId, WsNativeAlbumSPITest.JOHN_ID));

    final Future<RestfulCollection<Album>> resFut = albumSPI.getAlbums(uidSet, new GroupId(
            GroupId.Type.friends, "@friends"), null, null, new CollectionOptions(), null);

    final List<Album> resList = resFut.get().getList();

    Assert.assertEquals(4, resList.size());

    boolean found1 = false;
    boolean found2 = false;
    boolean found3 = false;
    boolean found4 = false;

    String id = null;
    for (final Album album : resList) {
      id = album.getId();

      switch (id) {
      case JANE_ALB_1_ID:
        found1 = true;
        break;

      case JANE_ALB_2_ID:
        found2 = true;
        break;

      case JANE_ALB_3_ID:
        found3 = true;
        break;

      case HORST_ALB_ID:
        found4 = true;
        break;

      default:
        throw new RuntimeException("unexpected ID: " + id);
      }
    }

    Assert.assertTrue(found1);
    Assert.assertTrue(found2);
    Assert.assertTrue(found3);
    Assert.assertTrue(found4);
  }

  /**
   * Tests the creation of an album.
   */
  @Test
  public void creationTest() {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.CREATE_ALBUM_QUERY);

    final Map<String, Object> albumMap = new HashMap<String, Object>();
    albumMap.put(WsNativeAlbumSPITest.ID_FIELD, WsNativeAlbumSPITest.HORST_ALB_ID);
    albumMap.put(WsNativeAlbumSPITest.TITLE_FIELD, WsNativeAlbumSPITest.HORST_ALB_TITLE);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeAlbumSPITest.HORST_ID);
    exQuery.setParameter(ShindigNativeQueries.ALBUM_OBJECT, albumMap);

    // create single use handler and album service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, new WebsockQuery(
            EQueryType.SUCCESS));
    final WsNativeAlbumSPI albumSPI = new WsNativeAlbumSPI(qHandler);

    // execute, check only possible through successful return
    final Album album = new AlbumImpl();
    album.setId(WsNativeAlbumSPITest.HORST_ALB_ID);
    album.setTitle(WsNativeAlbumSPITest.HORST_ALB_TITLE);

    albumSPI.createAlbum(new UserId(UserId.Type.userId, WsNativeAlbumSPITest.HORST_ID), null,
            album, null);

    // TODO: timeout?
  }

  /**
   * Tests updating an album.
   */
  @Test
  public void updateTest() {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.UPDATE_ALBUM_QUERY);

    final Map<String, Object> albumMap = new HashMap<String, Object>();
    albumMap.put(WsNativeAlbumSPITest.ID_FIELD, WsNativeAlbumSPITest.HORST_ALB_ID);
    albumMap.put(WsNativeAlbumSPITest.TITLE_FIELD, WsNativeAlbumSPITest.HORST_ALB_TITLE);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeAlbumSPITest.HORST_ID);
    exQuery.setParameter(ShindigNativeQueries.ALBUM_ID, WsNativeAlbumSPITest.HORST_ALB_ID);
    exQuery.setParameter(ShindigNativeQueries.ALBUM_OBJECT, albumMap);

    // create single use handler and album service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, new WebsockQuery(
            EQueryType.SUCCESS));
    final WsNativeAlbumSPI albumSPI = new WsNativeAlbumSPI(qHandler);

    // execute, check only possible through successful return
    final Album album = new AlbumImpl();
    album.setId(WsNativeAlbumSPITest.HORST_ALB_ID);
    album.setTitle(WsNativeAlbumSPITest.HORST_ALB_TITLE);

    albumSPI.updateAlbum(new UserId(UserId.Type.userId, WsNativeAlbumSPITest.HORST_ID), null,
            album, WsNativeAlbumSPITest.HORST_ALB_ID, null);
  }

  /**
   * Tests the deletion of an album.
   */
  @Test
  public void deletionTest() {
    // construct expected query
    final WebsockQuery exQuery = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    exQuery.setPayload(ShindigNativeQueries.DELETE_ALBUM_QUERY);

    exQuery.setParameter(ShindigNativeQueries.USER_ID, WsNativeAlbumSPITest.HORST_ID);
    exQuery.setParameter(ShindigNativeQueries.ALBUM_ID, WsNativeAlbumSPITest.HORST_ALB_ID);

    // create single use handler and album service
    final IQueryHandler qHandler = new TestQueryHandler(exQuery, new WebsockQuery(
            EQueryType.SUCCESS));
    final WsNativeAlbumSPI albumSPI = new WsNativeAlbumSPI(qHandler);

    // execute, check only possible through successful return
    albumSPI.deleteAlbum(new UserId(UserId.Type.userId, WsNativeAlbumSPITest.HORST_ID), null,
            WsNativeAlbumSPITest.HORST_ALB_ID, null);
  }
}
