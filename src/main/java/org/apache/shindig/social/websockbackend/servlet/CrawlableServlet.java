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
package org.apache.shindig.social.websockbackend.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shindig.common.servlet.InjectedServlet;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.websockbackend.WebsockConfig;
import org.apache.shindig.social.websockbackend.spi.IExtPersonService;

import com.google.inject.Inject;

/**
 * Servlet exposing an end point to retrieve a crawlable HTML list of all user profiles. Needs to be
 * enabled by setting "crawlable_servlet" to "true" and including a definition in the web.xml file.
 */
public class CrawlableServlet extends InjectedServlet {
  public static final String CRAWLABLE_ENABLED = "crawlable_servlet";

  private static final long serialVersionUID = 120863182368427073L;
  private final CollectionOptions fCollOpts;
  private final Set<String> fFields;

  private IExtPersonService fPersonSPI;
  private boolean fEnabled;

  /**
   * Creates an empty servlet with default retrieval parameters. Proper initialization requires a
   * person service and a configuration object to be set.
   */
  public CrawlableServlet() {
    this.fCollOpts = new CollectionOptions();
    this.fFields = new HashSet<String>();
    this.fFields.add(Person.Field.ID.toString());
    this.fFields.add("infoUrl");
  }

  /**
   * Sets the person service to use, preferably via dependency injection. Throws a
   * NullPointerException if the given service is null
   *
   * @param personService
   *          person service to use
   */
  @Inject
  public void setPersonSPI(IExtPersonService personService) {
    if (personService == null) {
      throw new NullPointerException("person service was null");
    }

    this.fPersonSPI = personService;
  }

  /**
   * Sets the configuration object to read from, preferably via dependency injection, determining
   * whether the servlet is enabled.
   *
   * @param config
   *          configuration object to use
   */
  @Inject
  public void setEnabled(WebsockConfig config) {
    if (config.getProperty(CrawlableServlet.CRAWLABLE_ENABLED) == null) {
      this.fEnabled = false;
    } else {
      this.fEnabled = Boolean.parseBoolean(config.getProperty(CrawlableServlet.CRAWLABLE_ENABLED));
    }
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (!this.fEnabled) {
      // TODO: return message

      return;
    }

    final PrintWriter out = response.getWriter();

    final Future<RestfulCollection<Person>> peopleFut = this.fPersonSPI.getAllPeople(
            this.fCollOpts, this.fFields, null);

    response.setContentType("text/html;charset=UTF-8");

    try {
      final List<Person> people = peopleFut.get().getList();

      // create an HTML list, with IDs as labels and profile URLs as hrefs
      out.write("<html><body>");
      for (final Person p : people) {
        out.write("<a href=\"");
        out.write(p.getProfileUrl());
        out.write("\">");
        out.write(p.getId());
        out.write("</a><br>");
      }
      out.write("</body></html>");
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }
}
