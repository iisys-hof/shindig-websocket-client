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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.shindig.social.opensocial.model.Address;
import org.apache.shindig.social.opensocial.model.Album;
import org.apache.shindig.social.opensocial.model.MediaItem.Type;

/**
 * Data transfer object containing album information.
 */
public class AlbumDTO extends ADataTransferObject implements Album {
  private static final String DESCRIPTION_FIELD = Album.Field.DESCRIPTION.toString();
  private static final String ID_FIELD = Album.Field.ID.toString();
  private static final String LOCATION_FIELD = Album.Field.LOCATION.toString();
  private static final String MEDIA_ITEM_COUNT_FIELD = Album.Field.MEDIA_ITEM_COUNT.toString();
  private static final String MEDIA_MIME_TYPE_FIELD = Album.Field.MEDIA_MIME_TYPE.toString();
  private static final String MEDIA_TYPE_FIELD = Album.Field.MEDIA_TYPE.toString();
  private static final String OWNER_ID_FIELD = Album.Field.OWNER_ID.toString();
  private static final String THUMBNAIL_FIELD = Album.Field.THUMBNAIL_URL.toString();
  private static final String TITLE_FIELD = Album.Field.TITLE.toString();

  /**
   * Creates an empty album data transfer object.
   */
  public AlbumDTO() {
    super();
  }

  public AlbumDTO(Map<String, Object> propMap) {
    super(propMap);
  }

  @Override
  public String getDescription() {
    return (String) this.fProperties.get(AlbumDTO.DESCRIPTION_FIELD);
  }

  @Override
  public void setDescription(String description) {
    if (description != null) {
      this.fProperties.put(AlbumDTO.DESCRIPTION_FIELD, description);
    } else {
      this.fProperties.remove(AlbumDTO.DESCRIPTION_FIELD);
    }
  }

  @Override
  public String getId() {
    return (String) this.fProperties.get(AlbumDTO.ID_FIELD);
  }

  @Override
  public void setId(String id) {
    if (id != null) {
      this.fProperties.put(AlbumDTO.ID_FIELD, id);
    } else {
      this.fProperties.remove(AlbumDTO.ID_FIELD);
    }
  }

  @Override
  public Address getLocation() {
    Address add = null;
    @SuppressWarnings("unchecked")
    final Map<String, Object> locMap = (Map<String, Object>) this.fProperties
            .get(AlbumDTO.LOCATION_FIELD);

    if (locMap != null) {
      add = new AddressDTO(locMap);
    }

    return add;
  }

  @Override
  public void setLocation(Address location) {
    // TODO: specify map implementation
    if (location != null) {
      final Map<String, Object> locMap = new HashMap<String, Object>();
      new AddressDTO(locMap).setData(location);
    } else {
      this.fProperties.remove(AlbumDTO.LOCATION_FIELD);
    }
  }

  @Override
  public Integer getMediaItemCount() {
    return (Integer) this.fProperties.get(AlbumDTO.MEDIA_ITEM_COUNT_FIELD);
  }

  @Override
  public void setMediaItemCount(Integer mediaItemCount) {
    if (mediaItemCount != null) {
      this.fProperties.put(AlbumDTO.MEDIA_ITEM_COUNT_FIELD, mediaItemCount);
    } else {
      this.fProperties.remove(AlbumDTO.MEDIA_ITEM_COUNT_FIELD);
    }
  }

  @Override
  public List<String> getMediaMimeType() {
    final List<String> medMimeTypes = listFromProperty(AlbumDTO.MEDIA_MIME_TYPE_FIELD, String.class);
    return medMimeTypes;
  }

  @Override
  public void setMediaMimeType(List<String> mediaMimeType) {
    if (mediaMimeType != null) {
      final String[] mimeArr = mediaMimeType.toArray(new String[mediaMimeType.size()]);
      this.fProperties.put(AlbumDTO.MEDIA_MIME_TYPE_FIELD, mimeArr);
    } else {
      this.fProperties.remove(AlbumDTO.MEDIA_MIME_TYPE_FIELD);
    }
  }

  @Override
  public List<Type> getMediaType() {
    final List<String> typeList = listFromProperty(AlbumDTO.MEDIA_TYPE_FIELD, String.class);

    if (typeList != null) {
      final List<Type> medTypeList = new LinkedList<Type>();

      for (final String typeString : typeList) {
        medTypeList.add(Type.valueOf(typeString));
      }

      return medTypeList;
    }

    return null;
  }

  @Override
  public void setMediaType(List<Type> mediaType) {
    if (mediaType != null) {
      int index = 0;
      final String[] typeArr = new String[mediaType.size()];
      for (final Type type : mediaType) {
        typeArr[index++] = type.name();
      }
      this.fProperties.put(AlbumDTO.MEDIA_TYPE_FIELD, typeArr);
    } else {
      this.fProperties.remove(AlbumDTO.MEDIA_TYPE_FIELD);
    }
  }

  @Override
  public String getOwnerId() {
    return (String) this.fProperties.get(AlbumDTO.OWNER_ID_FIELD);
  }

  @Override
  public void setOwnerId(String ownerId) {
    if (ownerId != null) {
      this.fProperties.put(AlbumDTO.OWNER_ID_FIELD, ownerId);
    } else {
      this.fProperties.remove(AlbumDTO.OWNER_ID_FIELD);
    }
  }

  @Override
  public String getThumbnailUrl() {
    return (String) this.fProperties.get(AlbumDTO.THUMBNAIL_FIELD);
  }

  @Override
  public void setThumbnailUrl(String thumbnailUrl) {
    if (thumbnailUrl != null) {
      this.fProperties.put(AlbumDTO.THUMBNAIL_FIELD, thumbnailUrl);
    } else {
      this.fProperties.remove(thumbnailUrl);
    }
  }

  @Override
  public String getTitle() {
    return (String) this.fProperties.get(AlbumDTO.TITLE_FIELD);
  }

  @Override
  public void setTitle(String title) {
    if (title != null) {
      this.fProperties.put(AlbumDTO.TITLE_FIELD, title);
    } else {
      this.fProperties.remove(AlbumDTO.TITLE_FIELD);
    }
  }

  /**
   * Sets the properties of this data transfer object to those of the given object. If the given
   * Object is null, all data is cleared.
   *
   * @param album
   *          album object containing data to set
   */
  public void setData(final Album album) {
    if (album == null) {
      this.fProperties.clear();
      return;
    }

    this.fProperties.put(AlbumDTO.DESCRIPTION_FIELD, album.getDescription());
    this.fProperties.put(AlbumDTO.ID_FIELD, album.getId());

    this.setLocation(album.getLocation());

    this.fProperties.put(AlbumDTO.MEDIA_ITEM_COUNT_FIELD, album.getMediaItemCount());

    this.setMediaMimeType(album.getMediaMimeType());
    this.setMediaType(album.getMediaType());

    this.fProperties.put(AlbumDTO.OWNER_ID_FIELD, album.getOwnerId());
    this.fProperties.put(AlbumDTO.THUMBNAIL_FIELD, album.getThumbnailUrl());
    this.fProperties.put(AlbumDTO.TITLE_FIELD, album.getTitle());
  }
}
