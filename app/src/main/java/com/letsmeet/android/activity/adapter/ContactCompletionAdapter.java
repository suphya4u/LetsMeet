package com.letsmeet.android.activity.adapter;

import android.content.Context;
import android.database.Cursor;
import android.letsmeet.com.letsmeet.R;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;

/**
 * Adapter to read contacts and filter with prefix.
 */
public class ContactCompletionAdapter extends SimpleCursorAdapter {

  private final static String[] FROM_COLUMNS = {
      ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
      ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI,
      ContactsContract.CommonDataKinds.Phone.NUMBER,
  };

  private final static int[] TO_IDS = {
      R.id.contact_name,
      R.id.contact_thumbnail,
      R.id.contact_number
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
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER,
        ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI,
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
        BaseColumns._ID
    };

    return context.getContentResolver().query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, contactsProjection, select, selectArgs, null);
  }
}
