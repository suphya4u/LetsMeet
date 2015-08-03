package com.letsmeet.server.apis.messages;

/**
 * Response for registration API.
 */
public class RegistrationResponse {

  private boolean isSuccess;

  private long userId;

  public boolean isSuccess() {
    return isSuccess;
  }

  public RegistrationResponse setIsSuccess(boolean isSuccess) {
    this.isSuccess = isSuccess;
    return this;
  }

  public long getUserId() {
    return userId;
  }

  public RegistrationResponse setUserId(long userId) {
    this.userId = userId;
    return this;
  }
}
