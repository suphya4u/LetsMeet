package com.letsmeet.server.apis.messages;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Response for create event api.
 */
public class CreateOrEditEventResponse {

  private long eventId;

  private List<String> phoneNumbersNotYetRegistered;

  public CreateOrEditEventResponse() {
    phoneNumbersNotYetRegistered = Lists.newArrayList();
  }

  public long getEventId() {
    return eventId;
  }

  public CreateOrEditEventResponse setEventId(long eventId) {
    this.eventId = eventId;
    return this;
  }

  public List<String> getPhoneNumbersNotYetRegistered() {
    return phoneNumbersNotYetRegistered;
  }

  public CreateOrEditEventResponse addPhoneNumberNotYetRegistered(
      String phoneNumberNotYetRegistered) {
    this.phoneNumbersNotYetRegistered.add(phoneNumberNotYetRegistered);
    return this;
  }
}
