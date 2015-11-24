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
package org.apache.shindig.social.websockbackend.events;

import java.io.PrintWriter;

import org.apache.shindig.social.websockbackend.WebsockConfig;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Listener for testing and debugging, logging out all events.
 */
@Singleton
public class LoggingListener implements IEventListener {
  private static final String LOG_ENABLED = "shindig.events.logging";
  private static final String LOG_FILE = "shindig.events.logging.file";

  private final PrintWriter fWriter;
  private final boolean fEnabled;

  /**
   * Creates a debug logging listener with the given configuration, starting the logging writer if
   * enabled. The configuration object must not be null.
   *
   * @param config
   *          confguration object to use
   * @throws Exception
   *           if startup fails
   */
  @Inject
  public LoggingListener(WebsockConfig config) throws Exception {
    this.fEnabled = Boolean.parseBoolean(config.getProperty(LoggingListener.LOG_ENABLED));

    if (this.fEnabled) {
      final String logFile = config.getProperty(LoggingListener.LOG_FILE);
      this.fWriter = new PrintWriter(logFile);

      // hook for clean shutdown
      Runtime.getRuntime().addShutdownHook(new Thread() {
        @Override
        public void run() {
          LoggingListener.this.fWriter.flush();
          LoggingListener.this.fWriter.close();
        }
      });
    } else {
      this.fWriter = null;
    }
  }

  @Override
  public void handleEvent(IShindigEvent event) {
    if (!this.fEnabled) {
      return;
    }

    try {
      this.fWriter.println("event: " + event.getType());

      this.fWriter.println("payload: " + event.getPayload());

      this.fWriter.println("token: " + event.getToken());

      this.fWriter.println("\n\n");

      this.fWriter.flush();
    } catch (final Exception e) {
      // TODO: logging?
    }
  }

}
