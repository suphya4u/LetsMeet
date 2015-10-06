package com.letsmeet.android.verification;

import android.content.Context;
import android.telephony.SmsManager;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.letsmeet.android.common.DateTimeUtils;
import com.letsmeet.server.eventService.model.EventDetails;

import java.util.List;

/**
 * Sends invitation sms.
 */
public class ShareInvitation {

  private static final String APP_LINK_MESSAGE = "RSVP and details: https://goo.gl/tester";
  private static final int ADDRESS_CHAR_LIMIT = 60;
  private static final int EVENT_NAME_CHAR_LIMIT = 26;

  public void sendInvitationSms(Context context, List<String> phoneNumbers, EventDetails event) {
    SmsManager smsManager = SmsManager.getDefault();
    String smsText = getMessageText(context, event);

    for (String phoneNumber : phoneNumbers) {
      smsManager.sendTextMessage(phoneNumber,
          null /* scAddress */,
          smsText,
          null /* sendIntent */,
          null /* deliveryIntent */);
    }
  }

  private String getMessageText(Context context, EventDetails event) {
    String address = "";
    if (!Strings.isNullOrEmpty(event.getLocation().getPlaceAddress())) {
      if (event.getLocation().getPlaceAddress().length() > ADDRESS_CHAR_LIMIT) {
        address = event.getLocation().getPlaceAddress().substring(0, ADDRESS_CHAR_LIMIT - 2) + "..";
      } else {
        address = event.getLocation().getPlaceAddress();
      }
    }

    String eventName = "";
    if (!Strings.isNullOrEmpty(event.getName())) {
      int availableChars = EVENT_NAME_CHAR_LIMIT + ADDRESS_CHAR_LIMIT - address.length();
      if (event.getName().length() > availableChars) {
        eventName = event.getName().substring(0, availableChars - 2) + "..";
      } else {
        eventName = event.getName();
      }
    }

    return "Invitation: " + eventName + "\n"
        + DateTimeUtils.getDisplayDateTime(context, event.getEventTimeMillis()) + "\n"
        + address + "\n"
        + APP_LINK_MESSAGE;
  }
}
