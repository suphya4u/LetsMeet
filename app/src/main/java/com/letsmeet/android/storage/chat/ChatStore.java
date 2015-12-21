package com.letsmeet.android.storage.chat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.letsmeet.android.activity.ChatActivity;

/**
 * SQL table contract
 */
public class ChatStore {

  private static final String TABLE_NAME = "ChatDb";
  public static final int DB_VERSION = 1;

  public static final String COLUMN_EVENT_ID = "eventId";
  public static final String COLUMN_SENDER_PHONE = "senderPhone";
  public static final String COLUMN_MESSAGE = "message";
  public static final String COLUMN_TIME_SENT = "timeSent";
  public static final String COLUMN_IS_MY_MESSAGE = "isMyMessage";
  public static final String COLUMN_SEND_STATUS = "sendStatus";
  public static final String COLUMN_READ_STATUS = "readStatus";
  public static final String NULL_COLUMN = "NULL_COLUMN";

  private static final String TYPE_TEXT = " TEXT";
  private static final String TYPE_INT = " INTEGER";
  private static final String COMMA_SEP = ",";

  public static String getCreateQuery() {
    return "CREATE TABLE " + TABLE_NAME + " ("
        + ChatMessage._ID + TYPE_INT + " PRIMARY KEY, "
        + COLUMN_EVENT_ID + TYPE_INT + COMMA_SEP
        + COLUMN_SENDER_PHONE + TYPE_TEXT + COMMA_SEP
        + COLUMN_MESSAGE + TYPE_TEXT + COMMA_SEP
        + COLUMN_TIME_SENT + TYPE_INT + COMMA_SEP
        + COLUMN_IS_MY_MESSAGE + TYPE_INT + COMMA_SEP
        + COLUMN_SEND_STATUS + TYPE_INT + COMMA_SEP
        + COLUMN_READ_STATUS + TYPE_INT
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

  public static Cursor getCursor(Context context, long eventId) {
    ChatDbHelper dbHelper = new ChatDbHelper(context);
    SQLiteDatabase db = dbHelper.getReadableDatabase();

    return db.rawQuery("SELECT * FROM " + TABLE_NAME
        + " WHERE " + COLUMN_EVENT_ID + "=" + eventId, null /* selectArgs */);
  }

  public static int getUnreadCount(Context context, long eventId) {
    Cursor cursor = getUnread(context, eventId, false /* includeAll */);
    return cursor.getCount();
  }

  public static int getTotalUnreadCount(Context context) {
    Cursor cursor = getUnread(context, 0 /* eventId */, true /* includeAll */);
    return cursor.getCount();
  }

  public static Cursor getAllUnread(Context context) {
    return getUnread(context, 0 /* eventId */, true /* includeAll */);
  }

  private static Cursor getUnread(Context context, long eventId, boolean includeAll) {
    ChatDbHelper dbHelper = new ChatDbHelper(context);
    SQLiteDatabase db = dbHelper.getReadableDatabase();

    String query = "SELECT * FROM " + TABLE_NAME + " WHERE "
        + COLUMN_READ_STATUS + "=" + ChatMessage.ReadStatus.UNREAD.ordinal();

    if (!includeAll) {
      query = query + " AND " + COLUMN_EVENT_ID + "=" + eventId;
    }

    return db.rawQuery(query, null /* selectArgs */);
  }

  public static void markAllAsRead(Context context, long eventId) {
    ChatDbHelper dbHelper = new ChatDbHelper(context);
    SQLiteDatabase db = dbHelper.getWritableDatabase();

    ChatMessage chatMessage = new ChatMessage().markAsRead();
    int readStatusNum = chatMessage.getReadStatusNum();
    ContentValues values = new ContentValues();
    values.put(COLUMN_READ_STATUS, readStatusNum);

    Cursor cursor = getCursor(context, eventId);
    while (cursor.moveToNext()) {
      int chatMessageId = cursor.getInt(cursor.getColumnIndex(ChatMessage._ID));

      db.update(TABLE_NAME, values, ChatMessage._ID + " = ?",
          new String[]{String.valueOf(chatMessageId)});
    }
    cursor.close();
  }

  private static ContentValues getContentValues(ChatMessage chatMessage) {
    ContentValues values = new ContentValues();
    values.put(COLUMN_EVENT_ID, chatMessage.getEventId());
    values.put(COLUMN_MESSAGE, chatMessage.getMessage());
    values.put(COLUMN_IS_MY_MESSAGE, chatMessage.isMyMessage());
    values.put(COLUMN_SENDER_PHONE, chatMessage.getSenderPhoneNumber());
    values.put(COLUMN_TIME_SENT, chatMessage.getTimeSent());
    values.put(COLUMN_SEND_STATUS, chatMessage.getSendStatusNum());
    values.put(COLUMN_READ_STATUS, chatMessage.getReadStatusNum());
    return values;
  }
}
