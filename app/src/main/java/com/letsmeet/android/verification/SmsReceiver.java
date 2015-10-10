package com.letsmeet.android.verification;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.letsmeet.android.apiclient.RegistrationServiceClient;
import com.letsmeet.android.config.Constants;
import com.letsmeet.android.storage.LocalStore;
import com.letsmeet.server.registration.model.RegistrationRequest;
import com.letsmeet.server.registration.model.RegistrationResponse;

import java.io.IOException;

/**
 * Receive sms for verification.
 */
public class SmsReceiver extends BroadcastReceiver {

  public static final String SMS_BUNDLE = "pdus";

  @Override public void onReceive(Context context, Intent intent) {
    Toast.makeText(context, "Sms Received", Toast.LENGTH_LONG).show();
    LocalStore localStore = LocalStore.getInstance(context);
    if (localStore.isPhoneVerified()) {
      disableSmsReceiver(context);
    }
    final Bundle intentExtras = intent.getExtras();
    if (intentExtras == null) {
      return;
    }
    Object[] smsObjects = (Object[]) intentExtras.get(SMS_BUNDLE);
    if (smsObjects != null) {
      for (Object sms : smsObjects) {
        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms);

        String smsBody = smsMessage.getMessageBody();
        String address = smsMessage.getOriginatingAddress();
        if (isVerificationMessage(context, address, smsBody)) {
          verificationComplete(localStore, context);
        }
      }
    }
  }

  void fakeVerification(Context context) {
    verificationComplete(LocalStore.getInstance(context), context);
  }

  private void verificationComplete(LocalStore localStore, Context context) {
    String name = localStore.getUserName();
    String phoneNumber = localStore.getUserPhoneNumber();
    String registrationId = localStore.getUserRegistrationId();
    final RegistrationRequest registrationRequest = new RegistrationRequest()
        .setName(name)
        .setPhoneNumber(phoneNumber)
        .setRegId(registrationId);
    registerUser(registrationRequest, context);
  }

  private boolean isVerificationMessage(Context context, String sender, String smsBody) {
    LocalStore localStore = LocalStore.getInstance(context);
    long verificationCode = localStore.getVerificationCode();
    if (smsBody.contains(Constants.SMS_TEXT_PREFIX)
        && smsBody.contains(String.valueOf(verificationCode))) {
      String userPhoneNumber = localStore.getUserPhoneNumber();
      return PhoneNumberUtils.compare(userPhoneNumber, sender);
    }
    return false;
  }

  private void registerUser(final RegistrationRequest registrationRequest, final Context context) {
    new AsyncTask<Void, Void, RegistrationResponse>() {

      @Override protected RegistrationResponse doInBackground(Void... params) {
        try {
          return RegistrationServiceClient.getInstance()
              .registerUser(registrationRequest);
        } catch (IOException e) {
          // Log to analytics.
        }
        return null;
      }

      @Override protected void onPostExecute(RegistrationResponse registrationResponse) {
        super.onPostExecute(registrationResponse);

        if (registrationResponse == null) {
          // TODO: Registration failed here. But verification message is already received.
          // So phone number is verified. We need a new status as phone verified but server
          // registration pending. So maybe de-couple these to tasks.
          // Main Actvity should check server registration complete or not and registration activity
          // should take appropriate steps based on status.
          // TODO: Also fix the toast message when this is fixed.
          Toast.makeText(context,
              "Failed to connect server. Please check your network connection. " +
                  "You may need to register again.",
              Toast.LENGTH_SHORT).show();
          return;
        }

        LocalStore localStore = LocalStore.getInstance(context);
        localStore.saveUserId(registrationResponse.getUserId());
        localStore.setVerificationComplete();

        // Verification complete. Broadcast.
        Intent broadcast = new Intent();
        broadcast.setAction(Constants.VERIFICATION_COMPLETE_BROADCAST);
        context.sendBroadcast(broadcast);
        disableSmsReceiver(context);
      }
    }.execute();
  }

  private void disableSmsReceiver(Context context) {
    ComponentName receiver = new ComponentName(context, SmsReceiver.class);
    PackageManager pm = context.getPackageManager();

    pm.setComponentEnabledSetting(receiver,
        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        PackageManager.DONT_KILL_APP);
  }
}
