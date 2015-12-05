package com.letsmeet.server.apis.messages;

/**
 * Request to get chats for an even.
 */
public class SendChatMessageRequest {

  private long userId;
  private long eventId;
  private String message;

  public long getUserId() {
    return userId;
  }

  public SendChatMessageRequest setUserId(long userId) {
    this.userId = userId;
    return this;
  }

  public long getEventId() {
    return eventId;
  }

  public SendChatMessageRequest setEventId(long eventId) {
    this.eventId = eventId;
    return this;
  }

  public String getMessage() {
    return message;
  }

  public SendChatMessageRequest setMessage(String message) {
    this.message = message;
    return this;
  }
}
