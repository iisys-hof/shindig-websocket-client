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

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.Operation;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.Service;
import org.apache.shindig.social.opensocial.model.Organization;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.service.SocialRequestItem;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.opensocial.spi.UserId.Type;
import org.apache.shindig.social.websockbackend.model.IExtOrgPerson;
import org.apache.shindig.social.websockbackend.model.IExtOrganization;
import org.apache.shindig.social.websockbackend.spi.IExtPersonService;
import org.apache.shindig.social.websockbackend.spi.IGraphService;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

/**
 * Extension of Shindig's person interface with the additional methods to create users and get a
 * list of all available users and additional graph-related requests.
 */
@Service(name = "user", path = "/{userId}+/{function}")
public class UserHandler {
  private static final Set<String> DEF_FIELDS = ImmutableSet.of(Person.Field.ID.toString(),
          Person.Field.NAME.toString());
  private final IExtPersonService fPersonSPI;
  private final IGraphService fGraphService;

  /**
   * Creates an extended person handler using the given extended person service and graph service to
   * retrieve data. Throws a NullPointerException if the given service is null.
   *
   * @param people
   *          extended person service to use
   * @param graphService
   *          graph service to use
   */
  @Inject
  public UserHandler(IExtPersonService people, IGraphService graphService) {
    if (people == null) {
      throw new NullPointerException("extended person service was null");
    }
    if (graphService == null) {
      throw new NullPointerException("graph service was null");
    }

    this.fGraphService = graphService;
    this.fPersonSPI = people;
  }

  /**
   * Handles a GET-request with the parameters represented by the given request item and returns the
   * people or groups that were found. Throws a NullPointerException if the given request item is
   * null.
   *
   * @param request
   *          item containing information about the request
   * @return people all available people
   * @throws ProtocolException
   *           if the request is flawed
   */
  @Operation(httpMethods = "GET", path = "/")
  public Future<?> getAll(final SocialRequestItem request) throws ProtocolException {
    final CollectionOptions collOpts = new CollectionOptions(request);
    final SecurityToken token = request.getToken();

    // limit default fields for people
    Set<String> fields = request.getFields();
    if (fields.isEmpty()) {
      fields = UserHandler.DEF_FIELDS;
    }

    return this.fPersonSPI.getAllPeople(collOpts, fields, token);
  }

  /**
   * Handles a GET-request with the parameters represented by the given request item and returns the
   * people or groups that were found. Throws a NullPointerException if the given request item is
   * null.
   *
   * @param request
   *          item containing information about the request
   * @return people all available people
   * @throws ProtocolException
   *           if the request is flawed
   */
  @Operation(httpMethods = "GET", path = "/{userId}+/fof/{depth}/{unknown}")
  public Future<?> getFof(final SocialRequestItem request) throws ProtocolException {
    final CollectionOptions collOpts = new CollectionOptions(request);

    // limit default fields for people
    Set<String> fields = request.getFields();
    if (fields.isEmpty()) {
      fields = UserHandler.DEF_FIELDS;
    }

    final String depthString = request.getParameter("depth");
    final String unknownString = request.getParameter("unknown");

    int depth = 2;
    boolean unknown = true;

    if (depthString != null) {
      depth = Integer.parseInt(depthString);
    }

    if (unknownString != null) {
      unknown = Boolean.parseBoolean(unknownString);
    }

    final Future<?> result = this.fGraphService.getFriendsOfFriends(request.getUsers(), depth,
            unknown, collOpts, fields, request.getToken());

    return result;
  }

  /**
   * Handles a GET-request with the parameters represented by the given request item and returns the
   * people or groups that were found. Throws a NullPointerException if the given request item is
   * null.
   *
   * @param request
   *          item containing information about the request
   * @return people all available people
   * @throws ProtocolException
   *           if the request is flawed
   */
  @Operation(httpMethods = "GET", path = "/{userId}+/sfriend/{minFriends}")
  public Future<?> getSFriend(final SocialRequestItem request) throws ProtocolException {
    final Set<UserId> userIds = request.getUsers();
    final CollectionOptions collOpts = new CollectionOptions(request);

    // limit default fields for people
    Set<String> fields = request.getFields();
    if (fields.isEmpty()) {
      fields = UserHandler.DEF_FIELDS;
    }

    // TODO: throw Exception when unusable
    final UserId userId = userIds.iterator().next();

    final String numberString = request.getParameter("minFriends");

    int number = 1;

    if (numberString != null) {
      number = Integer.parseInt(numberString);
    }

    final Future<?> result = this.fGraphService.getFriendRecommendation(userId, number, collOpts,
            fields, request.getToken());

    return result;
  }

  /**
   * Handles a GET-request with the parameters represented by the given request item and returns the
   * people or groups that were found. Throws a NullPointerException if the given request item is
   * null.
   *
   * @param request
   *          item containing information about the request
   * @return people all available people
   * @throws ProtocolException
   *           if the request is flawed
   */
  @Operation(httpMethods = "GET", path = "/{userId}+/sgroup/{minFriends}")
  public Future<?> getSGroup(final SocialRequestItem request) throws ProtocolException {
    final Set<UserId> userIds = request.getUsers();
    final CollectionOptions collOpts = new CollectionOptions(request);
    final Set<String> fields = request.getFields();

    // TODO: throw Exception when unusable
    final UserId userId = userIds.iterator().next();

    final String numberString = request.getParameter("minFriends");

    int number = 1;

    if (numberString != null) {
      number = Integer.parseInt(numberString);
    }

    final Future<?> result = this.fGraphService.getGroupRecommendation(userId, number, collOpts,
            fields, request.getToken());

    return result;
  }

  /**
   * Handles a GET-request with the parameters represented by the given request item and returns the
   * people or groups that were found. Throws a NullPointerException if the given request item is
   * null.
   *
   * @param request
   *          item containing information about the request
   * @return people all available people
   * @throws ProtocolException
   *           if the request is flawed
   */
  @Operation(httpMethods = "GET", path = "/{userId}+/spath/{targetId}")
  public Future<?> getSPath(final SocialRequestItem request) throws ProtocolException {
    final Set<UserId> userIds = request.getUsers();
    final CollectionOptions collOpts = new CollectionOptions(request);

    // limit default fields for people
    Set<String> fields = request.getFields();
    if (fields.isEmpty()) {
      fields = UserHandler.DEF_FIELDS;
    }

    // TODO: throw Exception when unusable
    final UserId userId = userIds.iterator().next();

    final String targetString = request.getParameter("targetId");

    final UserId targetId = new UserId(Type.userId, targetString);

    final Future<?> result = this.fGraphService.getShortestPath(userId, targetId, collOpts, fields,
            request.getToken());

    return result;
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
  @Operation(httpMethods = "POST", path = "/", bodyParam = "person")
  public Future<?> create(final SocialRequestItem request) throws ProtocolException {
    // get properly converted organizations
    final IExtOrgPerson orgPerson = request.getTypedParameter("person", IExtOrgPerson.class);
    final Person person = request.getTypedParameter("person", Person.class);

    // replace improperly converted organizations
    if (orgPerson != null) {
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
    }

    return this.fPersonSPI.createPerson(person, request.getToken());
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
  @Operation(httpMethods = "DELETE", path = "/{userId}")
  public Future<?> delete(final SocialRequestItem request) throws ProtocolException {
    final Set<UserId> idSet = request.getUsers();
    if (idSet.size() != 1) {
      throw new ProtocolException(HttpServletResponse.SC_NOT_ACCEPTABLE,
              "This method requires exactly one user ID.");
    }
    final UserId id = idSet.iterator().next();

    return this.fPersonSPI.deletePerson(id, request.getToken());
  }
}
