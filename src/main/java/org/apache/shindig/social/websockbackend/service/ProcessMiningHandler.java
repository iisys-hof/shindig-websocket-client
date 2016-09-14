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

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.HandlerPreconditions;
import org.apache.shindig.protocol.Operation;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.Service;
import org.apache.shindig.social.opensocial.model.Message;
import org.apache.shindig.social.opensocial.model.MessageCollection;
import org.apache.shindig.social.opensocial.service.SocialRequestItem;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.websockbackend.model.IProcessCycle;
import org.apache.shindig.social.websockbackend.spi.IProcessMiningService;

import com.google.inject.Inject;

/**
 * SCHub-specific service handler for process mining. It handles ...
 */
@Service(name = "processmining", path = "/{docType}")
public class ProcessMiningHandler {
  private final IProcessMiningService fProcessMiningSPI;

  /**
   * Creates an process mining handler using the given process mining service to retrieve data.
   * Throws a NullPointerException if the given service is null.
   *
   * @param skillService
   *          skill service to use
   */
  @Inject
  public ProcessMiningHandler(IProcessMiningService processMiningService) {
    if (processMiningService == null) {
      throw new NullPointerException("ProcessMining service was null!");
    }

    this.fProcessMiningSPI = processMiningService;
  }

  @Operation(httpMethods = "POST", path = "/{docType}", bodyParam = "cycle")
  public Future<?> addProcessCycle(final SocialRequestItem request) throws ProtocolException {
    final SecurityToken token = request.getToken();

    final String docType = request.getParameter("docType");

    final IProcessCycle cycle = request.getTypedParameter("cycle", IProcessCycle.class);

    final Future<?> result = this.fProcessMiningSPI.addProcessCycle(docType, cycle, token);

    return result;
  }
  
  @Operation(httpMethods = "GET")
  public Future<?> get(SocialRequestItem request) throws ProtocolException {	  
	final CollectionOptions collOpts = new CollectionOptions(request);
	final SecurityToken token = request.getToken();
	
	final String docType = request.getParameter("docType");

	final Future<?> result = this.fProcessMiningSPI.getProcessCycles(docType, collOpts, token);

	return result;
  }
  
  /**
   *
   * @param request
   *          item containing information about the request
   * @return empty future
   * @throws ProtocolException
   *           if the request is flawed
   */
  @Operation(httpMethods = "DELETE", path = "/{docType}")
  public Future<?> deleteProcessCycles(final SocialRequestItem request) throws ProtocolException {
    final SecurityToken token = request.getToken();
    final String docType = request.getParameter("docType");
    
    final Future<?> result = this.fProcessMiningSPI.deleteProcessCycles(docType, token);
    return result;
  }
}
