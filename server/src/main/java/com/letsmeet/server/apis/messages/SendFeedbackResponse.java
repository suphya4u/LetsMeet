package com.letsmeet.server.apis.messages;

/**
 * Response for send feedback API.
 */
public class SendFeedbackResponse {

  private boolean success;

  public boolean isSuccess() {
    return success;
  }

  public SendFeedbackResponse setSuccess(boolean success) {
    this.success = success;
    return this;
  }
}
