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
package org.apache.shindig.social.websockbackend.model.dto;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Abstract object for storing information objects in a map for data transfer.
 */
public abstract class ADataTransferObject {
  /**
   * Map containing the object's information objects by their property name.
   */
  protected final Map<String, Object> fProperties;

  /**
   * Creates an empty data transfer object.
   */
  public ADataTransferObject() {
    this.fProperties = new HashMap<String, Object>();
  }

  /**
   * Creates a data transfer object using the given map for internal property storage. The given map
   * must not be null.
   *
   * @param props
   *          map to use for internal property storage
   */
  public ADataTransferObject(Map<String, Object> props) {
    if (props == null) {
      throw new NullPointerException("property map was null");
    }

    this.fProperties = props;
  }

  /**
   * Returns the internal property map which can already be in a target format such as BSON or JSON.
   * Can not be named to represent a "getter" since Shindig's automatic conversion would convert the
   * values.
   *
   * @return internal property map
   */
  public Map<String, Object> propertyMap() {
    return this.fProperties;
  }

  /**
   * @param key
   *          key to get a value for
   * @return object associated with the key or null if there is none
   */
  public Object getProperty(String key) {
    return this.fProperties.get(key);
  }

  /**
   * Sets a property to a certain value.
   *
   * @param key
   *          key of the property to set
   * @param value
   *          value to set the property to
   */
  public void setProperty(String key, Object value) {
    this.fProperties.put(key, value);
  }

  /**
   * @param key
   *          key of the property to clear
   */
  public void clearProperty(String key) {
    this.fProperties.remove(key);
  }

  /**
   * Clears all properties.
   */
  public void clear() {
    this.fProperties.clear();
  }

  /**
   * Transforms an array of a type T from the properties to a list, if it exists. Only applicable
   * for primitive types. Throws an exception if types don't match or the attribute is not an array.
   *
   * @param property
   *          property name of the array
   * @param type
   *          type of object stored in the array
   * @return list of the array's elements or null
   */
  @SuppressWarnings("unchecked")
  protected <T> List<T> listFromProperty(String property, Class<T> type) {
    List<T> list = null;
    final Object value = this.fProperties.get(property);

    if (value != null) {
      if (value instanceof List<?>) {
        list = (List<T>) value;
      } else {
        list = Arrays.asList((T[]) value);
      }
    }

    return list;
  }

  /**
   * Removes all keys with null values from the internal property map.
   */
  public void stripNullValues() {
    final List<String> toRemove = new LinkedList<>();

    for (final Entry<String, Object> propE : this.fProperties.entrySet()) {
      if (propE.getValue() == null) {
        toRemove.add(propE.getKey());
      }
    }

    for (final String key : toRemove) {
      this.fProperties.remove(key);
    }
  }
}
