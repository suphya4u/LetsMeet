package com.letsmeet.android.widgets.contactselect;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.letsmeet.android.R;
import android.widget.TextView;

import com.google.api.client.util.Lists;
import com.google.common.collect.ImmutableList;
import com.letsmeet.android.widgets.contactselect.ContactInfo;
import com.letsmeet.android.widgets.contactselect.SelectContactsView;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * create an instance of this fragment.
 */
// TODO(suhas): Make this a view instead of fragment.
public class SelectContactFragment extends Fragment {

  private final SelectedContactsAdapter selectedContactAdapter;

  public SelectContactFragment() {
    // Required empty public constructor
    selectedContactAdapter = new SelectedContactsAdapter();
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

    final RecyclerView selectedContactsListView =
        (RecyclerView) rootView.findViewById(R.id.selected_contacts_list);
    final LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext());
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    selectedContactsListView.setLayoutManager(layoutManager);
    selectedContactsListView.setAdapter(selectedContactAdapter);

    SelectContactsView contactAutoComplete =
        (SelectContactsView) rootView.findViewById(R.id.contact_autocomplete);
    contactAutoComplete.setOnContactSelectionCallback(new SelectContactsView.OnContactSelection() {
      @Override public void handleContactSelect(ContactInfo contact) {
        selectedContactAdapter.addContact(contact);
      }
    });
    return rootView;
  }

  public List<ContactInfo> getSelectedContacts() {
    return selectedContactAdapter.getSelectedContacts();
  }

  public void setSelectedContacts(List<ContactInfo> contactInfos) {
    selectedContactAdapter.setSelectedContacts(contactInfos);
  }

  public void setError(String error) {
    SelectContactsView contactAutoComplete =
        (SelectContactsView) getActivity().findViewById(R.id.contact_autocomplete);
    contactAutoComplete.setError(error);

  }
}
