package com.letsmeet.android.common;

/**
 * Enum list used to identify event list type.
 */
public enum EventListType {

  UPCOMING(1),
  ALL(2);

  private final int id;

  EventListType(int id) {
    this.id = id;
  }
}
