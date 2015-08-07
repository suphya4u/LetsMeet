package com.letsmeet.android.activity.adapter;

import android.content.Context;
import android.database.Cursor;
import android.letsmeet.com.letsmeet.R;
import android.provider.ContactsContract;
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;

/**
 * Adapter to read contacts and filter with prefix.
 */
public class ContactCompletionAdapter extends SimpleCursorAdapter {

  private final static String[] FROM_COLUMNS = {
    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
    ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
  };

  private final static int[] TO_IDS = {
      R.id.contact_name,
      R.id.contact_thumbnail
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
        ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
        ContactsContract.Contacts.LOOKUP_KEY,
    };

    return context.getContentResolver().query(
        ContactsContract.Contacts.CONTENT_URI, contactsProjection, select, selectArgs, null);
  }
}
