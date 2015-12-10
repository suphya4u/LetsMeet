package com.letsmeet.android.storage.chat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper for chat db.
 */
public class ChatDbHelper extends SQLiteOpenHelper {

  private static final String DB_NAME = "letsMeetChats.db";
  private static final int DB_VERSION = 1;

  public ChatDbHelper(Context context) {
    super(context, DB_NAME, null /* cursorFactory */, DB_VERSION);
  }

  @Override public void onCreate(SQLiteDatabase db) {
    db.execSQL(ChatStore.getCreateQuery());
  }

  @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
  }
}
