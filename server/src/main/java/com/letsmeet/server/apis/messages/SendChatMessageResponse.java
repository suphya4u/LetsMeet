package com.letsmeet.server.apis.messages;

/**
 * Chats for an event.
 */
public class SendChatMessageResponse {

  private boolean success;

  public boolean isSuccess() {
    return success;
  }

  public SendChatMessageResponse setSuccess(boolean success) {
    this.success = success;
    return this;
  }
}
