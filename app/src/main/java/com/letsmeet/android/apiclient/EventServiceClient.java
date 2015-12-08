package com.letsmeet.android.apiclient;

import android.content.Context;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.letsmeet.android.apiclient.cache.EventDetailsCache;
import com.letsmeet.android.apiclient.cache.EventListCache;
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
// TODO: Caching should in a wrapper over ServiceClient than including it in here.
public class EventServiceClient {

  private static EventServiceClient selfInstance;
  private static EventService eventService;

  private final Context context;

  private EventServiceClient(Context context) {
    this.context = context;
  }

  public static EventServiceClient getInstance(Context context) {
    if (selfInstance == null) {
      selfInstance = new EventServiceClient(context);
    }
    return selfInstance;
  }

  public CreateOrEditEventResponse createEvent(CreateOrEditEventRequest request)
      throws IOException {
    EventService service = getService();
    CreateOrEditEventResponse response = service.createOrEditEvent(request).execute();
    invalidateEventListCache();
    invalidateEventDetailsCache(response.getEventId());
    return response;
  }

  // TODO: These two methods are not actually required if caching is moved to a wrapper. See top.
  public ListEventsForUserResponse listEventsFromCache(long userId, boolean ignorePastEvents)
      throws IOException {
    return new EventListCache(context).getEvents(ignorePastEvents);
  }

  public ListEventsForUserResponse listFreshEvents(long userId, boolean ignorePastEvents)
      throws IOException {
    EventListCache cache = new EventListCache(context);
    cache.invalidateAll();
    return cache.getEvents(ignorePastEvents);
  }

  public EventDetails getEventDetailsWithCaching(long eventId, long userId) throws IOException {
    return new EventDetailsCache(context).getEventDetails(eventId);
  }

  public ListEventsForUserResponse fetchEventsList(long userId, boolean ignorePastEvents)
      throws IOException {
    EventService service = getService();
    ListEventsForUserRequest request = new ListEventsForUserRequest()
        .setIgnorePastEvents(ignorePastEvents)
        .setUserId(userId);
    return service.eventsForUser(request).execute();
  }

  public EventDetails fetchEventDetails(long eventId, long userId) throws IOException {
    FetchEventDetailsRequest request = new FetchEventDetailsRequest()
        .setEventId(eventId)
        .setUserId(userId);
    FetchEventDetailsResponse response = getService().fetchEventDetails(request).execute();
    return response.getEventDetails();
  }

  public RsvpResponse rsvpEvent(RsvpRequest request) throws IOException {
    EventService service = getService();
    RsvpResponse response = service.rsvpEvent(request).execute();
    invalidateEventDetailsCache(request.getEventId());
    invalidateEventListCache();
    return response;
  }

  private void invalidateEventListCache() {
    new EventListCache(context).invalidateAll();
  }

  private void invalidateEventDetailsCache(long eventId) {
    new EventDetailsCache(context).invalidate(eventId);
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
