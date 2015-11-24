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

/**
 * List of internal event types.
 */
public enum ShindigEventType {
  // "listen to all events"
  ALL,

  // person service
  PROFILE_CREATED, PROFILE_UPDATED, PROFILE_DELETED,

  // activitystreams service
  ACTIVITY_CREATED, ACTIVITY_UPDATED, ACTIVITY_DELETED,

  // message service
  MESSAGE_SENT, MESSAGE_CREATED, MESSAGE_UPDATED, MESSAGE_DELETED;
}
