package com.letsmeet.server.apis.messages;

/**
 * Created by suhas on 7/30/15.
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
