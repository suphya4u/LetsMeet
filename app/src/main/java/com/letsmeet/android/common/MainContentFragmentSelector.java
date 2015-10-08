package com.letsmeet.android.common;

/**
 * Enum list used to identify event list type.
 */
public enum MainContentFragmentSelector {

  UPCOMING_EVENTS(1),
  ALL_EVENTS(2),
  SEND_FEEDBACK(3);

  private final int id;

  MainContentFragmentSelector(int id) {
    this.id = id;
  }
}
