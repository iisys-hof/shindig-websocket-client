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
package org.apache.shindig.social.websockbackend.model;

import java.util.List;

import org.apache.shindig.social.opensocial.model.Person;

/**
 * Skill set containing the skill's name and the people who linked this skill to a certain person.
 */
public interface ISkillSet {
  /**
   * @return the skills name
   */
  public String getName();

  /**
   * @param name
   *          the skill's new name
   */
  public void setName(String name);

  /**
   * @return list of people who linked this skill
   */
  public List<? extends Person> getPeople();

  /**
   * @param people
   *          new list of people who linked this skill
   */
  public void setPeople(List<? extends Person> people);

  /**
   * @return whether this skill has been confirmed
   */
  public Boolean getConfirmed();

  /**
   * @param confirmed
   *          whether this skill has been confirmed
   */
  public void setConfirmed(Boolean confirmed);
}
