package com.letsmeet.android.gcm;

import android.content.Context;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.letsmeet.android.config.Config;

import java.io.IOException;

public class GcmRegistrationHandler {

  public static String getRegistrationId(Context context) throws IOException {
    String msg;
    InstanceID instanceID = InstanceID.getInstance(context);
    String token = instanceID.getToken(Config.getGcmSenderId(),
        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
    return token;
  }
}