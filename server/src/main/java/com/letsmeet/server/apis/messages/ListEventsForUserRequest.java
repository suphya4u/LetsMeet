package com.letsmeet.server.apis.messages;

/**
 * Request for list events.
 */
public class ListEventsForUserRequest {

  private long userId;

  public long getUserId() {
    return userId;
  }

  public ListEventsForUserRequest setUserId(long userId) {
    this.userId = userId;
    return this;
  }
}
