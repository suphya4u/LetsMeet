package com.letsmeet.server.data;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.letsmeet.server.apis.messages.RsvpRequest;

/**
 * event-user mapping.
 */
@Entity
public class Invites {

  public enum Response {
    YES,
    MAYBE,
    NO,
    NO_RESPONSE
  }

  @Id
  Long id;

  @Index
  private long eventId;

  @Index
  private long userId;

  private Response response;

  // This is required for OfyService. It fails with no suitable constructor found if this does not exist.
  public Invites() {}

  public Invites(long eventId, long userId) {
    this.eventId = eventId;
    this.userId = userId;
    this.response = Response.NO_RESPONSE;
  }

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

  public Invites setFromRsvpRequestEnum(RsvpRequest.ResponseEnum response) {
    switch (response) {
      case YES:
        setResponse(Response.YES);
        break;
      case NO:
        setResponse(Response.NO);
        break;
      case MAYBE:
        setResponse(Response.MAYBE);
    }
    return this;
  }
}
