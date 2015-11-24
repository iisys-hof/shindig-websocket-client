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

import org.apache.shindig.social.opensocial.model.Organization;

import com.google.inject.ImplementedBy;

/**
 * Extended organization interface delivering methods to access a person's manager, secretary and
 * whether he or she is head of a department.
 */
@ImplementedBy(ExtOrganizationImpl.class)
public interface IExtOrganization extends Organization {
  /**
   * Name of the field containing a person's manager's ID.
   */
  public static final String MANAGER_ID_FIELD = "managerId";

  /**
   * Name of the field containing a person's secretary's ID.
   */
  public static final String SECRETARY_ID_FIELD = "secretaryId";

  /**
   * Name of the field containing the department's name.
   */
  public static final String DEPARTMENT_FIELD = "department";

  /**
   * Name of the field containing a boolean to determine whether he or she is head of the
   * department.
   */
  public static final String DEPARTMENT_HEAD_FIELD = "departmentHead";

  /**
   * Name of the field containing the organizational unit the user is in.
   */
  public static final String ORG_UNIT_FIELD = "orgUnit";

  /**
   * Name of the field containing the organizational unit the user is in.
   */
  public static final String LOCATION_FIELD = "location";

  /**
   * @return a person's manager's ID in the system
   */
  public String getManagerId();

  /**
   * @param managerId
   *          new ID of the person's manager
   */
  public void setManagerId(String managerId);

  /**
   * @return a person's secretary's ID in the system
   */
  public String getSecretaryId();

  /**
   * @param secretaryId
   *          new ID of the person's secretary
   */
  public void setSecretaryId(String secretaryId);

  /**
   * @return the name of the associated department
   */
  public String getDepartment();

  /**
   * @param deparment
   *          new name of the associated department
   */
  public void setDepartment(String deparment);

  /**
   * @return whether the associated person is head of the department
   */
  public Boolean isDepartmentHead();

  /**
   * @param departmentHead
   *          whether the person is head of the department
   */
  public void setDepartmentHead(Boolean departmentHead);

  /**
   * @return the organizational unit the user is in
   */
  public String getOrgUnit();

  /**
   * @param orgUnit
   *          the new organizational unit the user is in
   */
  public void setOrgUnit(String orgUnit);

  /**
   * @return user's location in the company (i.e. room number)
   */
  public String getLocation();

  /**
   * @param location
   *          user's new location in the company
   */
  public void setLocation(String location);
}
