package com.letsmeet.android.apiclient;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.letsmeet.android.config.Config;
import com.letsmeet.server.eventService.EventService;
import com.letsmeet.server.eventService.model.CreateOrEditEventRequest;
import com.letsmeet.server.eventService.model.CreateOrEditEventResponse;
import com.letsmeet.server.eventService.model.EventDetails;
import com.letsmeet.server.eventService.model.FetchEventDetailsRequest;
import com.letsmeet.server.eventService.model.FetchEventDetailsResponse;
import com.letsmeet.server.eventService.model.ListEventsForUserRequest;
import com.letsmeet.server.eventService.model.ListEventsForUserResponse;
import com.letsmeet.server.eventService.model.RsvpRequest;
import com.letsmeet.server.eventService.model.RsvpResponse;

import java.io.IOException;

/**
 * Client for Event APIs.
 */
public class EventServiceClient {

  private static EventServiceClient selfInstance;
  private static EventService eventService;

  private EventServiceClient() {}

  public static EventServiceClient getInstance() {
    if (selfInstance == null) {
      selfInstance = new EventServiceClient();
    }
    return selfInstance;
  }

  public CreateOrEditEventResponse createEvent(CreateOrEditEventRequest request)
      throws IOException {
    EventService service = getService();
    return service.createOrEditEvent(request).execute();
  }

  public ListEventsForUserResponse listEvents(long userId, boolean ignorePastEvents)
      throws IOException {
    EventService service = getService();
    ListEventsForUserRequest request = new ListEventsForUserRequest()
        .setIgnorePastEvents(ignorePastEvents)
        .setUserId(userId);
    return service.eventsForUser(request).execute();
  }

  public EventDetails GetEventDetails(long eventId, long userId) throws IOException {
    FetchEventDetailsRequest request = new FetchEventDetailsRequest()
        .setEventId(eventId)
        .setUserId(userId);
    FetchEventDetailsResponse response = getService().fetchEventDetails(request).execute();
    return response.getEventDetails();
  }

  public RsvpResponse rsvpEvent(RsvpRequest request) throws IOException {
    EventService service = getService();
    return service.rsvpEvent(request).execute();
  }

  private EventService getService() {
    if (eventService != null) {
      return eventService;
    }
    // TODO(suhas): This is common code for all services. Move it to a common place.
    EventService.Builder serviceBuilder = new EventService.Builder(
        AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
        .setRootUrl(Config.getServerUrl());

    if (Config.isEmulator()) {
      serviceBuilder.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
        @Override
        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
            throws IOException {
          abstractGoogleClientRequest.setDisableGZipContent(true);
        }
      });
    }
    eventService = serviceBuilder.build();
    return eventService;
  }
}
