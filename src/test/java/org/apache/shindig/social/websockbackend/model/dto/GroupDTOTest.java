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

import org.apache.shindig.social.opensocial.model.Group;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the group converter class.
 */
public class GroupDTOTest {
  private static final String ID = "group ID";
  private static final String TITLE = "group title";
  private static final String DESCRIPTION = "group description";

  private Map<String, Object> fGroupMap;

  /**
   * Sets up some test data.
   */
  @Before
  public void setupData() {
    this.fGroupMap = new HashMap<String, Object>();
    this.fGroupMap.put(Group.Field.ID.toString(), GroupDTOTest.ID);
    this.fGroupMap.put(Group.Field.TITLE.toString(), GroupDTOTest.TITLE);
    this.fGroupMap.put(Group.Field.DESCRIPTION.toString(), GroupDTOTest.DESCRIPTION);
  }

  /**
   * Test for conversion of existing data.
   */
  @Test
  public void conversionTest() {
    final Group group = new GroupDTO(this.fGroupMap);

    Assert.assertEquals(GroupDTOTest.ID, group.getId());
    Assert.assertEquals(GroupDTOTest.TITLE, group.getTitle());
    Assert.assertEquals(GroupDTOTest.DESCRIPTION, group.getDescription());
  }
}
