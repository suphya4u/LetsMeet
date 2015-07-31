package com.letsmeet.server.apis.messages;

import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

/**
 * Response for ListEvents
 */
// TODO(suhas): All fields here are repeated from CreateEventRequest. Consider putting all those in a data class.
public class ListEventsForUserResponse {

  List<EventDetails> eventsList;

  public ListEventsForUserResponse() {
    eventsList = Lists.newArrayList();
  }

  public List<EventDetails> getEventsList() {
    return eventsList;
  }

  public ListEventsForUserResponse addEvent(EventDetails event) {
    eventsList.add(event);
    return this;
  }

  public ListEventsForUserResponse addAllEvents(Collection<EventDetails> events) {
    eventsList.addAll(events);
    return this;
  }
}
