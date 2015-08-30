package com.letsmeet.server.apis.messages;

/**
 * Request for creating an event.
 */
public class CreateOrEditEventRequest {

  private EventDetails eventDetails;

  public EventDetails getEventDetails() {
    return eventDetails;
  }

  public CreateOrEditEventRequest setEvent(EventDetails eventDetails) {
    this.eventDetails = eventDetails;
    return this;
  }
}
