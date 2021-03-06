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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.opensocial.model.MediaItem;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.MediaItemService;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.websockbackend.model.dto.MediaItemDTO;
import org.apache.shindig.social.websockbackend.util.CollOptsConverter;

import com.google.common.util.concurrent.Futures;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.hofuniversity.iisys.neo4j.websock.queries.IQueryCallback;
import de.hofuniversity.iisys.neo4j.websock.queries.IQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.query.EQueryType;
import de.hofuniversity.iisys.neo4j.websock.query.WebsockQuery;
import de.hofuniversity.iisys.neo4j.websock.result.ListResult;
import de.hofuniversity.iisys.neo4j.websock.result.SingleResult;
import de.hofuniversity.iisys.neo4j.websock.shindig.ShindigNativeQueries;

/**
 * Implementation of the MediaItemService interface retrieving media item data from a remote Neo4j
 * graph database over a websocket.
 */
@Singleton
public class WsNativeMediaItemSPI implements MediaItemService {
  private final IQueryHandler fQueryHandler;

  private final Logger fLogger;

  /**
   * Creates a websocket media item service using the given query handler to dispatch queries to a
   * remote server. Throws a NullPointerException if the given query handler is null.
   *
   * @param qHandler
   *          query handler to use
   */
  @Inject
  public WsNativeMediaItemSPI(IQueryHandler qHandler) {
    if (qHandler == null) {
      throw new NullPointerException("query handler was null");
    }

    this.fQueryHandler = qHandler;

    this.fLogger = Logger.getLogger(this.getClass().getName());
  }

  @Override
  public Future<MediaItem> getMediaItem(UserId userId, String appId, String albumId,
          String mediaItemId, Set<String> fields, SecurityToken token) throws ProtocolException {
    MediaItem mediaItem = null;

    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.GET_MEDIA_ITEM_QUERY);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));
    query.setParameter(ShindigNativeQueries.APP_ID, appId);
    query.setParameter(ShindigNativeQueries.ALBUM_ID, albumId);
    query.setParameter(ShindigNativeQueries.MEDIA_ITEM_ID, mediaItemId);

    if (fields != null) {
      final List<String> fieldList = new ArrayList<String>(fields);
      query.setParameter(ShindigNativeQueries.FIELD_LIST, fieldList);
    }

    // execute
    final IQueryCallback result = this.fQueryHandler.sendQuery(query);
    SingleResult sResult = null;

    try {
      sResult = (SingleResult) result.get();

      // TODO: proper not found exception
      if (sResult == null) {
        return null;
      }
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not retrieve result", e);
    }

    @SuppressWarnings("unchecked")
    final Map<String, Object> map = (Map<String, Object>) sResult.getResults();
    mediaItem = new MediaItemDTO(map);

    return Futures.immediateFuture(mediaItem);
  }

  private Future<RestfulCollection<MediaItem>> converList(IQueryCallback result) {
    ListResult lResult = null;

    try {
      lResult = (ListResult) result.get();

      // TODO: proper not found exception
      if (lResult == null) {
        return null;
      }
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not retrieve results", e);
    }

    @SuppressWarnings("unchecked")
    final List<Map<String, Object>> mapList = (List<Map<String, Object>>) lResult.getResults();

    final List<MediaItem> mediaItems = new LinkedList<MediaItem>();

    for (final Map<String, Object> aMap : mapList) {
      mediaItems.add(new MediaItemDTO(aMap));
    }

    final RestfulCollection<MediaItem> rColl = new RestfulCollection<MediaItem>(mediaItems);
    rColl.setStartIndex(lResult.getFirst());
    rColl.setTotalResults(lResult.getTotal());
    rColl.setItemsPerPage(lResult.getMax());
    return Futures.immediateFuture((rColl));
  }

  @Override
  public Future<RestfulCollection<MediaItem>> getMediaItems(UserId userId, String appId,
          String albumId, Set<String> mediaItemIds, Set<String> fields, CollectionOptions options,
          SecurityToken token) throws ProtocolException {
    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.GET_MEDIA_ITEMS_BY_ID_QUERY);

    // set options
    CollOptsConverter.convert(options, query);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));
    query.setParameter(ShindigNativeQueries.APP_ID, appId);
    query.setParameter(ShindigNativeQueries.ALBUM_ID, albumId);

    if (mediaItemIds != null) {
      final List<String> itemIdList = new ArrayList<String>(mediaItemIds);
      query.setParameter(ShindigNativeQueries.MEDIA_ITEM_ID_LIST, itemIdList);
    }

    if (fields != null) {
      final List<String> fieldList = new ArrayList<String>(fields);
      query.setParameter(ShindigNativeQueries.FIELD_LIST, fieldList);
    }

    // execute
    final IQueryCallback callback = this.fQueryHandler.sendQuery(query);
    return converList(callback);
  }

  @Override
  public Future<RestfulCollection<MediaItem>> getMediaItems(UserId userId, String appId,
          String albumId, Set<String> fields, CollectionOptions options, SecurityToken token)
          throws ProtocolException {
    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.GET_MEDIA_ITEMS_QUERY);

    // set options
    CollOptsConverter.convert(options, query);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));
    query.setParameter(ShindigNativeQueries.APP_ID, appId);
    query.setParameter(ShindigNativeQueries.ALBUM_ID, albumId);

    if (fields != null) {
      final List<String> fieldList = new ArrayList<String>(fields);
      query.setParameter(ShindigNativeQueries.FIELD_LIST, fieldList);
    }

    // execute
    final IQueryCallback callback = this.fQueryHandler.sendQuery(query);
    return converList(callback);
  }

  @Override
  public Future<RestfulCollection<MediaItem>> getMediaItems(Set<UserId> userIds, GroupId groupId,
          String appId, Set<String> fields, CollectionOptions options, SecurityToken token)
          throws ProtocolException {
    String group = null;

    if (groupId != null) {
      // actual group
      if (groupId.getType() == GroupId.Type.objectId) {
        group = groupId.getObjectId().toString();
      }
      // group type
      else {
        group = '@' + groupId.getType().toString();
      }
    }

    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.GET_GROUP_MEDIA_ITEMS_QUERY);

    // set options
    CollOptsConverter.convert(options, query);

    // set parameters for method
    final List<String> idList = new LinkedList<String>();
    for (final UserId uid : userIds) {
      idList.add(uid.getUserId(token));
    }
    query.setParameter(ShindigNativeQueries.USER_ID_LIST, idList);

    query.setParameter(ShindigNativeQueries.GROUP_ID, group);
    query.setParameter(ShindigNativeQueries.APP_ID, appId);

    if (fields != null) {
      final List<String> fieldList = new ArrayList<String>(fields);
      query.setParameter(ShindigNativeQueries.FIELD_LIST, fieldList);
    }

    // execute
    final IQueryCallback callback = this.fQueryHandler.sendQuery(query);
    return converList(callback);
  }

  @Override
  public Future<Void> deleteMediaItem(UserId userId, String appId, String albumId,
          String mediaItemId, SecurityToken token) throws ProtocolException {
    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.DELETE_MEDIA_ITEM_QUERY);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));
    query.setParameter(ShindigNativeQueries.APP_ID, appId);
    query.setParameter(ShindigNativeQueries.ALBUM_ID, albumId);
    query.setParameter(ShindigNativeQueries.MEDIA_ITEM_ID, mediaItemId);

    // execute
    try {
      this.fQueryHandler.sendQuery(query).get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not delete media item", e);
    }

    return Futures.immediateFuture(null);
  }

  @Override
  public Future<Void> createMediaItem(UserId userId, String appId, String albumId,
          MediaItem mediaItem, SecurityToken token) throws ProtocolException {
    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.CREATE_MEDIA_ITEM_QUERY);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));
    query.setParameter(ShindigNativeQueries.APP_ID, appId);
    query.setParameter(ShindigNativeQueries.ALBUM_ID, albumId);

    final Map<String, Object> itMap = new HashMap<String, Object>();
    final MediaItemDTO dto = new MediaItemDTO(itMap);
    dto.setData(mediaItem);
    dto.stripNullValues();
    query.setParameter(ShindigNativeQueries.MEDIA_ITEM_OBJECT, itMap);

    // execute
    try {
      this.fQueryHandler.sendQuery(query).get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not create media item", e);
    }

    return Futures.immediateFuture(null);
  }

  @Override
  public Future<Void> updateMediaItem(UserId userId, String appId, String albumId,
          String mediaItemId, MediaItem mediaItem, SecurityToken token) throws ProtocolException {
    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.UPDATE_MEDIA_ITEM_QUERY);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));
    query.setParameter(ShindigNativeQueries.APP_ID, appId);
    query.setParameter(ShindigNativeQueries.ALBUM_ID, albumId);
    query.setParameter(ShindigNativeQueries.MEDIA_ITEM_ID, mediaItemId);

    final Map<String, Object> itMap = new HashMap<String, Object>();
    final MediaItemDTO dto = new MediaItemDTO(itMap);
    dto.setData(mediaItem);
    dto.stripNullValues();
    query.setParameter(ShindigNativeQueries.MEDIA_ITEM_OBJECT, itMap);

    // execute
    try {
      this.fQueryHandler.sendQuery(query).get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not update media item", e);
    }

    return Futures.immediateFuture(null);
  }
}
