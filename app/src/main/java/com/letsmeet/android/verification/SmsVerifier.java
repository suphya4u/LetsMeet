package com.letsmeet.android.verification;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;

import com.letsmeet.android.config.Config;
import com.letsmeet.android.config.Constants;
import com.letsmeet.android.storage.LocalStore;

/**
 * Phone number verifier.
 */
public class SmsVerifier {

  private static SmsVerifier instance;

  public static SmsVerifier getInstance() {
    if (instance == null) {
      instance = new SmsVerifier();
    }
    return instance;
  }

  public void verifyPhoneNumber(Context context, String phoneNumber) {
    LocalStore localStore = LocalStore.getInstance(context);
    long verificationCode = localStore.getVerificationCode();
    SmsManager smsManager = SmsManager.getDefault();

    enableSmsReceiver(context);

    // TODO(suhas): Use sentIntent and deliveryIntent to identify failures and delivery.
    smsManager.sendTextMessage(phoneNumber,
        null /* scAddress */,
        Constants.SMS_TEXT_PREFIX + verificationCode,
        null /* sendIntent */,
        null /* deliveryIntent */);

    if (Config.isEmulator() && !Config.isVerificationRequiredForEmulator()) {
      new SmsReceiver().fakeVerification(context);
    }
  }

  private void enableSmsReceiver(Context context) {
    ComponentName receiver = new ComponentName(context, SmsReceiver.class);
    PackageManager pm = context.getPackageManager();

    pm.setComponentEnabledSetting(receiver,
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        PackageManager.DONT_KILL_APP);
  }
}
