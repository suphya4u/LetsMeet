package com.letsmeet.android.apiclient.cache;

import android.content.Context;

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

  public void cacheData(Context context, ListEventsForUserResponse data) {
    FileOutputStream fos = null;
    try {
      fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
    } catch (FileNotFoundException e) {
      // File not found.
      return;
    }
    Gson gson = new Gson();
    try {
      fos.write(gson.toJson(data).getBytes());
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

    StringBuilder strBuffer = new StringBuilder();
    int content;
    try {
      while ((content = fis.read()) != -1) {
        strBuffer.append((char) content);
      }
      fis.close();
      Gson gson = new Gson();
      return gson.fromJson(strBuffer.toString(), ListEventsForUserResponse.class);
    } catch (IOException e) {
      // Exception while reading file.
      return null;
    }
  }
}
