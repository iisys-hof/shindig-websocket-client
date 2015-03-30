/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shindig.social.websockbackend.util;

import org.apache.shindig.protocol.model.SortOrder;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;

import de.hofuniversity.iisys.neo4j.websock.query.WebsockQuery;
import de.hofuniversity.iisys.neo4j.websock.session.WebsockConstants;
import de.hofuniversity.iisys.neo4j.websock.util.EFilterOperation;

/**
 * Utility class for converting Shindig's collection options to a transferable parameter map.
 */
public class CollOptsConverter {
  /**
   * Converts the given collection options to parameters which are set in the given websocket query.
   *
   * @param src
   *          collection options to convert
   * @param dst
   *          query to set options for
   */
  public static void convert(final CollectionOptions src, final WebsockQuery dst) {
    if (src == null || dst == null) {
      return;
    }

    // filtering
    if (src.getFilter() != null) {
      dst.setParameter(WebsockConstants.FILTER_FIELD, src.getFilter());
    }
    if (src.getFilterValue() != null) {
      dst.setParameter(WebsockConstants.FILTER_VALUE, src.getFilterValue());
    }

    String filterOp = null;
    if (src.getFilterOperation() != null) {
      switch (src.getFilterOperation()) {
      case contains:
        filterOp = EFilterOperation.CONTAINS.getCode();
        break;

      case equals:
        filterOp = EFilterOperation.EQUALS.getCode();
        break;

      case present:
        filterOp = EFilterOperation.HAS_PROPERTY.getCode();
        break;

      case startsWith:
        filterOp = EFilterOperation.STARTS_WITH.getCode();
        break;
      }

      dst.setParameter(WebsockConstants.FILTER_OPERATION, filterOp);
    }

    // sorting
    if (src.getSortBy() != null) {
      dst.setParameter(WebsockConstants.SORT_FIELD, src.getSortBy());
    }

    if (src.getSortOrder() == SortOrder.ascending) {
      dst.setParameter(WebsockConstants.SORT_ORDER, WebsockConstants.ASCENDING);
    } else if (src.getSortOrder() == SortOrder.descending) {
      dst.setParameter(WebsockConstants.SORT_ORDER, WebsockConstants.DESCENDING);
    }

    // pagination
    if (src.getFirst() > 0) {
      dst.setParameter(WebsockConstants.SUBSET_START, src.getFirst());
    }
    if (src.getMax() > 0) {
      dst.setParameter(WebsockConstants.SUBSET_SIZE, src.getMax());
    }
  }
}
