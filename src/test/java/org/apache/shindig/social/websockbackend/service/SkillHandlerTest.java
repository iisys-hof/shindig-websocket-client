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
package org.apache.shindig.social.websockbackend.service;

import java.util.List;
import java.util.concurrent.Future;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.testing.FakeGadgetToken;
import org.apache.shindig.protocol.DefaultHandlerRegistry;
import org.apache.shindig.protocol.HandlerExecutionListener;
import org.apache.shindig.protocol.HandlerRegistry;
import org.apache.shindig.protocol.RestHandler;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.protocol.conversion.BeanJsonConverter;
import org.apache.shindig.protocol.model.FilterOperation;
import org.apache.shindig.protocol.model.SortOrder;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.PersonService;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.opensocial.spi.UserId.Type;
import org.apache.shindig.social.websockbackend.model.ISkillSet;
import org.apache.shindig.social.websockbackend.spi.ISkillService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Futures;

/**
 * Test for the skill service handler of the graph back-end.
 */
public class SkillHandlerTest {
  private ISkillService fSkillSPI;
  private SkillHandler fSkills;
  private HandlerRegistry fRegistry;
  private BeanJsonConverter fConverter;

  private CollectionOptions fOpts;
  private SecurityToken fToken;

  @Before
  public void setup() {
    this.fSkillSPI = EasyMock.createNiceMock(ISkillService.class);
    this.fSkills = new SkillHandler(this.fSkillSPI);

    this.fConverter = EasyMock.createNiceMock(BeanJsonConverter.class);
    this.fRegistry = new DefaultHandlerRegistry(null, this.fConverter,
            new HandlerExecutionListener.NoOpHandler());
    this.fRegistry.addHandlers(ImmutableSet.<Object> of(this.fSkills));

    this.fOpts = new CollectionOptions();
    this.fOpts.setSortBy(PersonService.TOP_FRIENDS_SORT);
    this.fOpts.setSortOrder(SortOrder.ascending);
    this.fOpts.setFilter(null);
    this.fOpts.setFilterOperation(FilterOperation.contains);
    this.fOpts.setFilterValue("");
    this.fOpts.setFirst(0);
    this.fOpts.setMax(20);

    this.fToken = new FakeGadgetToken();
  }

  /**
   * Test routine for requests for a user's linked skills.
   *
   * @throws Exception
   *           if an exception occurs
   */
  @Test
  public void skillRetrievalTest() throws Exception {
    final String path = "/skills/john.doe";
    final RestHandler operation = this.fRegistry.getRestHandler(path, "GET");

    final List<ISkillSet> skillList = ImmutableList.of();
    final RestfulCollection<ISkillSet> data = new RestfulCollection<ISkillSet>(skillList);

    EasyMock.expect(
            this.fSkillSPI.getSkills(new UserId(Type.userId, "john.doe"), this.fOpts, this.fToken))
            .andReturn(Futures.immediateFuture(data));

    EasyMock.replay(this.fSkillSPI);

    Assert.assertEquals(
            data,
            operation.execute(Maps.<String, String[]> newHashMap(), null, this.fToken,
                    this.fConverter).get());
    EasyMock.verify(this.fSkillSPI);
  }

  /**
   * Test routine for requests for adding a skill to a user.
   *
   * @throws Exception
   *           if an exception occurs
   */
  @Test
  public void skillAddingTest() throws Exception {
    final String path = "/skills/john.doe/Added Skill";
    final RestHandler operation = this.fRegistry.getRestHandler(path, "POST");

    final Future<Void> future = Futures.immediateFuture(null);
    EasyMock.expect(
            this.fSkillSPI
                    .addSkill(new UserId(Type.userId, "john.doe"), "Added Skill", this.fToken))
            .andReturn(future);

    EasyMock.replay(this.fSkillSPI);

    Assert.assertNull(operation.execute(Maps.<String, String[]> newHashMap(), null, this.fToken,
            this.fConverter).get());
    EasyMock.verify(this.fSkillSPI);
  }

  /**
   * Test routine for requests for removing a skill from a user.
   *
   * @throws Exception
   *           if an exception occurs
   */
  @Test
  public void skillRemovalTest() throws Exception {
    final String path = "/skills/john.doe/Added Skill";
    final RestHandler operation = this.fRegistry.getRestHandler(path, "DELETE");

    final Future<Void> future = Futures.immediateFuture(null);
    EasyMock.expect(
            this.fSkillSPI.removeSkill(new UserId(Type.userId, "john.doe"), "Added Skill",
                    this.fToken)).andReturn(future);

    EasyMock.replay(this.fSkillSPI);

    Assert.assertNull(operation.execute(Maps.<String, String[]> newHashMap(), null, this.fToken,
            this.fConverter).get());
    EasyMock.verify(this.fSkillSPI);
  }
}
