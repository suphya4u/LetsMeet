package com.letsmeet.android.storage.chat;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * SQL table contract
 */
public class ChatStore {

  private static final String TABLE_NAME = "ChatDb";
  private static final String COLUMN_SENDER_PHONE = "senderPhone";
  private static final String COLUMN_MESSAGE = "message";
  private static final String COLUMN_TIME_SENT = "timeSent";
  private static final String COLUMN_IS_MY_MESSAGE = "isMyMessage";
  private static final String COLUMN_STATUS = "status";
  private static final String NULL_COLUMN = "NULL_COLUMN";

  private static final String TYPE_TEXT = " TEXT";
  private static final String TYPE_INT = " INTEGER";
  private static final String COMMA_SEP = ",";

  public static String getCreateQuery() {
    return "CREATE TABLE " + TABLE_NAME + " ("
        + ChatMessage._ID + TYPE_INT + " PRIMARY KEY, "
        + COLUMN_SENDER_PHONE + TYPE_TEXT + COMMA_SEP
        + COLUMN_MESSAGE + TYPE_TEXT + COMMA_SEP
        + COLUMN_TIME_SENT + TYPE_INT + COMMA_SEP
        + COLUMN_IS_MY_MESSAGE + TYPE_INT + COMMA_SEP
        + COLUMN_STATUS + TYPE_TEXT
        + " )";
  }

  public static long insert(Context context, ChatMessage chatMessage) {
    ChatDbHelper dbHelper = new ChatDbHelper(context);
    SQLiteDatabase db = dbHelper.getWritableDatabase();

    ContentValues values = new ContentValues();
    values.put(COLUMN_MESSAGE, chatMessage.getMessage());
    values.put(COLUMN_IS_MY_MESSAGE, chatMessage.isMyMessage());
    values.put(COLUMN_SENDER_PHONE, chatMessage.getSenderPhoneNumber());
    values.put(COLUMN_TIME_SENT, chatMessage.getTimeSent());
    values.put(COLUMN_STATUS, chatMessage.getStatusNum());

    return db.insert(TABLE_NAME, NULL_COLUMN, values);
  }
}
