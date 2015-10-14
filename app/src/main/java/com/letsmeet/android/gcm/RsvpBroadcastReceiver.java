package com.letsmeet.android.gcm;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.letsmeet.android.apiclient.EventServiceClient;
import com.letsmeet.android.config.Constants;
import com.letsmeet.android.storage.LocalStore;
import com.letsmeet.server.eventService.model.RsvpRequest;
import com.letsmeet.server.eventService.model.RsvpResponse;

import java.io.IOException;

/**
 * Receiver for rsvp events from notification.
 */
public class RsvpBroadcastReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    Bundle data = intent.getExtras();
    long eventId = data.getLong(Constants.EVENT_ID_FOR_RSVP);
    String rsvpResponse = data.getString(Constants.RSVP_RESPONSE_FROM_NOTIFICATION);

    if (eventId == 0 || Strings.isNullOrEmpty(rsvpResponse)) {
      return;
    }
    LocalStore localStore = LocalStore.getInstance(context);
    setRsvpResponse(localStore.getUserId(), eventId, rsvpResponse, context);

    NotificationManager notificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.cancelAll();
  }

  private void setRsvpResponse(final long userId, final long eventId, final String rsvpResponse,
      final Context context) {
    new AsyncTask<Void, Void, RsvpResponse>() {
      @Override protected RsvpResponse doInBackground(Void... params) {
        RsvpRequest request = new RsvpRequest()
            .setUserId(userId)
            .setEventId(eventId)
            .setResponse(rsvpResponse);
        try {
          return EventServiceClient.getInstance(context).rsvpEvent(request);
        } catch (IOException e) {
          // Log to analytics
        }
        return null;
      }

      @Override protected void onPostExecute(RsvpResponse serverResponse) {
        if (serverResponse == null) {
          Toast.makeText(context,
              "Failed to connect server. Please check your network connection",
              Toast.LENGTH_SHORT).show();
        }
      }
    }.execute();
  }
}
