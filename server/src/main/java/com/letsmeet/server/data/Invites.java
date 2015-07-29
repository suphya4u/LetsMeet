package com.letsmeet.server.data;

import com.googlecode.objectify.annotation.Index;

/**
 * event-user mapping.
 */
public class Invites {

  public enum Response {
    YES,
    MAYBE,
    NO,
    NO_RESPONSE
  }

  @Index
  private long eventId;

  @Index
  private long userId;

  private Response response;

  public long getEventId() {
    return eventId;
  }

  public Invites setEventId(long eventId) {
    this.eventId = eventId;
    return this;
  }

  public long getUserId() {
    return userId;
  }

  public Invites setUserId(long userId) {
    this.userId = userId;
    return this;
  }

  public Response getResponse() {
    return response;
  }

  public Invites setResponse(Response response) {
    this.response = response;
    return this;
  }
}
