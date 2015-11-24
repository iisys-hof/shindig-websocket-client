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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.shindig.protocol.model.ExtendableBean;
import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.model.ActivityObject;
import org.apache.shindig.social.opensocial.model.MediaLink;

/**
 * Data transfer object containing activity entry information. The openSocial and extensions have to
 * be set via the corresponding setters.
 */
public class ActivityEntryDTO extends ADataTransferObject implements ActivityEntry {
  private static final String ACTOR_FIELD = ActivityEntry.Field.ACTOR.toString();
  private static final String GENERATOR_FIELD = ActivityEntry.Field.GENERATOR.toString();
  private static final String OBJECT_FIELD = ActivityEntry.Field.OBJECT.toString();
  private static final String PROVIDER_FIELD = ActivityEntry.Field.PROVIDER.toString();
  private static final String TARGET_FIELD = ActivityEntry.Field.TARGET.toString();

  private static final String CONTENT_FIELD = ActivityEntry.Field.CONTENT.toString();
  private static final String ID_FIELD = ActivityEntry.Field.ID.toString();
  private static final String PUBLISHED_FIELD = ActivityEntry.Field.PUBLISHED.toString();
  private static final String TITLE_FIELD = ActivityEntry.Field.TITLE.toString();
  private static final String UPDATED_FIELD = ActivityEntry.Field.UPDATED.toString();
  private static final String URL_FIELD = ActivityEntry.Field.URL.toString();
  private static final String VERB_FIELD = ActivityEntry.Field.VERB.toString();

  private static final String OPENSOCIAL_FIELD = ActivityEntry.Field.OPENSOCIAL.toString();
  private static final String EXTENSIONS_FIELD = ActivityEntry.Field.EXTENSIONS.toString();

  private static final String ICON_FIELD = ActivityEntry.Field.ICON.toString();

  private ExtendableBean fOpenSocial, fExtensions;

  /**
   * Creates an empty activity entry data transfer object.
   */
  public ActivityEntryDTO() {
    super();
  }

  /**
   * Creates an activity entry data transfer object using the given map for internal property
   * storage The given map must not be null.
   *
   * @param props
   *          map to use for property storage
   */
  public ActivityEntryDTO(Map<String, Object> props) {
    super(props);
  }

  public int compareTo(ActivityEntry arg0) {
    // TODO ?
    return 0;
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

  public ActivityObject getActor() {
    ActivityObject actor = null;
    @SuppressWarnings("unchecked")
    final Map<String, Object> actMap = (Map<String, Object>) this.fProperties
            .get(ActivityEntryDTO.ACTOR_FIELD);

    if (actMap != null) {
      actor = new ActivityObjectDTO(actMap);
    }

    return actor;
  }

  public void setActor(ActivityObject actor) {
    // TODO: specify map implementation
    if (actor != null) {
      final Map<String, Object> actMap = new HashMap<String, Object>();
      final ActivityObjectDTO dto = new ActivityObjectDTO(actMap);
      dto.setData(actor);
      dto.stripNullValues();
      this.fProperties.put(ActivityEntryDTO.ACTOR_FIELD, actMap);
    } else {
      this.fProperties.put(ActivityEntryDTO.ACTOR_FIELD, null);
    }
  }

  public String getContent() {
    String content = null;
    final Object value = this.fProperties.get(ActivityEntryDTO.CONTENT_FIELD);

    if (value != null) {
      content = (String) value;
    }

    return content;
  }

  public void setContent(String content) {
    this.fProperties.put(ActivityEntryDTO.CONTENT_FIELD, content);
  }

  public ActivityObject getGenerator() {
    ActivityObject generator = null;
    @SuppressWarnings("unchecked")
    final Map<String, Object> genMap = (Map<String, Object>) this.fProperties
            .get(ActivityEntryDTO.GENERATOR_FIELD);

    if (genMap != null) {
      generator = new ActivityObjectDTO(genMap);
    }

    return generator;
  }

  public void setGenerator(ActivityObject generator) {
    // TODO: specify map implementation
    if (generator != null) {
      final Map<String, Object> genMap = new HashMap<String, Object>();
      final ActivityObjectDTO dto = new ActivityObjectDTO(genMap);
      dto.setData(generator);
      dto.stripNullValues();
      this.fProperties.put(ActivityEntryDTO.GENERATOR_FIELD, genMap);
    } else {
      this.fProperties.put(ActivityEntryDTO.GENERATOR_FIELD, null);
    }
  }

  public MediaLink getIcon() {
    MediaLink icon = null;
    @SuppressWarnings("unchecked")
    final Map<String, Object> iconMap = (Map<String, Object>) this.fProperties
            .get(ActivityEntryDTO.ICON_FIELD);

    if (iconMap != null) {
      icon = new MediaLinkDTO(iconMap);
    }

    return icon;
  }

  public void setIcon(MediaLink icon) {
    // TODO: specify map implementation
    if (icon != null) {
      final Map<String, Object> iconMap = new HashMap<String, Object>();
      new MediaLinkDTO(iconMap).setData(icon);
      this.fProperties.put(ActivityEntryDTO.ICON_FIELD, iconMap);
    } else {
      this.fProperties.put(ActivityEntryDTO.ICON_FIELD, null);
    }
  }

  public String getId() {
    String id = null;
    final Object value = this.fProperties.get(ActivityEntryDTO.ID_FIELD);

    if (value != null) {
      id = (String) value;
    }

    return id;
  }

  public void setId(String id) {
    this.fProperties.put(ActivityEntryDTO.ID_FIELD, id);
  }

  public ActivityObject getObject() {
    ActivityObject object = null;
    @SuppressWarnings("unchecked")
    final Map<String, Object> objMap = (Map<String, Object>) this.fProperties
            .get(ActivityEntryDTO.OBJECT_FIELD);

    if (objMap != null) {
      object = new ActivityObjectDTO(objMap);
    }

    return object;
  }

  public void setObject(ActivityObject object) {
    // TODO: specify map implementation
    if (object != null) {
      final Map<String, Object> objMap = new HashMap<String, Object>();
      final ActivityObjectDTO dto = new ActivityObjectDTO(objMap);
      dto.setData(object);
      dto.stripNullValues();
      this.fProperties.put(ActivityEntryDTO.OBJECT_FIELD, objMap);
    } else {
      this.fProperties.put(ActivityEntryDTO.OBJECT_FIELD, null);
    }
  }

  public String getPublished() {
    String published = null;
    final Object value = this.fProperties.get(ActivityEntryDTO.PUBLISHED_FIELD);

    if (value != null) {
      published = (String) value;
    }

    return published;
  }

  public void setPublished(String published) {
    this.fProperties.put(ActivityEntryDTO.PUBLISHED_FIELD, published);
  }

  public ActivityObject getProvider() {
    ActivityObject provider = null;
    @SuppressWarnings("unchecked")
    final Map<String, Object> provMap = (Map<String, Object>) this.fProperties
            .get(ActivityEntryDTO.PROVIDER_FIELD);

    if (provMap != null) {
      provider = new ActivityObjectDTO(provMap);
    }

    return provider;
  }

  public void setProvider(ActivityObject provider) {
    // TODO: specify map implementation
    if (provider != null) {
      final Map<String, Object> provMap = new HashMap<String, Object>();
      final ActivityObjectDTO dto = new ActivityObjectDTO(provMap);
      dto.setData(provider);
      dto.stripNullValues();
      this.fProperties.put(ActivityEntryDTO.PROVIDER_FIELD, provMap);
    } else {
      this.fProperties.put(ActivityEntryDTO.PROVIDER_FIELD, null);
    }
  }

  public ActivityObject getTarget() {
    ActivityObject target = null;
    @SuppressWarnings("unchecked")
    final Map<String, Object> targetMap = (Map<String, Object>) this.fProperties
            .get(ActivityEntryDTO.TARGET_FIELD);

    if (targetMap != null) {
      target = new ActivityObjectDTO(targetMap);
    }

    return target;
  }

  public void setTarget(ActivityObject target) {
    // TODO: specify map implementation
    if (target != null) {
      final Map<String, Object> tarMap = new HashMap<String, Object>();
      final ActivityObjectDTO dto = new ActivityObjectDTO(tarMap);
      dto.setData(target);
      dto.stripNullValues();
      this.fProperties.put(ActivityEntryDTO.TARGET_FIELD, tarMap);
    } else {
      this.fProperties.put(ActivityEntryDTO.TARGET_FIELD, null);
    }
  }

  public String getTitle() {
    String title = null;
    final Object value = this.fProperties.get(ActivityEntryDTO.TITLE_FIELD);

    if (value != null) {
      title = (String) value;
    }

    return title;
  }

  public void setTitle(String title) {
    this.fProperties.put(ActivityEntryDTO.TITLE_FIELD, title);
  }

  public String getUpdated() {
    String update = null;
    final Object value = this.fProperties.get(ActivityEntryDTO.UPDATED_FIELD);

    if (value != null) {
      update = (String) value;
    }

    return update;
  }

  public void setUpdated(String updated) {
    this.fProperties.put(ActivityEntryDTO.UPDATED_FIELD, updated);
  }

  public String getUrl() {
    String url = null;
    final Object value = this.fProperties.get(ActivityEntryDTO.URL_FIELD);

    if (value != null) {
      url = (String) value;
    }

    return url;
  }

  public void setUrl(String url) {
    this.fProperties.put(ActivityEntryDTO.URL_FIELD, url);
  }

  public String getVerb() {
    String verb = null;
    final Object value = this.fProperties.get(ActivityEntryDTO.VERB_FIELD);

    if (value != null) {
      verb = (String) value;
    }

    return verb;
  }

  public void setVerb(String verb) {
    this.fProperties.put(ActivityEntryDTO.VERB_FIELD, verb);
  }

  public ExtendableBean getOpenSocial() {
    return this.fOpenSocial;
  }

  public void setOpenSocial(ExtendableBean opensocial) {
    this.fOpenSocial = opensocial;

    this.fProperties.put(ActivityEntryDTO.OPENSOCIAL_FIELD, opensocial);
  }

  public ExtendableBean getExtensions() {
    return this.fExtensions;
  }

  public void setExtensions(ExtendableBean extensions) {
    this.fExtensions = extensions;

    this.fProperties.put(ActivityEntryDTO.EXTENSIONS_FIELD, extensions);
  }

  /**
   * Sets the properties of this data transfer object to the values specified in the given activity
   * entry object. If the given object is null, all properties are cleared.
   *
   * @param entry
   *          activity entry containing data to set
   */
  public void setData(final ActivityEntry entry) {
    if (entry == null) {
      this.fProperties.clear();
      return;
    }

    this.fProperties.put(ActivityEntryDTO.CONTENT_FIELD, entry.getContent());
    this.fProperties.put(ActivityEntryDTO.ID_FIELD, entry.getId());
    this.fProperties.put(ActivityEntryDTO.PUBLISHED_FIELD, entry.getPublished());
    this.fProperties.put(ActivityEntryDTO.TITLE_FIELD, entry.getTitle());
    this.fProperties.put(ActivityEntryDTO.UPDATED_FIELD, entry.getUpdated());
    this.fProperties.put(ActivityEntryDTO.URL_FIELD, entry.getUrl());
    this.fProperties.put(ActivityEntryDTO.VERB_FIELD, entry.getVerb());

    // TODO: generate title and body for deprecated activities if missing?

    // external objects
    this.setActor(entry.getActor());
    this.setGenerator(entry.getGenerator());
    this.setObject(entry.getObject());
    this.setProvider(entry.getProvider());
    this.setTarget(entry.getTarget());
    this.setIcon(entry.getIcon());
  }
}
