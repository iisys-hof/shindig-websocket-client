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
package org.apache.shindig.social.websockbackend.model;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * Person interface only used to trick the parser into using our extended organization
 * implementation.
 */
@ImplementedBy(ExtOrgPersonImpl.class)
public interface IExtOrgPerson {
  /**
   * Get a list of current or past organizational affiliations of this Person.
   *
   * @return a list of Organization objects
   */
  public List<IExtOrganization> getOrganizations();

  /**
   * Set a list of current or past organizational affiliations of this Person.
   *
   * @param organizations
   *          a list of Organization objects
   */
  void setOrganizations(List<IExtOrganization> organizations);
}
