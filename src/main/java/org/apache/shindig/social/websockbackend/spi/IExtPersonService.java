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
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.PersonService;
import org.apache.shindig.social.opensocial.spi.UserId;

/**
 * Extended version of shindig's person service with the additional methods to create users and get
 * a list of all available users.
 */
public interface IExtPersonService extends PersonService {
  /**
   * Retrieves all available people visible to the user as defined by the requested fields and
   * collection options. None of the parameters may be null.
   *
   * @param collectionOptions
   *          options concerning filtering and sorting
   * @param fields
   *          properties to retrieve
   * @param token
   *          security token
   * @return sorted and filtered collection of all users
   * @throws ProtocolException
   *           if the retrieval fails
   */
  public Future<RestfulCollection<Person>> getAllPeople(CollectionOptions collectionOptions,
          Set<String> fields, SecurityToken token) throws ProtocolException;

  /**
   * Creates a new user from the given person object. This should only be working if done by an
   * authorized user. None of the parameters may be null.
   *
   * @param person
   *          person object to take as a blueprint
   * @param token
   *          security token
   * @return newly created person object
   * @throws ProtocolException
   *           if user creation fails
   */
  public Future<Person> createPerson(Person person, SecurityToken token) throws ProtocolException;

  /**
   * Deletes the user with the given ID. This should only be working if done by an authorized user.
   *
   * @param id
   *          id of the person to delete
   * @param token
   *          security token
   * @return nothing
   * @throws ProtocolException
   *           if user deletion fails
   */
  public Future<Void> deletePerson(UserId id, SecurityToken token) throws ProtocolException;
}
