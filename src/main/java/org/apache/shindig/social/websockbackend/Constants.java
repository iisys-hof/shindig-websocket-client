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
package org.apache.shindig.social.websockbackend;

/**
 * Class containing globally used constants.
 */
public class Constants {
  /**
   * String representation for unknown values.
   */
  public static final String UNKNOWN = "unbekannt";

  /**
   * Name for anonymous users.
   */
  public static final String ANONYMOUS_NAME = "Anonym";

  /**
   * Tag for the ID node in the database.
   */
  public static final String ID_NODE = "id";

  /**
   * Tag for person nodes in the database.
   */
  public static final String PERSON_NODES = "persons";

  /**
   * Tag for application nodes in the database.
   */
  public static final String APP_NODES = "applications";

  /**
   * Tag for group nodes in the database.
   */
  public static final String GROUP_NODES = "groups";

  /**
   * Tag for message nodes in the database.
   */
  public static final String MESSAGE_NODES = "messages";

  /**
   * Tag for activity entry nodes in the database.
   */
  public static final String ACTIVITY_ENTRY_NODES = "activityentry";

  /**
   * Tag for file nodes in the database.
   */
  public static final String FILE_NODES = "uploads";

  /**
   * Name string for the default in box of every user.
   */
  public static final String INBOX_NAME = "inbox";

  /**
   * Name for an application ID property.
   */
  public static final String APP_ID = "appId";

  /**
   * Group ID for retrieving friend requests.
   */
  public static final String FRIEND_REQUESTS = "@friendrequests";
}
