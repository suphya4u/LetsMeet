package com.letsmeet.server.apis.messages;

/**
 * Created by suhas on 7/24/15.
 */
public class RegistrationResponse {

  private boolean isSuccess;

  public boolean isSuccess() {
    return isSuccess;
  }

  public RegistrationResponse setIsSuccess(boolean isSuccess) {
    this.isSuccess = isSuccess;
    return this;
  }
}
