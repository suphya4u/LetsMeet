package com.letsmeet.android.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.letsmeet.android.R;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.common.base.Strings;
import com.letsmeet.android.activity.ChatActivity;
import com.letsmeet.android.activity.EventDetailsActivity;
import com.letsmeet.android.apiclient.cache.EventListCache;
import com.letsmeet.android.common.ContactFetcher;
import com.letsmeet.android.common.DateTimeUtils;
import com.letsmeet.android.config.Constants;
import com.letsmeet.android.storage.chat.ChatMessage;
import com.letsmeet.android.storage.chat.ChatStore;
import com.letsmeet.android.widgets.contactselect.ContactInfo;

import java.util.Calendar;

public class GcmMessageHandler extends GcmListenerService {

  public GcmMessageHandler() {
  }

  @Override
  public void onMessageReceived(String from, Bundle data) {
    if (data == null || data.isEmpty()) {
      return;
    }
    String notificationType = data.getString(Constants.NOTIFICATION_TYPE_KEY);
    if (Constants.NOTIFICATION_TYPE_NEW_EVENT.equals(notificationType)) {
      createNewEventNotification(data);
    } else if (Constants.NOTIFICATION_TYPE_NEW_CHAT.equals(notificationType)) {
      createChatNotification(data);
    }
    // TODO: Handle other notifications.
  }

  private void createNewEventNotification(Bundle data) {
    long eventId = 0;
    String eventName = data.getString(Constants.NOTIFICATION_EVENT_NAME_KEY);
    if (Strings.isNullOrEmpty(eventName)) {
      eventName = "";
    }
    String eventTitle = "Invitation: " + eventName;
    String eventNotes = data.getString(Constants.NOTIFICATION_EVENT_DETAILS_KEY);
    long eventTimeMillis = 0;
    try {
      eventId = getEventId(data);
      if (eventId == 0) {
        return;
      }

      String eventTimeString = data.getString(Constants.NOTIFICATION_EVENT_TIME_KEY);
      if (!Strings.isNullOrEmpty(eventTimeString)) {
        eventTimeMillis = Long.parseLong(eventTimeString);
      }
    } catch (NumberFormatException ex) {
      // Failed to parse.
      // Log
    }

    Intent eventDetailsIntent = new Intent(this, EventDetailsActivity.class);
    eventDetailsIntent.putExtra(Constants.INTENT_EVENT_ID_KEY, String.valueOf(eventId));
    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
    stackBuilder.addParentStack(EventDetailsActivity.class);
    stackBuilder.addNextIntent(eventDetailsIntent);
    PendingIntent eventDetailsPendingIntent = stackBuilder.getPendingIntent(
        0, PendingIntent.FLAG_UPDATE_CURRENT);

    PendingIntent rsvpYesIntent = createPendingIntent(1, eventId, "YES");
    PendingIntent rsvpNoIntent = createPendingIntent(2, eventId, "NO");
    PendingIntent rsvpMaybeIntent = createPendingIntent(3, eventId, "MAYBE");

    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setAutoCancel(true)
        .setContentTitle(eventTitle)
        .setContentIntent(eventDetailsPendingIntent)
        .addAction(0, "Yes", rsvpYesIntent)
        .addAction(0, "No", rsvpNoIntent)
        .addAction(0, "Maybe", rsvpMaybeIntent);

    NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle()
        .setBigContentTitle(eventTitle);
    if (eventTimeMillis > 0) {
      String eventTimeLine = DateTimeUtils.getDisplayDateTime(this, eventTimeMillis);
      style.addLine(eventTimeLine);
    }
    if (!Strings.isNullOrEmpty(eventNotes)) {
      style.addLine(eventNotes);
    }
    notificationBuilder.setStyle(style);

    // New event received, invalidate event list cache.
    new EventListCache(this).invalidateAll();
    showNotification(notificationBuilder.build(), (int) eventId);
  }

  private long getEventId(Bundle data) {
    String eventIdString = data.getString(Constants.NOTIFICATION_EVENT_ID_KEY);
    if (Strings.isNullOrEmpty(eventIdString)) {
      return 0;
    }
    return Long.parseLong(eventIdString);
  }

  private PendingIntent createPendingIntent(int requestCode, long eventId, String rsvpResponse) {
    Intent rsvpBroadcast = new Intent();
    rsvpBroadcast.setAction(Constants.RSVP_FROM_NOTIFICATION_BROADCAST);
    rsvpBroadcast.putExtra(Constants.RSVP_RESPONSE_FROM_NOTIFICATION, rsvpResponse);
    rsvpBroadcast.putExtra(Constants.EVENT_ID_FOR_RSVP, eventId);
    return PendingIntent.getBroadcast(this, requestCode, rsvpBroadcast,
        PendingIntent.FLAG_CANCEL_CURRENT);
  }

  private void createChatNotification(Bundle data) {
    String senderPhone = data.getString(Constants.NOTIFICATION_FROM_PHONE_KEY);
    String message = data.getString(Constants.NOTIFICATION_CHAT_MESSAGE);
    String timestampStr = data.getString(Constants.NOTIFICATION_CHAT_TIME_KEY);
    String eventName = data.getString(Constants.NOTIFICATION_CHAT_EVENT_NAME);

    long eventId = 0;
    long timestamp = 0;
    try {
      eventId = getEventId(data);
      if (timestampStr != null) {
        timestamp = Long.parseLong(timestampStr);
      }
    } catch (NumberFormatException e) {
      // Failed to parse event id.
      // Log.
    }
    if (eventId == 0) {
      return;
    }

    if (timestamp == 0) {
      timestamp = Calendar.getInstance().getTimeInMillis();
    }

    ChatMessage chatMessage = new ChatMessage()
        .setEventId(eventId)
        .setMessage(message)
        .setIsMyMessage(false)
        .setSenderPhoneNumber(senderPhone)
        .setTimeSent(timestamp);
    ChatStore.insert(this, chatMessage);

    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
    stackBuilder.addParentStack(EventDetailsActivity.class);

    NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle()
        .setBigContentTitle("New message");

    Cursor cursor = ChatStore.getAllUnread(this);
    int unreadMessageCount = cursor.getCount();
    if (unreadMessageCount == 0) {
      return;
    }
    style.setSummaryText("Unread messages ( " + unreadMessageCount + ")");

    boolean multipleEvents = false;
    int totalLines = 0;
    while (cursor.moveToNext()) {
      if (totalLines++ < 8) {
        String content = getContent(cursor);
        style.addLine(content);
      }
      long unreadEventId = cursor.getLong(cursor.getColumnIndex(ChatStore.COLUMN_EVENT_ID));
      if (eventId != unreadEventId) {
        multipleEvents = true;
      }
    }

    if (!multipleEvents) {
      Intent eventDetailsIntent = new Intent(this, EventDetailsActivity.class);
      eventDetailsIntent.putExtra(Constants.INTENT_EVENT_ID_KEY, String.valueOf(eventId));
      stackBuilder.addNextIntent(eventDetailsIntent);

      Intent chatIntent = new Intent(this, ChatActivity.class);
      chatIntent.putExtra(Constants.INTENT_EVENT_ID_KEY, String.valueOf(eventId));
      chatIntent.putExtra(Constants.INTENT_EVENT_NAME_KEY,
          Strings.isNullOrEmpty(eventName) ? "Chats" : eventName);
      stackBuilder.addNextIntent(chatIntent);
    }
    PendingIntent chatPendingIntent = stackBuilder.getPendingIntent(
        0, PendingIntent.FLAG_UPDATE_CURRENT);

    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setAutoCancel(true)
        .setContentTitle("New chat message")
        .setContentIntent(chatPendingIntent)
        .setStyle(style);

    showNotification(notificationBuilder.build(), Constants.CHAT_NOTIFICATION_ID);

    // TODO: Do not show notification if user is already in chat activity for the event.
    // Possibly, user ordered broadcast receiver - one in activity and other that creates
    // notifications. The one in activity will register / receive only when activity is running
    // and abort broadcast, so notification one never gets it. If activity is not running, it will
    // directly go to notification broadcast receiver.
    Intent broadcast = new Intent();
    broadcast.setAction(Constants.NEW_CHAT_MESSAGE_BROADCAST);
    broadcast.putExtra(Constants.INTENT_EVENT_ID_KEY, String.valueOf(eventId));
    sendBroadcast(broadcast);
  }

  private String getContent(Cursor cursor) {
    String senderPhone = cursor.getString(cursor.getColumnIndex(ChatStore.COLUMN_SENDER_PHONE));
    String message = cursor.getString(cursor.getColumnIndex(ChatStore.COLUMN_MESSAGE));
    String senderName;
    ContactInfo contactInfo = ContactFetcher.getInstance()
        .getContactInfoByNumber(senderPhone, this);
    if (!Strings.isNullOrEmpty(contactInfo.getDisplayName())) {
      senderName = contactInfo.getDisplayName();
    } else {
      senderName = contactInfo.getPhoneNumber();
    }

    return senderName + ": " + message;
  }

  private void showNotification(Notification notification, int id) {
    // TODO: Merge notifications if not cleared.
    NotificationManager notificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(id, notification);
  }
}