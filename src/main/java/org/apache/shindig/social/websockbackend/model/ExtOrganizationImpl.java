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

import org.apache.shindig.social.core.model.OrganizationImpl;

/**
 * Extended organization basic implementation delivering methods to access a person's manager,
 * secretary and whether he or she is head of a department.
 */
public class ExtOrganizationImpl extends OrganizationImpl implements IExtOrganization {
  private String managerId;
  private String secretaryId;
  private String department;
  private Boolean departmentHead;
  private String orgUnit;
  private String location;

  @Override
  public String getManagerId() {
    return this.managerId;
  }

  @Override
  public void setManagerId(String managerId) {
    this.managerId = managerId;
  }

  @Override
  public String getSecretaryId() {
    return this.secretaryId;
  }

  @Override
  public void setSecretaryId(String secretaryId) {
    this.secretaryId = secretaryId;
  }

  @Override
  public String getDepartment() {
    return this.department;
  }

  @Override
  public void setDepartment(String deparment) {
    this.department = deparment;
  }

  @Override
  public Boolean isDepartmentHead() {
    return this.departmentHead;
  }

  @Override
  public void setDepartmentHead(Boolean departmentHead) {
    this.departmentHead = departmentHead;
  }

  @Override
  public String getOrgUnit() {
    return this.orgUnit;
  }

  @Override
  public void setOrgUnit(String orgUnit) {
    this.orgUnit = orgUnit;
  }

  @Override
  public String getLocation() {
    return this.location;
  }

  @Override
  public void setLocation(String location) {
    this.location = location;
  }
}
