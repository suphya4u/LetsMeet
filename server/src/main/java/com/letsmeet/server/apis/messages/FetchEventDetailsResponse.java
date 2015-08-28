package com.letsmeet.server.apis.messages;

/**
 * Response for Event details.
 */
public class FetchEventDetailsResponse {

  private EventDetails eventDetails;

  public EventDetails getEventDetails() {
    return eventDetails;
  }

  public FetchEventDetailsResponse setEventDetails(EventDetails eventDetails) {
    this.eventDetails = eventDetails;
    return this;
  }
}
