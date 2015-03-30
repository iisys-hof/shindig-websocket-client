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
import org.apache.shindig.social.opensocial.model.MediaItem;

/**
 * Data transfer object containing media item information.
 */
public class MediaItemDTO extends ADataTransferObject implements MediaItem {
  private static final String MIME_TYPE_FIELD = MediaItem.Field.MIME_TYPE.toString();
  private static final String TYPE_FIELD = MediaItem.Field.TYPE.toString();
  private static final String URL_FIELD = MediaItem.Field.URL.toString();
  private static final String THUMB_FIELD = MediaItem.Field.THUMBNAIL_URL.toString();
  private static final String ALBUM_ID_FIELD = MediaItem.Field.ALBUM_ID.toString();
  private static final String CREATED_FIELD = MediaItem.Field.CREATED.toString();
  private static final String DESCRIPTION_FIELD = MediaItem.Field.DESCRIPTION.toString();
  private static final String DURATION_FIELD = MediaItem.Field.DURATION.toString();
  private static final String SIZE_FIELD = MediaItem.Field.FILE_SIZE.toString();
  private static final String ID_FIELD = MediaItem.Field.ID.toString();
  private static final String LANGUAGE_FIELD = MediaItem.Field.LANGUAGE.toString();
  private static final String UPDATED_FIELD = MediaItem.Field.LAST_UPDATED.toString();
  private static final String NUM_COMM_FIELD = MediaItem.Field.NUM_COMMENTS.toString();
  private static final String NUM_VIEWS_FIELD = MediaItem.Field.NUM_VIEWS.toString();
  private static final String NUM_VOTES_FIELD = MediaItem.Field.NUM_VOTES.toString();
  private static final String RATING_FIELD = MediaItem.Field.RATING.toString();
  private static final String START_TIME_FIELD = MediaItem.Field.START_TIME.toString();
  private static final String TAGGED_FIELD = MediaItem.Field.TAGGED_PEOPLE.toString();
  private static final String TAGS_FIELD = MediaItem.Field.TAGS.toString();
  private static final String TITLE_FIELD = MediaItem.Field.TITLE.toString();

  private static final String LOCATION_FIELD = MediaItem.Field.LOCATION.toString();

  /**
   * Creates an empty media item data transfer object.
   */
  public MediaItemDTO() {
    super();
  }

  /**
   * Creates a media item data transfer object using the given map for internal property storage.
   * The given map must not be null.
   *
   * @param props
   *          map to use for property storage
   */
  public MediaItemDTO(Map<String, Object> props) {
    super(props);
  }

  public String getMimeType() {
    String mimeType = null;
    final Object value = this.fProperties.get(MediaItemDTO.MIME_TYPE_FIELD);

    if (value != null) {
      mimeType = (String) value;
    }

    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.fProperties.put(MediaItemDTO.MIME_TYPE_FIELD, mimeType);
  }

  public Type getType() {
    Type type = null;
    String typeString = null;
    final Object value = this.fProperties.get(MediaItemDTO.TYPE_FIELD);

    if (value != null) {
      typeString = (String) value;
      type = Type.valueOf(typeString);
    }

    return type;
  }

  public void setType(Type type) {
    if (type != null) {
      this.fProperties.put(MediaItemDTO.TYPE_FIELD, type.name());
    } else {
      this.fProperties.put(MediaItemDTO.TYPE_FIELD, null);
    }
  }

  public String getUrl() {
    String url = null;
    final Object value = this.fProperties.get(MediaItemDTO.URL_FIELD);

    if (value != null) {
      url = (String) value;
    }

    return url;
  }

  public void setUrl(String url) {
    this.fProperties.put(MediaItemDTO.URL_FIELD, url);
  }

  public String getThumbnailUrl() {
    String tUrl = null;
    final Object value = this.fProperties.get(MediaItemDTO.THUMB_FIELD);

    if (value != null) {
      tUrl = (String) value;
    }

    return tUrl;
  }

  public void setThumbnailUrl(String url) {
    this.fProperties.put(MediaItemDTO.THUMB_FIELD, url);
  }

  public String getAlbumId() {
    String albumId = null;
    final Object value = this.fProperties.get(MediaItemDTO.ALBUM_ID_FIELD);

    if (value != null) {
      albumId = (String) value;
    }

    return albumId;
  }

  public void setAlbumId(String albumId) {
    this.fProperties.put(MediaItemDTO.ALBUM_ID_FIELD, albumId);
  }

  public String getCreated() {
    String created = null;
    final Object value = this.fProperties.get(MediaItemDTO.CREATED_FIELD);

    if (value != null) {
      created = (String) value;
    }

    return created;
  }

  public void setCreated(String created) {
    this.fProperties.put(MediaItemDTO.CREATED_FIELD, created);
  }

  public String getDescription() {
    String description = null;
    final Object value = this.fProperties.get(MediaItemDTO.DESCRIPTION_FIELD);

    if (value != null) {
      description = (String) value;
    }

    return description;
  }

  public void setDescription(String description) {
    this.fProperties.put(MediaItemDTO.DESCRIPTION_FIELD, description);
  }

  public String getDuration() {
    String duration = null;
    final Object value = this.fProperties.get(MediaItemDTO.DURATION_FIELD);

    if (value != null) {
      duration = (String) value;
    }

    return duration;
  }

  public void setDuration(String duration) {
    this.fProperties.put(MediaItemDTO.DURATION_FIELD, duration);
  }

  public String getFileSize() {
    String fileSize = null;
    final Object value = this.fProperties.get(MediaItemDTO.SIZE_FIELD);

    if (value != null) {
      fileSize = (String) value;
    }

    return fileSize;
  }

  public void setFileSize(String fileSize) {
    this.fProperties.put(MediaItemDTO.SIZE_FIELD, fileSize);
  }

  public String getId() {
    String id = null;
    final Object value = this.fProperties.get(MediaItemDTO.ID_FIELD);

    if (value != null) {
      id = (String) value;
    }

    return id;
  }

  public void setId(String id) {
    this.fProperties.put(MediaItemDTO.ID_FIELD, id);
  }

  public String getLanguage() {
    String language = null;
    final Object value = this.fProperties.get(MediaItemDTO.LANGUAGE_FIELD);

    if (value != null) {
      language = (String) value;
    }

    return language;
  }

  public void setLanguage(String language) {
    this.fProperties.put(MediaItemDTO.LANGUAGE_FIELD, language);
  }

  public String getLastUpdated() {
    String updated = null;
    final Object value = this.fProperties.get(MediaItemDTO.UPDATED_FIELD);

    if (value != null) {
      updated = (String) value;
    }

    return updated;
  }

  public void setLastUpdated(String lastUpdated) {
    this.fProperties.put(MediaItemDTO.UPDATED_FIELD, lastUpdated);
  }

  public Address getLocation() {
    AddressDTO add = null;

    @SuppressWarnings("unchecked")
    final Map<String, Object> locMap = (Map<String, Object>) this.fProperties
            .get(MediaItemDTO.LOCATION_FIELD);

    if (locMap != null) {
      add = new AddressDTO(locMap);
    }

    return add;
  }

  public void setLocation(Address location) {
    // TODO: set map implementation
    if (location != null) {
      AddressDTO dto = null;

      if (location instanceof AddressDTO) {
        dto = (AddressDTO) location;
      } else {
        dto = new AddressDTO();
        dto.setData(location);
      }

      this.fProperties.put(MediaItemDTO.LOCATION_FIELD, dto.propertyMap());
    } else {
      this.fProperties.put(MediaItemDTO.LOCATION_FIELD, null);
    }
  }

  public String getNumComments() {
    String comments = null;
    final Object value = this.fProperties.get(MediaItemDTO.NUM_COMM_FIELD);

    if (value != null) {
      comments = (String) value;
    }

    return comments;
  }

  public void setNumComments(String numComments) {
    this.fProperties.put(MediaItemDTO.NUM_COMM_FIELD, numComments);
  }

  public String getNumViews() {
    String views = null;
    final Object value = this.fProperties.get(MediaItemDTO.NUM_VIEWS_FIELD);

    if (value != null) {
      views = (String) value;
    }

    return views;
  }

  public void setNumViews(String numViews) {
    this.fProperties.put(MediaItemDTO.NUM_VIEWS_FIELD, numViews);
  }

  public String getNumVotes() {
    String votes = null;
    final Object value = this.fProperties.get(MediaItemDTO.NUM_VOTES_FIELD);

    if (value != null) {
      votes = (String) value;
    }

    return votes;
  }

  public void setNumVotes(String numVotes) {
    this.fProperties.put(MediaItemDTO.NUM_VOTES_FIELD, numVotes);
  }

  public String getRating() {
    String rating = null;
    final Object value = this.fProperties.get(MediaItemDTO.RATING_FIELD);

    if (value != null) {
      rating = (String) value;
    }

    return rating;
  }

  public void setRating(String rating) {
    this.fProperties.put(MediaItemDTO.RATING_FIELD, rating);
  }

  public String getStartTime() {
    String startTime = null;
    final Object value = this.fProperties.get(MediaItemDTO.START_TIME_FIELD);

    if (value != null) {
      startTime = (String) value;
    }

    return startTime;
  }

  public void setStartTime(String startTime) {
    this.fProperties.put(MediaItemDTO.START_TIME_FIELD, startTime);
  }

  public String getTaggedPeople() {
    String tagPeople = null;
    final Object value = this.fProperties.get(MediaItemDTO.TAGGED_FIELD);

    if (value != null) {
      tagPeople = (String) value;
    }

    return tagPeople;
  }

  public void setTaggedPeople(String taggedPeople) {
    this.fProperties.put(MediaItemDTO.TAGGED_FIELD, taggedPeople);
  }

  public String getTags() {
    String tags = null;
    final Object value = this.fProperties.get(MediaItemDTO.TAGS_FIELD);

    if (value != null) {
      tags = (String) value;
    }

    return tags;
  }

  public void setTags(String tags) {
    this.fProperties.put(MediaItemDTO.TAGS_FIELD, tags);
  }

  public String getTitle() {
    String title = null;
    final Object value = this.fProperties.get(MediaItemDTO.TITLE_FIELD);

    if (value != null) {
      title = (String) value;
    }

    return title;
  }

  public void setTitle(String title) {
    this.fProperties.put(MediaItemDTO.TITLE_FIELD, title);
  }

  /**
   * Sets the properties of this data transfer object to those of the given object. If the given
   * Object is null, all data is cleared.
   *
   * @param mediaItem
   *          media item object containing the data to set
   */
  public void setData(final MediaItem mediaItem) {
    if (mediaItem == null) {
      this.fProperties.clear();
      return;
    }

    this.fProperties.put(MediaItemDTO.ALBUM_ID_FIELD, mediaItem.getAlbumId());
    this.fProperties.put(MediaItemDTO.CREATED_FIELD, mediaItem.getCreated());
    this.fProperties.put(MediaItemDTO.DESCRIPTION_FIELD, mediaItem.getDescription());
    this.fProperties.put(MediaItemDTO.DURATION_FIELD, mediaItem.getDuration());
    this.fProperties.put(MediaItemDTO.ID_FIELD, mediaItem.getId());
    this.fProperties.put(MediaItemDTO.LANGUAGE_FIELD, mediaItem.getLanguage());

    this.setLocation(mediaItem.getLocation());

    this.fProperties.put(MediaItemDTO.MIME_TYPE_FIELD, mediaItem.getMimeType());
    this.fProperties.put(MediaItemDTO.NUM_COMM_FIELD, mediaItem.getNumComments());
    this.fProperties.put(MediaItemDTO.NUM_VIEWS_FIELD, mediaItem.getNumViews());
    this.fProperties.put(MediaItemDTO.NUM_VOTES_FIELD, mediaItem.getNumVotes());
    this.fProperties.put(MediaItemDTO.RATING_FIELD, mediaItem.getRating());
    this.fProperties.put(MediaItemDTO.SIZE_FIELD, mediaItem.getFileSize());
    this.fProperties.put(MediaItemDTO.START_TIME_FIELD, mediaItem.getStartTime());
    this.fProperties.put(MediaItemDTO.TAGGED_FIELD, mediaItem.getTaggedPeople());
    this.fProperties.put(MediaItemDTO.TAGS_FIELD, mediaItem.getTags());
    this.fProperties.put(MediaItemDTO.THUMB_FIELD, mediaItem.getThumbnailUrl());
    this.fProperties.put(MediaItemDTO.TITLE_FIELD, mediaItem.getTitle());

    this.setType(mediaItem.getType());

    this.fProperties.put(MediaItemDTO.UPDATED_FIELD, mediaItem.getLastUpdated());
    this.fProperties.put(MediaItemDTO.URL_FIELD, mediaItem.getUrl());
  }
}
