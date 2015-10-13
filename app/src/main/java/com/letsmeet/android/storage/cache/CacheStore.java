package com.letsmeet.android.storage.cache;

import android.content.Context;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Store for caching.
 */
// TODO: Delete old cache, ones not used for X-days.
public class CacheStore<T extends GenericJson> {

  private static final String FILE_NAME_PREFIX = "events_cache_";
  private static final JsonFactory JSON_FACTORY = new AndroidJsonFactory();

  private final Context context;
  private final Class<T> type;
  private final Map<String, Object> locks;

  public CacheStore(Context context, Class<T> type) {
    this.context = context;
    this.type = type;
    locks = new HashMap<>();
  }

  public long getLastUpdatedTime(String key) {
    File file = new File(context.getFilesDir(), getFileName(key));
    return file.lastModified();
  }

  public void writeData(String key, T data) {
    FileOutputStream fos = null;
    try {
      fos = context.openFileOutput(getFileName(key), Context.MODE_PRIVATE);
    } catch (FileNotFoundException e) {
      // File not found.
      return;
    }
    try {
      synchronized (getLock(key)) {
        fos.write(JSON_FACTORY.toByteArray(data));
      }
      fos.close();
    } catch (IOException e) {
      // Failed to write.
    }
  }

  public T getData(String key) {
    FileInputStream fis = null;
    try {
      fis = context.openFileInput(getFileName(key));
    } catch (FileNotFoundException e) {
      // File not found.
      return null;
    }

    try {
      T response = null;
      synchronized (getLock(key)) {
        response = JSON_FACTORY.fromInputStream(fis, type);
      }
      fis.close();
      return response;
    } catch (IOException e) {
      // Exception while reading file.
      return null;
    }
  }

  private String getFileName(String key) {
    return FILE_NAME_PREFIX + type.getSimpleName() + "_" + key;
  }

  private Object getLock(String key) {
    synchronized (locks) {
      if (locks.containsKey(key)) {
        return locks.get(key);
      }
      Object lock = new Object();
      locks.put(key, lock);
      return lock;
    }
  }
}
