package com.letsmeet.android.activity.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.letsmeet.android.R;
import com.letsmeet.android.common.ContactFetcher;
import com.letsmeet.android.storage.chat.ChatStore;
import com.letsmeet.android.widgets.contactselect.ContactInfo;

import java.util.Map;

/**
 * Adapter for chat list.
 */
public class ChatListAdapter extends SimpleCursorAdapter {

  private final Context context;
  private final Map<String, String> colorMap;

  private int nextColor;

  private static final String[] COLORS = {
      "#AF96EA",
      "#D56A8C",
      "#AF9D80",
      "#00D86F",
      "#5D5B36",
      "#DEB833",
      "#4B42C2",
      "#DA99DC",
      "#AB296C",
      "#2B91E0"
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
    String senderName = "me";
    if (cursor.getInt(cursor.getColumnIndex(ChatStore.COLUMN_IS_MY_MESSAGE)) == 0) {
      String sender = cursor.getString(cursor.getColumnIndex(ChatStore.COLUMN_SENDER_PHONE));
      ContactInfo contactInfo = ContactFetcher.getInstance()
          .getContactInfoByNumber(sender, context);
      if (!Strings.isNullOrEmpty(contactInfo.getDisplayName())) {
        senderName = contactInfo.getDisplayName();
      } else {
        senderName = contactInfo.getPhoneNumber();
      }
    }
    String senderDisplayStr = senderName + ":";
    String color = getSenderColor(senderDisplayStr);

    chatMessageView.setText(Html.fromHtml(
        "<font color=\"" + color + "\"><strong>" + senderDisplayStr
            + "</strong></font> " + message));
  }

  public void updateData() {
    changeCursor(ChatStore.getCursor(context));
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
