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

import java.util.Date;
import java.util.List;

import org.apache.shindig.social.opensocial.model.Person;

public class ProcessCycleImpl implements IProcessCycle {

  private String docType;
  private String docId;
  private Date startDate;
  private Date endDate;
  private List<String> userList;

  @Override
  public String getType() {
    return this.docType;
  }

  @Override
  public void setType(String docType) {
    this.docType = docType;
  }

  @Override
  public String getDocId() {
    return this.docId;
  }

  @Override
  public void setDocId(String docId) {
    this.docId = docId;
  }

  @Override
  public Date getStartDate() {
    return this.startDate;
  }

  @Override
  public void setStartDate(Date start) {
    this.startDate = start;
  }

  @Override
  public Date getEndDate() {
    return this.endDate;
  }

  @Override
  public void setEndDate(Date end) {
    this.endDate = end;
  }

  @Override
  public List<String> getUserList() {
    return this.userList;
  }

  @Override
  public void setUserList(List<String> userList) {
    this.userList = userList;
  }

}
