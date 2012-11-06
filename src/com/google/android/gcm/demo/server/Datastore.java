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
 * This class is thread-safe but not persistent (it will lost the data when the
 * app is restarted) - it is meant just as an example.
 */
public final class Datastore {

	public static class Device {
		public String deviceId;
		public String regId;
		public String footballTeam;

		@Override
		public String toString() {
			return "Device [deviceId=" + deviceId + "\\n" + "regId=" + regId + "\\n" + "footballTeam=" + footballTeam + "]";
		}

	}
  
  private static final List<Device> devices = new ArrayList<Device>();
  private static final Logger logger =
      Logger.getLogger(Datastore.class.getName());

  private Datastore() {
    throw new UnsupportedOperationException();
  }

	/**
	 * Registers a device.
	 */
	public static void register(String regId, String deviceId) {
		logger.info("Registering " + regId);
		synchronized (devices) {
			for (Device device : devices) {
				if (device.deviceId.equals(deviceId))
					device.regId = regId;
				return;
			}
			Device device = new Device();
			device.regId = regId;
			device.deviceId = deviceId;
			devices.add(device);
		}
	}

  /**
   * Unregisters a device.
   */
  public static void unregister(String regId, String deviceId) {
    logger.info("Unregistering " + regId);
    synchronized (devices) {
    	for (Device device : devices) {
			if (device.deviceId.equals(deviceId))
				device.regId = null;
			return;
		}
    }
  }

  /**
   * Updates the registration id of a device.
   */
  public static void updateRegistration(String deviceId, String newId) {
    logger.info("Updating " + deviceId + " to " + newId);
    synchronized (devices) {
    	for (Device device : devices) {
			if (device.deviceId.equals(deviceId))
				device.regId = newId;
			return;
		}
    }
  }

  /**
   * Gets all registered devices.
   */
  public static List<Device> getDevices() {
    synchronized (devices) {
      return new ArrayList<Device>(devices);
    }
  }

	/**
	 * Favourite a particular team
	 * 
	 * @param regId
	 * @param uniqueId
	 */
	public static void favourite(String team, String deviceId) {
		logger.info("Favouriting " + deviceId + " for " + team);
		synchronized (devices) {
			for (Device device : devices) {
				if (device.deviceId.equals(deviceId)) {
					device.footballTeam = team;
					return;
				}
			}
		}
	}
	
	/**
	 * Unfavourite a particular team
	 * 
	 * @param team
	 * @param deviceId
	 */
	public static void unfavourite(String team, String deviceId) {
		logger.info("Unfavouriting " + deviceId + " for " + team);
		synchronized (devices) {
			for (Device device : devices) {
				if (device.deviceId.equals(deviceId)) {
					device.footballTeam = null;
					return;
				}
			}
		}
	}
}
