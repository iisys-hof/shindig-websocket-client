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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shindig.protocol.model.Enum;
import org.apache.shindig.protocol.model.EnumImpl;
import org.apache.shindig.social.core.model.BodyTypeImpl;
import org.apache.shindig.social.core.model.NameImpl;
import org.apache.shindig.social.core.model.UrlImpl;
import org.apache.shindig.social.opensocial.model.Account;
import org.apache.shindig.social.opensocial.model.Address;
import org.apache.shindig.social.opensocial.model.BodyType;
import org.apache.shindig.social.opensocial.model.Drinker;
import org.apache.shindig.social.opensocial.model.ListField;
import org.apache.shindig.social.opensocial.model.LookingFor;
import org.apache.shindig.social.opensocial.model.Name;
import org.apache.shindig.social.opensocial.model.NetworkPresence;
import org.apache.shindig.social.opensocial.model.Organization;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.model.Smoker;
import org.apache.shindig.social.opensocial.model.Url;
import org.apache.shindig.social.websockbackend.model.ws.GraphListFieldList;

/**
 * Data transfer object containing person information. The booleans hasApp, isOwner and isViewer
 * have to be set using the appropriate setters and not via properties.
 */
public class PersonDTO extends ADataTransferObject implements Person {
  private static final String ACCOUNTS_FIELD = Person.Field.ACCOUNTS.toString();
  private static final String ADDRESSES_FIELD = Person.Field.ADDRESSES.toString();
  private static final String APP_DATA_FIELD = Person.Field.APP_DATA.toString();
  private static final String CURR_LOC_FIELD = Person.Field.CURRENT_LOCATION.toString();
  private static final String ORGS_FIELD = Person.Field.ORGANIZATIONS.toString();

  private static final String EMAILS_FIELD = Person.Field.EMAILS.toString();
  private static final String IMS_FIELD = Person.Field.IMS.toString();
  private static final String PHONES_FIELD = Person.Field.PHONE_NUMBERS.toString();
  private static final String PHOTOS_FIELD = Person.Field.PHOTOS.toString();

  private static final String ABOUT_ME_FIELD = Person.Field.ABOUT_ME.toString();
  private static final String AGE_FIELD = Person.Field.AGE.toString();
  private static final String CHILDREN_FIELD = Person.Field.CHILDREN.toString();
  private static final String DISP_NAME_FIELD = Person.Field.DISPLAY_NAME.toString();
  private static final String ID_FIELD = Person.Field.ID.toString();
  private static final String JOB_INTS_FIELD = Person.Field.JOB_INTERESTS.toString();
  private static final String NICKNAME_FIELD = Person.Field.NICKNAME.toString();
  private static final String PREF_USRNAME_FIELD = Person.Field.PREFERRED_USERNAME.toString();
  private static final String PROFILE_URL_FIELD = Person.Field.PROFILE_URL.toString();
  private static final String REL_STAT_FIELD = Person.Field.RELATIONSHIP_STATUS.toString();
  private static final String STATUS_FIELD = Person.Field.STATUS.toString();
  private static final String THUMBNAIL_FIELD = Person.Field.THUMBNAIL_URL.toString();
  private static final String OFFSET_FIELD = Person.Field.UTC_OFFSET.toString();

  private static final String BDAY_FIELD = Person.Field.BIRTHDAY.toString();
  private static final String GENDER_FIELD = Person.Field.GENDER.toString();
  private static final String UPDATED_FIELD = Person.Field.LAST_UPDATED.toString();
  private static final String NET_PRES_FIELD = Person.Field.NETWORKPRESENCE.toString();
  private static final String PROF_VID_FIELD = Person.Field.PROFILE_VIDEO.toString();
  private static final String ADD_NAME_FIELD = Name.Field.ADDITIONAL_NAME.toString();
  private static final String FAM_NAME_FIELD = Name.Field.FAMILY_NAME.toString();
  private static final String FORMATTED_FIELD = Name.Field.FORMATTED.toString();
  private static final String GIV_NAME_FIELD = Name.Field.GIVEN_NAME.toString();
  private static final String HON_PREF_FIELD = Name.Field.HONORIFIC_PREFIX.toString();
  private static final String HON_SUFF_FIELD = Name.Field.HONORIFIC_SUFFIX.toString();

  private static final String ACTIVITIES_FIELD = Person.Field.ACTIVITIES.toString();
  private static final String BOOKS_FIELD = Person.Field.BOOKS.toString();
  private static final String INTERESTS_FIELD = Person.Field.INTERESTS.toString();
  private static final String LANGUAGES_FIELD = Person.Field.LANGUAGES_SPOKEN.toString();
  private static final String LOOKING_FIELD = Person.Field.LOOKING_FOR.toString();
  private static final String QUOTES_FIELD = Person.Field.QUOTES.toString();
  private static final String TAGS_FIELD = Person.Field.TAGS.toString();
  private static final String URLS_FIELD = Person.Field.URLS.toString();

  private static final String BUILD_FIELD = BodyType.Field.BUILD.toString();
  private static final String EYE_COLOR_FIELD = BodyType.Field.EYE_COLOR.toString();
  private static final String HAIR_COLOR_FIELD = BodyType.Field.HAIR_COLOR.toString();
  private static final String HEIGHT_FIELD = BodyType.Field.HEIGHT.toString();
  private static final String WEIGHT_FIELD = BodyType.Field.WEIGHT.toString();

  private static final String CARS_FIELD = Person.Field.CARS.toString();
  private static final String DRINKER_FIELD = Person.Field.DRINKER.toString();
  private static final String ETHNICITY_FIELD = Person.Field.ETHNICITY.toString();
  private static final String FASHION_FIELD = Person.Field.FASHION.toString();
  private static final String FOOD_FIELD = Person.Field.FOOD.toString();
  private static final String HAPPIEST_FIELD = Person.Field.HAPPIEST_WHEN.toString();
  private static final String HEROES_FIELD = Person.Field.HEROES.toString();
  private static final String HUMOR_FIELD = Person.Field.HUMOR.toString();
  private static final String LIVING_FIELD = Person.Field.LIVING_ARRANGEMENT.toString();
  private static final String MOVIES_FIELD = Person.Field.MOVIES.toString();
  private static final String MUSIC_FIELD = Person.Field.MUSIC.toString();
  private static final String PETS_FIELD = Person.Field.PETS.toString();
  private static final String POLITICS_FIELD = Person.Field.POLITICAL_VIEWS.toString();
  private static final String PROF_SONG = Person.Field.PROFILE_SONG.toString();
  private static final String RELIGION_FIELD = Person.Field.RELIGION.toString();
  private static final String ROMANCE_FIELD = Person.Field.ROMANCE.toString();
  private static final String SCARED_FIELD = Person.Field.SCARED_OF.toString();
  private static final String S_ORI_FIELD = Person.Field.SEXUAL_ORIENTATION.toString();
  private static final String SMOKER_FIELD = Person.Field.SMOKER.toString();
  private static final String SPORTS_FIELD = Person.Field.SPORTS.toString();
  private static final String TURN_OFFS_FIELD = Person.Field.TURN_OFFS.toString();
  private static final String TURN_ONS_FIELD = Person.Field.TURN_ONS.toString();
  private static final String TV_SHOWS_FIELD = Person.Field.TV_SHOWS.toString();

  private Boolean fHasApp;
  private boolean fIsOwner, fIsViewer;

  /**
   * Creates an empty person data transfer object.
   */
  public PersonDTO() {
    super();
  }

  /**
   * Creates a person data transfer object using the given map for internal property storage. The
   * given map must not be null.
   *
   * @param props
   *          map to use for internal property storage
   */
  public PersonDTO(Map<String, Object> props) {
    super(props);
  }

  public String getDisplayName() {
    String dName = null;
    final Object value = this.fProperties.get(PersonDTO.DISP_NAME_FIELD);

    if (value != null) {
      dName = (String) value;
    }

    return dName;
  }

  public void setDisplayName(String displayName) {
    this.fProperties.put(PersonDTO.DISP_NAME_FIELD, displayName);
  }

  public String getAboutMe() {
    String aboutMe = null;
    final Object value = this.fProperties.get(PersonDTO.ABOUT_ME_FIELD);

    if (value != null) {
      aboutMe = (String) value;
    }

    return aboutMe;
  }

  public void setAboutMe(String aboutMe) {
    this.fProperties.put(PersonDTO.ABOUT_ME_FIELD, aboutMe);
  }

  public List<Account> getAccounts() {
    // TODO: specify list implementation
    List<Account> accounts = null;
    @SuppressWarnings("unchecked")
    final List<Map<String, Object>> accMaps = (List<Map<String, Object>>) this.fProperties
            .get(PersonDTO.ACCOUNTS_FIELD);

    if (accMaps != null) {
      accounts = new ArrayList<Account>();

      for (final Map<String, Object> accMap : accMaps) {
        accounts.add(new AccountDTO(accMap));
      }
    }

    return accounts;
  }

  public void setAccounts(List<Account> accounts) {
    // TODO: specify list implementation
    if (accounts != null && !accounts.isEmpty()) {
      final List<Map<String, Object>> accList = new ArrayList<Map<String, Object>>();

      Map<String, Object> accMap = null;
      for (final Account acc : accounts) {
        accMap = new HashMap<String, Object>();
        new AccountDTO(accMap).setData(acc);
        accList.add(accMap);
      }

      this.fProperties.put(PersonDTO.ACCOUNTS_FIELD, accList);
    } else {
      this.fProperties.put(PersonDTO.ACCOUNTS_FIELD, null);
    }
  }

  public List<String> getActivities() {
    final List<String> activities = listFromProperty(PersonDTO.ACTIVITIES_FIELD, String.class);

    return activities;
  }

  public void setActivities(List<String> activities) {
    if (activities != null) {
      final String[] actArr = activities.toArray(new String[activities.size()]);
      this.fProperties.put(PersonDTO.ACTIVITIES_FIELD, actArr);
    } else {
      this.fProperties.remove(PersonDTO.ACTIVITIES_FIELD);
    }
  }

  public List<Address> getAddresses() {
    // TODO: specify list class
    List<Address> addresses = null;

    @SuppressWarnings("unchecked")
    final List<Map<String, Object>> addMaps = (List<Map<String, Object>>) this.fProperties
            .get(PersonDTO.ADDRESSES_FIELD);

    if (addMaps != null) {
      addresses = new ArrayList<Address>();

      for (final Map<String, Object> addMap : addMaps) {
        addresses.add(new AddressDTO(addMap));
      }
    }

    return addresses;
  }

  public void setAddresses(List<Address> addresses) {
    // TODO: specify list and map class
    if (addresses != null) {
      final List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();

      AddressDTO dto = null;
      Map<String, Object> addMap = null;
      for (final Address add : addresses) {
        addMap = new HashMap<String, Object>();

        dto = new AddressDTO(addMap);
        dto.setData(add);
        dto.stripNullValues();

        mapList.add(addMap);
      }

      this.fProperties.put(PersonDTO.ADDRESSES_FIELD, mapList);
    } else {
      this.fProperties.put(PersonDTO.ADDRESSES_FIELD, null);
    }
  }

  public Integer getAge() {
    return (Integer) this.fProperties.get(PersonDTO.AGE_FIELD);
  }

  public void setAge(Integer age) {
    this.fProperties.put(PersonDTO.AGE_FIELD, age);
  }

  @SuppressWarnings("unchecked")
  public Map<String, ?> getAppData() {
    return (Map<String, ?>) this.fProperties.get(PersonDTO.APP_DATA_FIELD);
  }

  public void setAppData(Map<String, ?> appData) {
    // TODO: specify map implementation
    this.fProperties.put(PersonDTO.APP_DATA_FIELD, appData);
  }

  public Date getBirthday() {
    Date bDay = null;
    final Object value = this.fProperties.get(PersonDTO.BDAY_FIELD);

    if (value != null) {
      long time = 0;

      // circumvent REST wrapper's faulty type casting
      if (value instanceof Long) {
        time = (Long) value;
      } else {
        time = (Integer) value;
      }

      bDay = new Date(time);
    }
    return bDay;
  }

  public void setBirthday(Date birthday) {
    if (birthday != null) {
      this.fProperties.put(PersonDTO.BDAY_FIELD, birthday.getTime());
    } else {
      this.fProperties.remove(PersonDTO.BDAY_FIELD);
    }
  }

  public BodyType getBodyType() {
    boolean noValues = true;
    final BodyType bType = new BodyTypeImpl();
    final Object buildValue = this.fProperties.get(PersonDTO.BUILD_FIELD);
    final Object eyeColorValue = this.fProperties.get(PersonDTO.EYE_COLOR_FIELD);
    final Object hairColorValue = this.fProperties.get(PersonDTO.HAIR_COLOR_FIELD);
    final Object heightValue = this.fProperties.get(PersonDTO.HEIGHT_FIELD);
    final Object weightValue = this.fProperties.get(PersonDTO.WEIGHT_FIELD);

    if (buildValue != null) {
      bType.setBuild((String) buildValue);
      noValues = false;
    }
    if (eyeColorValue != null) {
      bType.setEyeColor((String) eyeColorValue);
      noValues = false;
    }
    if (hairColorValue != null) {
      bType.setHairColor((String) hairColorValue);
      noValues = false;
    }
    if (heightValue != null) {
      bType.setHeight((Float) heightValue);
      noValues = false;
    }
    if (weightValue != null) {
      bType.setWeight((Float) weightValue);
      noValues = false;
    }

    // discard new object if empty
    if (!noValues) {
      return bType;
    }
    return null;
  }

  public void setBodyType(final BodyType bodyType) {
    if (bodyType != null) {
      final String build = bodyType.getBuild();
      final String eyeColor = bodyType.getEyeColor();
      final String hairColor = bodyType.getHairColor();
      final Float height = bodyType.getHeight();
      final Float weight = bodyType.getWeight();

      if (build != null) {
        this.fProperties.put(PersonDTO.BUILD_FIELD, build);
      } else {
        this.fProperties.remove(PersonDTO.BUILD_FIELD);
      }

      if (eyeColor != null) {
        this.fProperties.put(PersonDTO.EYE_COLOR_FIELD, eyeColor);
      } else {
        this.fProperties.remove(PersonDTO.EYE_COLOR_FIELD);
      }

      if (hairColor != null) {
        this.fProperties.put(PersonDTO.HAIR_COLOR_FIELD, hairColor);
      } else {
        this.fProperties.remove(PersonDTO.HAIR_COLOR_FIELD);
      }

      if (height != null) {
        this.fProperties.put(PersonDTO.HEIGHT_FIELD, height);
      } else {
        this.fProperties.remove(PersonDTO.HEIGHT_FIELD);
      }

      if (weight != null) {
        this.fProperties.put(PersonDTO.WEIGHT_FIELD, weight);
      } else {
        this.fProperties.remove(PersonDTO.WEIGHT_FIELD);
      }
    } else {
      this.fProperties.remove(PersonDTO.BUILD_FIELD);
      this.fProperties.remove(PersonDTO.EYE_COLOR_FIELD);
      this.fProperties.remove(PersonDTO.HAIR_COLOR_FIELD);
      this.fProperties.remove(PersonDTO.HEIGHT_FIELD);
      this.fProperties.remove(PersonDTO.WEIGHT_FIELD);
    }
  }

  public List<String> getBooks() {
    final List<String> books = listFromProperty(PersonDTO.BOOKS_FIELD, String.class);

    return books;
  }

  public void setBooks(List<String> books) {
    if (books != null) {
      final String[] bookArr = books.toArray(new String[books.size()]);
      this.fProperties.put(PersonDTO.BOOKS_FIELD, bookArr);
    } else {
      this.fProperties.remove(PersonDTO.BOOKS_FIELD);
    }
  }

  public List<String> getCars() {
    final List<String> cars = listFromProperty(PersonDTO.CARS_FIELD, String.class);

    return cars;
  }

  public void setCars(List<String> cars) {
    // TODO: specify list implementation
    if (cars != null) {
      final String[] carArr = cars.toArray(new String[cars.size()]);
      this.fProperties.put(PersonDTO.CARS_FIELD, carArr);
    } else {
      this.fProperties.remove(PersonDTO.CARS_FIELD);
    }
  }

  public String getChildren() {
    String children = null;
    final Object value = this.fProperties.get(PersonDTO.CHILDREN_FIELD);

    if (value != null) {
      children = (String) value;
    }

    return children;
  }

  public void setChildren(String children) {
    if (children != null) {
      this.fProperties.put(PersonDTO.CHILDREN_FIELD, children);
    } else {
      this.fProperties.remove(PersonDTO.CHILDREN_FIELD);
    }
  }

  public Address getCurrentLocation() {
    Address location = null;

    @SuppressWarnings("unchecked")
    final Map<String, Object> locMap = (Map<String, Object>) this.fProperties
            .get(PersonDTO.CURR_LOC_FIELD);

    if (locMap != null) {
      location = new AddressDTO(locMap);
    }

    return location;
  }

  public void setCurrentLocation(Address currentLocation) {
    // TODO: specify map implementation
    if (currentLocation != null) {
      final Map<String, Object> locMap = new HashMap<String, Object>();
      final AddressDTO dto = new AddressDTO(locMap);
      dto.setData(currentLocation);
      dto.stripNullValues();
      this.fProperties.put(PersonDTO.CURR_LOC_FIELD, locMap);
    } else {
      this.fProperties.remove(PersonDTO.CURR_LOC_FIELD);
    }
  }

  public Enum<Drinker> getDrinker() {
    Enum<Drinker> drinker = null;
    final Object value = this.fProperties.get(PersonDTO.DRINKER_FIELD);

    if (value != null) {
      drinker = new EnumImpl<Drinker>(Drinker.valueOf((String) value));
    }

    return drinker;
  }

  public void setDrinker(Enum<Drinker> newDrinker) {
    if (newDrinker != null) {
      this.fProperties.put(PersonDTO.DRINKER_FIELD, newDrinker.getValue().name());
    } else {
      this.fProperties.remove(PersonDTO.DRINKER_FIELD);
    }
  }

  public List<ListField> getEmails() {
    List<ListField> emails = null;

    @SuppressWarnings("unchecked")
    final Map<String, Object> lfMap = (Map<String, Object>) this.fProperties
            .get(PersonDTO.EMAILS_FIELD);

    if (lfMap != null) {
      emails = new GraphListFieldList(lfMap).toDTO();
    }

    return emails;
  }

  public void setEmails(List<ListField> emails) {
    // TODO: specify map and list implementation
    if (emails != null) {
      final Map<String, Object> lfMap = new HashMap<String, Object>();
      new GraphListFieldList(lfMap).store(emails);
      this.fProperties.put(PersonDTO.EMAILS_FIELD, lfMap);
    } else {
      this.fProperties.remove(PersonDTO.EMAILS_FIELD);
    }
  }

  public String getEthnicity() {
    String ethnicity = null;
    final Object value = this.fProperties.get(PersonDTO.ETHNICITY_FIELD);

    if (value != null) {
      ethnicity = (String) value;
    }

    return ethnicity;
  }

  public void setEthnicity(String ethnicity) {
    if (ethnicity != null) {
      this.fProperties.put(PersonDTO.ETHNICITY_FIELD, ethnicity);
    } else {
      this.fProperties.remove(PersonDTO.ETHNICITY_FIELD);
    }
  }

  public String getFashion() {
    String fashion = null;
    final Object value = this.fProperties.get(PersonDTO.FASHION_FIELD);

    if (value != null) {
      fashion = (String) value;
    }

    return fashion;
  }

  public void setFashion(String fashion) {
    if (fashion != null) {
      this.fProperties.put(PersonDTO.FASHION_FIELD, fashion);
    } else {
      this.fProperties.remove(PersonDTO.FASHION_FIELD);
    }
  }

  public List<String> getFood() {
    final List<String> foods = listFromProperty(PersonDTO.FOOD_FIELD, String.class);

    return foods;
  }

  public void setFood(List<String> food) {
    // TODO: specify list implementation
    if (food != null) {
      final String[] foodArr = food.toArray(new String[food.size()]);
      this.fProperties.put(PersonDTO.FOOD_FIELD, foodArr);
    } else {
      this.fProperties.remove(PersonDTO.FOOD_FIELD);
    }
  }

  public Gender getGender() {
    Gender gender = null;
    final Object value = this.fProperties.get(PersonDTO.GENDER_FIELD);

    if (value != null) {
      gender = Gender.valueOf((String) value);
    }

    return gender;
  }

  public void setGender(Gender newGender) {
    if (newGender != null) {
      this.fProperties.put(PersonDTO.GENDER_FIELD, newGender.name());
    } else {
      this.fProperties.remove(PersonDTO.GENDER_FIELD);
    }
  }

  public String getHappiestWhen() {
    String happiest = null;
    final Object value = this.fProperties.get(PersonDTO.HAPPIEST_FIELD);

    if (value != null) {
      happiest = (String) value;
    }

    return happiest;
  }

  public void setHappiestWhen(String happiestWhen) {
    if (happiestWhen != null) {
      this.fProperties.put(PersonDTO.HAPPIEST_FIELD, happiestWhen);
    } else {
      this.fProperties.remove(PersonDTO.HAPPIEST_FIELD);
    }
  }

  public Boolean getHasApp() {
    return this.fHasApp;
  }

  public void setHasApp(Boolean hasApp) {
    this.fHasApp = hasApp;
  }

  public List<String> getHeroes() {
    final List<String> heroes = listFromProperty(PersonDTO.HEROES_FIELD, String.class);

    return heroes;
  }

  public void setHeroes(List<String> heroes) {
    // TODO: specify list implementation
    if (heroes != null) {
      final String[] herArr = heroes.toArray(new String[heroes.size()]);
      this.fProperties.put(PersonDTO.HEROES_FIELD, herArr);
    } else {
      this.fProperties.remove(PersonDTO.HEROES_FIELD);
    }
  }

  public String getHumor() {
    String humor = null;
    final Object value = this.fProperties.get(PersonDTO.HUMOR_FIELD);

    if (value != null) {
      humor = (String) value;
    }

    return humor;
  }

  public void setHumor(String humor) {
    if (humor != null) {
      this.fProperties.put(PersonDTO.HUMOR_FIELD, humor);
    } else {
      this.fProperties.remove(PersonDTO.HUMOR_FIELD);
    }
  }

  public String getId() {
    String id = null;
    final Object value = this.fProperties.get(PersonDTO.ID_FIELD);

    if (value != null) {
      id = (String) value;
    }

    return id;
  }

  public void setId(String id) {
    if (id != null) {
      this.fProperties.put(PersonDTO.ID_FIELD, id);
    } else {
      this.fProperties.remove(PersonDTO.ID_FIELD);
    }
  }

  public List<ListField> getIms() {
    List<ListField> ims = null;
    @SuppressWarnings("unchecked")
    final Map<String, Object> lfMap = (Map<String, Object>) this.fProperties
            .get(PersonDTO.IMS_FIELD);

    if (lfMap != null) {
      ims = new GraphListFieldList(lfMap).toDTO();
    }

    return ims;
  }

  public void setIms(List<ListField> ims) {
    // TODO: specify list and map implementation
    if (ims != null) {
      final Map<String, Object> lfMap = new HashMap<>();
      new GraphListFieldList(lfMap).store(ims);
      this.fProperties.put(PersonDTO.IMS_FIELD, lfMap);
    } else {
      this.fProperties.remove(PersonDTO.IMS_FIELD);
    }
  }

  public List<String> getInterests() {
    final List<String> interests = listFromProperty(PersonDTO.INTERESTS_FIELD, String.class);

    return interests;
  }

  public void setInterests(List<String> interests) {
    // TODO: specify list implementation
    if (interests != null) {
      final String[] intArr = interests.toArray(new String[interests.size()]);
      this.fProperties.put(PersonDTO.INTERESTS_FIELD, intArr);
    } else {
      this.fProperties.remove(PersonDTO.INTERESTS_FIELD);
    }
  }

  public String getJobInterests() {
    String jobInt = null;
    final Object value = this.fProperties.get(PersonDTO.JOB_INTS_FIELD);

    if (value != null) {
      jobInt = (String) value;
    }

    return jobInt;
  }

  public void setJobInterests(String jobInterests) {
    if (jobInterests != null) {
      this.fProperties.put(PersonDTO.JOB_INTS_FIELD, jobInterests);
    } else {
      this.fProperties.remove(PersonDTO.JOB_INTS_FIELD);
    }
  }

  public List<String> getLanguagesSpoken() {
    final List<String> languages = listFromProperty(PersonDTO.LANGUAGES_FIELD, String.class);

    return languages;
  }

  public void setLanguagesSpoken(List<String> languagesSpoken) {
    if (languagesSpoken != null) {
      final String[] langArr = languagesSpoken.toArray(new String[languagesSpoken.size()]);
      this.fProperties.put(PersonDTO.LANGUAGES_FIELD, langArr);
    } else {
      this.fProperties.remove(PersonDTO.LANGUAGES_FIELD);
    }
  }

  public Date getUpdated() {
    Date updated = null;
    final Object value = this.fProperties.get(PersonDTO.UPDATED_FIELD);

    if (value != null) {
      final long time = (Long) value;
      updated = new Date(time);
    }
    return updated;
  }

  public void setUpdated(Date updated) {
    if (updated != null) {
      this.fProperties.put(PersonDTO.UPDATED_FIELD, updated.getTime());
    } else {
      this.fProperties.remove(PersonDTO.UPDATED_FIELD);
    }
  }

  public String getLivingArrangement() {
    String livingArr = null;
    final Object value = this.fProperties.get(PersonDTO.LIVING_FIELD);

    if (value != null) {
      livingArr = (String) value;
    }

    return livingArr;
  }

  public void setLivingArrangement(String livingArrangement) {
    if (livingArrangement != null) {
      this.fProperties.put(PersonDTO.LIVING_FIELD, livingArrangement);
    } else {
      this.fProperties.remove(PersonDTO.LIVING_FIELD);
    }
  }

  public List<Enum<LookingFor>> getLookingFor() {
    final String[] lookStrings = (String[]) this.fProperties.get(PersonDTO.LOOKING_FIELD);

    if (lookStrings != null) {
      final List<Enum<LookingFor>> lookFors = new ArrayList<Enum<LookingFor>>();
      LookingFor tmpLook = null;
      Enum<LookingFor> tmpEnum = null;
      for (final String entry : lookStrings) {
        tmpLook = LookingFor.valueOf(entry);
        tmpEnum = new EnumImpl<LookingFor>(tmpLook);
        lookFors.add(tmpEnum);
      }

      return lookFors;
    }

    return null;
  }

  public void setLookingFor(List<Enum<LookingFor>> lookingFor) {
    if (lookingFor != null) {
      int index = 0;
      final String[] lfArr = new String[lookingFor.size()];

      for (final Enum<LookingFor> lf : lookingFor) {
        lfArr[index++] = lf.getValue().name();
      }

      this.fProperties.put(PersonDTO.LOOKING_FIELD, lfArr);
    } else {
      this.fProperties.remove(PersonDTO.LOOKING_FIELD);
    }
  }

  public List<String> getMovies() {
    final List<String> movies = listFromProperty(PersonDTO.MOVIES_FIELD, String.class);

    return movies;
  }

  public void setMovies(List<String> movies) {
    if (movies != null) {
      final String[] movArr = movies.toArray(new String[movies.size()]);
      this.fProperties.put(PersonDTO.MOVIES_FIELD, movArr);
    } else {
      this.fProperties.remove(PersonDTO.MOVIES_FIELD);
    }
  }

  public List<String> getMusic() {
    final List<String> music = listFromProperty(PersonDTO.MUSIC_FIELD, String.class);

    return music;
  }

  public void setMusic(List<String> music) {
    if (music != null) {
      final String[] musArr = music.toArray(new String[music.size()]);
      this.fProperties.put(PersonDTO.MUSIC_FIELD, musArr);
    } else {
      this.fProperties.remove(PersonDTO.MUSIC_FIELD);
    }
  }

  public Name getName() {
    boolean noValues = true;
    Name name = null;
    final Object addNameValue = this.fProperties.get(PersonDTO.ADD_NAME_FIELD);
    final Object famNameValue = this.fProperties.get(PersonDTO.FAM_NAME_FIELD);
    final Object formNameValue = this.fProperties.get(PersonDTO.FORMATTED_FIELD);
    final Object givNameValue = this.fProperties.get(PersonDTO.GIV_NAME_FIELD);
    final Object honPrefValue = this.fProperties.get(PersonDTO.HON_PREF_FIELD);
    final Object honSuffValue = this.fProperties.get(PersonDTO.HON_SUFF_FIELD);

    name = new NameImpl();
    if (addNameValue != null) {
      name.setAdditionalName((String) addNameValue);
      noValues = false;
    }
    if (famNameValue != null) {
      name.setFamilyName((String) famNameValue);
      noValues = false;
    }
    if (formNameValue != null) {
      name.setFormatted((String) formNameValue);
      noValues = false;
    }
    if (givNameValue != null) {
      name.setGivenName((String) givNameValue);
      noValues = false;
    }
    if (honPrefValue != null) {
      name.setHonorificPrefix((String) honPrefValue);
      noValues = false;
    }
    if (honSuffValue != null) {
      name.setHonorificSuffix((String) honSuffValue);
      noValues = false;
    }

    // discard object if there are no values
    if (noValues) {
      name = null;
    }

    return name;
  }

  public void setName(final Name name) {
    if (name != null) {
      this.fProperties.put(PersonDTO.ADD_NAME_FIELD, name.getAdditionalName());
      this.fProperties.put(PersonDTO.FAM_NAME_FIELD, name.getFamilyName());
      this.fProperties.put(PersonDTO.FORMATTED_FIELD, name.getFormatted());
      this.fProperties.put(PersonDTO.GIV_NAME_FIELD, name.getGivenName());
      this.fProperties.put(PersonDTO.HON_PREF_FIELD, name.getHonorificPrefix());
      this.fProperties.put(PersonDTO.HON_SUFF_FIELD, name.getHonorificSuffix());
    }
  }

  public Enum<NetworkPresence> getNetworkPresence() {
    Enum<NetworkPresence> netPres = null;
    final Object value = this.fProperties.get(PersonDTO.NET_PRES_FIELD);

    if (value != null) {
      final NetworkPresence pres = NetworkPresence.valueOf((String) value);
      netPres = new EnumImpl<NetworkPresence>(pres);
    }

    return netPres;
  }

  public void setNetworkPresence(Enum<NetworkPresence> networkPresence) {
    if (networkPresence != null) {
      this.fProperties.put(PersonDTO.NET_PRES_FIELD, networkPresence.getValue().name());
    } else {
      this.fProperties.remove(PersonDTO.NET_PRES_FIELD);
    }
  }

  public String getNickname() {
    String nick = null;
    final Object value = this.fProperties.get(PersonDTO.NICKNAME_FIELD);

    if (value != null) {
      nick = (String) value;
    }

    return nick;
  }

  public void setNickname(String nickname) {
    if (nickname != null) {
      this.fProperties.put(PersonDTO.NICKNAME_FIELD, nickname);
    } else {
      this.fProperties.remove(PersonDTO.NICKNAME_FIELD);
    }
  }

  public List<Organization> getOrganizations() {
    // TODO: specify list implementation
    List<Organization> organizations = null;
    @SuppressWarnings("unchecked")
    final List<Map<String, Object>> mapList = (List<Map<String, Object>>) this.fProperties
            .get(PersonDTO.ORGS_FIELD);

    if (mapList != null) {
      organizations = new ArrayList<Organization>();

      for (final Map<String, Object> orgMap : mapList) {
        organizations.add(new OrganizationDTO(orgMap));
      }
    }

    return organizations;
  }

  public void setOrganizations(List<Organization> organizations) {
    // TODO: specify map and list implementation
    if (organizations != null) {
      final List<Map<String, Object>> orgMaps = new ArrayList<Map<String, Object>>();

      OrganizationDTO dto = null;
      Map<String, Object> orgMap = null;
      for (final Organization o : organizations) {
        orgMap = new HashMap<String, Object>();
        dto = new OrganizationDTO(orgMap);
        dto.setData(o);
        dto.stripNullValues();

        orgMaps.add(orgMap);
      }

      this.fProperties.put(PersonDTO.ORGS_FIELD, orgMaps);
    } else {
      this.fProperties.remove(PersonDTO.ORGS_FIELD);
    }
  }

  public String getPets() {
    String pets = null;
    final Object value = this.fProperties.get(PersonDTO.PETS_FIELD);

    if (value != null) {
      pets = (String) value;
    }

    return pets;
  }

  public void setPets(String pets) {
    if (pets != null) {
      this.fProperties.put(PersonDTO.PETS_FIELD, pets);
    } else {
      this.fProperties.remove(PersonDTO.PETS_FIELD);
    }
  }

  public List<ListField> getPhoneNumbers() {
    List<ListField> phones = null;
    @SuppressWarnings("unchecked")
    final Map<String, Object> lfMap = (Map<String, Object>) this.fProperties
            .get(PersonDTO.PHONES_FIELD);

    if (lfMap != null) {
      phones = new GraphListFieldList(lfMap).toDTO();
    }

    return phones;
  }

  public void setPhoneNumbers(List<ListField> phoneNumbers) {
    // TODO: specify list and map implementation
    if (phoneNumbers != null) {
      final Map<String, Object> lfMap = new HashMap<>();
      new GraphListFieldList(lfMap).store(phoneNumbers);
      this.fProperties.put(PersonDTO.PHONES_FIELD, lfMap);
    } else {
      this.fProperties.remove(PersonDTO.PHONES_FIELD);
    }
  }

  public List<ListField> getPhotos() {
    List<ListField> photos = null;
    @SuppressWarnings("unchecked")
    final Map<String, Object> lfMap = (Map<String, Object>) this.fProperties
            .get(PersonDTO.PHOTOS_FIELD);

    if (lfMap != null) {
      photos = new GraphListFieldList(lfMap).toDTO();
    }

    return photos;
  }

  public void setPhotos(List<ListField> photos) {
    // TODO: specify list and map implementation
    if (photos != null) {
      final Map<String, Object> lfMap = new HashMap<>();
      new GraphListFieldList(lfMap).store(photos);
      this.fProperties.put(PersonDTO.PHOTOS_FIELD, lfMap);
    } else {
      this.fProperties.remove(PersonDTO.PHOTOS_FIELD);
    }
  }

  public String getPoliticalViews() {
    String pViews = null;
    final Object value = this.fProperties.get(PersonDTO.POLITICS_FIELD);

    if (value != null) {
      pViews = (String) value;
    }

    return pViews;
  }

  public void setPoliticalViews(String politicalViews) {
    if (politicalViews != null) {
      this.fProperties.put(PersonDTO.POLITICS_FIELD, politicalViews);
    } else {
      this.fProperties.remove(PersonDTO.POLITICS_FIELD);
    }
  }

  public String getPreferredUsername() {
    String pName = null;
    final Object value = this.fProperties.get(PersonDTO.PREF_USRNAME_FIELD);

    if (value != null) {
      pName = (String) value;
    }

    return pName;
  }

  public void setPreferredUsername(String preferredString) {
    this.fProperties.put(PersonDTO.PREF_USRNAME_FIELD, preferredString);
  }

  public Url getProfileSong() {
    Url url = null;
    String urlString = null;
    final Object value = this.fProperties.get(PersonDTO.PROF_SONG);

    if (value != null) {
      urlString = (String) value;
      url = new UrlImpl();
      url.setLinkText(urlString);
      url.setValue(urlString);
    }

    return url;
  }

  public void setProfileSong(Url profileSong) {
    if (profileSong != null && profileSong.getValue() != null) {
      this.fProperties.put(PersonDTO.PROF_SONG, profileSong.getValue());
    } else {
      this.fProperties.remove(profileSong);
    }
  }

  public Url getProfileVideo() {
    Url url = null;
    String urlString = null;
    final Object value = this.fProperties.get(PersonDTO.PROF_VID_FIELD);

    if (value != null) {
      urlString = (String) value;
      url = new UrlImpl();
      url.setLinkText(urlString);
      url.setValue(urlString);
    }

    return url;
  }

  public void setProfileVideo(Url profileVideo) {
    if (profileVideo != null) {
      this.fProperties.put(PersonDTO.PROF_VID_FIELD, profileVideo.getValue());
    } else {
      this.fProperties.remove(PersonDTO.PROF_VID_FIELD);
    }
  }

  public List<String> getQuotes() {
    final List<String> quotes = listFromProperty(PersonDTO.QUOTES_FIELD, String.class);

    return quotes;
  }

  public void setQuotes(List<String> quotes) {
    if (quotes != null) {
      final String[] quoteArr = quotes.toArray(new String[quotes.size()]);
      this.fProperties.put(PersonDTO.QUOTES_FIELD, quoteArr);
    } else {
      this.fProperties.remove(PersonDTO.QUOTES_FIELD);
    }
  }

  public String getRelationshipStatus() {
    String relStat = null;
    final Object value = this.fProperties.get(PersonDTO.REL_STAT_FIELD);

    if (value != null) {
      relStat = (String) value;
    }

    return relStat;
  }

  public void setRelationshipStatus(String relationshipStatus) {
    if (relationshipStatus != null) {
      this.fProperties.put(PersonDTO.REL_STAT_FIELD, relationshipStatus);
    } else {
      this.fProperties.remove(PersonDTO.REL_STAT_FIELD);
    }
  }

  public String getReligion() {
    String religion = null;
    final Object value = this.fProperties.get(PersonDTO.RELIGION_FIELD);

    if (value != null) {
      religion = (String) value;
    }

    return religion;
  }

  public void setReligion(String religion) {
    if (religion != null) {
      this.fProperties.put(PersonDTO.RELIGION_FIELD, religion);
    } else {
      this.fProperties.remove(PersonDTO.RELIGION_FIELD);
    }
  }

  public String getRomance() {
    String romance = null;
    final Object value = this.fProperties.get(PersonDTO.ROMANCE_FIELD);

    if (value != null) {
      romance = (String) value;
    }

    return romance;
  }

  public void setRomance(String romance) {
    if (romance != null) {
      this.fProperties.put(PersonDTO.ROMANCE_FIELD, romance);
    } else {
      this.fProperties.remove(PersonDTO.ROMANCE_FIELD);
    }
  }

  public String getScaredOf() {
    String scared = null;
    final Object value = this.fProperties.get(PersonDTO.SCARED_FIELD);

    if (value != null) {
      scared = (String) value;
    }

    return scared;
  }

  public void setScaredOf(String scaredOf) {
    if (scaredOf != null) {
      this.fProperties.put(PersonDTO.SCARED_FIELD, scaredOf);
    } else {
      this.fProperties.remove(PersonDTO.SCARED_FIELD);
    }
  }

  public String getSexualOrientation() {
    String sOrientation = null;
    final Object value = this.fProperties.get(PersonDTO.S_ORI_FIELD);

    if (value != null) {
      sOrientation = (String) value;
    }

    return sOrientation;
  }

  public void setSexualOrientation(String sexualOrientation) {
    if (sexualOrientation != null) {
      this.fProperties.put(PersonDTO.S_ORI_FIELD, sexualOrientation);
    } else {
      this.fProperties.remove(PersonDTO.S_ORI_FIELD);
    }
  }

  public Enum<Smoker> getSmoker() {
    Enum<Smoker> smoker = null;
    final Object value = this.fProperties.get(PersonDTO.SMOKER_FIELD);

    if (value != null) {
      final Smoker smokerValue = Smoker.valueOf((String) value);
      smoker = new EnumImpl<Smoker>(smokerValue);
    }

    return smoker;
  }

  public void setSmoker(Enum<Smoker> newSmoker) {
    if (newSmoker != null) {
      this.fProperties.put(PersonDTO.SMOKER_FIELD, newSmoker.getValue().name());
    } else {
      this.fProperties.remove(PersonDTO.SMOKER_FIELD);
    }
  }

  public List<String> getSports() {
    final List<String> sports = listFromProperty(PersonDTO.SPORTS_FIELD, String.class);

    return sports;
  }

  public void setSports(List<String> sports) {
    if (sports != null) {
      final String[] spArr = sports.toArray(new String[sports.size()]);
      this.fProperties.put(PersonDTO.SPORTS_FIELD, spArr);
    } else {
      this.fProperties.remove(PersonDTO.SPORTS_FIELD);
    }
  }

  public String getStatus() {
    String status = null;
    final Object value = this.fProperties.get(PersonDTO.STATUS_FIELD);

    if (value != null) {
      status = (String) value;
    }

    return status;
  }

  public void setStatus(String status) {
    this.fProperties.put(PersonDTO.STATUS_FIELD, status);
  }

  public List<String> getTags() {
    final List<String> tags = listFromProperty(PersonDTO.TAGS_FIELD, String.class);

    return tags;
  }

  public void setTags(List<String> tags) {
    if (tags != null) {
      final String[] tagArr = tags.toArray(new String[tags.size()]);
      this.fProperties.put(PersonDTO.TAGS_FIELD, tagArr);
    } else {
      this.fProperties.remove(PersonDTO.TAGS_FIELD);
    }
  }

  public Long getUtcOffset() {
    Long offset = null;
    final Object value = this.fProperties.get(PersonDTO.OFFSET_FIELD);

    if (value != null) {
      offset = (Long) value;
    }

    return offset;
  }

  public void setUtcOffset(Long utcOffset) {
    this.fProperties.put(PersonDTO.OFFSET_FIELD, utcOffset);
  }

  public List<String> getTurnOffs() {
    final List<String> turnOffs = listFromProperty(PersonDTO.TURN_OFFS_FIELD, String.class);

    return turnOffs;
  }

  public void setTurnOffs(List<String> turnOffs) {
    if (turnOffs != null) {
      final String[] tosArr = turnOffs.toArray(new String[turnOffs.size()]);
      this.fProperties.put(PersonDTO.TURN_OFFS_FIELD, tosArr);
    } else {
      this.fProperties.remove(PersonDTO.TURN_OFFS_FIELD);
    }
  }

  public List<String> getTurnOns() {
    final List<String> turnOns = listFromProperty(PersonDTO.TURN_ONS_FIELD, String.class);

    return turnOns;
  }

  public void setTurnOns(List<String> turnOns) {
    if (turnOns != null) {
      final String[] tosArr = turnOns.toArray(new String[turnOns.size()]);
      this.fProperties.put(PersonDTO.TURN_ONS_FIELD, tosArr);
    } else {
      this.fProperties.remove(PersonDTO.TURN_ONS_FIELD);
    }
  }

  public List<String> getTvShows() {
    final List<String> shows = listFromProperty(PersonDTO.TV_SHOWS_FIELD, String.class);

    return shows;
  }

  public void setTvShows(List<String> tvShows) {
    if (tvShows != null) {
      final String[] tvsArr = tvShows.toArray(new String[tvShows.size()]);
      this.fProperties.put(PersonDTO.TV_SHOWS_FIELD, tvsArr);
    } else {
      this.fProperties.remove(PersonDTO.TV_SHOWS_FIELD);
    }
  }

  public List<Url> getUrls() {
    List<Url> urls = null;
    final String[] urlStrings = (String[]) this.fProperties.get(PersonDTO.URLS_FIELD);

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

      this.fProperties.put(PersonDTO.URLS_FIELD, urlArr);
    } else {
      this.fProperties.remove(PersonDTO.URLS_FIELD);
    }
  }

  public boolean getIsOwner() {
    return this.fIsOwner;
  }

  public void setIsOwner(boolean isOwner) {
    this.fIsOwner = isOwner;
  }

  public boolean getIsViewer() {
    return this.fIsViewer;
  }

  public void setIsViewer(boolean isViewer) {
    this.fIsViewer = isViewer;
  }

  public String getProfileUrl() {
    String url = null;
    final Object value = this.fProperties.get(PersonDTO.PROFILE_URL_FIELD);

    if (value != null) {
      url = (String) value;
    }

    return url;
  }

  public void setProfileUrl(String profileUrl) {
    this.fProperties.put(PersonDTO.PROFILE_URL_FIELD, profileUrl);
  }

  public String getThumbnailUrl() {
    String url = null;
    final Object value = this.fProperties.get(PersonDTO.THUMBNAIL_FIELD);

    if (value != null) {
      url = (String) value;
    }

    return url;
  }

  public void setThumbnailUrl(String thumbnailUrl) {
    this.fProperties.put(PersonDTO.THUMBNAIL_FIELD, thumbnailUrl);
  }

  /**
   * Sets the properties of this data transfer object to those of the given object. If the given
   * Object is null, all data is cleared.
   *
   * @param person
   *          person object containing data to set
   */
  public void setData(final Person person) {
    if (person == null) {
      this.fProperties.clear();
      return;
    }

    this.fProperties.put(PersonDTO.ABOUT_ME_FIELD, person.getAboutMe());
    // age is not stored anymore - refer to birthday
    // newValues.put(AGE_FIELD, person.getAge());
    this.fProperties.put(PersonDTO.CHILDREN_FIELD, person.getChildren());
    this.fProperties.put(PersonDTO.DISP_NAME_FIELD, person.getDisplayName());
    this.fProperties.put(PersonDTO.ID_FIELD, person.getId());
    this.fProperties.put(PersonDTO.JOB_INTS_FIELD, person.getJobInterests());
    this.fProperties.put(PersonDTO.NICKNAME_FIELD, person.getNickname());
    this.fProperties.put(PersonDTO.PREF_USRNAME_FIELD, person.getPreferredUsername());
    this.fProperties.put(PersonDTO.PROFILE_URL_FIELD, person.getProfileUrl());
    this.fProperties.put(PersonDTO.REL_STAT_FIELD, person.getRelationshipStatus());
    this.fProperties.put(PersonDTO.STATUS_FIELD, person.getStatus());
    this.fProperties.put(PersonDTO.THUMBNAIL_FIELD, person.getThumbnailUrl());
    this.fProperties.put(PersonDTO.OFFSET_FIELD, person.getUtcOffset());

    // non-standard types
    this.setBirthday(person.getBirthday());
    this.setGender(person.getGender());
    this.setUpdated(person.getUpdated());
    this.setNetworkPresence(person.getNetworkPresence());
    this.setProfileVideo(person.getProfileVideo());
    this.setName(person.getName());

    this.setActivities(person.getActivities());
    this.setBooks(person.getBooks());
    this.setInterests(person.getInterests());
    this.setLanguagesSpoken(person.getLanguagesSpoken());
    this.setLookingFor(person.getLookingFor());
    this.setQuotes(person.getQuotes());
    this.setTags(person.getTags());
    this.setUrls(person.getUrls());

    this.setAddresses(person.getAddresses());
    this.setAppData(person.getAppData());
    this.setCurrentLocation(person.getCurrentLocation());
    this.setOrganizations(person.getOrganizations());
    this.setEmails(person.getEmails());
    this.setIms(person.getIms());
    this.setPhoneNumbers(person.getPhoneNumbers());
    this.setPhotos(person.getPhotos());

    // reactivated fields (simple)
    this.fProperties.put(PersonDTO.ETHNICITY_FIELD, person.getEthnicity());
    this.fProperties.put(PersonDTO.FASHION_FIELD, person.getFashion());
    this.fProperties.put(PersonDTO.HAPPIEST_FIELD, person.getHappiestWhen());
    this.fProperties.put(PersonDTO.HUMOR_FIELD, person.getHumor());
    this.fProperties.put(PersonDTO.LIVING_FIELD, person.getLivingArrangement());
    this.fProperties.put(PersonDTO.PETS_FIELD, person.getPets());
    this.fProperties.put(PersonDTO.POLITICS_FIELD, person.getPoliticalViews());
    this.fProperties.put(PersonDTO.RELIGION_FIELD, person.getReligion());
    this.fProperties.put(PersonDTO.ROMANCE_FIELD, person.getRomance());
    this.fProperties.put(PersonDTO.SCARED_FIELD, person.getScaredOf());
    this.fProperties.put(PersonDTO.S_ORI_FIELD, person.getSexualOrientation());

    // reactivated fields (complex)
    this.setBodyType(person.getBodyType());
    this.setCars(person.getCars());
    this.setDrinker(person.getDrinker());
    this.setFood(person.getFood());
    this.setHeroes(person.getHeroes());
    this.setMovies(person.getMovies());
    this.setMusic(person.getMusic());
    this.setProfileSong(person.getProfileSong());
    this.setSmoker(person.getSmoker());
    this.setSports(person.getSports());
    this.setTurnOffs(person.getTurnOffs());
    this.setTurnOns(person.getTurnOns());
    this.setTvShows(person.getTvShows());
  }
}
