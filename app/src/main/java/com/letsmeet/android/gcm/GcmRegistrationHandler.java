package com.letsmeet.android.gcm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.letsmeet.android.activity.HomeActivity;
import com.letsmeet.android.apiclient.RegistrationServiceClient;
import com.letsmeet.android.config.Config;
import com.letsmeet.android.storage.LocalStore;
import com.letsmeet.server.registration.Registration;
import com.letsmeet.server.registration.model.RegistrationRequest;
import com.letsmeet.server.registration.model.RegistrationResponse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GcmRegistrationHandler {

  public static String getRegistrationId(Context context) throws IOException {
    String msg;
    InstanceID instanceID = InstanceID.getInstance(context);
    String token = instanceID.getToken(Config.getGcmSenderId(),
        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
    return token;
  }
}