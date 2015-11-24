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
 * Simple Bean implementation of a skill set.
 */
public class SkillSetImpl implements ISkillSet {

  private String fName;
  private List<? extends Person> fPeople;
  private Boolean fConfirmed;

  @Override
  public String getName() {
    return this.fName;
  }

  @Override
  public void setName(String name) {
    this.fName = name;
  }

  @Override
  public List<? extends Person> getPeople() {
    return this.fPeople;
  }

  @Override
  public void setPeople(List<? extends Person> people) {
    this.fPeople = people;
  }

  @Override
  public Boolean getConfirmed() {
    return this.fConfirmed;
  }

  @Override
  public void setConfirmed(Boolean confirmed) {
    this.fConfirmed = confirmed;
  }

}
