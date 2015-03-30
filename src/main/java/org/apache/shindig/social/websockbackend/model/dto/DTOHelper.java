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
package org.apache.shindig.social.websockbackend.model.dto;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Helper class for the conversion process from database nodes to transferable objects. It contains
 * a map of JSON fields that were split up into multiple properties and sets of fields that are
 * mapped to relations or are context sensitive.
 */
public class DTOHelper {
  private final Map<String, List<String>> fSplitFields;
  private final Set<String> fRelMapped, fContextSensitive;

  /**
   * Creates a DTO helper, returning the given map and lists. Accepts null for all parameters.
   *
   * @param splitFields
   * @param relMapped
   * @param contextSensitive
   */
  public DTOHelper(Map<String, List<String>> splitFields, Set<String> relMapped,
          Set<String> contextSensitive) {
    this.fSplitFields = splitFields;
    this.fRelMapped = relMapped;
    this.fContextSensitive = contextSensitive;
  }

  /**
   * Delivers a map of an objects's attributes that have been split into several node properties or
   * null if there are none. Selective copy operations will copy the
   *
   * @return map of split attributes
   */
  public Map<String, List<String>> getSplitFields() {
    return this.fSplitFields;
  }

  /**
   * Returns a set of properties which are mapped to relationships instead of node properties or
   * null if there are none.
   *
   * @return list of properties mapped to relationships
   */
  public Set<String> getRelationshipMapped() {
    return this.fRelMapped;
  }

  /**
   * Returns a set of properties which are context sensitive and as such can't be retrieved from the
   * database directly or null if there are none.
   *
   * @return list of context sensitive properties
   */
  public Set<String> getContextSensitive() {
    return this.fContextSensitive;
  }
}
