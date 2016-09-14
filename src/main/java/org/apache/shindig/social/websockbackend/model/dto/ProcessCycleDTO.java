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

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.shindig.social.websockbackend.model.IProcessCycle;

public class ProcessCycleDTO extends ADataTransferObject implements IProcessCycle {

  private static final String TYPE_FIELD = "type";
  private static final String ID_FIELD = "docId";
  private static final String START_FIELD = "startDate";
  private static final String END_FIELD = "endDate";
  private static final String USERLIST_FIELD = "userList";

  /**
   * Creates an empty process cycle transfer object.
   */
  public ProcessCycleDTO() {
    super();
  }

  /**
   * Creates a skill set transfer object using the given map for internal property storage. The
   * given map must not be null.
   *
   * @param props
   *          map to use for internal property storage
   */
  public ProcessCycleDTO(Map<String, Object> props) {
    super(props);
  }

  @Override
  public String getType() {
    String type = null;

    final Object value = this.fProperties.get(ProcessCycleDTO.TYPE_FIELD);
    if (value != null) {
      type = (String) value;
    }

    return type;
  }

  @Override
  public void setType(String docType) {
    this.fProperties.put(ProcessCycleDTO.TYPE_FIELD, docType);
  }

  @Override
  public String getDocId() {
    String id = null;

    final Object value = this.fProperties.get(ProcessCycleDTO.ID_FIELD);
    if (value != null) {
      id = (String) value;
    }

    return id;
  }

  @Override
  public void setDocId(String docId) {
    this.fProperties.put(ProcessCycleDTO.ID_FIELD, docId);
  }

  @Override
  public Date getStartDate() {
    Date startDate = null;
    final Object value = this.fProperties.get(ProcessCycleDTO.START_FIELD);

    if (value != null) {
      startDate = new Date((Long) value);
    }

    return startDate;
  }

  @Override
  public void setStartDate(Date start) {
    if (start != null) {
      this.fProperties.put(ProcessCycleDTO.START_FIELD, start.getTime());
    } else {
      this.fProperties.put(ProcessCycleDTO.START_FIELD, null);
    }
  }

  @Override
  public Date getEndDate() {
    Date endDate = null;
    final Object value = this.fProperties.get(ProcessCycleDTO.END_FIELD);

    if (value != null) {
      endDate = new Date((Long) value);
    }

    return endDate;
  }

  @Override
  public void setEndDate(Date end) {
    if (end != null) {
      this.fProperties.put(ProcessCycleDTO.END_FIELD, end.getTime());
    } else {
      this.fProperties.put(ProcessCycleDTO.END_FIELD, null);
    }
  }

  @Override
  public List<String> getUserList() {
//public List<? extends Person> getUserList() {
	/*
    List<Person> userList = null;

    @SuppressWarnings("unchecked")
    final List<Map<String, Object>> personMaps = (List<Map<String, Object>>) this.fProperties
            .get(ProcessCycleDTO.USERLIST_FIELD);

    if (personMaps != null) {
      userList = new ArrayList<Person>();

      for (final Map<String, Object> pMap : personMaps) {
        userList.add(new PersonDTO(pMap));
      }
    }
    */
    
    @SuppressWarnings("unchecked")
	final List<String> userList = (List<String>)this.fProperties;  

    return userList;
  }

  @Override
  public void setUserList(List<String> userList) {
    if (userList != null && !userList.isEmpty()) {
    	/*
      final List<Map<String, Object>> personList = new ArrayList<Map<String, Object>>();

      Map<String, Object> pMap = null;
      for (final Person acc : userList) {
        pMap = new HashMap<String, Object>();
        new PersonDTO(pMap).setData(acc);
        personList.add(pMap);
      }

      this.fProperties.put(ProcessCycleDTO.USERLIST_FIELD, personList);
      */
    	this.fProperties.put(ProcessCycleDTO.USERLIST_FIELD, userList);
    } else {
      this.fProperties.put(ProcessCycleDTO.USERLIST_FIELD, null);
    }
  }

  public void setData(final IProcessCycle cycle) {
    if (cycle == null) {
      this.fProperties.clear();
      return;
    }

    this.setType(cycle.getType());
    this.setDocId(cycle.getDocId());
    this.setStartDate(cycle.getStartDate());
    this.setEndDate(cycle.getEndDate());
    this.setUserList(cycle.getUserList());
  }

  public boolean containsKey(Object key) {
    return this.fProperties.containsKey(key);
  }

  public boolean containsValue(Object value) {
    return this.fProperties.containsValue(value);
  }
}
