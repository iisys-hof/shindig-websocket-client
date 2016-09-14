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
import org.apache.shindig.social.opensocial.spi.UserId;

/**
 * Interface for a service providing information about organizations and their hierarchical
 * structure.
 */
public interface IOrganizationService {
  /**
   * Retrieves the hierarchical path between two people over "manager of" relationships, including
   * paths that go up and down in the hierachy. The given user IDs must not be null.
   *
   * @param userId
   *          ID of the user at the beginning of the path
   * @param target
   *          ID of the user at the end of the path
   * @param fields
   *          list of fields to retrieve for people
   * @param token
   *          security token of the request
   * @return hierarchical path between the two people
   */
  public Future<RestfulCollection<Object>> getHierarchyPath(UserId userId, String target,
          Set<String> fields, SecurityToken token);
}
