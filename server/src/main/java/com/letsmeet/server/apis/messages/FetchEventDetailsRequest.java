package com.letsmeet.server.apis.messages;

/**
 * Request for event details.
 */
public class FetchEventDetailsRequest {

  private long eventId;
  private long userId;

  public long getEventId() {
    return eventId;
  }

  public FetchEventDetailsRequest setEventId(long eventId) {
    this.eventId = eventId;
    return this;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }
}
