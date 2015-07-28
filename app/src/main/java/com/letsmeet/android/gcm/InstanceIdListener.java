package com.letsmeet.android.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by suhas on 7/27/15.
 */
public class InstanceIdListener extends InstanceIDListenerService {

  @Override
  public void onTokenRefresh() {
    // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).

    // TODO(suhas): Implement this.
    // Fetch new token and update at server.
  }
}
