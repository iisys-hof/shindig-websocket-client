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

import java.util.Date;
import java.util.Map;

import org.apache.shindig.social.opensocial.model.Address;
import org.apache.shindig.social.opensocial.model.Organization;
import org.apache.shindig.social.websockbackend.model.IExtOrganization;

/**
 * Data transfer object containing organization information.
 */
public class OrganizationDTO extends ADataTransferObject implements IExtOrganization {
  private static final String DESCRIPTION_FIELD = Organization.Field.DESCRIPTION.toString();
  private static final String END_DATE_FIELD = Organization.Field.END_DATE.toString();
  private static final String FIELD_FIELD = Organization.Field.FIELD.toString();
  private static final String NAME_FIELD = Organization.Field.NAME.toString();
  private static final String SALARY_FIELD = Organization.Field.SALARY.toString();
  private static final String START_DATE_FIELD = Organization.Field.START_DATE.toString();
  private static final String SUB_FIELD_FIELD = Organization.Field.SUB_FIELD.toString();
  private static final String TITLE_FIELD = Organization.Field.TITLE.toString();
  private static final String WEBPAGE_FIELD = Organization.Field.WEBPAGE.toString();
  private static final String TYPE_FIELD = Organization.Field.TYPE.toString();
  private static final String PRIMARY_FIELD = Organization.Field.PRIMARY.toString();

  private static final String ADDRESS_FIELD = Organization.Field.ADDRESS.toString();

  /**
   * Creates an empty organization data transfer object.
   */
  public OrganizationDTO() {
    super();
  }

  /**
   * Creates an organization data transfer object using the given map for internal property storage.
   * The given map must not be null.
   *
   * @param props
   *          map to use for internal property storage
   */
  public OrganizationDTO(Map<String, Object> props) {
    super(props);
  }

  public Address getAddress() {
    Address address = null;

    @SuppressWarnings("unchecked")
    final Map<String, Object> addMap = (Map<String, Object>) this.fProperties
            .get(OrganizationDTO.ADDRESS_FIELD);

    if (addMap != null) {
      address = new AddressDTO(addMap);
    }

    return address;
  }

  public void setAddress(Address address) {
    if (address != null) {
      // TODO: specify map implementation
      final AddressDTO addDTO = new AddressDTO();
      addDTO.setData(address);
      addDTO.stripNullValues();
      this.fProperties.put(OrganizationDTO.ADDRESS_FIELD, addDTO.propertyMap());
    } else {
      this.fProperties.put(OrganizationDTO.ADDRESS_FIELD, null);
    }
  }

  public String getDescription() {
    String description = null;
    final Object value = this.fProperties.get(OrganizationDTO.DESCRIPTION_FIELD);

    if (value != null) {
      description = (String) value;
    }

    return description;
  }

  public void setDescription(String description) {
    this.fProperties.put(OrganizationDTO.DESCRIPTION_FIELD, description);
  }

  public Date getEndDate() {
    Date endDate = null;
    final Object value = this.fProperties.get(OrganizationDTO.END_DATE_FIELD);

    if (value != null) {
      final long time = (Long) value;
      endDate = new Date(time);
    }

    return endDate;
  }

  public void setEndDate(Date endDate) {
    if (endDate != null) {
      this.fProperties.put(OrganizationDTO.END_DATE_FIELD, endDate.getTime());
    } else {
      this.fProperties.put(OrganizationDTO.END_DATE_FIELD, null);
    }
  }

  public String getField() {
    String field = null;
    final Object value = this.fProperties.get(OrganizationDTO.FIELD_FIELD);

    if (value != null) {
      field = (String) value;
    }

    return field;
  }

  public void setField(String field) {
    this.fProperties.put(OrganizationDTO.FIELD_FIELD, field);
  }

  public String getName() {
    String name = null;
    final Object value = this.fProperties.get(OrganizationDTO.NAME_FIELD);

    if (value != null) {
      name = (String) value;
    }

    return name;
  }

  public void setName(String name) {
    this.fProperties.put(OrganizationDTO.NAME_FIELD, name);
  }

  public String getSalary() {
    String salary = null;
    final Object value = this.fProperties.get(OrganizationDTO.SALARY_FIELD);

    if (value != null) {
      salary = (String) value;
    }

    return salary;
  }

  public void setSalary(String salary) {
    this.fProperties.put(OrganizationDTO.SALARY_FIELD, salary);
  }

  public Date getStartDate() {
    Date startDate = null;
    final Object value = this.fProperties.get(OrganizationDTO.START_DATE_FIELD);

    if (value != null) {
      final long time = (Long) value;
      startDate = new Date(time);
    }

    return startDate;
  }

  public void setStartDate(Date startDate) {
    if (startDate != null) {
      this.fProperties.put(OrganizationDTO.START_DATE_FIELD, startDate.getTime());
    } else {
      this.fProperties.put(OrganizationDTO.START_DATE_FIELD, null);
    }
  }

  public String getSubField() {
    String subField = null;
    final Object value = this.fProperties.get(OrganizationDTO.SUB_FIELD_FIELD);

    if (value != null) {
      subField = (String) value;
    }

    return subField;
  }

  public void setSubField(String subField) {
    this.fProperties.put(OrganizationDTO.SUB_FIELD_FIELD, subField);
  }

  public String getTitle() {
    String title = null;
    final Object value = this.fProperties.get(OrganizationDTO.TITLE_FIELD);

    if (value != null) {
      title = (String) value;
    }

    return title;
  }

  public void setTitle(String title) {
    this.fProperties.put(OrganizationDTO.TITLE_FIELD, title);
  }

  public String getWebpage() {
    String webpage = null;
    final Object value = this.fProperties.get(OrganizationDTO.WEBPAGE_FIELD);

    if (value != null) {
      webpage = (String) value;
    }

    return webpage;
  }

  public void setWebpage(String webpage) {
    this.fProperties.put(OrganizationDTO.WEBPAGE_FIELD, webpage);
  }

  public String getType() {
    String type = null;
    final Object value = this.fProperties.get(OrganizationDTO.TYPE_FIELD);

    if (value != null) {
      type = (String) value;
    }

    return type;
  }

  public void setType(String type) {
    this.fProperties.put(OrganizationDTO.TYPE_FIELD, type);
  }

  public Boolean getPrimary() {
    Boolean primary = null;
    final Object value = this.fProperties.get(OrganizationDTO.PRIMARY_FIELD);

    if (value != null) {
      primary = (Boolean) value;
    }

    return primary;
  }

  public void setPrimary(Boolean primary) {
    this.fProperties.put(OrganizationDTO.PRIMARY_FIELD, primary);
  }

  @Override
  public String getManagerId() {
    String manId = null;
    final Object value = this.fProperties.get(IExtOrganization.MANAGER_ID_FIELD);

    if (value != null) {
      manId = (String) value;
    }

    return manId;
  }

  @Override
  public void setManagerId(String managerId) {
    this.fProperties.put(IExtOrganization.MANAGER_ID_FIELD, managerId);
  }

  @Override
  public String getSecretaryId() {
    String secId = null;
    final Object value = this.fProperties.get(IExtOrganization.SECRETARY_ID_FIELD);

    if (value != null) {
      secId = (String) value;
    }

    return secId;
  }

  @Override
  public void setSecretaryId(String secretaryId) {
    this.fProperties.put(IExtOrganization.SECRETARY_ID_FIELD, secretaryId);
  }

  @Override
  public String getDepartment() {
    String department = null;
    final Object value = this.fProperties.get(IExtOrganization.DEPARTMENT_FIELD);

    if (value != null) {
      department = (String) value;
    }

    return department;
  }

  @Override
  public void setDepartment(String deparment) {
    this.fProperties.put(IExtOrganization.DEPARTMENT_FIELD, deparment);
  }

  @Override
  public Boolean isDepartmentHead() {
    Boolean depHead = null;
    final Object value = this.fProperties.get(IExtOrganization.DEPARTMENT_HEAD_FIELD);

    if (value != null) {
      depHead = (Boolean) value;
    }

    return depHead;
  }

  @Override
  public void setDepartmentHead(Boolean departmentHead) {
    this.fProperties.put(IExtOrganization.DEPARTMENT_HEAD_FIELD, departmentHead);
  }

  @Override
  public String getOrgUnit() {
    String orgUnit = null;
    final Object value = this.fProperties.get(IExtOrganization.ORG_UNIT_FIELD);

    if (value != null) {
      orgUnit = (String) value;
    }

    return orgUnit;
  }

  @Override
  public void setOrgUnit(String orgUnit) {
    this.fProperties.put(IExtOrganization.ORG_UNIT_FIELD, orgUnit);
  }

  @Override
  public String getLocation() {
    String location = null;
    final Object value = this.fProperties.get(IExtOrganization.LOCATION_FIELD);

    if (value != null) {
      location = (String) value;
    }

    return location;
  }

  @Override
  public void setLocation(String location) {
    this.fProperties.put(IExtOrganization.LOCATION_FIELD, location);
  }

  @Override
  public String getSite() {
    String site = null;
    final Object value = this.fProperties.get(IExtOrganization.SITE_FIELD);

    if (value != null) {
      site = (String) value;
    }

    return site;
  }

  @Override
  public void setSite(String site) {
    this.fProperties.put(IExtOrganization.SITE_FIELD, site);
  }

  /**
   * Sets the properties of this data transfer object to those of the given object. If the given
   * Object is null, all data is cleared.
   *
   * @param organization
   *          organization object containing the data to set
   */
  public void setData(final Organization organization) {
    if (organization == null) {
      this.fProperties.clear();
      return;
    }

    // node properties
    this.fProperties.put(OrganizationDTO.DESCRIPTION_FIELD, organization.getDescription());
    this.fProperties.put(OrganizationDTO.FIELD_FIELD, organization.getField());
    this.fProperties.put(OrganizationDTO.NAME_FIELD, organization.getName());
    this.fProperties.put(OrganizationDTO.SUB_FIELD_FIELD, organization.getSubField());
    this.fProperties.put(OrganizationDTO.TYPE_FIELD, organization.getType());
    this.fProperties.put(OrganizationDTO.WEBPAGE_FIELD, organization.getWebpage());

    // relationship properties
    final Date endDate = organization.getEndDate();
    if (endDate != null) {
      this.fProperties.put(OrganizationDTO.END_DATE_FIELD, endDate.getTime());
    }
    this.fProperties.put(OrganizationDTO.PRIMARY_FIELD, organization.getPrimary());
    this.fProperties.put(OrganizationDTO.SALARY_FIELD, organization.getSalary());
    final Date startDate = organization.getStartDate();
    if (startDate != null) {
      this.fProperties.put(OrganizationDTO.START_DATE_FIELD, startDate.getTime());
    }
    this.fProperties.put(OrganizationDTO.TITLE_FIELD, organization.getTitle());

    // extended model properties
    if (organization instanceof IExtOrganization) {
      final IExtOrganization eOrg = (IExtOrganization) organization;

      this.fProperties.put(IExtOrganization.DEPARTMENT_FIELD, eOrg.getDepartment());
      this.fProperties.put(IExtOrganization.DEPARTMENT_HEAD_FIELD, eOrg.isDepartmentHead());
      this.fProperties.put(IExtOrganization.MANAGER_ID_FIELD, eOrg.getManagerId());
      this.fProperties.put(IExtOrganization.SECRETARY_ID_FIELD, eOrg.getSecretaryId());
      this.fProperties.put(IExtOrganization.ORG_UNIT_FIELD, eOrg.getOrgUnit());
      this.fProperties.put(IExtOrganization.LOCATION_FIELD, eOrg.getLocation());
      this.fProperties.put(IExtOrganization.SITE_FIELD, eOrg.getSite());
    }

    // address
    this.setAddress(organization.getAddress());
  }
}
