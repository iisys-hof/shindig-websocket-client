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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.shindig.social.websockbackend.WebsockConfig;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Central Event bus distributing incoming events to all listeners registered to specific or all
 * types. Events can either be handled in-thread or by a worker thread.
 */
@Singleton
public class ShindigEventBus implements Runnable {
  private static final String ASYNC_PROP = "shindig.events.handling.async";
  private static final String ASYNC_TIME_PROP = "shindig.events.handling.async.timer";

  private final Map<ShindigEventType, List<IEventListener>> fListeners;

  private final LinkedList<IShindigEvent> fQueue;

  private final boolean fAsync;
  private final long fAsyncTime;

  private final Logger fLogger;

  private boolean fActive;

  /**
   * Creates an empty event bus with no listeners or event providers.
   */
  @Inject
  public ShindigEventBus(WebsockConfig config) {
    this.fListeners = new HashMap<ShindigEventType, List<IEventListener>>();

    this.fQueue = new LinkedList<IShindigEvent>();

    this.fAsync = Boolean.parseBoolean(config.getProperty(ShindigEventBus.ASYNC_PROP));

    final String asyncTimeString = config.getProperty(ShindigEventBus.ASYNC_TIME_PROP);
    if (asyncTimeString != null && !asyncTimeString.isEmpty()) {
      this.fAsyncTime = Long.parseLong(asyncTimeString);
    } else {
      // TODO: proper default
      this.fAsyncTime = 1000;
    }

    this.fLogger = Logger.getLogger(this.getClass().getCanonicalName());

    // start worker thread
    if (this.fAsync) {
      new Thread(this).start();
    }
  }

  /**
   * Fires an event, handing it to all listeners registered to its type or all types.
   *
   * @param event
   *          event to distribute to listeners
   */
  public void fireEvent(IShindigEvent event) {
    if (this.fAsync) {
      synchronized (this.fQueue) {
        this.fQueue.push(event);
        this.fQueue.notify();
      }
    } else {
      notifyHandlers(event);
    }
  }

  @Override
  public void run() {
    this.fActive = true;

    final List<IShindigEvent> localQueue = new LinkedList<IShindigEvent>();

    try {
      while (this.fActive) {
        // copy events to local queue to desynchronized
        synchronized (this.fQueue) {
          localQueue.addAll(this.fQueue);
          this.fQueue.clear();
        }

        // handle events
        for (final IShindigEvent event : localQueue) {
          notifyHandlers(event);
        }
        localQueue.clear();

        // if queue is empty wait for notification or next iteration
        synchronized (this.fQueue) {
          if (this.fQueue.isEmpty()) {
            this.fQueue.wait(this.fAsyncTime);
          }
        }
      }
    } catch (final Exception e) {
      this.fLogger.log(Level.SEVERE, "event bus loop interrupted", e);
    }
  }

  /**
   * Stops the worker thread, if there is one.
   */
  public void stop() {
    this.fActive = false;
    synchronized (this.fQueue) {
      this.fQueue.notify();
    }
  }

  private void notifyHandlers(IShindigEvent event) {
    // make aggregated copy to desynchronize
    final List<IEventListener> listeners = new LinkedList<IEventListener>();

    // specific listeners
    synchronized (this.fListeners) {
      final List<IEventListener> addLists = this.fListeners.get(event.getType());
      if (addLists != null) {
        listeners.addAll(addLists);
      }
    }

    // listeners listening to all events
    synchronized (this.fListeners) {
      final List<IEventListener> addLists = this.fListeners.get(ShindigEventType.ALL);
      if (addLists != null) {
        listeners.addAll(addLists);
      }
    }

    // notify all
    for (final IEventListener lis : listeners) {
      try {
        lis.handleEvent(event);
      } catch (final Exception e) {
        this.fLogger.log(Level.SEVERE, "Exception handling event: " + event, e);
      }
    }
  }

  /**
   * Adds a listener to a specific type. The type can be specific or "ALL". The given listener and
   * type must not be null. Does not check for duplicate listener entries.
   *
   * @param type
   *          type to add a listener for
   * @param listener
   *          listener to add
   */
  public void addListener(ShindigEventType type, IEventListener listener) {
    List<IEventListener> listeners = null;

    // get or create list
    synchronized (this.fListeners) {
      listeners = this.fListeners.get(type);

      if (listeners == null) {
        listeners = new ArrayList<IEventListener>();
        this.fListeners.put(type, listeners);
      }
    }

    // add listener
    synchronized (listeners) {
      listeners.add(listener);
    }
  }

  /**
   * Adds a listener for a list of types. The list of types and the listener must not be null. Does
   * not check for duplicate listener entries.
   *
   * @param types
   *          list of types to listen for
   * @param listener
   *          listener to register for the given types
   */
  public void addListener(List<ShindigEventType> types, IEventListener listener) {
    for (final ShindigEventType type : types) {
      addListener(type, listener);
    }
  }

  /**
   * Removes a listener from a given type. The given type and listener must not be null. Does not
   * check whether there are further duplicates.
   *
   * @param type
   *          event type to remove a listener from
   * @param listener
   *          listener to unregister
   */
  public void removeListener(ShindigEventType type, IEventListener listener) {
    List<IEventListener> listeners = null;

    synchronized (this.fListeners) {
      listeners = this.fListeners.get(type);
    }

    if (listeners != null) {
      synchronized (listeners) {
        listeners.remove(listener);
      }
    }
  }

  /**
   * Removes a listener for all available types. Does not check whether there are further
   * duplicates.
   *
   * @param listener
   *          listener to unregister for all types
   */
  public void removeListener(IEventListener listener) {
    // remove listener for all types
    synchronized (this.fListeners) {
      List<IEventListener> listeners = null;

      for (final Entry<ShindigEventType, List<IEventListener>> listE : this.fListeners.entrySet()) {
        listeners = listE.getValue();

        if (listeners != null) {
          synchronized (listeners) {
            listeners.remove(listener);
          }
        }
      }
    }
  }

  /**
   * Removes all listeners.
   */
  public void clearListeners() {
    synchronized (this.fListeners) {
      this.fListeners.clear();
    }
  }

  /**
   * Removes all listeners for the given type. The given type must not be null.
   *
   * @param type
   *          type to remove all listeners for
   */
  public void clearListeners(ShindigEventType type) {
    synchronized (this.fListeners) {
      this.fListeners.remove(type);
    }
  }
}
