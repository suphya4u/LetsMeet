package com.letsmeet.android.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GcmMessageHandler extends GcmListenerService {

  public GcmMessageHandler() {
  }

  @Override
  public void onMessageReceived(String from, Bundle data) {
    if (data != null && !data.isEmpty()) {  // has effect of unparcelling Bundle
      // Since we're not using two way messaging, this is all we really to check for
      showToast("From: " + from + "\nMsg: " + data.getString("message"));
    }
  }

  protected void showToast(final String message) {
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
      }
    });
  }
}