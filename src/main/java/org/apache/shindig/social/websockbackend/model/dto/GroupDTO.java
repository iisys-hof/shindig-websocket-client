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

import org.apache.shindig.social.opensocial.model.Group;

/**
 * Data transfer object containing group information.
 */
public class GroupDTO extends ADataTransferObject implements Group {
  private static final String ID_FIELD = Group.Field.ID.toString();
  private static final String TITLE_FIELD = Group.Field.TITLE.toString();
  private static final String DESCRIPTION_FIELD = Group.Field.DESCRIPTION.toString();

  /**
   * Creates an empty group data transfer object.
   */
  public GroupDTO() {
    super();
  }

  /**
   * Creates a group data transfer object using the given map for internal property storage. The
   * given map must not be null.
   *
   *
   * @param props
   *          map to use for internal property storage
   */
  public GroupDTO(Map<String, Object> props) {
    super(props);
  }

  public String getId() {
    String id = null;
    final Object idVal = this.fProperties.get(GroupDTO.ID_FIELD);

    if (idVal != null) {
      id = (String) idVal;
    }

    return id;
  }

  public void setId(Object id) {
    this.fProperties.put(GroupDTO.ID_FIELD, id);
  }

  public String getTitle() {
    String title = null;
    final Object value = this.fProperties.get(GroupDTO.TITLE_FIELD);

    if (value != null) {
      title = (String) value;
    }

    return title;
  }

  public void setTitle(String title) {
    this.fProperties.put(GroupDTO.TITLE_FIELD, title);
  }

  public String getDescription() {
    String desc = null;
    final Object value = this.fProperties.get(GroupDTO.DESCRIPTION_FIELD);

    if (value != null) {
      desc = (String) value;
    }

    return desc;
  }

  public void setDescription(String description) {
    this.fProperties.put(GroupDTO.DESCRIPTION_FIELD, description);
  }

  /**
   * Sets the properties of this data transfer object to those of the given object. If the given
   * Object is null, all data is cleared.
   *
   * @param group
   *          group object containing data to set
   */
  public void setData(final Group group) {
    if (group == null) {
      this.fProperties.clear();
      return;
    }

    this.fProperties.put(GroupDTO.ID_FIELD, group.getId());
    this.fProperties.put(GroupDTO.TITLE_FIELD, group.getTitle());
    this.fProperties.put(GroupDTO.DESCRIPTION_FIELD, group.getDescription());
  }
}
