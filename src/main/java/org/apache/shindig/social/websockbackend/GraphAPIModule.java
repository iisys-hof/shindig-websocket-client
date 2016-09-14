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

import java.util.HashSet;
import java.util.Set;

import javax.websocket.MessageHandler;

import org.apache.shindig.social.core.config.SocialApiGuiceModule;
import org.apache.shindig.social.opensocial.service.GroupHandler;
import org.apache.shindig.social.opensocial.service.PersonHandler;
import org.apache.shindig.social.websockbackend.service.AutoCompleteHandler;
import org.apache.shindig.social.websockbackend.service.ExtMessageHandler;
import org.apache.shindig.social.websockbackend.service.ExtPersonHandler;
import org.apache.shindig.social.websockbackend.service.OrganizationHandler;
import org.apache.shindig.social.websockbackend.service.ProcessMiningHandler;
import org.apache.shindig.social.websockbackend.service.SkillHandler;
import org.apache.shindig.social.websockbackend.service.UserHandler;

/**
 * Extended API Guice module that adds graph-optimized functionality.
 */
public class GraphAPIModule extends SocialApiGuiceModule {
  @Override
  protected Set<Class<?>> getHandlers() {
    // shindig's own handlers
    final Set<Class<?>> handlers = new HashSet<Class<?>>();
    handlers.addAll(super.getHandlers());
    handlers.add(GroupHandler.class);

    // graph back-end handlers
    handlers.add(UserHandler.class);

    // skill management handler
    handlers.add(SkillHandler.class);

    // autocompletion handler
    handlers.add(AutoCompleteHandler.class);

    // organization handler
    handlers.add(OrganizationHandler.class);

    // process mining handler
    handlers.add(ProcessMiningHandler.class);

    // replacements
    handlers.remove(PersonHandler.class);
    handlers.add(ExtPersonHandler.class);

    handlers.remove(MessageHandler.class);
    handlers.add(ExtMessageHandler.class);

    return handlers;
  }
}
