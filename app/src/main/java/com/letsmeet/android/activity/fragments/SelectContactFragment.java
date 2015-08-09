package com.letsmeet.android.activity.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.letsmeet.com.letsmeet.R;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.api.client.util.Lists;
import com.google.common.collect.ImmutableList;
import com.letsmeet.android.widgets.ContactInfo;
import com.letsmeet.android.widgets.SelectContactsView;
import com.letsmeet.android.widgets.adapter.ContactCompletionAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * create an instance of this fragment.
 */
public class SelectContactFragment extends Fragment {

  private List<ContactInfo> selectedContacts;

  public SelectContactFragment() {
    // Required empty public constructor
    selectedContacts = Lists.newArrayList();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View rootView = inflater.inflate(R.layout.fragment_select_contact, container, false);

    final TextView selectedContactText = (TextView) rootView.findViewById(R.id.selected_contacts);

    SelectContactsView contactAutoComplete =
        (SelectContactsView) rootView.findViewById(R.id.contact_autocomplete);
    contactAutoComplete.setOnContactSelectionCallback(new SelectContactsView.OnContactSelection() {
      @Override public void handleContactSelect(ContactInfo contact) {
        selectedContacts.add(contact);
        String existingText = selectedContactText.getText().toString();
        selectedContactText.setText(existingText
            + contact.getDisplayName()
            + "<" + contact.getPhoneNumber() + ">");
      }
    });
    return rootView;
  }

  public ImmutableList<ContactInfo> getSelectedContacts() {
    return ImmutableList.copyOf(selectedContacts);
  }
}
