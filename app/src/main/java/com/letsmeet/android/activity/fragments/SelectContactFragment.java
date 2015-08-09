package com.letsmeet.android.activity.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.letsmeet.com.letsmeet.R;
import android.widget.AutoCompleteTextView;

import com.letsmeet.android.activity.adapter.ContactCompletionAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * create an instance of this fragment.
 */
public class SelectContactFragment extends Fragment {

  public SelectContactFragment() {
    // Required empty public constructor
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
    // TODO(suhas): Replace AutoCompleteTextView with customer View that can add more contacts.
    AutoCompleteTextView contactAutoComplete =
        (AutoCompleteTextView) rootView.findViewById(R.id.contact_autocomplete);
    ContactCompletionAdapter contactAdapter = new ContactCompletionAdapter(getActivity());
    contactAutoComplete.setAdapter(contactAdapter);
    return rootView;
  }
}
