package com.letsmeet.server.apis.messages;

/**
 * Request for send feedback API.
 */
public class SendFeedbackRequest {

  private long userId;
  private String feedback;
  private String appVersion;

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public String getFeedback() {
    return feedback;
  }

  public void setFeedback(String feedback) {
    this.feedback = feedback;
  }

  public String getAppVersion() {
    return appVersion;
  }

  public void setAppVersion(String appVersion) {
    this.appVersion = appVersion;
  }
}
