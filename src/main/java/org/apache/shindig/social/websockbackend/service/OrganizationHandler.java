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

import org.apache.shindig.protocol.Operation;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.Service;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.service.SocialRequestItem;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.websockbackend.spi.IOrganizationService;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

/**
 * Handler for an organization endpoint providing functionality regarding organizations and their
 * hierarchical structure.
 */
@Service(name = "organization", path = "/{userId}+/{function}")
public class OrganizationHandler {
  private static final Set<String> DEF_FIELDS = ImmutableSet.of(Person.Field.ID.toString(),
          Person.Field.NAME.toString());

  private final IOrganizationService fOrgs;

  /**
   * Creates a new organization handler using the given organization service to retrieve data. The
   * given service must not be null.
   *
   * @param orgs
   *          organization service to use
   */
  @Inject
  public OrganizationHandler(IOrganizationService orgs) {
    if (orgs == null) {
      throw new NullPointerException("organization service was null");
    }
    this.fOrgs = orgs;
  }

  /**
   * Handles a GET-request with the parameters represented by the given request item and returns the
   * hierarchical path between the specified people via userId and targetId. Throws a
   * NullPointerException if the given request item is null.
   *
   * @param request
   *          item containing information about the request
   * @return hierarchical path between the specified people
   * @throws ProtocolException
   *           if the request is flawed
   */
  @Operation(httpMethods = "GET", path = "/{userId}+/hierarchypath/{targetId}")
  public Future<?> getHierarchyPath(final SocialRequestItem request) {
    Set<String> fields = request.getFields();
    if (fields.isEmpty()) {
      fields = OrganizationHandler.DEF_FIELDS;
    }

    final Set<UserId> userIds = request.getUsers();
    // TODO: throw Exception when unusable
    final UserId userId = userIds.iterator().next();
    final String target = request.getParameter("targetId");

    return this.fOrgs.getHierarchyPath(userId, target, fields, request.getToken());
  }
}
