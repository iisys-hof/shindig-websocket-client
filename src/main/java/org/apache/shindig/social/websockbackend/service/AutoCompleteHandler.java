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

import java.util.concurrent.Future;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.Operation;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.Service;
import org.apache.shindig.social.opensocial.service.SocialRequestItem;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.websockbackend.spi.ISkillService;

import com.google.inject.Inject;

/**
 * SCHub-specific service handler for autocompletion requests based on text fragments. Currently,
 * autocompletion is only available for skills.
 */
@Service(name = "autocomplete", path = "/{type}")
public class AutoCompleteHandler {
  private final ISkillService fSkillSPI;

  /**
   * Creates an autocompletion handler using the given skill service. A NullPointerException is
   * thrown if the given handler is null.
   *
   * @param skillService
   *          skill autocompletion service
   */
  @Inject
  public AutoCompleteHandler(ISkillService skillService) {
    if (skillService == null) {
      throw new NullPointerException("skill service was null");
    }

    this.fSkillSPI = skillService;
  }

  /**
   * Handles a GET-request with the parameters represented by the given request item and returns
   * autocompletion suggestions. Throws a NullPointerException if the given request item is null.
   * This GET-request only has the "fragment" as an optional URL parameter.
   *
   * @param request
   *          item containing information about the request
   * @return skill autocompletion suggestions
   * @throws ProtocolException
   *           if the request is flawed
   */
  @Operation(httpMethods = "GET", path = "/skills")
  public Future<?> getSkillAutocomp(final SocialRequestItem request) throws ProtocolException {
    final CollectionOptions collOpts = new CollectionOptions(request);
    final SecurityToken token = request.getToken();

    final String fragment = request.getParameter("fragment");
    final Future<?> result = this.fSkillSPI.getSkillAutocomp(fragment, collOpts, token);

    return result;
  }
}
