package com.letsmeet.android.gcm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.letsmeet.android.activity.HomeActivity;
import com.letsmeet.server.registration.Registration;
import com.letsmeet.server.registration.model.RegistrationRequest;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GcmRegistrationAsyncTask extends AsyncTask<Void, Void, String> {
  private static Registration regService = null;
  private GoogleCloudMessaging gcm;
  private Context context;
  private String name;
  private String phone;
  private ProgressDialog pd;

  // TODO: change to your own sender ID to Google Developers Console project number, as per instructions above
  private static final String SENDER_ID = "291490378971";

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
          // Need setRootUrl and setGoogleClientRequestInitializer only for local testing,
          // otherwise they can be skipped
          .setRootUrl("http://10.0.2.2:8080/_ah/api/")
          .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
            @Override
            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
                throws IOException {
              abstractGoogleClientRequest.setDisableGZipContent(true);
            }
          });
      // end of optional local run code

      regService = builder.build();
    }

    String msg;
    try {
      if (gcm == null) {
        gcm = GoogleCloudMessaging.getInstance(context);
      }
      String regId = gcm.register(SENDER_ID);
      msg = "Device registered, registration ID=" + regId;

      // You should send the registration ID to your server over HTTP,
      // so it can use GCM/HTTP or CCS to send messages to your app.
      // The request to your server should be authenticated if your app
      // is using accounts.
      RegistrationRequest request = new RegistrationRequest()
          .setName(name)
          .setPhoneNumber(phone)
          .setRegId(regId);
      regService.register(request).execute();

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
    Intent intent = new Intent(context, HomeActivity.class); // Your list's Intent
    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
    context.startActivity(intent);
  }
}