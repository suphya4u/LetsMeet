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
import com.letsmeet.android.config.Config;
import com.letsmeet.android.storage.LocalStore;
import com.letsmeet.server.registration.Registration;
import com.letsmeet.server.registration.model.RegistrationRequest;
import com.letsmeet.server.registration.model.RegistrationResponse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GcmRegistrationAsyncTask extends AsyncTask<Void, Void, String> {
  private static Registration regService = null;
  private Context context;
  private String name;
  private String phone;
  private ProgressDialog pd;

  public GcmRegistrationAsyncTask(Context context, String name, String phone) {
    this.context = context;
    this.name = name;
    this.phone = phone;
    this.pd = new ProgressDialog(context);
  }

  @Override
  protected void onPreExecute() {
    pd.show();
  }

  @Override
  protected String doInBackground(Void... params) {
    if (regService == null) {
      Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(),
          new AndroidJsonFactory(), null)
          .setRootUrl(Config.getServerUrl());

      if (Config.isEmulator()) {
        builder.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
          @Override
          public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
              throws IOException {
            abstractGoogleClientRequest.setDisableGZipContent(true);
          }
        });
      }

      regService = builder.build();
    }

    String msg;
    try {
      InstanceID instanceID = InstanceID.getInstance(context);
      String token = instanceID.getToken(Config.getGcmSenderId(),
          GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
      msg = "Device registered, registration ID=" + token;

      // You should send the registration ID to your server over HTTP,
      // so it can use GCM/HTTP or CCS to send messages to your app.
      // The request to your server should be authenticated if your app
      // is using accounts.
      RegistrationRequest request = new RegistrationRequest()
          .setName(name)
          .setPhoneNumber(phone)
          .setRegId(token);
      RegistrationResponse response = regService.register(request).execute();
      LocalStore localStore = LocalStore.getInstance(context);
      localStore.saveUserId(response.getUserId());

    } catch (IOException ex) {
      ex.printStackTrace();
      msg = "Error: " + ex.getMessage();
    }
    return msg;
  }

  @Override
  protected void onPostExecute(String msg) {
    pd.hide();
    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    Logger.getLogger("REGISTRATION").log(Level.INFO, msg);

    Intent intent = new Intent(context, HomeActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    context.startActivity(intent);
  }
}