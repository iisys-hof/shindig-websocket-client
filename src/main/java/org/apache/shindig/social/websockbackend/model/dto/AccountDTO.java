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

import java.util.Map;

import org.apache.shindig.social.opensocial.model.Account;

/**
 * Data transfer object containing account information.
 */
public class AccountDTO extends ADataTransferObject implements Account {
  private static final String DOMAIN_FIELD = Account.Field.DOMAIN.toString();
  private static final String USER_ID_FIELD = Account.Field.USER_ID.toString();
  private static final String USER_NAME_FIELD = Account.Field.USERNAME.toString();

  /**
   * Creates an empty account data transfer object.
   */
  public AccountDTO() {
    super();
  }

  /**
   * Creates an account data transfer object, using the given map as property storage. The given map
   * must not be null.
   *
   * @param props
   *          map to use for internal property storage
   */
  public AccountDTO(Map<String, Object> props) {
    super(props);
  }

  public String getDomain() {
    String domain = null;
    final Object value = this.fProperties.get(AccountDTO.DOMAIN_FIELD);

    if (value != null) {
      domain = (String) value;
    }

    return domain;
  }

  public void setDomain(String domain) {
    this.fProperties.put(AccountDTO.DOMAIN_FIELD, AccountDTO.DOMAIN_FIELD);
  }

  public String getUserId() {
    String id = null;
    final Object value = this.fProperties.get(AccountDTO.USER_ID_FIELD);

    if (value != null) {
      id = (String) value;
    }

    return id;
  }

  public void setUserId(String userId) {
    this.fProperties.put(AccountDTO.USER_ID_FIELD, userId);
  }

  public String getUsername() {
    String name = null;
    final Object value = this.fProperties.get(AccountDTO.USER_NAME_FIELD);

    if (value != null) {
      name = (String) value;
    }

    return name;
  }

  public void setUsername(String username) {
    this.fProperties.put(AccountDTO.USER_NAME_FIELD, username);
  }

  /**
   * Sets the properties of this data transfer object to those of the given object. If the given
   * Object is null, all data is cleared.
   *
   * @param account
   *          account object containing data to set
   */
  public void setData(final Account account) {
    if (account == null) {
      this.fProperties.clear();
      return;
    }

    this.fProperties.put(AccountDTO.DOMAIN_FIELD, account.getDomain());
    this.fProperties.put(AccountDTO.USER_ID_FIELD, account.getUserId());
    this.fProperties.put(AccountDTO.USER_NAME_FIELD, account.getUsername());
  }
}
