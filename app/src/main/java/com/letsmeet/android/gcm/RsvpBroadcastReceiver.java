package com.letsmeet.android.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.letsmeet.android.apiclient.EventServiceClient;
import com.letsmeet.android.config.Constants;
import com.letsmeet.android.storage.LocalStore;
import com.letsmeet.server.eventService.model.RsvpRequest;

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
    setRsvpResponse(localStore.getUserId(), eventId, rsvpResponse);
  }

  private void setRsvpResponse(final long userId, final long eventId, final String rsvpResponse) {
    new AsyncTask<Void, Void, Void>() {
      @Override protected Void doInBackground(Void... params) {
        RsvpRequest request = new RsvpRequest()
            .setUserId(userId)
            .setEventId(eventId)
            .setResponse(rsvpResponse);
        EventServiceClient.getInstance().rsvpEvent(request);
        return null;
      }
    }.execute();
  }
}
