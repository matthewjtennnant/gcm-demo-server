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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.android.gcm.demo.server.Datastore.User;
import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

/**
 * Servlet that adds a new message to all registered users.
 * <p>
 * This servlet is used just by the browser (i.e., not device).
 */
@SuppressWarnings("serial")
public class SendAllMessagesServlet extends BaseServlet {

  private static final int MULTICAST_SIZE = 1000;

  private Sender sender;

  private static final Executor threadPool = Executors.newFixedThreadPool(5);

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    sender = newSender(config);
  }

  /**
   * Creates the {@link Sender} based on the servlet settings.
   */
  protected Sender newSender(ServletConfig config) {
    String key = (String) config.getServletContext()
        .getAttribute(ApiKeyInitializer.ATTRIBUTE_ACCESS_KEY);
    return new Sender(key);
  }

  /**
   * Processes the request to add a new message.
   */
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {
        List<User> users = Datastore.getUsers();
    String status;
        if (users.isEmpty()) {
            status = "Message ignored as there is no user registered!";
    } else {
      // NOTE: check below is for demonstration purposes; a real application
      // could always send a multicast, even for just one recipient
     /* if (devices.size() == 1) {
        // send a single message using plain post
        String registrationId = devices.get(0).regId;
        Message message = new Message.Builder().build();
        Device onlyDevice = devices.get(0);
        if (onlyDevice.footballTeam.equalsIgnoreCase("Manchester United") || onlyDevice.footballTeam.equalsIgnoreCase("Arsenal")) {
        	Result result = sender.send(message, registrationId, 5);
        	status = "Sent message to one device: " + result;
        }
        else {
        	status = "Device not favourited these teams ";
      	}
      } else {*/
        // send a multicast message using JSON
        // must split in chunks of 1000 devices (GCM limit)
            int total = users.size();
            List<User> partialUsers = new ArrayList<User>();
        int counter = 0;
        int tasks = 0;
            for (User user : users) {
          counter++;
                if (user.footballTeam.contains("manchester united") || user.footballTeam.contains("arsenal"))
                    partialUsers.add(user);
                int partialSize = partialUsers.size();
          if (partialSize == MULTICAST_SIZE || counter == total) {
                    asyncSend(partialUsers);
                    partialUsers.clear();
            tasks++;
          }
        }
        status = "Asynchronously sending " + tasks + " multicast messages to " +
 total + " users";
      }
    //}
    req.setAttribute(HomeServlet.ATTRIBUTE_STATUS, status.toString());
    getServletContext().getRequestDispatcher("/home").forward(req, resp);
  }

    private void asyncSend(final List<User> partialUsers) {
    // make a copy
    final List<String> ids = new ArrayList<String>();
        for (User user : partialUsers) {
            ids.add(user.regId);
  	}
    threadPool.execute(new Runnable() {

      @Override
    public void run() {
        Message message = new Message.Builder().delayWhileIdle(true).timeToLive(5400).collapseKey("12345").addData("type","football").
        		addData("home team","Man Utd 1").addData("away team","Arsenal 0").build();
        MulticastResult multicastResult;
        try {
          multicastResult = sender.send(message, ids, 5);
        } catch (IOException e) {
          logger.log(Level.SEVERE, "Error posting messages", e);
          return;
        }
        List<Result> results = multicastResult.getResults();
        // analyze the results
        for (int i = 0; i < ids.size(); i++) {
          String regId = ids.get(i);
          Result result = results.get(i);
          String messageId = result.getMessageId();
          if (messageId != null) {
                        logger.fine("Succesfully sent message to user: " + regId +
                "; messageId = " + messageId);
            String canonicalRegId = result.getCanonicalRegistrationId();
            if (canonicalRegId != null) {
              // same device has more than on registration id: update it
              logger.info("canonicalRegId " + canonicalRegId);
              Datastore.updateRegistration(regId, canonicalRegId);
            }
          } else {
            String error = result.getErrorCodeName();
            if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
              // application has been removed from device - unregister it
                            logger.info("Unregistered user: " + regId);
                            Datastore.unregister(regId, partialUsers.get(i).userId);
            } else {
              logger.severe("Error sending message to " + regId + ": " + error);
            }
          }
        }
      }});
  }

}
