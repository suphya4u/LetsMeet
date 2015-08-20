package com.letsmeet.server.apis.messages;

/**
 * Request for RSVP.
 */
public class RsvpRequest {

  public enum ResponseEnum {
    YES,
    MAYBE,
    NO,
  }

  private long userId;
  private long eventId;
  private ResponseEnum response;

  public long getUserId() {
    return userId;
  }

  public RsvpRequest setUserId(long userId) {
    this.userId = userId;
    return this;
  }

  public long getEventId() {
    return eventId;
  }

  public void setEventId(long eventId) {
    this.eventId = eventId;
  }

  public ResponseEnum getResponse() {
    return response;
  }

  public RsvpRequest setResponse(ResponseEnum response) {
    this.response = response;
    return this;
  }
}
