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
import org.apache.shindig.social.opensocial.model.Album;
import org.apache.shindig.social.opensocial.spi.AlbumService;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.websockbackend.model.dto.AlbumDTO;
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
 * Implementation of the AlbumService interface retrieving album data from a remote Neo4j graph
 * database over a websocket.
 */
@Singleton
public class WsNativeAlbumSPI implements AlbumService {
  private final IQueryHandler fQueryHandler;

  private final Logger fLogger;

  /**
   * Creates a websocket album service using the given query handler to dispatch queries to a remote
   * server. Throws a NullPointerException if the given query handler is null.
   *
   * @param qHandler
   *          query handler to use
   */
  @Inject
  public WsNativeAlbumSPI(IQueryHandler qHandler) {
    if (qHandler == null) {
      throw new NullPointerException("query handler was null");
    }

    this.fQueryHandler = qHandler;

    this.fLogger = Logger.getLogger(this.getClass().getName());
  }

  @Override
  public Future<Album> getAlbum(UserId userId, String appId, Set<String> fields, String albumId,
          SecurityToken token) throws ProtocolException {
    Album album = null;

    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.GET_ALBUM_QUERY);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));
    query.setParameter(ShindigNativeQueries.APP_ID, appId);
    query.setParameter(ShindigNativeQueries.ALBUM_ID, albumId);

    if (fields != null) {
      final List<String> fieldList = new ArrayList<String>(fields);
      query.setParameter(ShindigNativeQueries.FIELD_LIST, fieldList);
    }

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
    album = new AlbumDTO(map);

    return Futures.immediateFuture(album);
  }

  private Future<RestfulCollection<Album>> convertList(IQueryCallback result) {
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

    final List<Album> albums = new LinkedList<Album>();

    for (final Map<String, Object> aMap : mapList) {
      albums.add(new AlbumDTO(aMap));
    }

    final RestfulCollection<Album> rColl = new RestfulCollection<Album>(albums);
    rColl.setStartIndex(lResult.getFirst());
    rColl.setTotalResults(lResult.getTotal());
    rColl.setItemsPerPage(lResult.getMax());
    return Futures.immediateFuture((rColl));
  }

  @Override
  public Future<RestfulCollection<Album>> getAlbums(UserId userId, String appId,
          Set<String> fields, CollectionOptions options, Set<String> albumIds, SecurityToken token)
          throws ProtocolException {
    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.GET_ALBUMS_QUERY);

    // set options
    CollOptsConverter.convert(options, query);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));
    query.setParameter(ShindigNativeQueries.APP_ID, appId);

    if (fields != null) {
      final List<String> fieldList = new ArrayList<String>(fields);
      query.setParameter(ShindigNativeQueries.FIELD_LIST, fieldList);
    }

    if (albumIds != null) {
      final List<String> idList = new ArrayList<String>(albumIds);
      query.setParameter(ShindigNativeQueries.ALBUM_ID_LIST, idList);
    }

    // execute
    final IQueryCallback callback = this.fQueryHandler.sendQuery(query);
    return convertList(callback);
  }

  @Override
  public Future<RestfulCollection<Album>> getAlbums(Set<UserId> userIds, GroupId groupId,
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
    query.setPayload(ShindigNativeQueries.GET_GROUP_ALBUMS_QUERY);

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
    return convertList(callback);
  }

  @Override
  public Future<Void> deleteAlbum(UserId userId, String appId, String albumId, SecurityToken token)
          throws ProtocolException {
    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.DELETE_ALBUM_QUERY);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));
    query.setParameter(ShindigNativeQueries.APP_ID, appId);
    query.setParameter(ShindigNativeQueries.ALBUM_ID, albumId);

    // execute
    try {
      this.fQueryHandler.sendQuery(query).get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not delete album", e);
    }

    return Futures.immediateFuture(null);
  }

  @Override
  public Future<Void> createAlbum(UserId userId, String appId, Album album, SecurityToken token)
          throws ProtocolException {
    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.DELETE_ALBUM_QUERY);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));
    query.setParameter(ShindigNativeQueries.APP_ID, appId);

    final Map<String, Object> albMap = new HashMap<String, Object>();
    final AlbumDTO dto = new AlbumDTO(albMap);
    dto.setData(album);
    dto.stripNullValues();
    query.setParameter(ShindigNativeQueries.ALBUM_OBJECT, albMap);

    // execute
    try {
      this.fQueryHandler.sendQuery(query).get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not create album", e);
    }

    return Futures.immediateFuture(null);
  }

  @Override
  public Future<Void> updateAlbum(UserId userId, String appId, Album album, String albumId,
          SecurityToken token) throws ProtocolException {
    // create query
    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
    query.setPayload(ShindigNativeQueries.UPDATE_ALBUM_QUERY);

    // set parameters for method
    query.setParameter(ShindigNativeQueries.USER_ID, userId.getUserId(token));
    query.setParameter(ShindigNativeQueries.APP_ID, appId);

    final Map<String, Object> albMap = new HashMap<String, Object>();
    final AlbumDTO dto = new AlbumDTO(albMap);
    dto.setData(album);
    dto.stripNullValues();
    query.setParameter(ShindigNativeQueries.ALBUM_OBJECT, albMap);

    query.setParameter(ShindigNativeQueries.ALBUM_ID, albumId);

    // execute
    try {
      this.fQueryHandler.sendQuery(query).get();
    } catch (final Exception e) {
      e.printStackTrace();
      this.fLogger.log(Level.SEVERE, "server error", e);
      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "could not update album", e);
    }

    return Futures.immediateFuture(null);
  }
}
