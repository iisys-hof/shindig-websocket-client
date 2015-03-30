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

import java.util.Set;
import java.util.concurrent.Future;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.UserId;

/**
 * Interface for a service handling friendships, requests, accepting, denying and deletion.
 */
public interface IFriendService {
  /**
   * Retrieves a list of people who requested a friendship with the specified user.
   *
   * @param userId
   *          ID of the user to get friend requests for
   * @param collectionOptions
   *          how to filter, sort and paginate the collection
   * @param fields
   *          which fields to retrieve
   * @param token
   *          security token
   * @return people who requested a friendship
   */
  public Future<RestfulCollection<Person>> getRequests(UserId userId,
          CollectionOptions collectionOptions, Set<String> fields, SecurityToken token);

  /**
   * Requests a friendship with the given target person or confirms a friendship if there already is
   * a request from that person.
   *
   * @param userId
   *          ID of the user requesting or confirming a friendship
   * @param target
   *          person to request or confirm a friendship with
   * @param token
   *          security token
   * @return empty future if successful
   */
  public Future<Void> requestFriendship(UserId userId, Person target, SecurityToken token);

  /**
   * Denies a friendship request from the given target person or deletes an existing friendship with
   * him or her.
   *
   * @param userId
   *          ID of the user denying or deleting a friendship
   * @param target
   *          person from whom to deny a friendship request
   * @param token
   *          security token
   * @return empty future if successful
   */
  public Future<Void> denyFriendship(UserId userId, Person target, SecurityToken token);
}
