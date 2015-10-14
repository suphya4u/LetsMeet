package com.letsmeet.android.apiclient.cache;

import android.content.Context;

import com.letsmeet.android.apiclient.EventServiceClient;
import com.letsmeet.android.storage.LocalStore;
import com.letsmeet.android.storage.cache.Cache;
import com.letsmeet.server.eventService.model.EventDetails;

import java.io.IOException;

/**
 * Cache for event details.
 */
public class EventDetailsCache extends Cache<EventDetails> {

  public EventDetailsCache(Context context) {
    super(context, EventDetails.class);
  }

  public EventDetails getEventDetails(long eventId) throws IOException {
    return get(String.valueOf(eventId));
  }

  public void invalidate(long eventId) {
    invalidate(String.valueOf(eventId));
  }

  @Override
  protected EventDetails fetchData(String key) throws IOException {
    LocalStore localStore = LocalStore.getInstance(context);
    return EventServiceClient.getInstance(context).fetchEventDetails(Long.valueOf(key),
        localStore.getUserId());
  }
}
