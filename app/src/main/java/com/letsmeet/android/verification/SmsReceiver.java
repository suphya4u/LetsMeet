package com.letsmeet.android.verification;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.letsmeet.android.apiclient.RegistrationServiceClient;
import com.letsmeet.android.config.Constants;
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
    Toast.makeText(context, "Sms Received", Toast.LENGTH_LONG).show();
    LocalStore localStore = LocalStore.getInstance(context);
    if (localStore.isPhoneVerified()) {
      // TODO(suhas): Phone already verified but somehow receiver not yet stopped. Stop receiver.
    }
    final Bundle intentExtras = intent.getExtras();
    if (intentExtras == null) {
      return;
    }
    Object[] smsObjects = (Object[]) intentExtras.get(SMS_BUNDLE);
    for (Object sms : smsObjects) {
      SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms);

      String smsBody = smsMessage.getMessageBody();
      String address = smsMessage.getOriginatingAddress();
      if (!isVerificationMessage(context, address, smsBody)) {
        return;
      }

      String name = localStore.getUserName();
      String phoneNumber = localStore.getUserPhoneNumber();
      String registrationId = localStore.getUserRegistrationId();
      final RegistrationRequest registrationRequest = new RegistrationRequest()
          .setName(name)
          .setPhoneNumber(phoneNumber)
          .setRegId(registrationId);
      registerUser(registrationRequest, context);

      // Verification complete. Broadcast.
      Intent broadcast = new Intent();
      broadcast.setAction(Constants.VERIFICATION_COMPLETE_BROADCAST);
      context.sendBroadcast(broadcast);
      disableSmsReceiver(context);
    }
  }

  private boolean isVerificationMessage(Context context, String sender, String smsBody) {
    LocalStore localStore = LocalStore.getInstance(context);
    long verificationCode = localStore.getVerificationCode();
    if (smsBody.contains(Constants.SMS_TEXT_PREFIX)
        && smsBody.contains(String.valueOf(verificationCode))) {
      // TODO(suhas): Verify sender of message matches the phone number in record
      // (maybe without country code).
      return true;
    }
    return false;
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

  private void disableSmsReceiver(Context context) {
    ComponentName receiver = new ComponentName(context, SmsReceiver.class);
    PackageManager pm = context.getPackageManager();

    pm.setComponentEnabledSetting(receiver,
        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        PackageManager.DONT_KILL_APP);
  }
}
