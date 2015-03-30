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
package org.apache.shindig.social.websockbackend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletResponse;

import org.apache.shindig.config.ContainerConfig;
import org.apache.shindig.protocol.HandlerPreconditions;
import org.apache.shindig.protocol.Operation;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.protocol.Service;
import org.apache.shindig.social.opensocial.model.Organization;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.service.SocialRequestItem;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.websockbackend.Constants;
import org.apache.shindig.social.websockbackend.model.IExtOrgPerson;
import org.apache.shindig.social.websockbackend.model.IExtOrganization;
import org.apache.shindig.social.websockbackend.spi.IExtPersonService;
import org.apache.shindig.social.websockbackend.spi.IFriendService;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.Futures;
import com.google.inject.Inject;

/**
 * Extension of shindig's person interface with modified methods to update users and create
 * friendships.
 *
 * Derived from Apache Shindig's PersonHandler.
 */
@Service(name = "people", path = "/{userId}+/{groupId}/{personId}+")
public class ExtPersonHandler {
  private final IExtPersonService fPersonSPI;
  private final IFriendService fFriendSPI;

  /**
   * Creates an extended person service handler, exposing creation, deletion and friendship methods.
   * Throws a NullPointerException if any of the given services is null.
   *
   * @param personService
   *          person service to handle requests
   * @param friendService
   *          friend service to handle requests
   * @param config
   *          container configuration object
   */
  @Inject
  public ExtPersonHandler(IExtPersonService personService, IFriendService friendService,
          ContainerConfig config) {
    if (personService == null) {
      throw new NullPointerException("person service was null");
    }
    if (friendService == null) {
      throw new NullPointerException("friendship service was null");
    }

    this.fPersonSPI = personService;
    this.fFriendSPI = friendService;
  }

  /**
   * Modified to enable friend requests requests. Allowed end-points /people/{userId}+/{groupId}
   * /people/{userId}/{groupId}/{optionalPersonId}+
   *
   * examples: /people/john.doe/@all /people/john.doe/@friends /people/john.doe/@self
   */
  @Operation(httpMethods = "GET")
  public Future<?> get(SocialRequestItem request) throws ProtocolException {
    final GroupId groupId = request.getGroup();
    final Set<String> optionalPersonId = ImmutableSet.copyOf(request.getListParameter("personId"));
    final Set<String> fields = request.getFields();
    final Set<UserId> userIds = request.getUsers();

    // Preconditions
    HandlerPreconditions.requireNotEmpty(userIds, "No userId specified");
    if (userIds.size() > 1 && !optionalPersonId.isEmpty()) {
      throw new IllegalArgumentException("Cannot fetch personIds for multiple userIds");
    }

    final CollectionOptions options = new CollectionOptions(request);

    if (userIds.size() == 1) {
      if (optionalPersonId.isEmpty()) {
        if (groupId.getType() == GroupId.Type.self) {
          // If a filter is set then we have to call getPeople(),
          // otherwise use the simpler getPerson()
          if (options.getFilter() != null) {
            Person p = null;
            RestfulCollection<Person> people = null;
            try {
              people = this.fPersonSPI.getPeople(userIds, groupId, options, fields,
                      request.getToken()).get();
            } catch (final Exception e) {
              throw new RuntimeException(e);
            }

            if (people.getList() != null && people.getList().size() > 0) {
              p = people.getList().get(0);
            }

            return Futures.immediateFuture(p);
          } else {
            return this.fPersonSPI.getPerson(userIds.iterator().next(), fields, request.getToken());
          }
        } else if (Constants.FRIEND_REQUESTS.equals(groupId.getObjectId())) {
          return this.fFriendSPI.getRequests(userIds.iterator().next(), options, fields,
                  request.getToken());
        } else {
          return this.fPersonSPI.getPeople(userIds, groupId, options, fields, request.getToken());
        }
      } else if (optionalPersonId.size() == 1) {
        // TODO: Add some crazy concept to handle the userId?
        final Set<UserId> optionalUserIds = ImmutableSet.of(new UserId(UserId.Type.userId,
                optionalPersonId.iterator().next()));

        Person p = null;
        RestfulCollection<Person> people = null;

        try {
          people = this.fPersonSPI.getPeople(optionalUserIds, new GroupId(GroupId.Type.self, null),
                  options, fields, request.getToken()).get();
        } catch (final Exception e) {
          throw new RuntimeException(e);
        }

        if (people.getList() != null && people.getList().size() > 0) {
          p = people.getList().get(0);
        }

        return Futures.immediateFuture(p);
      } else {
        final ImmutableSet.Builder<UserId> personIds = ImmutableSet.builder();
        for (final String pid : optionalPersonId) {
          personIds.add(new UserId(UserId.Type.userId, pid));
        }
        // Every other case is a collection response of optional person
        // IDs
        return this.fPersonSPI.getPeople(personIds.build(), new GroupId(GroupId.Type.self, null),
                options, fields, request.getToken());
      }
    }

    // Every other case is a collection response.
    return this.fPersonSPI.getPeople(userIds, groupId, options, fields, request.getToken());
  }

  /**
   * Modified to support extended model. Allowed end-points /people/{userId}/{groupId}
   *
   * examples: /people/john.doe/@all /people/john.doe/@friends /people/john.doe/@self
   */
  @Operation(httpMethods = "PUT", bodyParam = "person")
  public Future<?> update(SocialRequestItem request) throws ProtocolException {
    final Set<UserId> userIds = request.getUsers();

    // Enforce preconditions - exactly one user is specified
    HandlerPreconditions.requireNotEmpty(userIds, "No userId specified");
    HandlerPreconditions.requireSingular(userIds, "Multiple userIds not supported");

    // get properly converted organizations
    final IExtOrgPerson orgPerson = request.getTypedParameter("person", IExtOrgPerson.class);
    final Person person = request.getTypedParameter("person", Person.class);

    // replace improperly converted organizations
    final List<IExtOrganization> extOrgs = orgPerson.getOrganizations();
    List<Organization> orgs = person.getOrganizations();
    if (extOrgs != null) {
      if (orgs == null) {
        orgs = new ArrayList<Organization>();
        person.setOrganizations(orgs);
      } else {
        orgs.clear();
      }

      orgs.addAll(extOrgs);
    }

    // Update person and return it
    return this.fPersonSPI.updatePerson(Iterables.getOnlyElement(userIds), person,
            request.getToken());
  }

  /**
   * Handles a POST-request with the parameters represented by the given request item, creates the
   * contained person an returns it. Throws a NullPointerException if the given request item is
   * null.
   *
   * @param request
   *          item containing information about the request
   * @return people matching the request
   * @throws ProtocolException
   *           if the request is flawed
   */
  @Operation(httpMethods = "POST", bodyParam = "person")
  public Future<?> create(SocialRequestItem request) throws ProtocolException {
    final GroupId groupId = request.getGroup();

    // get properly converted organizations
    final IExtOrgPerson orgPerson = request.getTypedParameter("person", IExtOrgPerson.class);
    final Person person = request.getTypedParameter("person", Person.class);

    // replace improperly converted organizations
    final List<IExtOrganization> extOrgs = orgPerson.getOrganizations();
    List<Organization> orgs = person.getOrganizations();
    if (extOrgs != null) {
      if (orgs == null) {
        orgs = new ArrayList<Organization>();
        person.setOrganizations(orgs);
      } else {
        orgs.clear();
      }

      orgs.addAll(extOrgs);
    }

    Future<?> response = null;

    if (groupId.getType() == GroupId.Type.friends) {
      // TODO: throw Exception when unusable
      final UserId userId = request.getUsers().iterator().next();
      response = this.fFriendSPI.requestFriendship(userId, person, request.getToken());
    } else {
      response = this.fPersonSPI.createPerson(person, request.getToken());
    }

    return response;
  }

  /**
   * Handles a deletion request represented by the given request item and deletes the person with
   * the given ID. Throws a NullPointerException if the given request item is null.
   *
   * @param request
   *          item containing information about the request
   * @return nothing
   * @throws ProtocolException
   *           if the request is flawed
   */
  @Operation(httpMethods = "DELETE", bodyParam = "person")
  public Future<?> delete(SocialRequestItem request) throws ProtocolException {
    final Set<UserId> idSet = request.getUsers();
    if (idSet.size() != 1) {
      throw new ProtocolException(HttpServletResponse.SC_NOT_ACCEPTABLE,
              "This method requires exactly one user ID.");
    }
    final UserId id = idSet.iterator().next();

    final GroupId groupId = request.getGroup();

    final Person p = request.getTypedParameter("person", Person.class);

    Future<?> response = null;

    if (groupId.getType() == GroupId.Type.friends) {
      response = this.fFriendSPI.denyFriendship(id, p, request.getToken());
    } else {
      response = this.fPersonSPI.deletePerson(id, request.getToken());
    }

    return response;
  }
}
