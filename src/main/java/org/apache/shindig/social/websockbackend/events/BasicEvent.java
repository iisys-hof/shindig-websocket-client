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
package org.apache.shindig.social.websockbackend.events;

import java.util.Map;

import org.apache.shindig.auth.SecurityToken;

/**
 * Basic event implementation with setters and getters and no initial internal miscellaneous data
 * map. A miscellaneous data map can be set after creation.
 */
public class BasicEvent implements IShindigEvent {
  private final ShindigEventType fType;
  private Object fPayload;
  private SecurityToken fToken;

  private Map<String, String> fProperties;

  /**
   * Creates a basic event of the given type. The given type must not be null.
   *
   * @param type
   *          type of the new event
   */
  public BasicEvent(ShindigEventType type) {
    if (type == null) {
      throw new NullPointerException("event type must not be null");
    }

    this.fType = type;
  }

  public ShindigEventType getType() {
    return this.fType;
  }

  @Override
  public Object getPayload() {
    return this.fPayload;
  }

  /**
   * @param payload
   *          new payload object for the event
   */
  public void setPayload(Object payload) {
    this.fPayload = payload;
  }

  @Override
  public SecurityToken getToken() {
    return this.fToken;
  }

  /**
   * @param token
   *          new security token for the event
   */
  public void setToken(SecurityToken token) {
    this.fToken = token;
  }

  @Override
  public Map<String, String> getProperties() {
    return this.fProperties;
  }

  /**
   * @param props
   *          new internal property map
   */
  public void setProperties(Map<String, String> props) {
    this.fProperties = props;
  }

  @Override
  public String toString() {
    return "{ShindigEvent,type=" + this.fType + ",payload=" + this.fPayload + ",props="
            + this.fProperties + "}";
  }
}
