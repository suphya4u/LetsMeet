package com.letsmeet.android.verification;

import android.telephony.SmsManager;

import com.letsmeet.server.eventService.model.EventDetails;

import java.util.List;

/**
 * Sends invitation sms.
 */
public class ShareInvitation {

  public void sendInvitationSms(List<String> phoneNumbers, EventDetails event) {
    SmsManager smsManager = SmsManager.getDefault();
    String smsText = getMessageText(event);

    for (String phoneNumber : phoneNumbers) {
      smsManager.sendTextMessage(phoneNumber,
          null /* scAddress */,
          smsText,
          null /* sendIntent */,
          null /* deliveryIntent */);
    }
  }

  private String getMessageText(EventDetails event) {
    // TODO(suhas): Make sure we truncate the text to max sms limit or use multi sender.
    return "Invitation: " + event.getName() + "\n" + "Other details here..";
  }
}
