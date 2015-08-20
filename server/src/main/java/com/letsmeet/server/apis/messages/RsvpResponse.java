package com.letsmeet.server.apis.messages;

/**
 * Response for RSVP.
 */
public class RsvpResponse {
  private boolean success;

  public boolean isSuccess() {
    return success;
  }

  public RsvpResponse setSuccess(boolean success) {
    this.success = success;
    return this;
  }
}
