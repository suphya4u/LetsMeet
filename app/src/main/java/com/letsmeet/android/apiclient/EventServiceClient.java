package com.letsmeet.android.apiclient;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.letsmeet.server.eventService.EventService;
import com.letsmeet.server.eventService.model.CreateEventRequest;
import com.letsmeet.server.eventService.model.CreateEventResponse;

import java.io.IOException;

/**
 * Client for Event APIs.
 */
public class EventServiceClient {

  private static EventServiceClient selfInstance;
  private static EventService eventService;

  private EventServiceClient() {};

  public static EventServiceClient getInstance() {
    if (selfInstance == null) {
      selfInstance = new EventServiceClient();
    }
    return selfInstance;
  }

  public CreateEventResponse createEvent(CreateEventRequest request) {
    try {
      EventService service = getService();
      return service.createEvent(request).execute();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private EventService getService() {
    if (eventService != null) {
      return eventService;
    }
    // TODO(suhas): This is common code for all services. Move it to a common place.
    EventService.Builder serviceBuilder = new EventService.Builder(
        AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);
    // Need setRootUrl and setGoogleClientRequestInitializer only for local testing.
    // TODO(suhas): This is required only for runninly locally. Maybe create a flag to identify
    // local run.
    // TODO(suhas): Move server path to a constant common between all services.
    serviceBuilder.setRootUrl("http://10.0.2.2:8080/_ah/api/")
        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
          @Override
          public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
              throws IOException {
            abstractGoogleClientRequest.setDisableGZipContent(true);
          }
        });
    eventService = serviceBuilder.build();
    return eventService;
  }
}
