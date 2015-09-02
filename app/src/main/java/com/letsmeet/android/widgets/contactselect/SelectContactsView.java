package com.letsmeet.android.widgets.contactselect;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

/**
 * Widget to select contacts.
 */
public class SelectContactsView extends AutoCompleteTextView {

  private ContactCompletionAdapter contactAdapter;
  private OnContactSelection contactSelectionCallback;

  public SelectContactsView(Context context, AttributeSet attrs) {
    super(context, attrs);
    contactAdapter = new ContactCompletionAdapter(context);
    setAdapter(contactAdapter);
    setOnItemClickListener(new ItemClickListener());
  }

  public void setOnContactSelectionCallback(OnContactSelection callback) {
    contactSelectionCallback = callback;
  }

  private class ItemClickListener implements AdapterView.OnItemClickListener {
    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      ContactInfo selectedContact = contactAdapter.getSelectedContact(position);
      onContactSelection(selectedContact);
    }
  }

  public interface OnContactSelection {
    void handleContactSelect(ContactInfo selectedContact);
  }

  private void onContactSelection(ContactInfo selectedContact) {
    if (contactSelectionCallback != null) {
      contactSelectionCallback.handleContactSelect(selectedContact);
    }
  }
}
