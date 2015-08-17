package com.letsmeet.android.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.common.base.Strings;

import java.util.Date;

/**
 * Local storage.
 */
public class LocalStore {

  private static final String PREF_FILE_KEY = "com.letsmeet.prefs";
  private static final String USER_ID_KEY = "USER_ID";
  private static final String USER_NAME_KEY = "USER_NAME";
  private static final String USER_PHONE_NUMBER_KEY = "USER_PHONE_NUMBER";
  private static final String USER_REGISTRATION_ID_KEY = "USER_REGISTRATION_ID";
  private static final String PHONE_VERIFICATION_STATUS_KEY = "PHONE_VERIFICATION_STATUS";

  private static final String VERIFICATION_STATUS_STARTED = "VERIFICATION_STARTED";
  private static final String VERIFICATION_STATUS_VERIFIED = "VERIFICATION_COMPLETE";
  private static final String SMS_VERIFICATION_CODE_KEY = "SMS_VERIFICATION_CODE";

  private Context context;

  public static LocalStore getInstance(Context context) {
    // TODO(suhas): can this be singleton?
    return new LocalStore(context);
  }

  private LocalStore(Context context) {
    this.context = context;
  }

  public void saveUserId(long userId) {
    SharedPreferences.Editor editor = getSharedPrefEditor();
    editor.putLong(USER_ID_KEY, userId);
    editor.commit();
  }

  public long getUserId() {
    SharedPreferences sharedPref = getSharedPrefs();
    return sharedPref.getLong(USER_ID_KEY, 0);
  }

  public void saveUserData(String name, String phoneNumber, String registrationId) {
    SharedPreferences.Editor editor = getSharedPrefEditor();
    editor.putString(USER_NAME_KEY, name);
    editor.putString(USER_PHONE_NUMBER_KEY, phoneNumber);
    editor.putString(USER_REGISTRATION_ID_KEY, registrationId);
    editor.commit();
  }

  public void setVerificationStarted() {
    SharedPreferences.Editor editor = getSharedPrefEditor();
    editor.putString(PHONE_VERIFICATION_STATUS_KEY, VERIFICATION_STATUS_STARTED);
    editor.commit();
  }

  public void setVerificationComplete() {
    SharedPreferences.Editor editor = getSharedPrefEditor();
    editor.putString(PHONE_VERIFICATION_STATUS_KEY, VERIFICATION_STATUS_VERIFIED);
    editor.commit();
  }

  public boolean isRegistered() {
    String verificationStatus = getStringProperty(PHONE_VERIFICATION_STATUS_KEY);
    return !Strings.isNullOrEmpty(verificationStatus);
  }

  public boolean isPhoneVerified() {
    String verificationStatus = getStringProperty(PHONE_VERIFICATION_STATUS_KEY);
    return VERIFICATION_STATUS_VERIFIED.equals(verificationStatus);
  }

  public String getUserName() {
    return getStringProperty(USER_NAME_KEY);
  }

  public String getUserPhoneNumber() {
    return getStringProperty(USER_PHONE_NUMBER_KEY);
  }

  public String getUserRegistrationId() {
    return getStringProperty(USER_REGISTRATION_ID_KEY);
  }

  public long getVerificationCode() {
    SharedPreferences prefs = getSharedPrefs();
    long verificationCode = prefs.getLong(SMS_VERIFICATION_CODE_KEY, 0);
    if (verificationCode == 0) {
      verificationCode = (new Date().getTime()) % 1000;
      SharedPreferences.Editor editor = getSharedPrefEditor();
      editor.putLong(SMS_VERIFICATION_CODE_KEY, verificationCode);
      editor.commit();
    }
    return verificationCode;
  }

  private String getStringProperty(String propertyName) {
    SharedPreferences prefs = getSharedPrefs();
    return prefs.getString(propertyName, "");
  }

  private SharedPreferences.Editor getSharedPrefEditor() {
    return getSharedPrefs().edit();
  }

  private SharedPreferences getSharedPrefs() {
    return context.getSharedPreferences(PREF_FILE_KEY, Context.MODE_PRIVATE);
  }
}
