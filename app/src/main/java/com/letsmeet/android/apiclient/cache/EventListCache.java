package com.letsmeet.android.apiclient.cache;

import android.content.Context;

import com.letsmeet.android.apiclient.EventServiceClient;
import com.letsmeet.android.storage.LocalStore;
import com.letsmeet.android.storage.cache.Cache;
import com.letsmeet.server.eventService.model.ListEventsForUserResponse;

import java.io.IOException;

/**
 * Cache for events list.
 */
public class EventListCache extends Cache<ListEventsForUserResponse> {

  private static final String UPCOMING_EVENTS_KEY = "upcoming_events_list";
  private static final String ALL_EVENTS_KEY = "all_events_list";

  public EventListCache(Context context) {
    super(context, ListEventsForUserResponse.class);
  }

  public ListEventsForUserResponse getEvents(boolean ignorePastEvents) throws IOException {
    return get(ignorePastEvents ? UPCOMING_EVENTS_KEY : ALL_EVENTS_KEY);
  }

  public void invalidateAll() {
    invalidate(UPCOMING_EVENTS_KEY);
    invalidate(ALL_EVENTS_KEY);
  }

  @Override protected ListEventsForUserResponse fetchData(String key) throws IOException {
    LocalStore localStore = LocalStore.getInstance(context);
    return EventServiceClient.getInstance(context).fetchEventsList(localStore.getUserId(),
        key.equals(UPCOMING_EVENTS_KEY));
  }
}
