package com.letsmeet.android.verification;

import android.content.Context;
import android.telephony.SmsManager;

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
    // TODO(suhas): Use sentIntent and deliveryIntent to identify failures and delivery.
    smsManager.sendTextMessage(phoneNumber,
        null /* scAddress */,
        Constants.SMS_TEXT_PREFIX + verificationCode,
        null /* sendIntent */,
        null /* deliveryIntent */);
  }
}
