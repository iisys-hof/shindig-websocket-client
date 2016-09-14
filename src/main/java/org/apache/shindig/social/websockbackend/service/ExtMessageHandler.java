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
package org.apache.shindig.social.websockbackend.service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletResponse;

import org.apache.shindig.protocol.HandlerPreconditions;
import org.apache.shindig.protocol.Operation;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.Service;
import org.apache.shindig.social.opensocial.model.Message;
import org.apache.shindig.social.opensocial.model.MessageCollection;
import org.apache.shindig.social.opensocial.service.SocialRequestItem;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.CollectionOptionsFactory;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.websockbackend.spi.IExtMessageService;

import com.google.inject.Inject;

/**
 * Extension of shindig's message service that returns a message object when sending messages.
 *
 * Derived from Apache Shindig's MessageHandler.
 */
@Service(name = "messages", path = "/{userId}+/{msgCollId}/{messageIds}+")
public class ExtMessageHandler {
  private final IExtMessageService service;
  private final CollectionOptionsFactory collectionOptionsFactory;

  @Inject
  public ExtMessageHandler(IExtMessageService service,
          CollectionOptionsFactory collectionOptionsFactory) {
    this.service = service;
    this.collectionOptionsFactory = collectionOptionsFactory;
  }

  @Operation(httpMethods = "DELETE")
  public Future<?> delete(SocialRequestItem request) throws ProtocolException {

    final Set<UserId> userIds = request.getUsers();
    final String msgCollId = request.getParameter("msgCollId");
    final List<String> messageIds = request.getListParameter("messageIds");

    HandlerPreconditions.requireNotEmpty(userIds, "No userId specified");
    HandlerPreconditions.requireSingular(userIds, "Multiple userIds not supported");

    if (msgCollId == null) {
      throw new ProtocolException(HttpServletResponse.SC_BAD_REQUEST,
              "A message collection is required");
    }

    final UserId user = request.getUsers().iterator().next();

    if (messageIds == null || messageIds.isEmpty()) {
      // MessageIds may be null if the complete collection should be deleted
      return this.service.deleteMessageCollection(user, msgCollId, request.getToken());
    }
    // Delete specific messages
    return this.service.deleteMessages(user, msgCollId, messageIds, request.getToken());
  }

  @Operation(httpMethods = "GET")
  public Future<?> get(SocialRequestItem request) throws ProtocolException {

    final Set<UserId> userIds = request.getUsers();
    final String msgCollId = request.getParameter("msgCollId");
    final List<String> messageIds = request.getListParameter("messageIds");

    final CollectionOptions options = this.collectionOptionsFactory.create(request);

    HandlerPreconditions.requireNotEmpty(userIds, "No userId specified");
    HandlerPreconditions.requireSingular(userIds, "Multiple userIds not supported");

    final UserId user = request.getUsers().iterator().next();

    if (msgCollId == null) {
      // No message collection specified, return list of message collections
      final Set<String> fields = request.getFields(MessageCollection.Field.ALL_FIELDS);
      return this.service.getMessageCollections(user, fields, options, request.getToken());
    }
    // If messageIds are specified return them, otherwise return entries in the given collection.
    final Set<String> fields = request.getFields(Message.Field.ALL_FIELDS);
    return this.service.getMessages(user, msgCollId, fields, messageIds, options,
            request.getToken());
  }

  /**
   * Creates a new message collection or message
   */
  @Operation(httpMethods = "POST", bodyParam = "entity")
  public Future<?> create(SocialRequestItem request) throws ProtocolException {

    final Set<UserId> userIds = request.getUsers();
    final String msgCollId = request.getParameter("msgCollId");
    final List<String> messageIds = request.getListParameter("messageIds");

    HandlerPreconditions.requireNotEmpty(userIds, "No userId specified");
    HandlerPreconditions.requireSingular(userIds, "Multiple userIds not supported");

    final UserId user = request.getUsers().iterator().next();

    if (msgCollId == null) {
      // Request to create a new message collection
      final MessageCollection msgCollection = request.getTypedParameter("entity",
              MessageCollection.class);

      return this.service.createMessageCollection(user, msgCollection, request.getToken());
    }

    // A message collection has been specified, allow for posting

    HandlerPreconditions.requireEmpty(messageIds, "Message IDs not allowed here, use PUT instead");

    final Message message = request.getTypedParameter("entity", Message.class);
    HandlerPreconditions.requireNotEmpty(message.getRecipients(), "No recipients specified");

    return this.service.createAndReturnMessage(userIds.iterator().next(), request.getAppId(),
            msgCollId, message, request.getToken());
  }

  /**
   * Handles modifying a message or a message collection.
   */
  @Operation(httpMethods = "PUT", bodyParam = "entity")
  public Future<?> modify(SocialRequestItem request) throws ProtocolException {

    final Set<UserId> userIds = request.getUsers();
    final String msgCollId = request.getParameter("msgCollId");
    final List<String> messageIds = request.getListParameter("messageIds");

    HandlerPreconditions.requireNotEmpty(userIds, "No userId specified");
    HandlerPreconditions.requireSingular(userIds, "Multiple userIds not supported");

    final UserId user = request.getUsers().iterator().next();

    if (msgCollId == null) {
      throw new ProtocolException(HttpServletResponse.SC_BAD_REQUEST,
              "A message collection is required");
    }

    if (messageIds.isEmpty()) {
      // No message IDs specified, this is a PUT to a message collection
      final MessageCollection msgCollection = request.getTypedParameter("entity",
              MessageCollection.class);
      if (msgCollection == null) {
        throw new ProtocolException(HttpServletResponse.SC_BAD_REQUEST,
                "cannot parse message collection");
      }

      // TODO, do more validation.

      return this.service.modifyMessageCollection(user, msgCollection, request.getToken());
    }

    HandlerPreconditions.requireSingular(messageIds, "Only one messageId at a time");

    final Message message = request.getTypedParameter("entity", Message.class);
    // TODO, do more validation.

    if (message == null || message.getId() == null) {
      throw new ProtocolException(HttpServletResponse.SC_BAD_REQUEST,
              "cannot parse message or missing ID");
    }

    return this.service.modifyMessage(user, msgCollId, messageIds.get(0), message,
            request.getToken());
  }

}
