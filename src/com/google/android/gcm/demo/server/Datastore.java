/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gcm.demo.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Simple implementation of a data store using standard Java collections.
 * <p>
 * This class is thread-safe but not persistent (it will lost the data when the app is restarted) - it is meant just as
 * an example.
 */
public final class Datastore {

    public static class User {
        public String userId;
        public String regId;
        public String footballTeam;

        @Override
        public String toString() {
            return "User [userId=" + userId + ", regId=" + regId + ", footballTeams=" + footballTeam + "]";
        }
    }

    private static final List<User> users = new ArrayList<User>();
    private static final Logger logger = Logger.getLogger(Datastore.class.getName());

    private Datastore() {
        throw new UnsupportedOperationException();
    }

    /**
     * Registers a device.
     */
    public static void register(String regId, String deviceId) {
        logger.info("Registering " + regId);
        synchronized (users) {
            for (User device : users) {
                if (device.userId.equals(deviceId))
                    device.regId = regId;
                return;
            }
            User user = new User();
            user.regId = regId;
            user.userId = deviceId;
            users.add(user);
        }
    }

    /**
     * Unregisters a device.
     */
    public static void unregister(String regId, String userId) {
        logger.info("Unregistering " + regId);
        synchronized (users) {
            for (User user : users) {
                if (user.userId.equals(userId))
                    user.regId = null;
                return;
            }
        }
    }

    /**
     * Updates the registration id of a device.
     */
    public static void updateRegistration(String userId, String newId) {
        logger.info("Updating " + userId + " to " + newId);
        synchronized (users) {
            for (User device : users) {
                if (device.userId.equals(userId))
                    device.regId = newId;
                return;
            }
        }
    }

    /**
     * Gets all registered devices.
     */
    public static List<User> getUsers() {
        synchronized (users) {
            return new ArrayList<User>(users);
        }
    }

    /**
     * Favourite a particular team
     * 
     * @param regId
     * @param uniqueId
     */
    public static void favourite(String team, String userId) {
        logger.info("Favouriting " + userId + " for " + team);
        synchronized (users) {
            for (User user : users) {
                if (user.userId.equals(userId)) {
                    user.footballTeam = team;
                    return;
                }
            }
        }
    }

}
