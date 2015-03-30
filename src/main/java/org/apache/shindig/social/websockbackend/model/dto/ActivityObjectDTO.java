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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.shindig.protocol.model.ExtendableBean;
import org.apache.shindig.social.opensocial.model.ActivityObject;
import org.apache.shindig.social.opensocial.model.MediaLink;

/**
 * Data transfer object containing activity entry information. The openSocial has to be set via the
 * corresponding setters.
 */
public class ActivityObjectDTO extends ADataTransferObject implements ActivityObject {
  private static final String CONTENT_FIELD = ActivityObject.Field.CONTENT.toString();
  private static final String DISP_NAME_FIELD = ActivityObject.Field.DISPLAY_NAME.toString();
  private static final String ID_FIELD = ActivityObject.Field.ID.toString();
  private static final String OBJ_TYPE_FIELD = ActivityObject.Field.OBJECT_TYPE.toString();
  private static final String PUBLISHED_FIELD = ActivityObject.Field.PUBLISHED.toString();
  private static final String SUMMARY_FIELD = ActivityObject.Field.SUMMARY.toString();
  private static final String UPDATED_FIELD = ActivityObject.Field.UPDATED.toString();
  private static final String URL_FIELD = ActivityObject.Field.URL.toString();

  private static final String DOWN_DUPS_FIELD = ActivityObject.Field.DOWNSTREAM_DUPLICATES
          .toString();
  private static final String IMAGE_FIELD = ActivityObject.Field.IMAGE.toString();
  private static final String UPSTREAM_DUPS_FIELD = ActivityObject.Field.UPSTREAM_DUPLICATES
          .toString();
  private static final String OPENSOCIAL_FIELD = ActivityObject.Field.OPENSOCIAL.toString();

  private static final String ATTACHMENTS_FIELD = ActivityObject.Field.ATTACHMENTS.toString();
  private static final String AUTHOR_FIELD = ActivityObject.Field.AUTHOR.toString();

  private ExtendableBean fOpenSocial;

  /**
   * Creates an empty activity object data transfer object.
   */
  public ActivityObjectDTO() {
    super();
  }

  /**
   * Creates an activity object data transfer object using the given map for internal property
   * storage. The given map must not be null-
   *
   * @param props
   *          map to use for internal property storage
   */
  public ActivityObjectDTO(Map<String, Object> props) {
    super(props);
  }

  public boolean containsKey(Object key) {
    return this.fProperties.containsKey(key);
  }

  public boolean containsValue(Object value) {
    return this.fProperties.containsValue(value);
  }

  public Set<java.util.Map.Entry<String, Object>> entrySet() {
    return this.fProperties.entrySet();
  }

  public Object get(Object key) {
    return this.fProperties.get(key);
  }

  public boolean isEmpty() {
    return this.fProperties.isEmpty();
  }

  public Set<String> keySet() {
    return this.fProperties.keySet();
  }

  public Object put(String key, Object value) {
    return this.fProperties.put(key, value);
  }

  public void putAll(Map<? extends String, ? extends Object> m) {
    this.fProperties.putAll(m);
  }

  public Object remove(Object key) {
    return this.fProperties.remove(key);
  }

  public int size() {
    return this.fProperties.size();
  }

  public Collection<Object> values() {
    return this.fProperties.values();
  }

  public List<ActivityObject> getAttachments() {
    List<ActivityObject> attachments = null;

    @SuppressWarnings("unchecked")
    final List<Map<String, Object>> attMaps = (List<Map<String, Object>>) this.fProperties
            .get(ActivityObjectDTO.ATTACHMENTS_FIELD);
    if (attMaps != null && !attMaps.isEmpty()) {
      attachments = new ArrayList<ActivityObject>();
      ActivityObjectDTO attDTO = null;
      for (final Map<String, Object> att : attMaps) {
        attDTO = new ActivityObjectDTO(att);
        attachments.add(attDTO);
      }
    }

    return attachments;
  }

  public void setAttachments(List<ActivityObject> attachments) {
    if (attachments != null && !attachments.isEmpty()) {
      // TODO: specify list and map implementation
      final List<Map<String, Object>> attMaps = new ArrayList<>();

      Map<String, Object> objMap = null;
      for (final ActivityObject actObj : attachments) {
        objMap = new HashMap<String, Object>();
        new ActivityObjectDTO(objMap).setData(actObj);
        attMaps.add(actObj);
      }

      this.fProperties.put(ActivityObjectDTO.ATTACHMENTS_FIELD, attMaps);
    }
  }

  public ActivityObject getAuthor() {
    ActivityObject author = null;

    @SuppressWarnings("unchecked")
    final Map<String, Object> authMap = (Map<String, Object>) this.fProperties
            .get(ActivityObjectDTO.AUTHOR_FIELD);

    if (authMap != null) {
      author = new ActivityObjectDTO(authMap);
    }

    return author;
  }

  public void setAuthor(ActivityObject author) {
    // TODO: specify map class
    if (author != null) {
      final Map<String, Object> actObjMap = new HashMap<String, Object>();
      new ActivityObjectDTO(actObjMap).setData(author);

      this.fProperties.put(ActivityObjectDTO.AUTHOR_FIELD, actObjMap);
    }
  }

  public String getContent() {
    String content = null;
    final Object value = this.fProperties.get(ActivityObjectDTO.CONTENT_FIELD);

    if (value != null) {
      content = (String) value;
    }

    return content;
  }

  public void setContent(String content) {
    this.fProperties.put(ActivityObjectDTO.CONTENT_FIELD, content);
  }

  public String getDisplayName() {
    String dName = null;
    final Object value = this.fProperties.get(ActivityObjectDTO.DISP_NAME_FIELD);

    if (value != null) {
      dName = (String) value;
    }

    return dName;
  }

  public void setDisplayName(String displayName) {
    this.fProperties.put(ActivityObjectDTO.DISP_NAME_FIELD, displayName);
  }

  public List<String> getDownstreamDuplicates() {
    return listFromProperty(ActivityObjectDTO.DOWN_DUPS_FIELD, String.class);
  }

  public void setDownstreamDuplicates(List<String> downstreamDuplicates) {
    if (downstreamDuplicates != null) {
      final String[] dDups = downstreamDuplicates.toArray(new String[downstreamDuplicates.size()]);
      this.fProperties.put(ActivityObjectDTO.DOWN_DUPS_FIELD, dDups);
    } else {
      this.fProperties.put(ActivityObjectDTO.DOWN_DUPS_FIELD, null);
    }
  }

  public String getId() {
    String id = null;
    final Object value = this.fProperties.get(ActivityObjectDTO.ID_FIELD);

    if (value != null) {
      id = (String) value;
    }

    return id;
  }

  public void setId(String id) {
    this.fProperties.put(ActivityObjectDTO.ID_FIELD, id);
  }

  public MediaLink getImage() {
    MediaLink image = null;

    @SuppressWarnings("unchecked")
    final Map<String, Object> linkMap = (Map<String, Object>) this.fProperties
            .get(ActivityObjectDTO.IMAGE_FIELD);

    if (linkMap != null) {
      image = new MediaLinkDTO(linkMap);
    }

    return image;
  }

  public void setImage(MediaLink image) {
    // TODO: specify map implementation
    if (image != null) {
      final Map<String, Object> linkMap = new HashMap<String, Object>();
      new MediaLinkDTO(linkMap).setData(image);
      this.fProperties.put(ActivityObjectDTO.IMAGE_FIELD, linkMap);
    }
  }

  public String getObjectType() {
    String oType = null;
    final Object value = this.fProperties.get(ActivityObjectDTO.OBJ_TYPE_FIELD);

    if (value != null) {
      oType = (String) value;
    }

    return oType;
  }

  public void setObjectType(String objectType) {
    this.fProperties.put(ActivityObjectDTO.OBJ_TYPE_FIELD, objectType);
  }

  public String getPublished() {
    String published = null;
    final Object value = this.fProperties.get(ActivityObjectDTO.PUBLISHED_FIELD);

    if (value != null) {
      published = (String) value;
    }

    return published;
  }

  public void setPublished(String published) {
    this.fProperties.put(ActivityObjectDTO.PUBLISHED_FIELD, published);
  }

  public String getSummary() {
    String summary = null;
    final Object value = this.fProperties.get(ActivityObjectDTO.SUMMARY_FIELD);

    if (value != null) {
      summary = (String) value;
    }

    return summary;
  }

  public void setSummary(String summary) {
    this.fProperties.put(ActivityObjectDTO.SUMMARY_FIELD, summary);
  }

  public String getUpdated() {
    String updated = null;
    final Object value = this.fProperties.get(ActivityObjectDTO.UPDATED_FIELD);

    if (value != null) {
      updated = (String) value;
    }

    return updated;
  }

  public void setUpdated(String updated) {
    this.fProperties.put(ActivityObjectDTO.UPDATED_FIELD, updated);
  }

  public List<String> getUpstreamDuplicates() {
    return listFromProperty(ActivityObjectDTO.UPSTREAM_DUPS_FIELD, String.class);
  }

  public void setUpstreamDuplicates(List<String> upstreamDuplicates) {
    if (upstreamDuplicates != null) {
      final String[] uDups = upstreamDuplicates.toArray(new String[upstreamDuplicates.size()]);
      this.fProperties.put(ActivityObjectDTO.UPSTREAM_DUPS_FIELD, uDups);
    } else {
      this.fProperties.put(ActivityObjectDTO.UPSTREAM_DUPS_FIELD, null);
    }
  }

  public String getUrl() {
    String url = null;
    final Object value = this.fProperties.get(ActivityObjectDTO.URL_FIELD);

    if (value != null) {
      url = (String) value;
    }

    return url;
  }

  public void setUrl(String url) {
    this.fProperties.put(ActivityObjectDTO.URL_FIELD, url);
  }

  public ExtendableBean getOpenSocial() {
    return this.fOpenSocial;
  }

  public void setOpenSocial(ExtendableBean opensocial) {
    this.fOpenSocial = opensocial;

    this.fProperties.put(ActivityObjectDTO.OPENSOCIAL_FIELD, opensocial);
  }

  /**
   * Sets the properties of this data transfer object to those of the given object. If the given
   * Object is null, all data is cleared.
   *
   * @param object
   *          activity object containing data to set
   */
  public void setData(final ActivityObject object) {
    if (object == null) {
      this.fProperties.clear();
      return;
    }

    this.fProperties.put(ActivityObjectDTO.CONTENT_FIELD, object.getContent());
    this.fProperties.put(ActivityObjectDTO.DISP_NAME_FIELD, object.getDisplayName());
    this.fProperties.put(ActivityObjectDTO.ID_FIELD, object.getId());
    this.fProperties.put(ActivityObjectDTO.OBJ_TYPE_FIELD, object.getObjectType());
    this.fProperties.put(ActivityObjectDTO.PUBLISHED_FIELD, object.getPublished());
    this.fProperties.put(ActivityObjectDTO.SUMMARY_FIELD, object.getSummary());
    this.fProperties.put(ActivityObjectDTO.UPDATED_FIELD, object.getUpdated());
    this.fProperties.put(ActivityObjectDTO.URL_FIELD, object.getUrl());

    // set lists
    this.setDownstreamDuplicates(object.getDownstreamDuplicates());
    this.setUpstreamDuplicates(object.getUpstreamDuplicates());

    // set related activity objects
    this.setAttachments(object.getAttachments());
    this.setAuthor(object.getAuthor());
    this.setImage(object.getImage());
  }
}
