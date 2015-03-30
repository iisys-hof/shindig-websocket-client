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

import java.util.Map;

import org.apache.shindig.social.opensocial.model.Address;

/**
 * Data transfer object containing address information.
 */
public class AddressDTO extends ADataTransferObject implements Address {
  private static final String COUNTRY_FIELD = Address.Field.COUNTRY.toString();
  private static final String LATITUDE_FIELD = Address.Field.LATITUDE.toString();
  private static final String LOCALITY_FIELD = Address.Field.LOCALITY.toString();
  private static final String LONGITUDE_FIELD = Address.Field.LONGITUDE.toString();
  private static final String POSTAL_FIELD = Address.Field.POSTAL_CODE.toString();
  private static final String REGION_FIELD = Address.Field.REGION.toString();
  private static final String ADDRESS_FIELD = Address.Field.STREET_ADDRESS.toString();
  private static final String TYPE_FIELD = Address.Field.TYPE.toString();
  private static final String FORMATTED_FIELD = Address.Field.FORMATTED.toString();
  private static final String PRIMARY_FIELD = Address.Field.PRIMARY.toString();

  /**
   * Creates an empty address data transfer object.
   */
  public AddressDTO() {
    super();
  }

  public AddressDTO(Map<String, Object> props) {
    super(props);
  }

  public String getCountry() {
    String country = null;
    final Object value = this.fProperties.get(AddressDTO.COUNTRY_FIELD);

    if (value != null) {
      country = (String) value;
    }

    return country;
  }

  public void setCountry(String country) {
    this.fProperties.put(AddressDTO.COUNTRY_FIELD, country);
  }

  public Float getLatitude() {
    Float latitude = null;
    final Object value = this.fProperties.get(AddressDTO.LATITUDE_FIELD);

    if (value != null) {
      if (value instanceof Float) {
        latitude = (Float) value;
      } else {
        latitude = ((Double) value).floatValue();
      }
    }

    return latitude;
  }

  public void setLatitude(Float latitude) {
    this.fProperties.put(AddressDTO.LATITUDE_FIELD, latitude);
  }

  public String getLocality() {
    String locality = null;
    final Object value = this.fProperties.get(AddressDTO.LOCALITY_FIELD);

    if (value != null) {
      locality = (String) value;
    }

    return locality;
  }

  public void setLocality(String locality) {
    this.fProperties.put(AddressDTO.LOCALITY_FIELD, locality);
  }

  public Float getLongitude() {
    Float longitude = null;
    final Object value = this.fProperties.get(AddressDTO.LONGITUDE_FIELD);

    if (value != null) {
      if (value instanceof Float) {
        longitude = (Float) value;
      } else {
        longitude = ((Double) value).floatValue();
      }
    }

    return longitude;
  }

  public void setLongitude(Float longitude) {
    this.fProperties.put(AddressDTO.LONGITUDE_FIELD, longitude);
  }

  public String getPostalCode() {
    String code = null;
    final Object value = this.fProperties.get(AddressDTO.POSTAL_FIELD);

    if (value != null) {
      code = (String) value;
    }

    return code;
  }

  public void setPostalCode(String postalCode) {
    this.fProperties.put(AddressDTO.POSTAL_FIELD, postalCode);
  }

  public String getRegion() {
    String region = null;
    final Object value = this.fProperties.get(AddressDTO.REGION_FIELD);

    if (value != null) {
      region = (String) value;
    }

    return region;
  }

  public void setRegion(String region) {
    this.fProperties.put(AddressDTO.REGION_FIELD, region);
  }

  public String getStreetAddress() {
    String street = null;
    final Object value = this.fProperties.get(AddressDTO.ADDRESS_FIELD);

    if (value != null) {
      street = (String) value;
    }

    return street;
  }

  public void setStreetAddress(String streetAddress) {
    this.fProperties.put(AddressDTO.ADDRESS_FIELD, streetAddress);
  }

  public String getType() {
    String type = null;
    final Object value = this.fProperties.get(AddressDTO.TYPE_FIELD);

    if (value != null) {
      type = (String) value;
    }

    return type;
  }

  public void setType(String type) {
    this.fProperties.put(AddressDTO.TYPE_FIELD, type);
  }

  public String getFormatted() {
    String formatted = null;
    final Object value = this.fProperties.get(AddressDTO.FORMATTED_FIELD);

    if (value != null) {
      formatted = (String) value;
    }

    return formatted;
  }

  public void setFormatted(String formatted) {
    this.fProperties.put(AddressDTO.FORMATTED_FIELD, formatted);
  }

  public Boolean getPrimary() {
    Boolean primary = null;
    final Object value = this.fProperties.get(AddressDTO.PRIMARY_FIELD);

    if (value != null) {
      primary = (Boolean) value;
    }

    return primary;
  }

  public void setPrimary(Boolean primary) {
    this.fProperties.put(AddressDTO.PRIMARY_FIELD, primary);
  }

  /**
   * Sets the properties of this data transfer object to those of the given object. If the given
   * Object is null, all data is cleared.
   *
   * @param address
   *          address object containing data to set
   */
  public void setData(final Address address) {
    if (address == null) {
      this.fProperties.clear();
      return;
    }

    this.fProperties.put(AddressDTO.COUNTRY_FIELD, address.getCountry());
    this.fProperties.put(AddressDTO.FORMATTED_FIELD, address.getFormatted());
    this.fProperties.put(AddressDTO.LATITUDE_FIELD, address.getLatitude());
    this.fProperties.put(AddressDTO.LOCALITY_FIELD, address.getLocality());
    this.fProperties.put(AddressDTO.LONGITUDE_FIELD, address.getLongitude());
    this.fProperties.put(AddressDTO.POSTAL_FIELD, address.getPostalCode());
    this.fProperties.put(AddressDTO.PRIMARY_FIELD, address.getPrimary());
    this.fProperties.put(AddressDTO.REGION_FIELD, address.getRegion());
    this.fProperties.put(AddressDTO.ADDRESS_FIELD, address.getStreetAddress());
    this.fProperties.put(AddressDTO.TYPE_FIELD, address.getType());
  }
}
