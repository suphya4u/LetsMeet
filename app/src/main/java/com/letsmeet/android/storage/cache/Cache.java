package com.letsmeet.android.storage.cache;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.api.client.json.GenericJson;

import java.io.IOException;
import java.util.Calendar;

/**
 * Cache.
 */
public abstract class Cache<T extends GenericJson> {

  private static final long SOFT_CACHE_EXPIRATION_TIME_MS = 180000L; // 3 minutes
  private static final long HARD_CACHE_EXPIRATION_TIME_MS = 300000L; // 5 minutes

  protected final Context context;

  private final CacheStore<T> cacheStore;

  protected Cache(Context context, Class<T> type) {
    this.context = context;
    this.cacheStore = new CacheStore<>(context, type);
  }

  protected abstract T fetchData(String key) throws IOException;

  protected T get(String key) throws IOException {
    long lastUpdateTimeMs = cacheStore.getLastUpdatedTime(key);
    long currentTimeMs = Calendar.getInstance().getTimeInMillis();

    if ((currentTimeMs - lastUpdateTimeMs) < SOFT_CACHE_EXPIRATION_TIME_MS) {
      return getCachedIfPresent(key, false /* no refresh */);
    }

    if ((currentTimeMs - lastUpdateTimeMs) < HARD_CACHE_EXPIRATION_TIME_MS) {
      return getCachedIfPresent(key, true /* refresh cache */);
    }

    return fetchIfPossible(key);
  }

  protected void invalidate(String key) {
    cacheStore.invalidate(key);
  }

  private T fetchIfPossible(String key) throws IOException {
    T data = null;
    try {
      data = fetchData(key);
      updateCacheInBackground(key, data);
    } catch (IOException e) {
      showNetworkError();
      data = cacheStore.getData(key);
      if (data == null) {
        throw e;
      }
    }
    return data;
  }

  private T getCachedIfPresent(String key, boolean refreshCache) throws IOException {
    T cachedData = cacheStore.getData(key);
    if (cachedData == null) {
      T fetchedData = fetchData(key);
      updateCacheInBackground(key, fetchedData);
      return fetchedData;
    } else if (refreshCache) {
      fetchAndUpdateCacheInBackground(key);
    }
    return cachedData;
  }

  private void updateCacheInBackground(final String key, final T data) {
    new AsyncTask<Void, Void, Void>() {
      @Override protected Void doInBackground(Void... params) {
        cacheStore.writeData(key, data);
        return null;
      }
    }.execute();
  }

  private void fetchAndUpdateCacheInBackground(final String key) {
    new AsyncTask<Void, Void, Void>() {
      @Override protected Void doInBackground(Void... params) {
        T data = null;
        try {
          data = fetchData(key);
        } catch (IOException e) {
          showNetworkError();
        }
        updateCacheInBackground(key, data);
        return null;
      }
    }.execute();
  }

  private void showNetworkError() {
    // TODO: Show toast here using handler.
    if (context instanceof Activity) {
      Activity activityContext = (Activity) context;
      activityContext.runOnUiThread(new Runnable() {
        @Override public void run() {
          Toast.makeText(context, "Network Error, Please check your network", Toast.LENGTH_LONG)
              .show();
        }
      });
    }
  }
}
