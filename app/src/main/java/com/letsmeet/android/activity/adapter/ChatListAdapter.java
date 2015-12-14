package com.letsmeet.android.activity.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;

import com.letsmeet.android.R;
import com.letsmeet.android.storage.chat.ChatStore;

/**
 * Adapter for chat list.
 */
public class ChatListAdapter extends SimpleCursorAdapter {

  private final Context context;

  private final static String[] FROM_COLUMNS = {
      ChatStore.COLUMN_MESSAGE
  };

  private final static int[] TO_IDS = {
      R.id.test_chat
  };

  public ChatListAdapter(Context context) {
    super(context, R.layout.chat_item, null /* cursor */, FROM_COLUMNS, TO_IDS, 0 /* flags */);

    this.context = context;

    setFilterQueryProvider(new FilterQueryProvider() {
      public Cursor runQuery(CharSequence str) {
        return getCursor(str);
      }
    });
  }

  @Override
  public CharSequence convertToString(@NonNull Cursor cursor) {
    int index = cursor.getColumnIndex(ChatStore.COLUMN_MESSAGE);
    return cursor.getString(index);
  }

  private Cursor getCursor(CharSequence str) {
    return ChatStore.getCursor(context);
  }
}
