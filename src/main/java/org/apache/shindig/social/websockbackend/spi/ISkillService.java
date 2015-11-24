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

import java.util.concurrent.Future;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.websockbackend.model.ISkillSet;

/**
 * Interface for a service providing skill name autocompletion, skill retrieval for people,
 * including the people who linked the skills, linking of skills to people and removing skill links.
 */
public interface ISkillService {

  /**
   * Retrieves autocompletions suggestions for skill names based on the given fragment.
   *
   * @param fragment
   *          text fragment to autocomplete
   * @param collectionOptions
   *          how to filter, sort and paginate the collection
   * @param token
   *          security token
   * @return suggested autocompleted skill names
   */
  public Future<RestfulCollection<String>> getSkillAutocomp(String fragment,
          CollectionOptions collectionOptions, SecurityToken token);

  /**
   * Retrieves the skills that were linked to the specified person, including the people who linked
   * them for each entry.
   *
   * @param userId
   *          ID of the user to get skill links for
   * @param collectionOptions
   *          how to filter, sort and paginate the collection
   * @param token
   *          security token
   * @return skills linked to the person and people who linked them
   */
  public Future<RestfulCollection<ISkillSet>> getSkills(UserId userId,
          CollectionOptions collectionOptions, SecurityToken token);

  /**
   * Links a skill to a user, registering the "viewer" from the security token as the person who
   * created the link.
   *
   * @param userId
   *          ID of the user to add a skill link for
   * @param skill
   *          skill to link the user to
   * @param token
   *          security token
   * @return empty future
   */
  public Future<Void> addSkill(UserId userId, String skill, SecurityToken token);

  /**
   * Removes an existing from a the given user to the given skill, in the name of the user noted as
   * the "viewer" in the security token.
   *
   * @param userId
   *          ID of the user to remove a skill link from
   * @param skill
   *          skill to delete a link to
   * @param token
   *          security token
   * @return empty future
   */
  public Future<Void> removeSkill(UserId userId, String skill, SecurityToken token);
}
