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
 * Interface for generic internal Shindig events.
 */
public interface IShindigEvent {
  /**
   * @return type of the event
   */
  public ShindigEventType getType();

  /**
   * Returns a potential Payload, i.e. objects that were created, manipulated or deleted or at least
   * their IDs.
   *
   * @return event's payload
   */
  public Object getPayload();

  /**
   * Returns a potential Security token the request causing the event was executed with. This can be
   * used to find out which user and application triggered it.
   *
   * @return SecurityToken the request was executed with
   */
  public SecurityToken getToken();

  /**
   * Returns a map of optional miscellaneous properties of this event providing context information
   *
   * @return a map of miscellaneous properites
   */
  public Map<String, String> getProperties();
}
