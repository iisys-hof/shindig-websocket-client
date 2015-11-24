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
package org.apache.shindig.social.websockbackend.spi.cypher;

import java.util.Set;
import java.util.concurrent.Future;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.spi.ActivityService;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.UserId;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Implementation of the ActivityService interface retrieving activity data from a remote Neo4j
 * graph database over a websocket using Cypher. DISCONTINUED
 */
@Singleton
public class WsCypherActivitySPI implements ActivityService {
  /**
   * Empty constructor.
   */
  @Inject
  public WsCypherActivitySPI() {

  }

  @Override
  public Future<RestfulCollection<Activity>> getActivities(Set<UserId> userIds, GroupId groupId,
          String appId, Set<String> fields, CollectionOptions options, SecurityToken token)
          throws ProtocolException {
    return null;
  }

  @Override
  public Future<RestfulCollection<Activity>> getActivities(UserId userId, GroupId groupId,
          String appId, Set<String> fields, CollectionOptions options, Set<String> activityIds,
          SecurityToken token) throws ProtocolException {
    return null;
  }

  @Override
  public Future<Activity> getActivity(UserId userId, GroupId groupId, String appId,
          Set<String> fields, String activityId, SecurityToken token) throws ProtocolException {
    return null;
  }

  @Override
  public Future<Void> deleteActivities(UserId userId, GroupId groupId, String appId,
          Set<String> activityIds, SecurityToken token) throws ProtocolException {
    return null;
  }

  @Override
  public Future<Void> createActivity(UserId userId, GroupId groupId, String appId,
          Set<String> fields, Activity activity, SecurityToken token) throws ProtocolException {
    return null;
  }
}
