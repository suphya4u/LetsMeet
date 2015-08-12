package com.letsmeet.server.notifications;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.letsmeet.server.apis.messages.EventDetails;
import com.letsmeet.server.data.UserRecord;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import static com.letsmeet.server.OfyService.ofy;

/**
 * Send notifications to devices.
 */
public class GcmNotifier {

  private static GcmNotifier instance;

  private static final Logger log = Logger.getLogger(GcmNotifier.class.getName());

  private static final String API_KEY = System.getProperty("gcm.api.key");

  private final Sender sender;

  public GcmNotifier() {
    sender = new Sender(API_KEY);
  }

  public static GcmNotifier getInstance() {
    if (instance == null) {
      instance = new GcmNotifier();
    }
    return instance;
  }

  public void notifyNewEvent(List<UserRecord> users, EventDetails event) {
    List<String> registrationIds = Lists.newArrayList();
    for (UserRecord user : users) {
      if (user != null && !Strings.isNullOrEmpty(user.getRegId())) {
        registrationIds.add(user.getRegId());
      }
    }

    Message message = new Message.Builder()
        .timeToLive(3 * 60 * 60) // 3 hours
        .build();

    MulticastResult multicastResult = null;
    try {
      multicastResult = sender.send(message, registrationIds, 5);
    } catch (IOException e) {
      log.severe("Failed to send notifications");
      e.printStackTrace();
    }
    List<Result> resultList = multicastResult.getResults();
    if (users.size() < resultList.size()) {
      log.severe("Message list is bigger than user list");
    }
    for (int i = 0 ; i < resultList.size(); i++) {
      Result result = resultList.get(i);
      UserRecord user = users.get(i);
      if (user == null || result == null) {
        log.severe("user or result not available");
        continue;
      }
      if (result.getMessageId() != null) {
        String canonicalRegId = result.getCanonicalRegistrationId();
        if (canonicalRegId != null && !user.getRegId().equals(canonicalRegId)) {
          log.info("Registration Id " + user.getRegId()
              + " is not changed to " + canonicalRegId);
          user.setRegId(canonicalRegId);
          ofy().save().entity(user).now();
        }
      } else {
        String error = result.getErrorCodeName();
        if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
          log.warning("Registration Id " + user.getRegId()
              + " no longer registered with GCM, removing from datastore");
          ofy().delete().entity(user).now();
        } else {
          log.warning("Error when sending message : " + error);
        }
      }
    }
  }
}
