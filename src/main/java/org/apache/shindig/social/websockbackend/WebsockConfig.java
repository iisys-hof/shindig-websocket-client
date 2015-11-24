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
package org.apache.shindig.social.websockbackend;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.google.inject.Singleton;

/**
 * Utility class that reads and provides the configuration.
 */
@Singleton
public class WebsockConfig {
  private static final String PROPERTIES = "websocket-backend";

  private final Map<String, String> fProperties;

  /**
   * Initializes the class by reading the configuration properties file.
   *
   * @throws Exception
   *           if any errors occur
   */
  public WebsockConfig() throws Exception {
    final ClassLoader loader = Thread.currentThread().getContextClassLoader();
    final ResourceBundle rb = ResourceBundle.getBundle(WebsockConfig.PROPERTIES,
            Locale.getDefault(), loader);

    this.fProperties = new HashMap<String, String>();

    String key = null;
    String value = null;
    final Enumeration<String> keys = rb.getKeys();
    while (keys.hasMoreElements()) {
      key = keys.nextElement();
      value = rb.getString(key);

      this.fProperties.put(key, value);
    }
  }

  /**
   * Creates an empty configuration object, without reading a properties, for testing purposes.
   *
   * @param test
   *          redundant parameter
   */
  public WebsockConfig(boolean test) {
    this.fProperties = new HashMap<String, String>();
  }

  /**
   * Sets the value for a property key.
   *
   * @param key
   *          key of the property
   * @param value
   *          value of the property
   */
  public void setProperty(String key, String value) {
    this.fProperties.put(key, value);
  }

  /**
   * Returns the value for a property key or null if it doesn't exist.
   *
   * @param key
   *          key of the property
   * @return value of the property
   */
  public String getProperty(String key) {
    return this.fProperties.get(key);
  }
}
