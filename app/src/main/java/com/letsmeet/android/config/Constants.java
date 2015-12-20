package com.letsmeet.android.config;

import com.letsmeet.android.R;

/**
 * Constants used
 */
public class Constants {

  public static final String SMS_TEXT_PREFIX = "This is your verification code for Lets Meet App : ";

  public static final String VERIFICATION_COMPLETE_BROADCAST = "VERIFICATION_COMPLETE";
  public static final String NEW_CHAT_MESSAGE_BROADCAST = "NEW_CHAT_MESSAGE";

  public static final String RSVP_FROM_NOTIFICATION_BROADCAST =
      "com.letsmeet.android.rsvp.UPDATE_RESPONSE";
  public static final String RSVP_RESPONSE_FROM_NOTIFICATION = "RSVP_RESPONSE_FROM_NOTIFICATION";

  public static final String INTENT_EVENT_ID_KEY = "EVENT_ID";
  public static final String INTENT_EVENT_NAME_KEY = "EVENT_NAME";

  public static final String NOTIFICATION_TYPE_KEY = "NOTIFICATION_TYPE";
  public static final String NOTIFICATION_TYPE_NEW_EVENT = "NEW_EVENT";
  public static final String NOTIFICATION_TYPE_NEW_CHAT = "NEW_CHAT";

  public static final String NOTIFICATION_EVENT_NAME_KEY = "EVENT_NAME";
  public static final String NOTIFICATION_EVENT_DETAILS_KEY = "EVENT_DETAILS";
  public static final String NOTIFICATION_EVENT_TIME_KEY = "EVENT_TIME";
  public static final String NOTIFICATION_EVENT_ID_KEY = "EVENT_ID";

  public static final String NOTIFICATION_FROM_PHONE_KEY = "FROM_PHONE_NUMBER";
  public static final String NOTIFICATION_CHAT_TIME_KEY = "CHAT_MESSAGE_SENT_TIME";
  public static final String NOTIFICATION_CHAT_MESSAGE = "CHAT_MESSAGE";
  public static final String NOTIFICATION_CHAT_EVENT_NAME = "EVENT_NAME";

  public static final String EVENT_ID_FOR_RSVP = "EVENT_ID_FOR_RSVP";

  public static final String DEFAULT_CONTACT_IMAGE = String.valueOf(R.mipmap.ic_launcher);
}
