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
package org.apache.shindig.social.websockbackend.model.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.websockbackend.model.ISkillSet;

/**
 * Skill set implementation using a key-value map for internal property storage. Automatically
 * converts contained person objects.
 */
public class SkillSetDTO extends ADataTransferObject implements ISkillSet {

  private static final String NAME_FIELD = "name";
  private static final String PEOPLE_FIELD = "people";
  private static final String CONFIRMED_FIELD = "confirmed";

  /**
   * Creates an empty skill set transfer object.
   */
  public SkillSetDTO() {
    super();
  }

  /**
   * Creates a skill set transfer object using the given map for internal property storage. The
   * given map must not be null.
   *
   * @param props
   *          map to use for internal property storage
   */
  public SkillSetDTO(Map<String, Object> props) {
    super(props);
  }

  @Override
  public String getName() {
    String name = null;

    final Object value = this.fProperties.get(SkillSetDTO.NAME_FIELD);

    if (value != null) {
      name = (String) value;
    }

    return name;
  }

  @Override
  public void setName(String name) {
    this.fProperties.put(SkillSetDTO.NAME_FIELD, name);
  }

  @Override
  public List<Person> getPeople() {
    List<Person> people = null;

    // TODO: specify list implementation
    @SuppressWarnings("unchecked")
    final List<Map<String, Object>> personMaps = (List<Map<String, Object>>) this.fProperties
            .get(SkillSetDTO.PEOPLE_FIELD);

    if (personMaps != null) {
      people = new ArrayList<Person>();

      for (final Map<String, Object> pMap : personMaps) {
        people.add(new PersonDTO(pMap));
      }
    }

    return people;
  }

  @Override
  public void setPeople(List<? extends Person> people) {
    // TODO: specify list implementation
    if (people != null && !people.isEmpty()) {
      final List<Map<String, Object>> personList = new ArrayList<Map<String, Object>>();

      Map<String, Object> pMap = null;
      for (final Person acc : people) {
        pMap = new HashMap<String, Object>();
        new PersonDTO(pMap).setData(acc);
        personList.add(pMap);
      }

      this.fProperties.put(SkillSetDTO.PEOPLE_FIELD, personList);
    } else {
      this.fProperties.put(SkillSetDTO.PEOPLE_FIELD, null);
    }
  }

  @Override
  public Boolean getConfirmed() {
    Boolean confirmed = null;

    final Object value = this.fProperties.get(SkillSetDTO.CONFIRMED_FIELD);

    if (value != null) {
      confirmed = (Boolean) value;
    }

    return confirmed;
  }

  @Override
  public void setConfirmed(Boolean confirmed) {
    this.fProperties.put(SkillSetDTO.CONFIRMED_FIELD, confirmed);
  }

}
