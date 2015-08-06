package com.letsmeet.android.activity.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.letsmeet.com.letsmeet.R;
import android.os.Build;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.api.client.util.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suhas on 8/4/15.
 */
public class ContactCompletionAdapter extends SimpleCursorAdapter {

  private final static String[] FROM_COLUMNS = {
      Build.VERSION.SDK_INT
          >= Build.VERSION_CODES.HONEYCOMB ?
          ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
          ContactsContract.Contacts.DISPLAY_NAME
  };

  private final static int[] TO_IDS = {
      R.id.contact_name
  };

  private final Context context;

  public ContactCompletionAdapter(Context context) {
    super(context, R.layout.contact_item, null, FROM_COLUMNS, TO_IDS, 0);

    // TODO(suhas): Override the method instead of calling this with constructor.
    setFilterQueryProvider(new FilterQueryProvider() {
      public Cursor runQuery(CharSequence str) {
        return getCursor(str);
      }
    });

    this.context = context;
  }

  @Override
  public CharSequence convertToString(Cursor cursor) {
    int index = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
    return cursor.getString(index);
  }

  private Cursor getCursor(CharSequence str) {
    String select = ContactsContract.Contacts.DISPLAY_NAME + " LIKE ? ";
    String[]  selectArgs = { "%" + str + "%"};
    String[] contactsProjection = new String[] {
        ContactsContract.Contacts._ID,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.Contacts.LOOKUP_KEY,  };

    return context.getContentResolver().query(
        ContactsContract.Contacts.CONTENT_URI, contactsProjection, select, selectArgs, null);
  }
}
