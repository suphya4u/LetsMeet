package com.letsmeet.server.apis.messages;

/**
 * Request for event details.
 */
public class FetchEventDetailsRequest {

  private long eventId;

  public long getEventId() {
    return eventId;
  }

  public FetchEventDetailsRequest setEventId(long eventId) {
    this.eventId = eventId;
    return this;
  }
}
