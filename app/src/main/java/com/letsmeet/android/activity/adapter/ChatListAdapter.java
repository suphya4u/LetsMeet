package com.letsmeet.android.activity.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.common.collect.Maps;
import com.letsmeet.android.R;
import com.letsmeet.android.storage.chat.ChatStore;

import java.util.Map;

/**
 * Adapter for chat list.
 */
public class ChatListAdapter extends SimpleCursorAdapter {

  private final Context context;
  private final Map<String, String> colorMap;

  private int nextColor;

  private static final String[] COLORS = {
      "#FF0000",
      "#00FF00",
      "#0000FF",
  };

  private final static String[] FROM_COLUMNS = {
      ChatStore.COLUMN_MESSAGE
  };

  private final static int[] TO_IDS = {
      R.id.single_chat_message
  };

  public ChatListAdapter(Context context) {
    super(context, R.layout.chat_item, ChatStore.getCursor(context), FROM_COLUMNS, TO_IDS,
        0 /* flags */);

    this.context = context;
    colorMap = Maps.newHashMap();
    nextColor = 0;
  }

  @Override
  public CharSequence convertToString(@NonNull Cursor cursor) {
    int index = cursor.getColumnIndex(ChatStore.COLUMN_MESSAGE);
    return cursor.getString(index);
  }

  @Override public void bindView(View view, Context context, Cursor cursor) {
    TextView chatMessageView = (TextView) view.findViewById(R.id.single_chat_message);
    String message = cursor.getString(cursor.getColumnIndex(ChatStore.COLUMN_MESSAGE));
    String sender = "me:";
    if (cursor.getInt(cursor.getColumnIndex(ChatStore.COLUMN_IS_MY_MESSAGE)) == 0) {
      sender = cursor.getString(cursor.getColumnIndex(ChatStore.COLUMN_SENDER_PHONE)) + ":";
    }
    String color = getSenderColor(sender);

    chatMessageView.setText(Html.fromHtml(
        "<font color=\"" + color + "\"><strong>" + sender + "</strong></font> " + message));
  }

  private String getSenderColor(String sender) {
    if (colorMap.containsKey(sender)) {
      return colorMap.get(sender);
    }
    String color = COLORS[nextColor];
    colorMap.put(sender, color);
    nextColor = (nextColor + 1) % COLORS.length;
    return color;
  }
}
