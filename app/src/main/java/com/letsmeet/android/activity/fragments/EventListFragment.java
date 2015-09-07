package com.letsmeet.android.activity.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.letsmeet.android.R;
import com.letsmeet.android.common.EventListType;

/**
 * Fragment to render events list.
 */
public class EventListFragment extends Fragment {

  private EventListType eventListType;

  public static EventListFragment newInstance(EventListType eventListType) {
    EventListFragment fragment = new EventListFragment();
    fragment.eventListType = eventListType;
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_upcoming_events, container, false);
    TextView placeholder = (TextView) view.findViewById(R.id.placeholder);
    placeholder.setText("New update: " + eventListType.name());
    return view;
  }
}
