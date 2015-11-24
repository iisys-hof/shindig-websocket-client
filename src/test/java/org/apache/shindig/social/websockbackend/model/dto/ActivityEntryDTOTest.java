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
import java.util.Map;

import org.apache.shindig.social.core.model.ActivityEntryImpl;
import org.apache.shindig.social.core.model.ActivityObjectImpl;
import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.model.ActivityObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the activity entry and activity object converter classes.
 */
public class ActivityEntryDTOTest {
  private static final String CONTENT = "activity content";
  private static final String ID = "activity id";
  private static final String PUBLISHED = "activity published";
  private static final String TITLE = "activity title";
  private static final String UPDATED = "activity updated";
  private static final String URL = "activity url";
  private static final String VERB = "activity verb";

  private static final String OBJ_CONTENT = "activity object content";
  private static final String OBJ_DISP_NAME = "activity object display name";
  private static final String OBJ_TYPE = "activity object object type";
  private static final String OBJ_PUBLISHED = "activity object published";
  private static final String OBJ_SUMMARY = "activity object summary";
  private static final String OBJ_UPDATED = "activity object updated";
  private static final String OBJ_URL = "activity object url";

  private static final String ACTOR_ID = "actor";
  private static final String OBJECT_ID = "object";
  private static final String TARGET_ID = "target";
  private static final String PROVIDER_ID = "provider";
  private static final String GENERATOR_ID = "generator";

  private Map<String, Object> fEntryMap, fActorMap, fObjectMap, fTargetMap, fProviderMap,
          fGeneratorMap;

  /**
   * Sets up some sample data for testing purposes.
   */
  @Before
  public void createData() {
    this.fEntryMap = new HashMap<String, Object>();
    this.fActorMap = new HashMap<String, Object>();
    this.fObjectMap = new HashMap<String, Object>();
    this.fTargetMap = new HashMap<String, Object>();
    this.fGeneratorMap = new HashMap<String, Object>();
    this.fProviderMap = new HashMap<String, Object>();

    this.fEntryMap.put(ActivityEntry.Field.CONTENT.toString(), ActivityEntryDTOTest.CONTENT);
    this.fEntryMap.put(ActivityEntry.Field.ID.toString(), ActivityEntryDTOTest.ID);
    this.fEntryMap.put(ActivityEntry.Field.PUBLISHED.toString(), ActivityEntryDTOTest.PUBLISHED);
    this.fEntryMap.put(ActivityEntry.Field.TITLE.toString(), ActivityEntryDTOTest.TITLE);
    this.fEntryMap.put(ActivityEntry.Field.UPDATED.toString(), ActivityEntryDTOTest.UPDATED);
    this.fEntryMap.put(ActivityEntry.Field.URL.toString(), ActivityEntryDTOTest.URL);
    this.fEntryMap.put(ActivityEntry.Field.VERB.toString(), ActivityEntryDTOTest.VERB);

    fillActObj(this.fActorMap);
    this.fActorMap.put(ActivityObject.Field.ID.toString(), ActivityEntryDTOTest.ACTOR_ID);

    fillActObj(this.fGeneratorMap);
    this.fGeneratorMap.put(ActivityObject.Field.ID.toString(), ActivityEntryDTOTest.GENERATOR_ID);

    fillActObj(this.fObjectMap);
    this.fObjectMap.put(ActivityObject.Field.ID.toString(), ActivityEntryDTOTest.OBJECT_ID);

    fillActObj(this.fProviderMap);
    this.fProviderMap.put(ActivityObject.Field.ID.toString(), ActivityEntryDTOTest.PROVIDER_ID);

    fillActObj(this.fTargetMap);
    this.fTargetMap.put(ActivityObject.Field.ID.toString(), ActivityEntryDTOTest.TARGET_ID);

    this.fEntryMap.put(ActivityEntry.Field.ACTOR.toString(), this.fActorMap);
    this.fEntryMap.put(ActivityEntry.Field.OBJECT.toString(), this.fObjectMap);
    this.fEntryMap.put(ActivityEntry.Field.TARGET.toString(), this.fTargetMap);
    this.fEntryMap.put(ActivityEntry.Field.GENERATOR.toString(), this.fGeneratorMap);
    this.fEntryMap.put(ActivityEntry.Field.PROVIDER.toString(), this.fProviderMap);
  }

  private void fillActObj(Map<String, Object> node) {
    node.put(ActivityObject.Field.CONTENT.toString(), ActivityEntryDTOTest.OBJ_CONTENT);
    node.put(ActivityObject.Field.DISPLAY_NAME.toString(), ActivityEntryDTOTest.OBJ_DISP_NAME);
    node.put(ActivityObject.Field.OBJECT_TYPE.toString(), ActivityEntryDTOTest.OBJ_TYPE);
    node.put(ActivityObject.Field.PUBLISHED.toString(), ActivityEntryDTOTest.OBJ_PUBLISHED);
    node.put(ActivityObject.Field.SUMMARY.toString(), ActivityEntryDTOTest.OBJ_SUMMARY);
    node.put(ActivityObject.Field.UPDATED.toString(), ActivityEntryDTOTest.OBJ_UPDATED);
    node.put(ActivityObject.Field.URL.toString(), ActivityEntryDTOTest.OBJ_URL);
  }

  /**
   * Test for conversion of existing data.
   */
  @Test
  public void conversionTest() {
    final ActivityEntry actE = new ActivityEntryDTO(this.fEntryMap);

    Assert.assertEquals(ActivityEntryDTOTest.CONTENT, actE.getContent());
    Assert.assertEquals(ActivityEntryDTOTest.ID, actE.getId());
    Assert.assertEquals(ActivityEntryDTOTest.PUBLISHED, actE.getPublished());
    Assert.assertEquals(ActivityEntryDTOTest.TITLE, actE.getTitle());
    Assert.assertEquals(ActivityEntryDTOTest.UPDATED, actE.getUpdated());
    Assert.assertEquals(ActivityEntryDTOTest.URL, actE.getUrl());
    Assert.assertEquals(ActivityEntryDTOTest.VERB, actE.getVerb());

    final ActivityObject actor = actE.getActor();
    Assert.assertEquals(ActivityEntryDTOTest.ACTOR_ID, actor.getId());
    validateObject(actor);

    final ActivityObject object = actE.getObject();
    Assert.assertEquals(ActivityEntryDTOTest.OBJECT_ID, object.getId());
    validateObject(object);

    final ActivityObject target = actE.getTarget();
    Assert.assertEquals(ActivityEntryDTOTest.TARGET_ID, target.getId());
    validateObject(target);

    final ActivityObject generator = actE.getGenerator();
    Assert.assertEquals(ActivityEntryDTOTest.GENERATOR_ID, generator.getId());
    validateObject(target);

    final ActivityObject provider = actE.getProvider();
    Assert.assertEquals(ActivityEntryDTOTest.PROVIDER_ID, provider.getId());
    validateObject(provider);
  }

  private void validateObject(ActivityObject obj) {
    Assert.assertEquals(ActivityEntryDTOTest.OBJ_CONTENT, obj.getContent());
    Assert.assertEquals(ActivityEntryDTOTest.OBJ_DISP_NAME, obj.getDisplayName());
    Assert.assertEquals(ActivityEntryDTOTest.OBJ_PUBLISHED, obj.getPublished());
    Assert.assertEquals(ActivityEntryDTOTest.OBJ_SUMMARY, obj.getSummary());
    Assert.assertEquals(ActivityEntryDTOTest.OBJ_TYPE, obj.getObjectType());
    Assert.assertEquals(ActivityEntryDTOTest.OBJ_UPDATED, obj.getUpdated());
    Assert.assertEquals(ActivityEntryDTOTest.OBJ_URL, obj.getUrl());
  }

  /**
   * Test for value storing capabilities.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void storageTest() {
    final ActivityEntryDTO actE = new ActivityEntryDTO(this.fEntryMap);

    // modify data
    final ActivityEntry modActE = new ActivityEntryImpl();
    modActE.setContent(actE.getContent() + " mod");
    modActE.setPublished(actE.getPublished() + " mod");
    modActE.setTitle(actE.getTitle() + " mod");
    modActE.setUpdated(actE.getUpdated() + " mod");
    modActE.setUrl(actE.getUrl() + " mod");
    modActE.setVerb(actE.getVerb() + " mod");

    ActivityObject actor = actE.getActor();
    actor = modifyObject(actor);
    modActE.setActor(actor);

    ActivityObject object = actE.getObject();
    object = modifyObject(object);
    modActE.setObject(object);

    ActivityObject target = actE.getTarget();
    target = modifyObject(target);
    modActE.setTarget(target);

    ActivityObject generator = actE.getGenerator();
    generator = modifyObject(generator);
    modActE.setGenerator(generator);

    ActivityObject provider = actE.getProvider();
    provider = modifyObject(provider);
    modActE.setProvider(provider);

    // set data
    actE.setData(modActE);

    // check result
    Assert.assertEquals(ActivityEntryDTOTest.CONTENT + " mod",
            this.fEntryMap.get(ActivityEntry.Field.CONTENT.toString()));
    Assert.assertEquals(ActivityEntryDTOTest.PUBLISHED + " mod",
            this.fEntryMap.get(ActivityEntry.Field.PUBLISHED.toString()));
    Assert.assertEquals(ActivityEntryDTOTest.TITLE + " mod",
            this.fEntryMap.get(ActivityEntry.Field.TITLE.toString()));
    Assert.assertEquals(ActivityEntryDTOTest.UPDATED + " mod",
            this.fEntryMap.get(ActivityEntry.Field.UPDATED.toString()));
    Assert.assertEquals(ActivityEntryDTOTest.URL + " mod",
            this.fEntryMap.get(ActivityEntry.Field.URL.toString()));
    Assert.assertEquals(ActivityEntryDTOTest.VERB + " mod",
            this.fEntryMap.get(ActivityEntry.Field.VERB.toString()));

    validateObject((Map<String, Object>) this.fEntryMap.get(ActivityEntry.Field.ACTOR.toString()));
    validateObject((Map<String, Object>) this.fEntryMap.get(ActivityEntry.Field.OBJECT.toString()));
    validateObject((Map<String, Object>) this.fEntryMap.get(ActivityEntry.Field.TARGET.toString()));
    validateObject((Map<String, Object>) this.fEntryMap.get(ActivityEntry.Field.GENERATOR
            .toString()));
    validateObject((Map<String, Object>) this.fEntryMap
            .get(ActivityEntry.Field.PROVIDER.toString()));
  }

  private ActivityObject modifyObject(ActivityObject obj) {
    final ActivityObject modObj = new ActivityObjectImpl();

    modObj.setId(obj.getId() + " mod");
    modObj.setContent(obj.getContent() + " mod");
    modObj.setDisplayName(obj.getDisplayName() + " mod");
    modObj.setObjectType(obj.getObjectType() + " mod");
    modObj.setPublished(obj.getPublished() + " mod");
    modObj.setSummary(obj.getSummary() + " mod");
    modObj.setUpdated(obj.getUpdated() + " mod");
    modObj.setUrl(obj.getUrl() + " mod");

    return modObj;
  }

  private void validateObject(Map<String, Object> obj) {
    Assert.assertTrue(obj.get(ActivityObject.Field.ID.toString()).toString().endsWith(" mod"));

    Assert.assertEquals(ActivityEntryDTOTest.OBJ_CONTENT + " mod",
            obj.get(ActivityObject.Field.CONTENT.toString()));
    Assert.assertEquals(ActivityEntryDTOTest.OBJ_DISP_NAME + " mod",
            obj.get(ActivityObject.Field.DISPLAY_NAME.toString()));
    Assert.assertEquals(ActivityEntryDTOTest.OBJ_PUBLISHED + " mod",
            obj.get(ActivityObject.Field.PUBLISHED.toString()));
    Assert.assertEquals(ActivityEntryDTOTest.OBJ_SUMMARY + " mod",
            obj.get(ActivityObject.Field.SUMMARY.toString()));
    Assert.assertEquals(ActivityEntryDTOTest.OBJ_TYPE + " mod",
            obj.get(ActivityObject.Field.OBJECT_TYPE.toString()));
    Assert.assertEquals(ActivityEntryDTOTest.OBJ_UPDATED + " mod",
            obj.get(ActivityObject.Field.UPDATED.toString()));
    Assert.assertEquals(ActivityEntryDTOTest.OBJ_URL + " mod",
            obj.get(ActivityObject.Field.URL.toString()));
  }
}
