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
import org.apache.shindig.social.opensocial.model.Url;

/**
 * Data transfer object containing message information.
 */
public class MessageDTO extends ADataTransferObject implements Message {
  private static final String APP_URL_FIELD = Message.Field.APP_URL.toString();
  private static final String BODY_FIELD = Message.Field.BODY.toString();
  private static final String BODY_ID_FIELD = Message.Field.BODY_ID.toString();
  private static final String ID_FIELD = Message.Field.ID.toString();
  private static final String REPLY_FIELD = Message.Field.IN_REPLY_TO.toString();
  private static final String SENDER_ID_FIELD = Message.Field.SENDER_ID.toString();
  private static final String TITLE_FIELD = Message.Field.TITLE.toString();
  private static final String TITLE_ID_FIELD = Message.Field.TITLE_ID.toString();

  private static final String TYPE_FIELD = Message.Field.TYPE.toString();
  private static final String TIME_FIELD = Message.Field.TIME_SENT.toString();
  private static final String UPDATED_FIELD = Message.Field.UPDATED.toString();
  private static final String RECIPIENTS_FIELD = Message.Field.RECIPIENTS.toString();
  private static final String URLS_FIELD = Message.Field.URLS.toString();
  private static final String URL_TYPES_FIELD = "urls_types";
  private static final String URL_TEXTS_FIELD = "urls_linkTexts";

  private static final String STATUS_FIELD = Message.Field.STATUS.toString();
  private static final String COLL_IDS_FIELD = Message.Field.COLLECTION_IDS.toString();
  private static final String REPLIES_FIELD = Message.Field.REPLIES.toString();

  /**
   * Creates an empty message data transfer object.
   */
  public MessageDTO() {
    super();
  }

  /**
   * Creates a message data transfer object using the given map for internal property storage. The
   * given map must not be null.
   *
   * @param props
   *          map to use for internal property storage
   */
  public MessageDTO(Map<String, Object> props) {
    super(props);
  }

  public String getAppUrl() {
    String appUrl = null;
    final Object value = this.fProperties.get(MessageDTO.APP_URL_FIELD);

    if (value != null) {
      appUrl = (String) value;
    }

    return appUrl;
  }

  public void setAppUrl(String url) {
    this.fProperties.put(MessageDTO.APP_URL_FIELD, url);
  }

  public String getBody() {
    String body = null;
    final Object value = this.fProperties.get(MessageDTO.BODY_FIELD);

    if (value != null) {
      body = (String) value;
    }

    return body;
  }

  public void setBody(String newBody) {
    this.fProperties.put(MessageDTO.BODY_FIELD, newBody);
  }

  public String getBodyId() {
    String bodyId = null;
    final Object value = this.fProperties.get(MessageDTO.BODY_ID_FIELD);

    if (value != null) {
      bodyId = (String) value;
    }

    return bodyId;
  }

  public void setBodyId(String bodyId) {
    this.fProperties.put(MessageDTO.BODY_ID_FIELD, bodyId);
  }

  public List<String> getCollectionIds() {
    return listFromProperty(MessageDTO.COLL_IDS_FIELD, String.class);
  }

  public void setCollectionIds(List<String> collectionIds) {
    if (collectionIds != null) {
      final String[] collArr = collectionIds.toArray(new String[collectionIds.size()]);
      this.fProperties.put(MessageDTO.COLL_IDS_FIELD, collArr);
    } else {
      this.fProperties.put(MessageDTO.COLL_IDS_FIELD, null);
    }
  }

  public String getId() {
    String id = null;
    final Object value = this.fProperties.get(MessageDTO.ID_FIELD);

    if (value != null) {
      id = (String) value;
    }

    return id;
  }

  public void setId(String id) {
    this.fProperties.put(MessageDTO.ID_FIELD, id);
  }

  public String getInReplyTo() {
    String parentId = null;
    final Object value = this.fProperties.get(MessageDTO.REPLY_FIELD);

    if (value != null) {
      parentId = (String) value;
    }

    return parentId;
  }

  public void setInReplyTo(String parentId) {
    this.fProperties.put(MessageDTO.REPLY_FIELD, parentId);
  }

  public List<String> getRecipients() {
    final List<String> recipients = listFromProperty(MessageDTO.RECIPIENTS_FIELD, String.class);

    return recipients;
  }

  public List<String> getReplies() {
    return listFromProperty(MessageDTO.REPLIES_FIELD, String.class);
  }

  public Status getStatus() {
    Status status = null;
    final Object value = this.fProperties.get(MessageDTO.STATUS_FIELD);

    if (value != null) {
      final String statusString = (String) value;
      status = Message.Status.valueOf(statusString);
    }

    return status;
  }

  public void setStatus(Status status) {
    if (status != null) {
      this.fProperties.put(MessageDTO.STATUS_FIELD, status.name());
    } else {
      this.fProperties.put(MessageDTO.STATUS_FIELD, null);
    }
  }

  public void setRecipients(List<String> recipients) {
    if (recipients != null) {
      final String[] recArr = recipients.toArray(new String[recipients.size()]);
      this.fProperties.put(MessageDTO.RECIPIENTS_FIELD, recArr);
    } else {
      this.fProperties.put(MessageDTO.RECIPIENTS_FIELD, null);
    }
  }

  public String getSenderId() {
    String senderId = null;
    final Object value = this.fProperties.get(MessageDTO.SENDER_ID_FIELD);

    if (value != null) {
      senderId = (String) value;
    }

    return senderId;
  }

  public void setSenderId(String senderId) {
    this.fProperties.put(MessageDTO.SENDER_ID_FIELD, senderId);
  }

  public Date getTimeSent() {
    Date timeSent = null;
    final Object value = this.fProperties.get(MessageDTO.TIME_FIELD);

    if (value != null) {
      timeSent = new Date((Long) value);
    }

    return timeSent;
  }

  public void setTimeSent(Date timeSent) {
    if (timeSent != null) {
      this.fProperties.put(MessageDTO.TIME_FIELD, timeSent.getTime());
    } else {
      this.fProperties.put(MessageDTO.TIME_FIELD, null);
    }
  }

  public String getTitle() {
    String title = null;
    final Object value = this.fProperties.get(MessageDTO.TITLE_FIELD);

    if (value != null) {
      title = (String) value;
    }

    return title;
  }

  public void setTitle(String newTitle) {
    this.fProperties.put(MessageDTO.TITLE_FIELD, newTitle);
  }

  public String getTitleId() {
    String title = null;
    final Object value = this.fProperties.get(MessageDTO.TITLE_ID_FIELD);

    if (value != null) {
      title = (String) value;
    }

    return title;
  }

  public void setTitleId(String titleId) {
    this.fProperties.put(MessageDTO.TITLE_ID_FIELD, titleId);
  }

  public Type getType() {
    Type type = null;
    final Object value = this.fProperties.get(MessageDTO.TYPE_FIELD);

    if (value != null) {
      type = Message.Type.valueOf((String) value);
    }

    return type;
  }

  public void setType(Type newType) {
    if (newType != null) {
      this.fProperties.put(MessageDTO.TYPE_FIELD, newType.name());
    } else {
      this.fProperties.put(MessageDTO.TYPE_FIELD, null);
    }
  }

  public Date getUpdated() {
    Date updated = null;
    final Object value = this.fProperties.get(MessageDTO.UPDATED_FIELD);

    if (value != null) {
      updated = new Date((Long) value);
    }

    return updated;
  }

  public void setUpdated(Date updated) {
    if (updated != null) {
      this.fProperties.put(MessageDTO.UPDATED_FIELD, updated.getTime());
    } else {
      this.fProperties.put(MessageDTO.UPDATED_FIELD, null);
    }
  }

  @SuppressWarnings("unchecked")
  public List<Url> getUrls() {
    List<Url> urls = null;

    // primary values
    final List<String> urlStrings = (List<String>) this.fProperties.get(MessageDTO.URLS_FIELD);

    // optional types
    final List<String> urlTypes = (List<String>) this.fProperties.get(MessageDTO.URL_TYPES_FIELD);

    // optional display Texts
    final List<String> urlTexts = (List<String>) this.fProperties.get(MessageDTO.URL_TEXTS_FIELD);

    if (urlStrings != null) {
      urls = new ArrayList<Url>();
      Url tempUrl = null;

      int index = 0;
      for (final String string : urlStrings) {
        tempUrl = new UrlImpl();
        tempUrl.setValue(string);

        if (urlTypes != null && urlTypes.size() > index) {
          tempUrl.setType(urlTypes.get(index));
        }
        if (urlTexts != null && urlTexts.size() > index) {
          tempUrl.setLinkText(urlTexts.get(index));
        }

        ++index;
        urls.add(tempUrl);
      }
    }

    return urls;
  }

  public void setUrls(List<Url> urls) {
    if (urls != null) {
      int index = 0;
      final String[] urlArr = new String[urls.size()];
      final String[] urlTypes = new String[urls.size()];
      final String[] urlTexts = new String[urls.size()];

      for (final Url url : urls) {
        urlArr[index] = url.getValue();
        urlTypes[index] = url.getType();
        urlTexts[index] = url.getLinkText();
        index++;
      }

      this.fProperties.put(MessageDTO.URLS_FIELD, urlArr);
      this.fProperties.put(MessageDTO.URL_TYPES_FIELD, urlTypes);
      this.fProperties.put(MessageDTO.URL_TEXTS_FIELD, urlTexts);
    } else {
      this.fProperties.put(MessageDTO.URLS_FIELD, null);
      this.fProperties.put(MessageDTO.URL_TYPES_FIELD, null);
      this.fProperties.put(MessageDTO.URL_TEXTS_FIELD, null);
    }
  }

  public String sanitizeHTML(String htmlStr) {
    // TODO ? - definition is unclear
    return null;
  }

  /**
   * Sets the properties of this data transfer object to those of the given object. If the given
   * Object is null, all data is cleared.
   *
   * @param message
   *          message object containing the data to set
   */
  public void setData(final Message message) {
    if (message == null) {
      this.fProperties.clear();
      return;
    }

    this.fProperties.put(MessageDTO.APP_URL_FIELD, message.getAppUrl());
    this.fProperties.put(MessageDTO.BODY_FIELD, message.getBody());
    this.fProperties.put(MessageDTO.BODY_ID_FIELD, message.getBodyId());

    this.setCollectionIds(message.getCollectionIds());

    this.fProperties.put(MessageDTO.ID_FIELD, message.getId());

    this.setRecipients(message.getRecipients());

    this.fProperties.put(MessageDTO.REPLY_FIELD, message.getInReplyTo());
    this.fProperties.put(MessageDTO.SENDER_ID_FIELD, message.getSenderId());

    this.setStatus(message.getStatus());
    this.setTimeSent(message.getTimeSent());

    this.fProperties.put(MessageDTO.TITLE_FIELD, message.getTitle());
    this.fProperties.put(MessageDTO.TITLE_ID_FIELD, message.getTitleId());

    this.setType(message.getType());
    this.setUpdated(message.getUpdated());
    this.setUrls(message.getUrls());
  }
}
