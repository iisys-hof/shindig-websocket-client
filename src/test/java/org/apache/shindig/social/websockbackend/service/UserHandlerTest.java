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

import java.io.StringReader;
import java.util.List;
import java.util.Set;

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
import org.apache.shindig.social.core.model.PersonImpl;
import org.apache.shindig.social.opensocial.model.Group;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.PersonService;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.opensocial.spi.UserId.Type;
import org.apache.shindig.social.websockbackend.spi.IExtPersonService;
import org.apache.shindig.social.websockbackend.spi.IGraphService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Futures;

/**
 * Test for the user service handler of the graph back-end.
 */
public class UserHandlerTest {
  private static final Set<String> DEF_FIELDS = ImmutableSet.of(Person.Field.ID.toString(),
          Person.Field.NAME.toString());

  private IExtPersonService fPersonSPI;
  private IGraphService fGraphSPI;
  private UserHandler fUsers;
  private HandlerRegistry fRegistry;
  private BeanJsonConverter fConverter;

  private CollectionOptions fOpts;
  private SecurityToken fToken;

  @Before
  public void setup() {
    this.fPersonSPI = EasyMock.createNiceMock(IExtPersonService.class);
    this.fGraphSPI = EasyMock.createNiceMock(IGraphService.class);
    this.fUsers = new UserHandler(this.fPersonSPI, this.fGraphSPI);

    this.fConverter = EasyMock.createNiceMock(BeanJsonConverter.class);
    this.fRegistry = new DefaultHandlerRegistry(null, this.fConverter,
            new HandlerExecutionListener.NoOpHandler());
    this.fRegistry.addHandlers(ImmutableSet.<Object> of(this.fUsers));

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
   * Test routine for requests for all users.
   *
   * @throws Exception
   *           if an exception occurs
   */
  @Test
  public void getAllTest() throws Exception {
    final String path = "/user/";
    final RestHandler operation = this.fRegistry.getRestHandler(path, "GET");

    final List<Person> personList = ImmutableList.of();
    final RestfulCollection<Person> data = new RestfulCollection<Person>(personList);

    EasyMock.expect(
            this.fPersonSPI.getAllPeople(this.fOpts, UserHandlerTest.DEF_FIELDS, this.fToken))
            .andReturn(Futures.immediateFuture(data));

    EasyMock.replay(this.fPersonSPI);
    Assert.assertEquals(
            data,
            operation.execute(Maps.<String, String[]> newHashMap(), null, this.fToken,
                    this.fConverter).get());
    EasyMock.verify(this.fPersonSPI);
  }

  /**
   * Test routine for requests for friends of a friend for one or more users.
   *
   * @throws Exception
   *           if an exception occurs
   */
  @Test
  public void getFofTest() throws Exception {
    // parameters
    String path = "/user/john.doe/fof/3/false";
    RestHandler operation = this.fRegistry.getRestHandler(path, "GET");

    final List<Person> personList = ImmutableList.of();
    final RestfulCollection<Person> data = new RestfulCollection<Person>(personList);

    Set<UserId> idSet = ImmutableSet.of(new UserId(Type.userId, "john.doe"));

    EasyMock.expect(
            this.fGraphSPI.getFriendsOfFriends(idSet, 3, false, this.fOpts,
                    UserHandlerTest.DEF_FIELDS, this.fToken)).andReturn(
            Futures.immediateFuture(data));

    EasyMock.replay(this.fGraphSPI);
    Assert.assertEquals(
            data,
            operation.execute(Maps.<String, String[]> newHashMap(), null, this.fToken,
                    this.fConverter).get());
    EasyMock.verify(this.fGraphSPI);

    // default parameters, multiple users
    EasyMock.reset(this.fGraphSPI);
    path = "/user/john.doe,jane.doe/fof/";
    operation = this.fRegistry.getRestHandler(path, "GET");

    idSet = ImmutableSet.of(new UserId(Type.userId, "john.doe"),
            new UserId(Type.userId, "jane.doe"));

    EasyMock.expect(
            this.fGraphSPI.getFriendsOfFriends(idSet, 2, true, this.fOpts,
                    UserHandlerTest.DEF_FIELDS, this.fToken)).andReturn(
            Futures.immediateFuture(data));

    EasyMock.replay(this.fGraphSPI);
    Assert.assertEquals(
            data,
            operation.execute(Maps.<String, String[]> newHashMap(), null, this.fToken,
                    this.fConverter).get());
    EasyMock.verify(this.fGraphSPI);
  }

  /**
   * Test routine for requests for friend suggestions for a user.
   *
   * @throws Exception
   *           if an exception occurs
   */
  @Test
  public void getSFriendTest() throws Exception {
    final String path = "/user/john.doe/sfriend/2";
    final RestHandler operation = this.fRegistry.getRestHandler(path, "GET");

    final List<Person> personList = ImmutableList.of();
    final RestfulCollection<Person> data = new RestfulCollection<Person>(personList);

    EasyMock.expect(
            this.fGraphSPI.getFriendRecommendation(new UserId(Type.userId, "john.doe"), 2,
                    this.fOpts, UserHandlerTest.DEF_FIELDS, this.fToken)).andReturn(
            Futures.immediateFuture(data));

    EasyMock.replay(this.fGraphSPI);
    Assert.assertEquals(
            data,
            operation.execute(Maps.<String, String[]> newHashMap(), null, this.fToken,
                    this.fConverter).get());
    EasyMock.verify(this.fGraphSPI);
  }

  /**
   * Test routine for requests for all users.
   *
   * @throws Exception
   *           if an exception occurs
   */
  @Test
  public void getSGroupTest() throws Exception {
    final String path = "/user/john.doe/sgroup/2";
    final RestHandler operation = this.fRegistry.getRestHandler(path, "GET");

    final List<Group> groupList = ImmutableList.of();
    final RestfulCollection<Group> data = new RestfulCollection<Group>(groupList);

    EasyMock.expect(
            this.fGraphSPI.getGroupRecommendation(new UserId(Type.userId, "john.doe"), 2,
                    this.fOpts, ImmutableSet.<String> of(), this.fToken)).andReturn(
            Futures.immediateFuture(data));

    EasyMock.replay(this.fGraphSPI);
    Assert.assertEquals(
            data,
            operation.execute(Maps.<String, String[]> newHashMap(), null, this.fToken,
                    this.fConverter).get());
    EasyMock.verify(this.fGraphSPI);
  }

  /**
   * Test routine for requests for the shortest path between two users.
   *
   * @throws Exception
   *           if an exception occurs
   */
  @Test
  public void getSPathTest() throws Exception {
    final String path = "/user/john.doe/spath/jane.doe";
    final RestHandler operation = this.fRegistry.getRestHandler(path, "GET");

    final List<Person> personList = ImmutableList.of();
    final RestfulCollection<Person> data = new RestfulCollection<Person>(personList);

    EasyMock.expect(
            this.fGraphSPI.getShortestPath(new UserId(Type.userId, "john.doe"), new UserId(
                    Type.userId, "jane.doe"), this.fOpts, UserHandlerTest.DEF_FIELDS, this.fToken))
            .andReturn(Futures.immediateFuture(data));

    EasyMock.replay(this.fGraphSPI);
    Assert.assertEquals(
            data,
            operation.execute(Maps.<String, String[]> newHashMap(), null, this.fToken,
                    this.fConverter).get());
    EasyMock.verify(this.fGraphSPI);
  }

  /**
   * Test routine for requests for a user creation.
   *
   * @throws Exception
   *           if an exception occurs
   */
  @Test
  public void createTest() throws Exception {
    final String path = "/user/";
    final RestHandler operation = this.fRegistry.getRestHandler(path, "POST");

    final Person p = new PersonImpl();
    p.setId("john.doe");

    final String jsonPerson = "{person: {id: 'john.doe'}}";

    EasyMock.expect(this.fConverter.convertToObject(jsonPerson, Person.class)).andReturn(p);

    EasyMock.expect(this.fPersonSPI.createPerson(p, this.fToken)).andReturn(
            Futures.immediateFuture(p));

    EasyMock.replay(this.fConverter, this.fPersonSPI);
    Assert.assertEquals(
            p,
            operation.execute(Maps.<String, String[]> newHashMap(), new StringReader(jsonPerson),
                    this.fToken, this.fConverter).get());
    EasyMock.verify(this.fConverter, this.fPersonSPI);
  }

  /**
   * Test routine for requests for a user deletion.
   *
   * @throws Exception
   *           if an exception occurs
   */
  @Test
  public void deleteTest() throws Exception {
    final String path = "/user/john.doe";
    final RestHandler operation = this.fRegistry.getRestHandler(path, "DELETE");

    EasyMock.expect(this.fPersonSPI.deletePerson(new UserId(Type.userId, "john.doe"), this.fToken))
            .andReturn(Futures.immediateFuture((Void) null));

    EasyMock.replay(this.fConverter, this.fPersonSPI);
    Assert.assertEquals(
            null,
            operation.execute(Maps.<String, String[]> newHashMap(), null, this.fToken,
                    this.fConverter).get());
    EasyMock.verify(this.fConverter, this.fPersonSPI);
  }
}
