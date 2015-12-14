package com.letsmeet.server.apis.messages;

/**
 * Chats for an event.
 */
public class SendChatMessageResponse {

  private boolean success;
  private long timestamp;

  public boolean isSuccess() {
    return success;
  }

  public SendChatMessageResponse setSuccess(boolean success) {
    this.success = success;
    return this;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public SendChatMessageResponse setTimestamp(long timestamp) {
    this.timestamp = timestamp;
    return this;
  }
}
