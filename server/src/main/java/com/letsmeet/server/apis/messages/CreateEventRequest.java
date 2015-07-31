package com.letsmeet.server.apis.messages;

import com.google.appengine.api.datastore.GeoPt;

import java.util.Date;
import java.util.List;

/**
 * Request for creating an event.
 */
public class CreateEventRequest {

  private EventDetails eventDetails;

  public EventDetails getEventDetails() {
    return eventDetails;
  }

  public CreateEventRequest setEvent(EventDetails eventDetails) {
    this.eventDetails = eventDetails;
    return this;
  }
}
