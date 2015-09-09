package com.letsmeet.server.apis.messages;

/**
 * Request for list events.
 */
public class ListEventsForUserRequest {

  private long userId;
  private boolean ignorePastEvents;

  public long getUserId() {
    return userId;
  }

  public ListEventsForUserRequest setUserId(long userId) {
    this.userId = userId;
    return this;
  }

  public boolean getIgnorePastEvents() {
    return ignorePastEvents;
  }

  public ListEventsForUserRequest setIgnorePastEvents(boolean ignorePastEvents) {
    this.ignorePastEvents = ignorePastEvents;
    return this;
  }
}
