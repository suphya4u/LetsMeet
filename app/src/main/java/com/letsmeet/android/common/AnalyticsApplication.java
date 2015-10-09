package com.letsmeet.android.common;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.letsmeet.android.R;

/**
 * Analytics Application.
 */
public class AnalyticsApplication extends Application {
  private Tracker mTracker;

  /**
   * Gets the default {@link Tracker} for this {@link Application}.
   * @return tracker
   */
  synchronized public Tracker getDefaultTracker() {
    if (mTracker == null) {
      GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
      // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG

      // TODO: R.xml.global_tracker is not resolvable. Maybe issues with google-services.json
      // R.xml.global_tracker is supposed to be generated through google-services.json
      // Configuration details -
      // https://developers.google.com/analytics/devguides/collection/android/v4/
      // build.gradle files need to be updated as per the doc, but it fails with some random
      // errors - check project level gradle file and app gradle file for more details.

      //mTracker = analytics.newTracker(R.xml.global_tracker);
    }
    return mTracker;
  }
}