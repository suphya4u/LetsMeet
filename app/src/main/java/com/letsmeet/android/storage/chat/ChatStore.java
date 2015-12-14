package com.letsmeet.android.storage.chat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * SQL table contract
 */
public class ChatStore {

  private static final String TABLE_NAME = "ChatDb";
  public static final String COLUMN_SENDER_PHONE = "senderPhone";
  public static final String COLUMN_MESSAGE = "message";
  public static final String COLUMN_TIME_SENT = "timeSent";
  public static final String COLUMN_IS_MY_MESSAGE = "isMyMessage";
  public static final String COLUMN_STATUS = "status";
  public static final String NULL_COLUMN = "NULL_COLUMN";

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

    ContentValues values = getContentValues(chatMessage);
    return db.insert(TABLE_NAME, NULL_COLUMN, values);
  }

  public static long update(Context context, long chatId, ChatMessage chatMessage) {
    ChatDbHelper dbHelper = new ChatDbHelper(context);
    SQLiteDatabase db = dbHelper.getWritableDatabase();

    ContentValues values = getContentValues(chatMessage);
    return db.update(TABLE_NAME, values, ChatMessage._ID + " = ?",
        new String[]{String.valueOf(chatId)});
  }

  public static Cursor getCursor(Context context) {
    ChatDbHelper dbHelper = new ChatDbHelper(context);
    SQLiteDatabase db = dbHelper.getReadableDatabase();

    return db.rawQuery("SELECT * FROM " + TABLE_NAME, null /* selectArgs */);
  }

  private static ContentValues getContentValues(ChatMessage chatMessage) {
    ContentValues values = new ContentValues();
    values.put(COLUMN_MESSAGE, chatMessage.getMessage());
    values.put(COLUMN_IS_MY_MESSAGE, chatMessage.isMyMessage());
    values.put(COLUMN_SENDER_PHONE, chatMessage.getSenderPhoneNumber());
    values.put(COLUMN_TIME_SENT, chatMessage.getTimeSent());
    values.put(COLUMN_STATUS, chatMessage.getStatusNum());
    return values;
  }

}
