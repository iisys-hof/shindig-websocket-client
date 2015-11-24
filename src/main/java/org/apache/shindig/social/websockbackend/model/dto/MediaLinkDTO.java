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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.shindig.protocol.model.ExtendableBean;
import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.model.MediaLink;

/**
 * Data transfer object containing media link information. The opensocial has to be set via the
 * corresponding setter.
 */
public class MediaLinkDTO extends ADataTransferObject implements MediaLink {
  private static final String DURATION_FIELD = MediaLink.Field.DURATION.toString();
  private static final String HEIGHT_FIELD = MediaLink.Field.HEIGHT.toString();
  private static final String URL_FIELD = MediaLink.Field.URL.toString();
  private static final String WIDTH_FIELD = MediaLink.Field.WIDTH.toString();
  private static final String OPENSOCIAL_FIELD = ActivityEntry.Field.OPENSOCIAL.toString();

  private ExtendableBean fOpenSocial;

  /**
   * Creates an empty media link data transfer object.
   */
  public MediaLinkDTO() {
    super();
  }

  /**
   * Creates a media link data transfer object using the given map for internal property storage.
   * The given map must not be null.
   *
   * @param props
   *          map to use for internal property storage
   */
  public MediaLinkDTO(Map<String, Object> props) {
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

  public Integer getDuration() {
    Integer duration = null;
    final Object value = this.fProperties.get(MediaLinkDTO.DURATION_FIELD);

    if (value != null) {
      duration = (Integer) value;
    }

    return duration;
  }

  public void setDuration(Integer duration) {
    this.fProperties.put(MediaLinkDTO.DURATION_FIELD, duration);
  }

  public Integer getHeight() {
    Integer height = null;
    final Object value = this.fProperties.get(MediaLinkDTO.HEIGHT_FIELD);

    if (value != null) {
      height = (Integer) value;
    }

    return height;
  }

  public void setHeight(Integer height) {
    this.fProperties.put(MediaLinkDTO.HEIGHT_FIELD, height);
  }

  public String getUrl() {
    String url = null;
    final Object value = this.fProperties.get(MediaLinkDTO.URL_FIELD);

    if (value != null) {
      url = (String) value;
    }

    return url;
  }

  public void setUrl(String url) {
    this.fProperties.put(MediaLinkDTO.URL_FIELD, url);
  }

  public Integer getWidth() {
    Integer width = null;
    final Object value = this.fProperties.get(MediaLinkDTO.WIDTH_FIELD);

    if (value != null) {
      width = (Integer) value;
    }

    return width;
  }

  public void setWidth(Integer width) {
    this.fProperties.put(MediaLinkDTO.WIDTH_FIELD, width);
  }

  public ExtendableBean getOpenSocial() {
    return this.fOpenSocial;
  }

  public void setOpenSocial(ExtendableBean opensocial) {
    this.fOpenSocial = opensocial;

    this.fProperties.put(MediaLinkDTO.OPENSOCIAL_FIELD, opensocial);
  }

  /**
   * Sets the properties of this data transfer object to those of the given object. If the given
   * Object is null, all data is cleared.
   *
   * @param mediaLink
   *          media link object containing data to set
   */
  public void setData(final MediaLink mediaLink) {
    if (mediaLink == null) {
      this.fProperties.clear();
      return;
    }

    this.fProperties.put(MediaLinkDTO.DURATION_FIELD, mediaLink.getDuration());
    this.fProperties.put(MediaLinkDTO.HEIGHT_FIELD, mediaLink.getHeight());
    this.fProperties.put(MediaLinkDTO.URL_FIELD, mediaLink.getUrl());
    this.fProperties.put(MediaLinkDTO.WIDTH_FIELD, mediaLink.getWidth());

    // TODO: opensocial?
  }
}
