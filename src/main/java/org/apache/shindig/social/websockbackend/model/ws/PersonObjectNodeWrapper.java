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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.shindig.social.opensocial.model.ActivityObject;
import org.apache.shindig.social.opensocial.model.Name;
import org.apache.shindig.social.opensocial.model.Person;

import com.google.common.collect.ImmutableSet;

/**
 * Wrapper class, intercepting calls to a real person map to emulate an activity object while
 * providing full linking, yet no manipulation functionality.
 */
public class PersonObjectNodeWrapper implements Map<String, Object> {
  private static final String ID_FIELD = Person.Field.ID.toString();
  private static final String DISPLAY_FIELD = Person.Field.DISPLAY_NAME.toString();
  private static final String TYPE_FIELD = ActivityObject.Field.OBJECT_TYPE.toString();

  private static final String FORMATTED_FIELD = Name.Field.FORMATTED.toString();

  private static final Set<String> AVAIL_FIELDS = ImmutableSet.of(PersonObjectNodeWrapper.ID_FIELD,
          PersonObjectNodeWrapper.DISPLAY_FIELD, PersonObjectNodeWrapper.TYPE_FIELD);

  private static final String OBJECT_TYPE = "person";

  private final Map<String, Object> fMap;

  /**
   * Creates a person map wrapper around the given person property map. The given map must not be
   * null.
   *
   * @param personMap
   *          actual person property map
   */
  public PersonObjectNodeWrapper(Map<String, Object> personMap) {
    if (personMap == null) {
      throw new NullPointerException("person map was null");
    }

    this.fMap = personMap;
  }

  private String getDisplay() {
    String display = (String) this.fMap.get(PersonObjectNodeWrapper.DISPLAY_FIELD);

    // try formatted name if display name is not set
    if (display == null) {
      display = (String) this.fMap.get(PersonObjectNodeWrapper.FORMATTED_FIELD);
    }

    // if there still is no name, use the ID which has to be set
    if (display == null) {
      display = (String) this.fMap.get(PersonObjectNodeWrapper.ID_FIELD);
    }

    return display;
  }

  @Override
  public Object get(Object key) {
    if (PersonObjectNodeWrapper.TYPE_FIELD.equals(key)) {
      return PersonObjectNodeWrapper.OBJECT_TYPE;
    } else if (PersonObjectNodeWrapper.DISPLAY_FIELD.equals(key)) {
      return getDisplay();
    } else if (PersonObjectNodeWrapper.AVAIL_FIELDS.contains(key)) {
      return this.fMap.get(key);
    } else {
      return null;
    }
  }

  @Override
  public boolean containsKey(Object key) {
    return PersonObjectNodeWrapper.AVAIL_FIELDS.contains(key);
  }

  @Override
  public Object remove(Object key) {
    // inactive
    return null;
  }

  @Override
  public Object put(String key, Object value) {
    // inactive
    return null;
  }

  @Override
  public void clear() {
    // inactive
  }

  @Override
  public boolean containsValue(Object value) {
    // inactive
    return false;
  }

  @Override
  public Set<Entry<String, Object>> entrySet() {
    final Set<Entry<String, Object>> set = new HashSet<Entry<String, Object>>();

    for (final String key : PersonObjectNodeWrapper.AVAIL_FIELDS) {
      set.add(new Entry<String, Object>() {

        @Override
        public String getKey() {
          return key;
        }

        @Override
        public Object getValue() {
          return get(key);
        }

        @Override
        public Object setValue(Object arg0) {
          return null;
        }

      });
    }

    return set;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public Set<String> keySet() {
    return PersonObjectNodeWrapper.AVAIL_FIELDS;
  }

  @Override
  public void putAll(Map<? extends String, ? extends Object> m) {
    // inactive
  }

  @Override
  public int size() {
    // inactive
    return 0;
  }

  @Override
  public Collection<Object> values() {
    // inactive
    return null;
  }

}
