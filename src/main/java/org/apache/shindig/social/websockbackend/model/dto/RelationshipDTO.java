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
package org.apache.shindig.social.websockbackend.model.dto;

/**
 * DTO representing a simple typed relationship, without a direction and properties.
 */
public class RelationshipDTO {
  private final String fType;

  /**
   * Creates a relationship with the given type which should include a direction. The given type
   * should not be null.
   *
   * @param type
   *          type of the relationship
   */
  public RelationshipDTO(String type) {
    this.fType = type;
  }

  /**
   * @return type of the relationship
   */
  public String getType() {
    return this.fType;
  }

  // TODO: additional direction?

  /**
   * @return true, to indicate in the resulting JSON object that this is a relationship
   */
  public boolean getRelationship() {
    return true;
  }

  // TODO: key-value fields?
}
