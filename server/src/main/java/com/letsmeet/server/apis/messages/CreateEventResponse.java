package com.letsmeet.server.apis.messages;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Response for create event api.
 */
public class CreateEventResponse {

  private long eventId;

  private List<String> phoneNumbersNotYetRegistered;

  public CreateEventResponse() {
    phoneNumbersNotYetRegistered = Lists.newArrayList();
  }

  public long getEventId() {
    return eventId;
  }

  public CreateEventResponse setEventId(long eventId) {
    this.eventId = eventId;
    return this;
  }

  public List<String> getPhoneNumbersNotYetRegistered() {
    return phoneNumbersNotYetRegistered;
  }

  public CreateEventResponse addPhoneNumberNotYetRegistered(
      String phoneNumberNotYetRegistered) {
    this.phoneNumbersNotYetRegistered.add(phoneNumberNotYetRegistered);
    return this;
  }
}
