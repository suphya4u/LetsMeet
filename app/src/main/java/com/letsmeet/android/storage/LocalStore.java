package com.letsmeet.android.storage;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Local storage.
 */
public class LocalStore {

  private static final String PREF_FILE_KEY = "com.letsmeet.prefs";
  private static final String USER_ID_KEY = "USER_ID";

  private Context context;

  public static LocalStore getInstance(Context context) {
    // TODO(suhas): can this be singleton?
    return new LocalStore(context);
  }

  private LocalStore(Context context) {
    this.context = context;
  }

  public void saveUserId(long userId) {
    SharedPreferences sharedPref = getSharedPrefs();
    SharedPreferences.Editor editor = sharedPref.edit();
    editor.putLong(USER_ID_KEY, userId);
    editor.commit();
  }

  public long getUserId() {
    SharedPreferences sharedPref = getSharedPrefs();
    return sharedPref.getLong(USER_ID_KEY, 0);
  }

  private SharedPreferences getSharedPrefs() {
    return context.getSharedPreferences(PREF_FILE_KEY, Context.MODE_PRIVATE);
  }
}
