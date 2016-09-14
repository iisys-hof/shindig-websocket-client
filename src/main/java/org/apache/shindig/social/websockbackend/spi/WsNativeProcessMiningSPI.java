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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.opensocial.model.Message;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.websockbackend.events.BasicEvent;
import org.apache.shindig.social.websockbackend.events.ShindigEventType;
import org.apache.shindig.social.websockbackend.model.IProcessCycle;
import org.apache.shindig.social.websockbackend.model.dto.MessageCollectionDTO;
import org.apache.shindig.social.websockbackend.model.dto.ProcessCycleDTO;
import org.apache.shindig.social.websockbackend.util.CollOptsConverter;

import com.google.common.util.concurrent.Futures;
import com.google.inject.Inject;

import de.hofuniversity.iisys.neo4j.websock.queries.IQueryCallback;
import de.hofuniversity.iisys.neo4j.websock.queries.IQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.query.EQueryType;
import de.hofuniversity.iisys.neo4j.websock.query.WebsockQuery;
import de.hofuniversity.iisys.neo4j.websock.result.ListResult;
import de.hofuniversity.iisys.neo4j.websock.result.SingleResult;
import de.hofuniversity.iisys.neo4j.websock.shindig.ShindigNativeQueries;

/**
 * Implementation of the process mining service retrieving process cycle data from a remote Neo4j
 * graph database over a websocket.
 */
public class WsNativeProcessMiningSPI implements IProcessMiningService {
	
	private static final String ENDDATE_FIELD = "endDate";

	private final IQueryHandler fQueryHandler;
	private final Logger fLogger;

	@Inject
	public WsNativeProcessMiningSPI(IQueryHandler qHandler) {
	    if (qHandler == null) {
	      throw new NullPointerException("query handler was null");
	    }
	
	    this.fQueryHandler = qHandler;
	    this.fLogger = Logger.getLogger(this.getClass().getName());
	}
	
	@Override
	public Future<Void> addProcessCycle(String docId, String docType, String start, String end,
	          List<String> userList, SecurityToken token) throws ProtocolException {
	    /*
	     * // check linking person parameter if (token == null || token.getViewerId() == null ||
	     * token.getViewerId().isEmpty()) { throw new
	     * ProtocolException(HttpServletResponse.SC_BAD_REQUEST,
	     * "viewer ID from security token is required"); }
	     *
	     * // create query final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
	     * query.setPayload(ShindigNativeQueries.ADD_PROCESS_CYCLE_QUERY);
	     *
	     * // set parameters for method query.setParameter(ShindigNativeQueries.PROCESS_CYCLE_DOC_ID,
	     * docId); query.setParameter(key, docType);
	     *
	     * // execute final IQueryCallback result = this.fQueryHandler.sendQuery(query);
	     *
	     * try { result.get(); } catch(final Exception e) { e.printStackTrace();
	     * this.fLogger.log(Level.SEVERE, "server error", e); throw new
	     * ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "failed to execute query",
	     * e); }
	     */
	
	    return Futures.immediateFuture(null);
	}
	
	@Override
	public Future<IProcessCycle> addProcessCycle(String docType, IProcessCycle cycle, SecurityToken token) {
	    // check linking person parameter
	    /*
	     * if (token == null || token.getViewerId() == null || token.getViewerId().isEmpty()) { throw
	     * new ProtocolException(HttpServletResponse.SC_BAD_REQUEST,
	     * "viewer ID from security token is required"); }
	     */
	
	    // convert to map
	    final Map<String, Object> cycleMap = new HashMap<String, Object>();
	    ProcessCycleDTO dto = new ProcessCycleDTO(cycleMap);
	    dto.setData(cycle);
	
	    // create query
	    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
	    query.setPayload(ShindigNativeQueries.ADD_PROCESS_CYCLE_QUERY);
	
	    // set parameters for method
	    query.setParameter(ShindigNativeQueries.PROCESS_CYCLE_DOC_TYPE, docType);
	    query.setParameter(ShindigNativeQueries.PROCESS_CYCLE_OBJECT, cycleMap);
	
	    // execute
	    final IQueryCallback result = this.fQueryHandler.sendQuery(query);
	
	    SingleResult sResult = null;
	    
	    try {
	    	sResult = (SingleResult) result.get();
	    } catch (final Exception e) {
	      e.printStackTrace();
	      this.fLogger.log(Level.SEVERE, "server error", e);
	      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
	              "failed to execute query", e);
	    }
	    
	    @SuppressWarnings("unchecked")
	    final Map<String, Object> map = (Map<String, Object>) sResult.getResults();
	    dto = new ProcessCycleDTO(map);
	    
	    return Futures.immediateFuture((IProcessCycle) dto);
	}
	
	@Override
	public Future<RestfulCollection<IProcessCycle>> getProcessCycles(String docType, CollectionOptions options,
			SecurityToken token) {
		final List<IProcessCycle> cycles = new ArrayList<IProcessCycle>();
		
		final String sortField = options.getSortBy();
	    if (sortField == null) {
	      options.setSortBy(WsNativeProcessMiningSPI.ENDDATE_FIELD);
	    }
	    
	    // create query
	    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
	    query.setPayload(ShindigNativeQueries.GET_PROCESS_CYCLES_QUERY);
	    
	    // set options
	    CollOptsConverter.convert(options, query);
	    
	    // set parameters for method
	    if(docType!=null)
	    	query.setParameter(ShindigNativeQueries.PROCESS_CYCLE_DOC_TYPE, docType);
	    
	    // execute query
	    final IQueryCallback result = this.fQueryHandler.sendQuery(query);

	    ListResult resultList = null;
	    
	    try {
	        resultList = (ListResult) result.get();
	    } catch (final Exception e) {
	        e.printStackTrace();
	        this.fLogger.log(Level.SEVERE, "server error", e);
	        throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
	                "could not retrieve results", e);
	    }
	    
	    @SuppressWarnings("unchecked")
	    final List<Map<String, Object>> mapList = (List<Map<String, Object>>) resultList.getResults();
	    
	    // convert the items requested
	    if (mapList != null) {
	      ProcessCycleDTO dto = null;
	      for (final Map<String, Object> sMap : mapList) {
	        dto = new ProcessCycleDTO(sMap);
	        cycles.add(dto);
	      }
	    }
	    
	    // wrap result
	    final RestfulCollection<IProcessCycle> processCycleColl = new RestfulCollection<IProcessCycle>(cycles);
	    processCycleColl.setItemsPerPage(resultList.getMax());
	    processCycleColl.setStartIndex(resultList.getFirst());
	    processCycleColl.setTotalResults(resultList.getTotal());
	    return Futures.immediateFuture(processCycleColl);
	}

	public Future<Void> deleteProcessCycles(String docType, SecurityToken token) throws ProtocolException {

	    // create query
	    final WebsockQuery query = new WebsockQuery(EQueryType.PROCEDURE_CALL);
	    query.setPayload(ShindigNativeQueries.DELETE_PROCESS_CYCLES_QUERY);

	    // set parameters for method
	    query.setParameter(ShindigNativeQueries.PROCESS_CYCLE_DOC_TYPE, docType);
	    
	    // execute
	    final IQueryCallback result = this.fQueryHandler.sendQuery(query);

	    try {
	      result.get();
	    } catch (final Exception e) {
	      e.printStackTrace();
	      this.fLogger.log(Level.SEVERE, "server error", e);
	      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
	              "could not delete process cycles", e);
	    }

	    return Futures.immediateFuture(null);
	  }
}
