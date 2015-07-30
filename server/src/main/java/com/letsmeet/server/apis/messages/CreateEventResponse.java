package com.letsmeet.server.apis.messages;

/**
 * Response for create event api.
 */
public class CreateEventResponse {

  private long eventId;

  public long getEventId() {
    return eventId;
  }

  public CreateEventResponse setEventId(long eventId) {
    this.eventId = eventId;
    return this;
  }
}
