package com.letsmeet.android.common;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.letsmeet.android.widgets.contactselect.ContactInfo;

/**
 *
 */
public class ContactFetcher {

  String[] LOOKUP_FIELDS = new String[]{
      ContactsContract.PhoneLookup.DISPLAY_NAME,
      ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI
  };

  public static ContactFetcher getInstance() {
    return new ContactFetcher();
  }

  public ContactInfo getContactInfoByNumber(String phoneNumber, Context context) {
    Uri uri = Uri.withAppendedPath(
        ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
    Cursor cursor = context.getContentResolver().query(uri, LOOKUP_FIELDS, null, null, null);
    ContactInfo contactInfo = new ContactInfo().setPhoneNumber(phoneNumber);
    if (cursor.moveToFirst()) {
      contactInfo
          .setDisplayName(cursor.getString(
              cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)))
          .setThumbnailUrl(cursor.getString(
              cursor.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI)));
      cursor.close();
    }
    return contactInfo;
  }
}
