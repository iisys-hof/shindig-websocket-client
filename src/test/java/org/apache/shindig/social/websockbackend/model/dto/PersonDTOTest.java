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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shindig.social.opensocial.model.Account;
import org.apache.shindig.social.opensocial.model.Address;
import org.apache.shindig.social.opensocial.model.ListField;
import org.apache.shindig.social.opensocial.model.LookingFor;
import org.apache.shindig.social.opensocial.model.Name;
import org.apache.shindig.social.opensocial.model.NetworkPresence;
import org.apache.shindig.social.opensocial.model.Organization;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.model.Url;
import org.apache.shindig.social.websockbackend.model.IExtOrganization;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the person, account, organization, address and list field converter classes.
 */
public class PersonDTOTest {
  private static final String ABOUT_ME = "person's about me";
  private static final String CHILDREN = "person's children";
  private static final String DISP_NAME = "person's display name";
  private static final String ID = "person's ID";
  private static final String JOB_INTS = "person's job interests";
  private static final String NICKNAME = "person's nickname";
  private static final String PREF_USERNAME = "person's preferred username";
  private static final String PROFILE_URL = "person's profile URL";
  private static final String REL_STAT = "person's relationship status";
  private static final String STATUS = "person's status";
  private static final String THUMBNAIL = "person's thumbnail URL";
  private static final String PROFILE_VID = "person's profile video URL";

  private static final String ADD_NAME = "person's additional name";
  private static final String FAM_NAME = "person's family name";
  private static final String FORMATTED = "person's formatted name";
  private static final String GIV_NAME = "person's given name";
  private static final String HON_PREF = "person's honorarific prefix";
  private static final String HON_SUFF = "person's honorarific suffix";

  private static final Long OFFSET = 42L;
  private static final Long BIRTHDAY = System.currentTimeMillis();
  private static final String GENDER = Person.Gender.female.name();
  private static final Long UPDATED = System.currentTimeMillis();
  private static final String NET_PRES = NetworkPresence.ONLINE.name();

  private static final String[] ACTIVITIES = { "activity1", "activity2" };
  private static final String[] BOOKS = { "book1", "book2" };
  private static final String[] INTERESTS = { "interest1", "interest2" };
  private static final String[] LANGUAGES = { "language1", "language2" };
  private static final String[] LOOKING_FOR = { LookingFor.FRIENDS.name(), LookingFor.RANDOM.name() };
  private static final String[] QUOTES = { "quote1", "quote2" };
  private static final String[] TAGS = { "tag1", "tag2" };
  private static final String[] URLS = { "url1", "url2" };

  // account
  private static final String ACC_DOMAIN = "account domain";
  private static final String ACC_USER_ID = "account user ID";
  private static final String ACC_USER_NAME = "account user name";

  // address
  private static final String ADD_COUNTRY = "address country";
  private static final Float ADD_LATITUDE = 42.0f;
  private static final Float ADD_LONGITUDE = 3.14159f;
  private static final String ADD_LOCALITY = "address locality";
  private static final String ADD_POSTAL = "address postal code";
  private static final String ADD_REGION = "address region";
  private static final String ADD_ADDRESS = "address street address";
  private static final String ADD_TYPE = "address type";
  private static final String ADD_FORMATTED = "address formatted";
  private static final Boolean ADD_PRIMARY = true;

  // organization, affiliation
  private static final String ORG_DESCRIPTION = "organization description";
  private static final Long AFF_END_DATE = System.currentTimeMillis();
  private static final String ORG_FIELD = "organization field";
  private static final String ORG_NAME = "organization name";
  private static final String AFF_SALARY = "affiliation salary";
  private static final Long AFF_START_DATE = System.currentTimeMillis();
  private static final String ORG_SUB_FIELD = "organization sub-field";
  private static final String AFF_TITLE = "affiliation title";
  private static final String ORG_WEB_PAGE = "organization web page";
  private static final String ORG_TYPE = "organization type";
  private static final Boolean AFF_PRIMARY = true;
  private static final String EXT_AFF_DEPARTMENT = "affiliation department";
  private static final String EXT_AFF_MANAGER_ID = "affiliation manager ID";
  private static final String EXT_AFF_SECR_ID = "affiliation secretary ID";
  private static final Boolean EXT_AFF_DEP_HEAD = true;

  // list field list data
  private static final String[] LFL_TYPES = { "type1", "type2", "type3" };
  private static final String[] LFL_VALUES = { "value1", "value2", "value3" };
  private static final Integer LFL_PRIMARY = 1;

  private Map<String, Object> fPersonNode;

  /**
   * Sets up some test data.
   */
  @Before
  public void setupData() {
    this.fPersonNode = new HashMap<String, Object>();

    this.fPersonNode.put(Person.Field.ABOUT_ME.toString(), PersonDTOTest.ABOUT_ME);
    this.fPersonNode.put(Person.Field.CHILDREN.toString(), PersonDTOTest.CHILDREN);
    this.fPersonNode.put(Person.Field.DISPLAY_NAME.toString(), PersonDTOTest.DISP_NAME);
    this.fPersonNode.put(Person.Field.ID.toString(), PersonDTOTest.ID);
    this.fPersonNode.put(Person.Field.JOB_INTERESTS.toString(), PersonDTOTest.JOB_INTS);
    this.fPersonNode.put(Person.Field.NICKNAME.toString(), PersonDTOTest.NICKNAME);
    this.fPersonNode.put(Person.Field.PREFERRED_USERNAME.toString(), PersonDTOTest.PREF_USERNAME);
    this.fPersonNode.put(Person.Field.PROFILE_URL.toString(), PersonDTOTest.PROFILE_URL);
    this.fPersonNode.put(Person.Field.RELATIONSHIP_STATUS.toString(), PersonDTOTest.REL_STAT);
    this.fPersonNode.put(Person.Field.CHILDREN.toString(), PersonDTOTest.CHILDREN);
    this.fPersonNode.put(Person.Field.STATUS.toString(), PersonDTOTest.STATUS);
    this.fPersonNode.put(Person.Field.THUMBNAIL_URL.toString(), PersonDTOTest.THUMBNAIL);
    this.fPersonNode.put(Person.Field.PROFILE_VIDEO.toString(), PersonDTOTest.PROFILE_VID);

    this.fPersonNode.put(Name.Field.ADDITIONAL_NAME.toString(), PersonDTOTest.ADD_NAME);
    this.fPersonNode.put(Name.Field.FAMILY_NAME.toString(), PersonDTOTest.FAM_NAME);
    this.fPersonNode.put(Name.Field.FORMATTED.toString(), PersonDTOTest.FORMATTED);
    this.fPersonNode.put(Name.Field.GIVEN_NAME.toString(), PersonDTOTest.GIV_NAME);
    this.fPersonNode.put(Name.Field.HONORIFIC_PREFIX.toString(), PersonDTOTest.HON_PREF);
    this.fPersonNode.put(Name.Field.HONORIFIC_SUFFIX.toString(), PersonDTOTest.HON_SUFF);

    this.fPersonNode.put(Person.Field.UTC_OFFSET.toString(), PersonDTOTest.OFFSET);
    this.fPersonNode.put(Person.Field.BIRTHDAY.toString(), PersonDTOTest.BIRTHDAY);
    this.fPersonNode.put(Person.Field.GENDER.toString(), PersonDTOTest.GENDER);
    this.fPersonNode.put(Person.Field.LAST_UPDATED.toString(), PersonDTOTest.UPDATED);
    this.fPersonNode.put(Person.Field.NETWORKPRESENCE.toString(), PersonDTOTest.NET_PRES);

    this.fPersonNode.put(Person.Field.ACTIVITIES.toString(), PersonDTOTest.ACTIVITIES);
    this.fPersonNode.put(Person.Field.BOOKS.toString(), PersonDTOTest.BOOKS);
    this.fPersonNode.put(Person.Field.INTERESTS.toString(), PersonDTOTest.INTERESTS);
    this.fPersonNode.put(Person.Field.LANGUAGES_SPOKEN.toString(), PersonDTOTest.LANGUAGES);
    this.fPersonNode.put(Person.Field.LOOKING_FOR.toString(), PersonDTOTest.LOOKING_FOR);
    this.fPersonNode.put(Person.Field.QUOTES.toString(), PersonDTOTest.QUOTES);
    this.fPersonNode.put(Person.Field.TAGS.toString(), PersonDTOTest.TAGS);
    this.fPersonNode.put(Person.Field.URLS.toString(), PersonDTOTest.URLS);

    // account
    final Map<String, Object> accRel = new HashMap<String, Object>();
    accRel.put(Account.Field.DOMAIN.toString(), PersonDTOTest.ACC_DOMAIN);
    accRel.put(Account.Field.USER_ID.toString(), PersonDTOTest.ACC_USER_ID);
    accRel.put(Account.Field.USERNAME.toString(), PersonDTOTest.ACC_USER_NAME);

    final List<Map<String, Object>> accList = new ArrayList<Map<String, Object>>();
    accList.add(accRel);
    this.fPersonNode.put(Person.Field.ACCOUNTS.toString(), accList);

    // address (located at and currently at)
    final Map<String, Object> addNode = new HashMap<String, Object>();

    addNode.put(Address.Field.COUNTRY.toString(), PersonDTOTest.ADD_COUNTRY);
    addNode.put(Address.Field.FORMATTED.toString(), PersonDTOTest.ADD_FORMATTED);
    addNode.put(Address.Field.LATITUDE.toString(), PersonDTOTest.ADD_LATITUDE);
    addNode.put(Address.Field.LONGITUDE.toString(), PersonDTOTest.ADD_LONGITUDE);
    addNode.put(Address.Field.LOCALITY.toString(), PersonDTOTest.ADD_LOCALITY);
    addNode.put(Address.Field.POSTAL_CODE.toString(), PersonDTOTest.ADD_POSTAL);
    addNode.put(Address.Field.PRIMARY.toString(), PersonDTOTest.ADD_PRIMARY);
    addNode.put(Address.Field.REGION.toString(), PersonDTOTest.ADD_REGION);
    addNode.put(Address.Field.STREET_ADDRESS.toString(), PersonDTOTest.ADD_ADDRESS);
    addNode.put(Address.Field.TYPE.toString(), PersonDTOTest.ADD_TYPE);

    this.fPersonNode.put(Person.Field.CURRENT_LOCATION.toString(), addNode);

    final List<Map<String, Object>> addList = new ArrayList<Map<String, Object>>();
    addList.add(addNode);
    this.fPersonNode.put(Person.Field.ADDRESSES.toString(), addList);

    // organization, affiliation
    final Map<String, Object> orgNode = new HashMap<String, Object>();

    orgNode.put(Organization.Field.DESCRIPTION.toString(), PersonDTOTest.ORG_DESCRIPTION);
    orgNode.put(Organization.Field.FIELD.toString(), PersonDTOTest.ORG_FIELD);
    orgNode.put(Organization.Field.NAME.toString(), PersonDTOTest.ORG_NAME);
    orgNode.put(Organization.Field.SUB_FIELD.toString(), PersonDTOTest.ORG_SUB_FIELD);
    orgNode.put(Organization.Field.TYPE.toString(), PersonDTOTest.ORG_TYPE);
    orgNode.put(Organization.Field.WEBPAGE.toString(), PersonDTOTest.ORG_WEB_PAGE);

    orgNode.put(Organization.Field.END_DATE.toString(), PersonDTOTest.AFF_END_DATE);
    orgNode.put(Organization.Field.PRIMARY.toString(), PersonDTOTest.AFF_PRIMARY);
    orgNode.put(Organization.Field.SALARY.toString(), PersonDTOTest.AFF_SALARY);
    orgNode.put(Organization.Field.START_DATE.toString(), PersonDTOTest.AFF_START_DATE);
    orgNode.put(Organization.Field.TITLE.toString(), PersonDTOTest.AFF_TITLE);
    orgNode.put(IExtOrganization.DEPARTMENT_FIELD, PersonDTOTest.EXT_AFF_DEPARTMENT);
    orgNode.put(IExtOrganization.MANAGER_ID_FIELD, PersonDTOTest.EXT_AFF_MANAGER_ID);
    orgNode.put(IExtOrganization.SECRETARY_ID_FIELD, PersonDTOTest.EXT_AFF_SECR_ID);
    orgNode.put(IExtOrganization.DEPARTMENT_HEAD_FIELD, PersonDTOTest.EXT_AFF_DEP_HEAD);

    orgNode.put(Organization.Field.ADDRESS.toString(), addNode);

    final List<Map<String, Object>> orgList = new ArrayList<Map<String, Object>>();
    orgList.add(orgNode);
    this.fPersonNode.put(Person.Field.ORGANIZATIONS.toString(), orgList);

    // list field lists - same converter class, different links
    final Map<String, Object> lflNode = new HashMap<String, Object>();
    lflNode.put(ListField.Field.TYPE.toString(), PersonDTOTest.LFL_TYPES);
    lflNode.put(ListField.Field.VALUE.toString(), PersonDTOTest.LFL_VALUES);
    lflNode.put(ListField.Field.PRIMARY.toString(), PersonDTOTest.LFL_PRIMARY);

    // phone numbers
    this.fPersonNode.put(Person.Field.PHONE_NUMBERS.toString(), lflNode);

    // email addresses
    this.fPersonNode.put(Person.Field.EMAILS.toString(), lflNode);

    // instant messengers
    this.fPersonNode.put(Person.Field.IMS.toString(), lflNode);
  }

  /**
   * Test for conversion of existing data.
   */
  @Test
  public void conversionTest() {
    final Person p = new PersonDTO(this.fPersonNode);

    Assert.assertEquals(PersonDTOTest.ABOUT_ME, p.getAboutMe());
    Assert.assertEquals(PersonDTOTest.CHILDREN, p.getChildren());
    Assert.assertEquals(PersonDTOTest.DISP_NAME, p.getDisplayName());
    Assert.assertEquals(PersonDTOTest.ID, p.getId());
    Assert.assertEquals(PersonDTOTest.JOB_INTS, p.getJobInterests());
    Assert.assertEquals(PersonDTOTest.NICKNAME, p.getNickname());
    Assert.assertEquals(PersonDTOTest.PREF_USERNAME, p.getPreferredUsername());
    Assert.assertEquals(PersonDTOTest.PROFILE_URL, p.getProfileUrl());
    Assert.assertEquals(PersonDTOTest.REL_STAT, p.getRelationshipStatus());
    Assert.assertEquals(PersonDTOTest.STATUS, p.getStatus());
    Assert.assertEquals(PersonDTOTest.THUMBNAIL, p.getThumbnailUrl());
    Assert.assertEquals(PersonDTOTest.PROFILE_VID, p.getProfileVideo().getValue());

    final Name n = p.getName();
    Assert.assertEquals(n.getAdditionalName(), PersonDTOTest.ADD_NAME);
    Assert.assertEquals(n.getFamilyName(), PersonDTOTest.FAM_NAME);
    Assert.assertEquals(n.getFormatted(), PersonDTOTest.FORMATTED);
    Assert.assertEquals(n.getGivenName(), PersonDTOTest.GIV_NAME);
    Assert.assertEquals(n.getHonorificPrefix(), PersonDTOTest.HON_PREF);
    Assert.assertEquals(n.getHonorificSuffix(), PersonDTOTest.HON_SUFF);

    Assert.assertEquals(PersonDTOTest.OFFSET, p.getUtcOffset());
    Assert.assertEquals(new Date(PersonDTOTest.BIRTHDAY), p.getBirthday());
    Assert.assertEquals(PersonDTOTest.GENDER, p.getGender().name());
    Assert.assertEquals(new Date(PersonDTOTest.UPDATED), p.getUpdated());
    Assert.assertEquals(PersonDTOTest.NET_PRES, p.getNetworkPresence().getValue().name());

    final List<String> activities = p.getActivities();
    Assert.assertEquals(2, activities.size());
    Assert.assertTrue(activities.contains(PersonDTOTest.ACTIVITIES[0]));
    Assert.assertTrue(activities.contains(PersonDTOTest.ACTIVITIES[1]));

    final List<String> books = p.getBooks();
    Assert.assertEquals(2, books.size());
    Assert.assertTrue(books.contains(PersonDTOTest.BOOKS[0]));
    Assert.assertTrue(books.contains(PersonDTOTest.BOOKS[1]));

    final List<String> interests = p.getInterests();
    Assert.assertEquals(2, interests.size());
    Assert.assertTrue(interests.contains(PersonDTOTest.INTERESTS[0]));
    Assert.assertTrue(interests.contains(PersonDTOTest.INTERESTS[1]));

    final List<String> languages = p.getLanguagesSpoken();
    Assert.assertEquals(2, languages.size());
    Assert.assertTrue(languages.contains(PersonDTOTest.LANGUAGES[0]));
    Assert.assertTrue(languages.contains(PersonDTOTest.LANGUAGES[1]));

    final List<org.apache.shindig.protocol.model.Enum<LookingFor>> lookings = p.getLookingFor();
    Assert.assertEquals(2, lookings.size());
    final String looking1 = lookings.get(0).getValue().name();
    final String looking2 = lookings.get(1).getValue().name();
    Assert.assertTrue(looking1.equals(PersonDTOTest.LOOKING_FOR[0])
            || looking2.equals(PersonDTOTest.LOOKING_FOR[0]));
    Assert.assertTrue(looking1.equals(PersonDTOTest.LOOKING_FOR[1])
            || looking2.equals(PersonDTOTest.LOOKING_FOR[1]));

    final List<String> quotes = p.getQuotes();
    Assert.assertEquals(2, quotes.size());
    Assert.assertTrue(quotes.contains(PersonDTOTest.QUOTES[0]));
    Assert.assertTrue(quotes.contains(PersonDTOTest.QUOTES[1]));

    final List<String> tags = p.getTags();
    Assert.assertEquals(2, tags.size());
    Assert.assertTrue(tags.contains(PersonDTOTest.TAGS[0]));
    Assert.assertTrue(tags.contains(PersonDTOTest.TAGS[1]));

    final List<Url> urls = p.getUrls();
    Assert.assertEquals(2, urls.size());
    final String url1 = urls.get(0).getValue();
    final String url2 = urls.get(1).getValue();
    Assert.assertTrue(url1.equals(PersonDTOTest.URLS[0]) || url2.equals(PersonDTOTest.URLS[0]));
    Assert.assertTrue(url1.equals(PersonDTOTest.URLS[1]) || url2.equals(PersonDTOTest.URLS[1]));

    // account
    final List<Account> accounts = p.getAccounts();
    Assert.assertEquals(1, accounts.size());
    final Account acc = accounts.get(0);
    Assert.assertEquals(PersonDTOTest.ACC_DOMAIN, acc.getDomain());
    Assert.assertEquals(PersonDTOTest.ACC_USER_ID, acc.getUserId());
    Assert.assertEquals(PersonDTOTest.ACC_USER_NAME, acc.getUsername());

    // address
    final Address add = p.getCurrentLocation();
    Assert.assertEquals(PersonDTOTest.ADD_ADDRESS, add.getStreetAddress());
    Assert.assertEquals(PersonDTOTest.ADD_COUNTRY, add.getCountry());
    Assert.assertEquals(PersonDTOTest.ADD_FORMATTED, add.getFormatted());
    Assert.assertEquals(PersonDTOTest.ADD_LOCALITY, add.getLocality());
    Assert.assertEquals(PersonDTOTest.ADD_POSTAL, add.getPostalCode());
    Assert.assertEquals(PersonDTOTest.ADD_REGION, add.getRegion());
    Assert.assertEquals(PersonDTOTest.ADD_TYPE, add.getType());
    Assert.assertEquals(PersonDTOTest.ADD_LATITUDE, add.getLatitude());
    Assert.assertEquals(PersonDTOTest.ADD_LONGITUDE, add.getLongitude());
    Assert.assertEquals(PersonDTOTest.ADD_PRIMARY, add.getPrimary());

    // sanity check, same address
    final List<Address> addresses = p.getAddresses();
    Assert.assertEquals(1, addresses.size());
    Assert.assertEquals(addresses.get(0).getFormatted(), PersonDTOTest.ADD_FORMATTED);

    // organization, affiliation
    final List<Organization> organizations = p.getOrganizations();
    Assert.assertEquals(1, organizations.size());
    final IExtOrganization org = (IExtOrganization) organizations.get(0);
    Assert.assertEquals(PersonDTOTest.EXT_AFF_DEPARTMENT, org.getDepartment());
    Assert.assertEquals(PersonDTOTest.ORG_DESCRIPTION, org.getDescription());
    Assert.assertEquals(PersonDTOTest.ORG_FIELD, org.getField());
    Assert.assertEquals(PersonDTOTest.EXT_AFF_MANAGER_ID, org.getManagerId());
    Assert.assertEquals(PersonDTOTest.ORG_NAME, org.getName());
    Assert.assertEquals(PersonDTOTest.AFF_SALARY, org.getSalary());
    Assert.assertEquals(PersonDTOTest.EXT_AFF_SECR_ID, org.getSecretaryId());
    Assert.assertEquals(PersonDTOTest.ORG_SUB_FIELD, org.getSubField());
    Assert.assertEquals(PersonDTOTest.AFF_TITLE, org.getTitle());
    Assert.assertEquals(PersonDTOTest.ORG_TYPE, org.getType());
    Assert.assertEquals(PersonDTOTest.ORG_WEB_PAGE, org.getWebpage());
    Assert.assertEquals(new Date(PersonDTOTest.AFF_END_DATE), org.getEndDate());
    Assert.assertEquals(new Date(PersonDTOTest.AFF_START_DATE), org.getStartDate());
    Assert.assertEquals(PersonDTOTest.AFF_PRIMARY, org.getPrimary());
    Assert.assertEquals(PersonDTOTest.EXT_AFF_DEP_HEAD, org.isDepartmentHead());

    // sanity check, same address
    Assert.assertEquals(org.getAddress().getFormatted(), PersonDTOTest.ADD_FORMATTED);

    // phone numbers
    final List<ListField> phones = p.getPhoneNumbers();
    Assert.assertEquals(3, phones.size());

    final boolean[] entries = { false, false, false };
    String type = null;
    String value = null;
    Boolean primary = null;
    for (final ListField lf : phones) {
      type = lf.getType();
      value = lf.getValue();
      primary = lf.getPrimary();

      for (int i = 0; i < 3; ++i) {
        if (type.equals(PersonDTOTest.LFL_TYPES[i]) && value.equals(PersonDTOTest.LFL_VALUES[i])
                && (!primary || PersonDTOTest.LFL_PRIMARY == i)) {
          entries[i] = true;
        }
      }
    }
    Assert.assertTrue(entries[0] && entries[1] && entries[2]);

    // email addresses, sanity check
    final List<ListField> emails = p.getEmails();
    Assert.assertEquals(3, emails.size());

    // instant messengers, sanity check
    final List<ListField> ims = p.getIms();
    Assert.assertEquals(3, ims.size());
  }

  /**
   * Test for value storing capabilities.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void storageTest() {
    final Person p = new PersonDTO(this.fPersonNode);

    final Map<String, Object> newPerson = new HashMap<String, Object>();
    final PersonDTO gPerson = new PersonDTO(newPerson);
    gPerson.setData(p);

    Assert.assertEquals(PersonDTOTest.ABOUT_ME, newPerson.get(Person.Field.ABOUT_ME.toString()));
    Assert.assertEquals(PersonDTOTest.CHILDREN, newPerson.get(Person.Field.CHILDREN.toString()));
    Assert.assertEquals(PersonDTOTest.DISP_NAME,
            newPerson.get(Person.Field.DISPLAY_NAME.toString()));
    Assert.assertEquals(PersonDTOTest.JOB_INTS,
            newPerson.get(Person.Field.JOB_INTERESTS.toString()));
    Assert.assertEquals(PersonDTOTest.NICKNAME, newPerson.get(Person.Field.NICKNAME.toString()));
    Assert.assertEquals(PersonDTOTest.PREF_USERNAME,
            newPerson.get(Person.Field.PREFERRED_USERNAME.toString()));
    Assert.assertEquals(PersonDTOTest.PROFILE_URL,
            newPerson.get(Person.Field.PROFILE_URL.toString()));
    Assert.assertEquals(PersonDTOTest.REL_STAT,
            newPerson.get(Person.Field.RELATIONSHIP_STATUS.toString()));
    Assert.assertEquals(PersonDTOTest.STATUS, newPerson.get(Person.Field.STATUS.toString()));
    Assert.assertEquals(PersonDTOTest.THUMBNAIL,
            newPerson.get(Person.Field.THUMBNAIL_URL.toString()));
    Assert.assertEquals(PersonDTOTest.PROFILE_VID,
            newPerson.get(Person.Field.PROFILE_VIDEO.toString()));

    Assert.assertEquals(PersonDTOTest.ADD_NAME,
            newPerson.get(Name.Field.ADDITIONAL_NAME.toString()));
    Assert.assertEquals(PersonDTOTest.FAM_NAME, newPerson.get(Name.Field.FAMILY_NAME.toString()));
    Assert.assertEquals(PersonDTOTest.FORMATTED, newPerson.get(Name.Field.FORMATTED.toString()));
    Assert.assertEquals(PersonDTOTest.GIV_NAME, newPerson.get(Name.Field.GIVEN_NAME.toString()));
    Assert.assertEquals(PersonDTOTest.HON_PREF,
            newPerson.get(Name.Field.HONORIFIC_PREFIX.toString()));
    Assert.assertEquals(PersonDTOTest.HON_SUFF,
            newPerson.get(Name.Field.HONORIFIC_SUFFIX.toString()));

    Assert.assertEquals(PersonDTOTest.OFFSET, newPerson.get(Person.Field.UTC_OFFSET.toString()));
    Assert.assertEquals(PersonDTOTest.BIRTHDAY, newPerson.get(Person.Field.BIRTHDAY.toString()));
    Assert.assertEquals(PersonDTOTest.GENDER, newPerson.get(Person.Field.GENDER.toString()));
    Assert.assertEquals(PersonDTOTest.UPDATED, newPerson.get(Person.Field.LAST_UPDATED.toString()));
    Assert.assertEquals(PersonDTOTest.NET_PRES,
            newPerson.get(Person.Field.NETWORKPRESENCE.toString()));

    final String[] activities = (String[]) newPerson.get(Person.Field.ACTIVITIES.toString());
    Assert.assertArrayEquals(PersonDTOTest.ACTIVITIES, activities);

    final String[] books = (String[]) newPerson.get(Person.Field.BOOKS.toString());
    Assert.assertArrayEquals(PersonDTOTest.BOOKS, books);

    final String[] interests = (String[]) newPerson.get(Person.Field.INTERESTS.toString());
    Assert.assertArrayEquals(PersonDTOTest.INTERESTS, interests);

    final String[] languages = (String[]) newPerson.get(Person.Field.LANGUAGES_SPOKEN.toString());
    Assert.assertArrayEquals(PersonDTOTest.LANGUAGES, languages);

    final String[] looking = (String[]) newPerson.get(Person.Field.LOOKING_FOR.toString());
    Assert.assertArrayEquals(PersonDTOTest.LOOKING_FOR, looking);

    final String[] quotes = (String[]) newPerson.get(Person.Field.QUOTES.toString());
    Assert.assertArrayEquals(PersonDTOTest.QUOTES, quotes);

    final String[] tags = (String[]) newPerson.get(Person.Field.TAGS.toString());
    Assert.assertArrayEquals(PersonDTOTest.TAGS, tags);

    final String[] urls = (String[]) newPerson.get(Person.Field.URLS.toString());
    Assert.assertArrayEquals(PersonDTOTest.URLS, urls);

    // address (split up due to lack of unique identifiers)
    final Map<String, Object> currAdd = (Map<String, Object>) newPerson
            .get(Person.Field.CURRENT_LOCATION.toString());

    Assert.assertEquals(PersonDTOTest.ADD_COUNTRY, currAdd.get(Address.Field.COUNTRY.toString()));
    Assert.assertEquals(PersonDTOTest.ADD_FORMATTED,
            currAdd.get(Address.Field.FORMATTED.toString()));
    Assert.assertEquals(PersonDTOTest.ADD_LATITUDE, currAdd.get(Address.Field.LATITUDE.toString()));
    Assert.assertEquals(PersonDTOTest.ADD_LOCALITY, currAdd.get(Address.Field.LOCALITY.toString()));
    Assert.assertEquals(PersonDTOTest.ADD_LONGITUDE,
            currAdd.get(Address.Field.LONGITUDE.toString()));
    Assert.assertEquals(PersonDTOTest.ADD_POSTAL, currAdd.get(Address.Field.POSTAL_CODE.toString()));
    Assert.assertEquals(PersonDTOTest.ADD_PRIMARY, currAdd.get(Address.Field.PRIMARY.toString()));
    Assert.assertEquals(PersonDTOTest.ADD_REGION, currAdd.get(Address.Field.REGION.toString()));
    Assert.assertEquals(PersonDTOTest.ADD_ADDRESS,
            currAdd.get(Address.Field.STREET_ADDRESS.toString()));
    Assert.assertEquals(PersonDTOTest.ADD_TYPE, currAdd.get(Address.Field.TYPE.toString()));

    final List<Map<String, Object>> locAdd = (List<Map<String, Object>>) newPerson
            .get(Person.Field.ADDRESSES.toString());
    Assert.assertEquals(1, locAdd.size());
    Assert.assertEquals(locAdd.get(0).get(Address.Field.FORMATTED.toString()),
            PersonDTOTest.ADD_FORMATTED);

    // affiliation and organization
    final List<Map<String, Object>> orgs = (List<Map<String, Object>>) newPerson
            .get(Person.Field.ORGANIZATIONS.toString());
    Assert.assertEquals(1, orgs.size());

    final Map<String, Object> aff = orgs.get(0);
    Assert.assertEquals(PersonDTOTest.AFF_SALARY, aff.get(Organization.Field.SALARY.toString()));
    Assert.assertEquals(PersonDTOTest.AFF_TITLE, aff.get(Organization.Field.TITLE.toString()));
    Assert.assertEquals(PersonDTOTest.AFF_END_DATE, aff.get(Organization.Field.END_DATE.toString()));
    Assert.assertEquals(PersonDTOTest.AFF_PRIMARY, aff.get(Organization.Field.PRIMARY.toString()));
    Assert.assertEquals(PersonDTOTest.AFF_START_DATE,
            aff.get(Organization.Field.START_DATE.toString()));
    Assert.assertEquals(PersonDTOTest.EXT_AFF_DEPARTMENT,
            aff.get(IExtOrganization.DEPARTMENT_FIELD));
    Assert.assertEquals(PersonDTOTest.EXT_AFF_MANAGER_ID,
            aff.get(IExtOrganization.MANAGER_ID_FIELD));
    Assert.assertEquals(PersonDTOTest.EXT_AFF_SECR_ID, aff.get(IExtOrganization.SECRETARY_ID_FIELD));
    Assert.assertEquals(PersonDTOTest.EXT_AFF_DEP_HEAD,
            aff.get(IExtOrganization.DEPARTMENT_HEAD_FIELD));

    Assert.assertEquals(PersonDTOTest.ORG_DESCRIPTION,
            aff.get(Organization.Field.DESCRIPTION.toString()));
    Assert.assertEquals(PersonDTOTest.ORG_FIELD, aff.get(Organization.Field.FIELD.toString()));
    Assert.assertEquals(PersonDTOTest.ORG_NAME, aff.get(Organization.Field.NAME.toString()));
    Assert.assertEquals(PersonDTOTest.ORG_SUB_FIELD,
            aff.get(Organization.Field.SUB_FIELD.toString()));
    Assert.assertEquals(PersonDTOTest.ORG_TYPE, aff.get(Organization.Field.TYPE.toString()));
    Assert.assertEquals(PersonDTOTest.ORG_WEB_PAGE, aff.get(Organization.Field.WEBPAGE.toString()));

    final Map<String, Object> orgAdd = (Map<String, Object>) aff.get(Organization.Field.ADDRESS
            .toString());
    Assert.assertEquals(orgAdd.get(Address.Field.FORMATTED.toString()), PersonDTOTest.ADD_FORMATTED);

    // phone numbers
    final Map<String, Object> phoneNode = (Map<String, Object>) newPerson
            .get(Person.Field.PHONE_NUMBERS.toString());
    String[] types = (String[]) phoneNode.get(ListField.Field.TYPE.toString());
    String[] values = (String[]) phoneNode.get(ListField.Field.VALUE.toString());
    Integer primary = (Integer) phoneNode.get(ListField.Field.PRIMARY.toString());

    Assert.assertEquals(3, types.length);
    Assert.assertEquals(3, values.length);
    Assert.assertEquals(PersonDTOTest.LFL_PRIMARY, primary);

    Assert.assertTrue(Arrays.equals(types, PersonDTOTest.LFL_TYPES));
    Assert.assertTrue(Arrays.equals(values, PersonDTOTest.LFL_VALUES));

    // email addresses, sanity check
    final Map<String, Object> mailNode = (Map<String, Object>) newPerson.get(Person.Field.EMAILS
            .toString());
    types = (String[]) mailNode.get(ListField.Field.TYPE.toString());
    values = (String[]) mailNode.get(ListField.Field.VALUE.toString());
    primary = (Integer) mailNode.get(ListField.Field.PRIMARY.toString());

    Assert.assertEquals(3, types.length);
    Assert.assertEquals(3, values.length);
    Assert.assertEquals(PersonDTOTest.LFL_PRIMARY, primary);

    // instant messengers, sanity check
    final Map<String, Object> imNode = (Map<String, Object>) newPerson.get(Person.Field.IMS
            .toString());
    types = (String[]) imNode.get(ListField.Field.TYPE.toString());
    values = (String[]) imNode.get(ListField.Field.VALUE.toString());
    primary = (Integer) imNode.get(ListField.Field.PRIMARY.toString());

    Assert.assertEquals(3, types.length);
    Assert.assertEquals(3, values.length);
    Assert.assertEquals(PersonDTOTest.LFL_PRIMARY, primary);
  }
}
