package com.letsmeet.android.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.letsmeet.com.letsmeet.R;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateFormat;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.common.base.Strings;
import com.letsmeet.android.activity.HomeActivity;

import java.util.Calendar;
import java.util.TimeZone;

public class GcmMessageHandler extends GcmListenerService {

  private static final String NOTIFICATION_EVENT_NAME_KEY = "EVENT_NAME";
  private static final String NOTIFICATION_EVENT_DETAILS_KEY = "EVENT_DETAILS";
  private static final String NOTIFICATION_EVENT_TIME_KEY = "EVENT_TIME";

  public GcmMessageHandler() {
  }

  @Override
  public void onMessageReceived(String from, Bundle data) {
    if (data == null || data.isEmpty()) {
      return;
    }
    String eventName = "Invitation: " + data.getString(NOTIFICATION_EVENT_NAME_KEY);
    String eventNotes = data.getString(NOTIFICATION_EVENT_DETAILS_KEY);
    long eventTimeMillis = 0;
    try {
      eventTimeMillis = Long.parseLong(data.getString(NOTIFICATION_EVENT_TIME_KEY));
    } catch (NumberFormatException ex) {
      // Failed to parse.
      // Log
    }

    Intent intent = new Intent(this, HomeActivity.class);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
        PendingIntent.FLAG_CANCEL_CURRENT);

    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(eventName)
        .setContentText("These are some random notes about the event that is happening on some" +
            " random day and random time. This text is intentionally big to test how it shows up")
        // TODO(suhas): Add actions for Yes, No, Maybe.
        .setContentIntent(pendingIntent);

    NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
    style.setBuilder(notificationBuilder);
    style.setBigContentTitle(eventName);
    if (!Strings.isNullOrEmpty(eventNotes)) {
      style.addLine(eventNotes);
    }
    if (eventTimeMillis > 0) {
      Calendar eventTime = Calendar.getInstance(TimeZone.getDefault());
      eventTime.setTimeInMillis(eventTimeMillis);
      String eventDateString = DateFormat.getDateFormat(this).format(eventTime.getTime());
      String eventTimeString = DateFormat.getTimeFormat(this).format(eventTime.getTime());
      style.addLine(eventDateString + " " + eventTimeString);
    }
    notificationBuilder.setStyle(style);

    NotificationManager notificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    int notificationId = 1;
    notificationManager.notify(notificationId, notificationBuilder.build());
  }
}