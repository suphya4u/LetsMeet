package com.letsmeet.android.config;

/**
 * Configure app.
 */
public class Config {

  public static final boolean IS_SERVER_RUNNING_LOCALLY = true;
  public static final String SERVER_URL = "https://oval-botany-101702.appspot.com/_ah/api/";
  public static final String LOCAL_SERVER_FOR_EMULATOR = "http://10.0.2.2:8080/_ah/api/";

  // TODO(suhas): Find better way.
  public static final String LOCAL_SERVER_FOR_REAL_DEVICE = "http://192.168.1.3:8080/_ah/api/";
  public static final boolean IS_EMULATOR = true;

  public static final boolean IS_VERIFICATION_REQUIRED_FOR_EMULATOR = false;

  // TODO(suhas): Update this when server project id finalizes.
  private static final String GCM_SENDER_ID = "291490378971";

  public static boolean isEmulator() {
    return IS_EMULATOR;
  }

  public static String getGcmSenderId() {
    return GCM_SENDER_ID;
  }

  public static String getServerUrl() {
    if (IS_SERVER_RUNNING_LOCALLY) {
      if (IS_EMULATOR) {
        return LOCAL_SERVER_FOR_EMULATOR;
      }
      return LOCAL_SERVER_FOR_REAL_DEVICE;
    }
    return SERVER_URL;
  }

  public static boolean isVerificationRequiredForEmulator() {
    return IS_VERIFICATION_REQUIRED_FOR_EMULATOR;
  }
}
