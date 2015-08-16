package com.letsmeet.android.verification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.letsmeet.android.activity.HomeActivity;
import com.letsmeet.android.apiclient.RegistrationServiceClient;
import com.letsmeet.android.storage.LocalStore;
import com.letsmeet.server.registration.model.RegistrationRequest;
import com.letsmeet.server.registration.model.RegistrationResponse;

/**
 * Receive sms for verification.
 */
public class SmsReceiver extends BroadcastReceiver {

  public static final String SMS_BUNDLE = "pdus";

  // TODO(suhas): Stop receiver once phone number is verified.
  @Override public void onReceive(Context context, Intent intent) {
    final Bundle intentExtras = intent.getExtras();
    if (intentExtras != null) {
      Object[] smsObjects = (Object[]) intentExtras.get(SMS_BUNDLE);
      String smsMessageStr = "";
      for (Object sms : smsObjects) {
        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms);

        String smsBody = smsMessage.getMessageBody();
        String address = smsMessage.getOriginatingAddress();

        // TODO(suhas): Verify sender of message matches the phone number in record
        // (maybe without country code).
        // TODO(suhas): Verify verification code matches with the one sent out.
        smsMessageStr += "SMS From: " + address + "\n";
        smsMessageStr += smsBody + "\n";
      }
      Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();

      LocalStore localStore = LocalStore.getInstance(context);
      String name = localStore.getUserName();
      String phoneNumber = localStore.getUserPhoneNumber();
      String registrationId = localStore.getUserRegistrationId();
      final RegistrationRequest registrationRequest = new RegistrationRequest()
          .setName(name)
          .setPhoneNumber(phoneNumber)
          .setRegId(registrationId);
      registerUser(registrationRequest, context);
    }
  }

  private void registerUser(final RegistrationRequest registrationRequest, final Context context) {
    new AsyncTask<Void, Void, Void>() {

      @Override protected Void doInBackground(Void... params) {
        RegistrationResponse response = RegistrationServiceClient.getInstance()
            .registerUser(registrationRequest);
        LocalStore localStore = LocalStore.getInstance(context);
        localStore.saveUserId(response.getUserId());
        localStore.setVerificationComplete();
        return null;
      }
    }.execute();
  }
}
