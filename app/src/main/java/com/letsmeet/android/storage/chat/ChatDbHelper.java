package com.letsmeet.android.storage.chat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper for chat db.
 */
public class ChatDbHelper extends SQLiteOpenHelper {

  private static final String DB_NAME = "letsMeetChats.db";

  public ChatDbHelper(Context context) {
    super(context, DB_NAME, null /* cursorFactory */, ChatStore.DB_VERSION);
  }

  @Override public void onCreate(SQLiteDatabase db) {
    db.execSQL(ChatStore.getCreateQuery());
  }

  @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
  }
}
