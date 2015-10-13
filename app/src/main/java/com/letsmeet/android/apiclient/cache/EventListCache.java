package com.letsmeet.android.apiclient.cache;

import android.content.Context;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.gson.Gson;
import com.letsmeet.server.eventService.model.ListEventsForUserResponse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Caches events list response.
 */
public class EventListCache {

  private static final String FILE_NAME = "event_list_cache";
  private static final JsonFactory JSON_FACTORY = new AndroidJsonFactory();

  public void cacheData(Context context, ListEventsForUserResponse data) {
    FileOutputStream fos = null;
    try {
      fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
    } catch (FileNotFoundException e) {
      // File not found.
      return;
    }
    try {
      fos.write(JSON_FACTORY.toByteArray(data));
      fos.close();
    } catch (IOException e) {
      // Failed to write.
    }
  }

  public ListEventsForUserResponse getEventsList(Context context) {
    FileInputStream fis = null;
    try {
      fis = context.openFileInput(FILE_NAME);
    } catch (FileNotFoundException e) {
      // File not found.
      return null;
    }

    try {
      ListEventsForUserResponse res = JSON_FACTORY.fromInputStream(fis, ListEventsForUserResponse.class);
      fis.close();
      return res;
    } catch (IOException e) {
      // Exception while reading file.
      return null;
    }
  }
}
