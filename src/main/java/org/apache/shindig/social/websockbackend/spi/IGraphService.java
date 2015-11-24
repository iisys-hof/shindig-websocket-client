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

import java.util.Set;
import java.util.concurrent.Future;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.opensocial.model.Group;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.UserId;

/**
 * Interface for special requests that are largely possible with reasonable performance through the
 * usage through the use of a graph database.
 */
public interface IGraphService {
  /**
   * Retrieves a list of people who are friends of friends up to a certain depth. Optionally known
   * people can be filtered out.
   *
   * @param userIds
   *          IDs of the friends to get friends for
   * @param depth
   *          graph traversal depth
   * @param unknown
   *          whether to only return unknown people
   * @param collectionOptions
   *          how to filter, sort and paginate the collection
   * @param fields
   *          which fields to retrieve
   * @param token
   *          security token
   * @return collection of friends of friends
   */
  public Future<RestfulCollection<Person>> getFriendsOfFriends(Set<UserId> userIds, int depth,
          boolean unknown, CollectionOptions collectionOptions, Set<String> fields,
          SecurityToken token);

  /**
   * Tries to determine the shortest path between two people over friendship relations and returns
   * the people passed along this path. Returns an empty set if origin and target are identical.
   *
   * @param userId
   *          ID of the user to determine a shortest path for
   * @param targetId
   *          ID of the user that is at the end of the path
   * @param collectionOptions
   *          how to filter, sort and paginate the collection
   * @param fields
   *          which fields to retrieve
   * @param token
   *          security token
   * @return path to target person over friend relations
   */
  public Future<RestfulCollection<Person>> getShortestPath(UserId userId, UserId targetId,
          CollectionOptions collectionOptions, Set<String> fields, SecurityToken token);

  /**
   * Determines which groups most friends are in, that the user is not yet in and returns the top
   * results.
   *
   * @param userId
   *          ID of the user to get a group recommendation for
   * @param number
   *          minimum number of friends in a group
   * @param collectionOptions
   *          how to filter, sort and paginate the collection
   * @param fields
   *          which fields to retrieve
   * @param token
   *          security token
   * @return groups that many friends are in
   */
  public Future<RestfulCollection<Group>> getGroupRecommendation(UserId userId, int number,
          CollectionOptions collectionOptions, Set<String> fields, SecurityToken token);

  /**
   * Determines which people most friends are friends with, that the user is not yet friends with
   * and returns the top results.
   *
   * @param userId
   *          ID of the user to get a friend recommendation for
   * @param number
   *          minimum number of friends who are friends with a person
   * @param collectionOptions
   *          how to filter, sort and paginate the collection
   * @param fields
   *          which fields to retrieve
   * @param token
   *          security token
   * @return friends that many friends are friends with
   */
  public Future<RestfulCollection<Person>> getFriendRecommendation(UserId userId, int number,
          CollectionOptions collectionOptions, Set<String> fields, SecurityToken token);
}
