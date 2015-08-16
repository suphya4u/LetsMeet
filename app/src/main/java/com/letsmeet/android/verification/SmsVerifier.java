package com.letsmeet.android.verification;

import android.telephony.SmsManager;

/**
 * Phone number verifier.
 */
public class SmsVerifier {

  // TODO(suhas): Use dynamic message with verification code to check.
  private static final String SMS_TEXT = "Test Sms.";

  private static SmsVerifier instance;

  public static SmsVerifier getInstance() {
    if (instance == null) {
      instance = new SmsVerifier();
    }
    return instance;
  }

  public void verifyPhoneNumber(String phoneNumber) {
    SmsManager smsManager = SmsManager.getDefault();
    // TODO(suhas): Use sentIntent and deliveryIntent to identify failures and delivery.
    smsManager.sendTextMessage(phoneNumber,
        null /* scAddress */,
        SMS_TEXT,
        null /* sendIntent */,
        null /* deliveryIntent */);
  }
}
