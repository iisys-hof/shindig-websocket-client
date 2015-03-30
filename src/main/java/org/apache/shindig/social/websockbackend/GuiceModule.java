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
package org.apache.shindig.social.websockbackend;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.shindig.social.core.oauth2.OAuth2DataService;
import org.apache.shindig.social.core.oauth2.OAuth2DataServiceImpl;
import org.apache.shindig.social.core.oauth2.OAuth2Service;
import org.apache.shindig.social.core.oauth2.OAuth2ServiceImpl;
import org.apache.shindig.social.opensocial.oauth.OAuthDataStore;
import org.apache.shindig.social.opensocial.spi.ActivityService;
import org.apache.shindig.social.opensocial.spi.ActivityStreamService;
import org.apache.shindig.social.opensocial.spi.AlbumService;
import org.apache.shindig.social.opensocial.spi.AppDataService;
import org.apache.shindig.social.opensocial.spi.GroupService;
import org.apache.shindig.social.opensocial.spi.MediaItemService;
import org.apache.shindig.social.opensocial.spi.MessageService;
import org.apache.shindig.social.opensocial.spi.PersonService;
import org.apache.shindig.social.sample.oauth.SampleOAuthDataStore;
import org.apache.shindig.social.websockbackend.spi.IExtPersonService;
import org.apache.shindig.social.websockbackend.spi.IFriendService;
import org.apache.shindig.social.websockbackend.spi.IGraphService;
import org.apache.shindig.social.websockbackend.spi.WsNativeActivitySPI;
import org.apache.shindig.social.websockbackend.spi.WsNativeActivityStreamSPI;
import org.apache.shindig.social.websockbackend.spi.WsNativeAlbumSPI;
import org.apache.shindig.social.websockbackend.spi.WsNativeAppDataSPI;
import org.apache.shindig.social.websockbackend.spi.WsNativeFriendSPI;
import org.apache.shindig.social.websockbackend.spi.WsNativeGraphSPI;
import org.apache.shindig.social.websockbackend.spi.WsNativeGroupSPI;
import org.apache.shindig.social.websockbackend.spi.WsNativeMediaItemSPI;
import org.apache.shindig.social.websockbackend.spi.WsNativeMessageSPI;
import org.apache.shindig.social.websockbackend.spi.WsNativePersonSPI;
import org.apache.shindig.social.websockbackend.spi.cypher.WsCypherActivitySPI;
import org.apache.shindig.social.websockbackend.spi.cypher.WsCypherActivityStreamSPI;
import org.apache.shindig.social.websockbackend.spi.cypher.WsCypherGraphSPI;
import org.apache.shindig.social.websockbackend.spi.cypher.WsCypherGroupSPI;
import org.apache.shindig.social.websockbackend.spi.cypher.WsCypherMessageSPI;
import org.apache.shindig.social.websockbackend.spi.cypher.WsCypherPersonSPI;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import de.hofuniversity.iisys.neo4j.websock.MultiWebSocketConnector;
import de.hofuniversity.iisys.neo4j.websock.WebSocketConnector;
import de.hofuniversity.iisys.neo4j.websock.queries.IQueryHandler;

/**
 * Module for Google Guice, used to link all implementations of this component to their interfaces
 * for later dependency injection.
 */
public class GuiceModule extends AbstractModule {
  private static final String SUBSYSTEM_NAME = "Neo4j Websocket Back-End";

  private static final String WEBSOCKET_URI = "websocket.uri";
  private static final String WEBSOCKET_CONNS = "websocket.connections";

  private static final String WEBSOCKET_FORMAT = "websocket.format";
  private static final String WEBSOCKET_COMP = "websocket.compression";

  private static final String QUERY_TIMEOUT = "websocket.query.timeout";
  
  private static final String BACKEND_IMPL = "shindig.backend.implementation";

  private static final String AUTH_USER = "websocket.auth.user";
  private static final String AUTH_PASS = "websocket.auth.password";
  private static final String AUTH_PASS_HASH =
          "websocket.auth.password.hashed";

  @Override
  protected void configure() {
    final Logger logger = Logger.getLogger(GuiceModule.SUBSYSTEM_NAME);
    
    try {
      final WebsockConfig config = bindConnector();

      // TODO:?
      // the JSON implementation of some modules need this path
      bind(String.class).annotatedWith(Names.named("shindig.canonical.json.db")).toInstance(
              "sampledata/canonicaldb.json");

      // bind the graph back-end
      String implementation = config.getProperty(BACKEND_IMPL);
      
      if(implementation != null
              && implementation.equals("cypher"))
      {
        //Cypher version (incomplete)
        logger.info("binding Cypher back-end routines");
        
        // official services
        this.bind(ActivityService.class).to(WsCypherActivitySPI.class);
        this.bind(ActivityStreamService.class).to(WsCypherActivityStreamSPI.class);
        this.bind(GroupService.class).to(WsCypherGroupSPI.class);
        this.bind(MessageService.class).to(WsCypherMessageSPI.class);
        this.bind(PersonService.class).to(WsCypherPersonSPI.class);
        
        // own services
        this.bind(IExtPersonService.class).to(WsCypherPersonSPI.class);
        this.bind(IGraphService.class).to(WsCypherGraphSPI.class);

        //services that do not have a Cypher implementation yet
        this.bind(IFriendService.class).to(WsNativeFriendSPI.class);
        this.bind(AlbumService.class).to(WsNativeAlbumSPI.class);
        this.bind(AppDataService.class).to(WsNativeAppDataSPI.class);
        this.bind(MediaItemService.class).to(WsNativeMediaItemSPI.class);
      }
      else
      {
        //Native version
        logger.info("binding native back-end routines");
        
        // official services
        this.bind(ActivityService.class).to(WsNativeActivitySPI.class);
        this.bind(ActivityStreamService.class).to(WsNativeActivityStreamSPI.class);
        this.bind(AlbumService.class).to(WsNativeAlbumSPI.class);
        this.bind(AppDataService.class).to(WsNativeAppDataSPI.class);
        this.bind(GroupService.class).to(WsNativeGroupSPI.class);
        this.bind(MediaItemService.class).to(WsNativeMediaItemSPI.class);
        this.bind(MessageService.class).to(WsNativeMessageSPI.class);
        this.bind(PersonService.class).to(WsNativePersonSPI.class);

        // own services
        this.bind(IExtPersonService.class).to(WsNativePersonSPI.class);
        this.bind(IFriendService.class).to(WsNativeFriendSPI.class);
        this.bind(IGraphService.class).to(WsNativeGraphSPI.class);
      }

      // TODO:?
      // those authorization classes might have to be extended later
      this.bind(OAuthDataStore.class).to(SampleOAuthDataStore.class);
      this.bind(OAuth2Service.class).to(OAuth2ServiceImpl.class);
      this.bind(OAuth2DataService.class).to(OAuth2DataServiceImpl.class);
    } catch (final Exception e) {
      String message = e.getMessage() + '\n';
      for (final StackTraceElement ste : e.getStackTrace()) {
        message += ste.toString() + '\n';
      }
      logger.log(Level.SEVERE, message);
      e.printStackTrace();
    }
  }

  private WebsockConfig bindConnector() throws Exception {
    // read configuration
    final WebsockConfig config = new WebsockConfig();

    final int connNum = Integer.parseInt(config.getProperty(GuiceModule.WEBSOCKET_CONNS));

    final String format = config.getProperty(GuiceModule.WEBSOCKET_FORMAT);
    final String compression = config.getProperty(GuiceModule.WEBSOCKET_COMP);

    long timeout = 30;
    try {
      timeout = Long.parseLong(config.getProperty(GuiceModule.QUERY_TIMEOUT));
    } catch (final Exception e) {
      e.printStackTrace();
    }

    final String user = config.getProperty(GuiceModule.AUTH_USER);
    final String password = config.getProperty(GuiceModule.AUTH_PASS);

    boolean passHashed = false;
    try {
      passHashed = Boolean.parseBoolean(config.getProperty(GuiceModule.AUTH_PASS_HASH));
    } catch (final Exception e) {
      e.printStackTrace();
    }

    // get connection URIs
    final List<String> uris = new ArrayList<String>();
    int i = 0;
    String uri = config.getProperty(GuiceModule.WEBSOCKET_URI + i);
    while (uri != null) {
      uris.add(uri);
      uri = config.getProperty(GuiceModule.WEBSOCKET_URI + ++i);
    }

    // get configured Websocket, connect and bind
    if (uris.size() == 1 && connNum == 1) {
      // single connection
      final WebSocketConnector connector = new WebSocketConnector(uris.get(0), format, compression);

      if (user == null || password == null || user.isEmpty() || password.isEmpty()) {
        connector.connect();
      } else {
        connector.connect(user, password.toCharArray(), passHashed);
      }

      bind(WebSocketConnector.class).toInstance(connector);

      // TODO: terminate connection on shutdown

      // query handler for SPIs
      final IQueryHandler queryHandler = connector.getQueryHandler();
      bind(IQueryHandler.class).toInstance(queryHandler);
    } else if (uris.size() > 1 || connNum > 1) {
      // multiple connections
      final MultiWebSocketConnector connector = new MultiWebSocketConnector(uris, connNum, format,
              compression);

      if (user == null || password == null || user.isEmpty() || password.isEmpty()) {
        connector.connect();
      } else {
        connector.connect(user, password.toCharArray(), passHashed);
      }

      connector.getQueryHandler().setTimeout(timeout * 1000);
      bind(MultiWebSocketConnector.class).toInstance(connector);

      // TODO: terminate connection on shutdown

      // query handler for SPIs
      final IQueryHandler queryHandler = connector.getQueryHandler();
      bind(IQueryHandler.class).toInstance(queryHandler);
    } else if (uris.size() == 0) {
      throw new Exception("no websocket URIs defined");
    } else {
      throw new Exception("there needs to be at least one connection per URI");
    }
    
    return config;
  }
}
