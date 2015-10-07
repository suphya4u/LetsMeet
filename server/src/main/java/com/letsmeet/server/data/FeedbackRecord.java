package com.letsmeet.server.data;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Data object stored as feedback.
 */
@Entity
public class FeedbackRecord {

  @Id
  Long id;
  private long userId;
  private String feedback;
  private long timestamp;
  private String appVersion;

  public long getId() {
    return id;
  }

  public long getUserId() {
    return userId;
  }

  public FeedbackRecord setUserId(long userId) {
    this.userId = userId;
    return this;
  }

  public String getFeedback() {
    return feedback;
  }

  public FeedbackRecord setFeedback(String feedback) {
    this.feedback = feedback;
    return this;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public FeedbackRecord setTimestamp(long timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  public String getAppVersion() {
    return appVersion;
  }

  public FeedbackRecord setAppVersion(String appVersion) {
    this.appVersion = appVersion;
    return this;
  }
}
