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
package org.apache.shindig.social.websockbackend.spi;

import java.util.concurrent.Future;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.social.opensocial.model.Message;
import org.apache.shindig.social.opensocial.spi.MessageService;
import org.apache.shindig.social.opensocial.spi.UserId;

/**
 * Extended version of the message service with a message creation method that returns the created
 * message.
 */
public interface IExtMessageService extends MessageService {
  /**
   * Extends the createMessage-method by a return type.
   *
   * @param userId
   *          user ID of the creating user
   * @param appId
   *          ID of the creating application
   * @param msgCollId
   *          collection ID to create the message in
   * @param message
   *          message to create
   * @param token
   *          security token supplied
   * @return created message
   * @throws ProtocolException
   *           if creation fails
   */
  public Future<Message> createAndReturnMessage(UserId userId, String appId, String msgCollId,
          Message message, SecurityToken token) throws ProtocolException;
}
