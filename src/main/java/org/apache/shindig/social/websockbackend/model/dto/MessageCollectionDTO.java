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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.shindig.social.core.model.UrlImpl;
import org.apache.shindig.social.opensocial.model.Message;
import org.apache.shindig.social.opensocial.model.MessageCollection;
import org.apache.shindig.social.opensocial.model.Url;

/**
 * Data transfer object containing message collection information.
 */
public class MessageCollectionDTO extends ADataTransferObject implements MessageCollection {
  private static final String ID_FIELD = MessageCollection.Field.ID.toString();
  private static final String TITLE_FIELD = MessageCollection.Field.TITLE.toString();
  private static final String UPDATED_FIELD = MessageCollection.Field.UPDATED.toString();
  private static final String URLS_FIELD = Message.Field.URLS.toString();

  private static final String TOTAL_FIELD = MessageCollection.Field.TOTAL.toString();
  private static final String UNREAD_FIELD = MessageCollection.Field.UNREAD.toString();

  /**
   * Creates an empty message collection data transfer object.
   */
  public MessageCollectionDTO() {
    super();
  }

  /**
   * Creates a message collection data transfer object using the given map for internal property
   * storage The given map must not be null.
   *
   * @param props
   *          map to use for property storage
   */
  public MessageCollectionDTO(Map<String, Object> props) {
    super(props);
  }

  public String getId() {
    String id = null;
    final Object value = this.fProperties.get(MessageCollectionDTO.ID_FIELD);

    if (value != null) {
      id = (String) value;
    }

    return id;
  }

  public void setId(String id) {
    this.fProperties.put(MessageCollectionDTO.ID_FIELD, id);
  }

  public String getTitle() {
    String title = null;
    final Object value = this.fProperties.get(MessageCollectionDTO.TITLE_FIELD);

    if (value != null) {
      title = (String) value;
    }

    return title;
  }

  public void setTitle(String newTitle) {
    this.fProperties.put(MessageCollectionDTO.TITLE_FIELD, newTitle);
  }

  public Integer getTotal() {
    Integer total = null;
    final Object value = this.fProperties.get(MessageCollectionDTO.TOTAL_FIELD);

    if (value != null) {
      if (value instanceof Integer) {
        total = (Integer) value;
      } else {
        total = ((Long) value).intValue();
      }
    }

    return total;
  }

  public void setTotal(Integer total) {
    // server-side computed value
    this.fProperties.put(MessageCollectionDTO.TOTAL_FIELD, total);
  }

  public Integer getUnread() {
    Integer unread = null;
    final Object value = this.fProperties.get(MessageCollectionDTO.UNREAD_FIELD);

    if (value != null) {
      if (value instanceof Integer) {
        unread = (Integer) value;
      } else {
        unread = ((Long) value).intValue();
      }
    }

    return unread;
  }

  public void setUnread(Integer unread) {
    // server-side computed value
    this.fProperties.put(MessageCollectionDTO.UNREAD_FIELD, unread);
  }

  public Date getUpdated() {
    Date updated = null;
    final Object value = this.fProperties.get(MessageCollectionDTO.UPDATED_FIELD);

    if (value != null) {
      final long time = (Long) value;
      updated = new Date(time);
    }
    return updated;
  }

  public void setUpdated(Date updated) {
    if (updated != null) {
      this.fProperties.put(MessageCollectionDTO.UPDATED_FIELD, updated.getTime());
    } else {
      this.fProperties.put(MessageCollectionDTO.UPDATED_FIELD, null);
    }
  }

  public List<Url> getUrls() {
    List<Url> urls = null;
    final String[] urlStrings = (String[]) this.fProperties.get(MessageCollectionDTO.URLS_FIELD);

    if (urlStrings != null) {
      urls = new ArrayList<Url>();
      Url tempUrl = null;
      for (final String string : urlStrings) {
        tempUrl = new UrlImpl();
        tempUrl.setLinkText(string);
        tempUrl.setValue(string);
        urls.add(tempUrl);
      }
    }

    return urls;
  }

  public void setUrls(List<Url> urls) {
    if (urls != null) {
      int index = 0;
      final String[] urlArr = new String[urls.size()];

      for (final Url url : urls) {
        urlArr[index++] = url.getValue();
      }

      this.fProperties.put(MessageCollectionDTO.URLS_FIELD, urlArr);
    } else {
      this.fProperties.put(MessageCollectionDTO.URLS_FIELD, null);
    }
  }

  /**
   * Sets the properties of this data transfer object to those of the given object. If the given
   * Object is null, all data is cleared.
   *
   * @param coll
   *          message collection object containing data to set
   */
  public void setData(final MessageCollection coll) {
    if (coll == null) {
      this.fProperties.clear();
      return;
    }

    this.fProperties.put(MessageCollectionDTO.ID_FIELD, coll.getId());
    this.fProperties.put(MessageCollectionDTO.TITLE_FIELD, coll.getTitle());

    this.setUpdated(coll.getUpdated());
    this.setUrls(coll.getUrls());
  }
}
