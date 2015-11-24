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
package org.apache.shindig.social.websockbackend.service;

import java.util.Set;
import java.util.concurrent.Future;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.Operation;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.Service;
import org.apache.shindig.social.opensocial.service.SocialRequestItem;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.websockbackend.spi.ISkillService;

import com.google.inject.Inject;

/**
 * SCHub-specific service handler for skill management. It handles retrieval of people's linked
 * skills, adding skill links to people and removing them again.
 */
@Service(name = "skills", path = "/{userId}")
public class SkillHandler {
  private final ISkillService fSkillSPI;

  /**
   * Creates an skill management handler using the given skill service to retrieve data. Throws a
   * NullPointerException if the given service is null.
   *
   * @param skillService
   *          skill service to use
   */
  @Inject
  public SkillHandler(ISkillService skillService) {
    if (skillService == null) {
      throw new NullPointerException("skill service was null");
    }

    this.fSkillSPI = skillService;
  }

  /**
   * Retrieves the skills that were linked to the specified person, including the people who linked
   * them for each entry. This GET-request only requires the userId as a path parameter.
   *
   * @param request
   *          item containing information about the request
   * @return skills linked to the specified person and people linking it
   * @throws ProtocolException
   *           if the request is flawed
   */
  @Operation(httpMethods = "GET", path = "/{userId}")
  public Future<?> getSkills(final SocialRequestItem request) throws ProtocolException {
    final CollectionOptions collOpts = new CollectionOptions(request);
    final SecurityToken token = request.getToken();

    final Set<UserId> userIds = request.getUsers();
    final UserId userId = userIds.iterator().next();

    final Future<?> result = this.fSkillSPI.getSkills(userId, collOpts, token);

    return result;
  }

  /**
   * Links a skill to a user, registering the "viewer" from the security token as the person who
   * created the link. This GET-request requires the userId and the skill as path parameters. The
   * linking user must be set as the "viewer" in the attached security token.
   *
   * @param request
   *          item containing information about the request
   * @return empty future
   * @throws ProtocolException
   *           if the request is flawed
   */
  @Operation(httpMethods = "POST", path = "/{userId}/{skill}")
  public Future<?> addSkill(final SocialRequestItem request) throws ProtocolException {
    final SecurityToken token = request.getToken();

    final Set<UserId> userIds = request.getUsers();
    final UserId userId = userIds.iterator().next();

    final String skill = request.getParameter("skill");
    final Future<?> result = this.fSkillSPI.addSkill(userId, skill, token);

    return result;
  }

  /**
   * Removes an existing from a the given user to the given skill, in the name of the user noted as
   * the "viewer" in the security token. This GET-request requires the userId and the skill as path
   * parameters. The linking user must be set as the "viewer" in the attached security token.
   *
   * @param request
   *          item containing information about the request
   * @return empty future
   * @throws ProtocolException
   *           if the request is flawed
   */
  @Operation(httpMethods = "DELETE", path = "/{userId}/{skill}")
  public Future<?> removeSkill(final SocialRequestItem request) throws ProtocolException {
    final SecurityToken token = request.getToken();

    final Set<UserId> userIds = request.getUsers();
    final UserId userId = userIds.iterator().next();

    final String skill = request.getParameter("skill");
    final Future<?> result = this.fSkillSPI.removeSkill(userId, skill, token);

    return result;
  }
}
