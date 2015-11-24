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
package org.apache.shindig.social.websockbackend.model.ws;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.shindig.social.core.model.ListFieldImpl;
import org.apache.shindig.social.opensocial.model.ListField;

/**
 * Converter class for list field list nodes in the graph that can create a transferable object.
 */
public class GraphListFieldList {
  private static final String VALUE_FIELD = ListField.Field.VALUE.toString();
  private static final String TYPE_FIELD = ListField.Field.TYPE.toString();
  private static final String PRIMARY_FIELD = ListField.Field.PRIMARY.toString();

  private final Map<String, Object> fMap;

  /**
   * Creates a list field list converter, taking properties from the given map. Throws a
   * NullPointerException if the parameter is null.
   *
   * @param map
   *          map representing the list of list fields
   */
  public GraphListFieldList(Map<String, Object> map) {
    if (map == null) {
      throw new NullPointerException("underyling map was null");
    }

    this.fMap = map;
  }

  /**
   * @return transferable list field list
   */
  public List<ListField> toDTO() {
    List<ListField> list = null;

    String[] values = null;
    String[] types = null;
    Integer primary = null;

    // TODO: remove list support

    // get fields from database
    if (this.fMap.containsKey(GraphListFieldList.VALUE_FIELD)) {
      final Object valueObj = this.fMap.get(GraphListFieldList.VALUE_FIELD);
      if (valueObj instanceof String[]) {
        values = (String[]) valueObj;
      } else {
        @SuppressWarnings("unchecked")
        final List<String> valList = (List<String>) valueObj;
        values = valList.toArray(new String[valList.size()]);
      }
    }

    if (this.fMap.containsKey(GraphListFieldList.TYPE_FIELD)) {
      final Object typeObj = this.fMap.get(GraphListFieldList.TYPE_FIELD);

      if (typeObj instanceof String[]) {
        types = (String[]) typeObj;
      } else {
        @SuppressWarnings("unchecked")
        final List<String> typeList = (List<String>) typeObj;
        types = typeList.toArray(new String[typeList.size()]);
      }
    }

    if (this.fMap.containsKey(GraphListFieldList.PRIMARY_FIELD)) {
      primary = (Integer) this.fMap.get(GraphListFieldList.PRIMARY_FIELD);
    }

    // convert available list fields
    if (values != null) {
      final int valLen = values.length;
      list = new ArrayList<ListField>();

      ListField field = null;
      for (int i = 0; i < valLen; ++i) {
        field = new ListFieldImpl();
        field.setValue(values[i]);

        if (types != null && i < types.length) {
          field.setType(types[i]);
        }

        if (primary != null && primary == i) {
          field.setPrimary(true);
        } else {
          field.setPrimary(false);
        }

        list.add(field);
      }
    }

    return list;
  }

  /**
   * Stores the given list field list in the wrapped property map. If the given list is null or
   * empty, the data in the map will be deleted.
   *
   * @param list
   *          list of list fields to store
   */
  public void store(final List<ListField> list) {
    if (list == null || list.isEmpty()) {
      this.fMap.remove(GraphListFieldList.VALUE_FIELD);
      this.fMap.remove(GraphListFieldList.TYPE_FIELD);
      this.fMap.remove(GraphListFieldList.PRIMARY_FIELD);
    } else {
      final int size = list.size();
      final String[] values = new String[size];
      final String[] types = new String[size];
      Integer primaryNum = null;

      String type = null;
      Boolean primary = null;

      ListField field = null;
      for (int i = 0; i < size; ++i) {
        field = list.get(i);
        values[i] = field.getValue();

        // Neo4j does not support null values
        type = field.getType();
        primary = field.getPrimary();

        if (type == null) {
          type = "";
        }

        if (primaryNum == null && primary != null && primary) {
          primaryNum = i;
        }

        types[i] = type;
      }

      this.fMap.put(GraphListFieldList.VALUE_FIELD, values);
      this.fMap.put(GraphListFieldList.TYPE_FIELD, types);
      this.fMap.put(GraphListFieldList.PRIMARY_FIELD, primaryNum);
    }
  }
}
