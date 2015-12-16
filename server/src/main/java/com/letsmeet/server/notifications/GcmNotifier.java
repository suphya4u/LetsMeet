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
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import static com.letsmeet.server.OfyService.ofy;

/**
 * Send notifications to devices.
 */
public class GcmNotifier {

  // TODO(suhas): Move these constants to a common place which can be shared with app.
  private static final String NOTIFICATION_TYPE = "NOTIFICATION_TYPE";
  private static final String NOTIFICATION_EVENT_NAME_KEY = "EVENT_NAME";
  private static final String NOTIFICATION_EVENT_DETAILS_KEY = "EVENT_DETAILS";
  private static final String NOTIFICATION_EVENT_TIME_KEY = "EVENT_TIME";
  private static final String NEW_EVENT_NOTIFICATION = "NEW_EVENT";
  private static final String NEW_CHAT_NOTIFICATION = "NEW_CHAT";
  private static final String NOTIFICATION_EVENT_ID_KEY = "EVENT_ID";
  private static final String NOTIFICATION_FROM_PHONE_KEY = "FROM_PHONE_NUMBER";
  private static final String NOTIFICATION_CHAT_TIME_KEY = "CHAT_MESSAGE_SENT_TIME";
  private static final String NOTIFICATION_CHAT_MESSAGE = "CHAT_MESSAGE";

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
    Message message = new Message.Builder()
        .timeToLive(3 * 60 * 60) // 3 hours
        .addData(NOTIFICATION_EVENT_ID_KEY, String.valueOf(event.getEventId()))
        .addData(NOTIFICATION_TYPE, NEW_EVENT_NOTIFICATION)
        .addData(NOTIFICATION_EVENT_NAME_KEY, event.getName())
        .addData(NOTIFICATION_EVENT_DETAILS_KEY, event.getNotes())
        .addData(NOTIFICATION_EVENT_TIME_KEY,
            // TODO(suhas): Use event time instead of current time.
            String.valueOf(Calendar.getInstance().getTimeInMillis()))
        .collapseKey("Invitations available")
        .build();
    broadcast(users, message);
  }

  public void notifyNewChat(List<UserRecord> users, String chatMessage, String fromPhoneNumber,
      long eventId, long time) {
    Message message = new Message.Builder()
        .timeToLive(24 * 60 * 60) // 1 day
        .addData(NOTIFICATION_FROM_PHONE_KEY, String.valueOf(fromPhoneNumber))
        .addData(NOTIFICATION_TYPE, NEW_CHAT_NOTIFICATION)
        .addData(NOTIFICATION_EVENT_ID_KEY, String.valueOf(eventId))
        .addData(NOTIFICATION_CHAT_TIME_KEY,
            String.valueOf(time))
        .addData(NOTIFICATION_CHAT_MESSAGE, chatMessage)
        .collapseKey("Chats available")
        .build();
    broadcast(users, message);
  }

  private void broadcast(List<UserRecord> users, Message message) {
    List<String> registrationIds = Lists.newArrayList();
    for (UserRecord user : users) {
      if (user != null && !Strings.isNullOrEmpty(user.getRegId())) {
        registrationIds.add(user.getRegId());
      }
    }
    if (registrationIds.isEmpty()) {
      return;
    }

    MulticastResult multicastResult = null;
    try {
      multicastResult = sender.send(message, registrationIds, 5);
    } catch (IOException e) {
      log.severe("Failed to send notifications");
      e.printStackTrace();
      return;
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
        if (!Strings.isNullOrEmpty(canonicalRegId) && !user.getRegId().equals(canonicalRegId)) {
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
