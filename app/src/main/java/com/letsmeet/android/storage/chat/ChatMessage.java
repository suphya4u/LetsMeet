package com.letsmeet.android.storage.chat;

import android.provider.BaseColumns;

/**
 * Chat message
 */
public class ChatMessage implements BaseColumns {

  private enum Status {
    NA,
    SUCCESS,
    PENDING,
    FAILED
  }

  private String senderPhoneNumber;
  private long eventId;
  private long timeSent;
  private String message;
  private boolean isMyMessage;
  private Status status = Status.NA;

  public String getSenderPhoneNumber() {
    return senderPhoneNumber;
  }

  public ChatMessage setSenderPhoneNumber(String senderPhoneNumber) {
    this.senderPhoneNumber = senderPhoneNumber;
    return this;
  }

  public long getEventId() {
    return eventId;
  }

  public ChatMessage setEventId(long eventId) {
    this.eventId = eventId;
    return this;
  }

  public long getTimeSent() {
    return timeSent;
  }

  public ChatMessage setTimeSent(long timeSent) {
    this.timeSent = timeSent;
    return this;
  }

  public String getMessage() {
    return message;
  }

  public ChatMessage setMessage(String message) {
    this.message = message;
    return this;
  }

  public boolean isMyMessage() {
    return isMyMessage;
  }

  public ChatMessage setIsMyMessage(boolean isMyMessage) {
    this.isMyMessage = isMyMessage;
    return this;
  }

  public ChatMessage markPending() {
    status = Status.PENDING;
    return this;
  }

  public ChatMessage markComplete() {
    status = Status.SUCCESS;
    return this;
  }

  public ChatMessage markFailed() {
    status = Status.FAILED;
    return this;
  }

  public int getStatusNum() {
    return status.ordinal();
  }
}
