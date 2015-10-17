package com.letsmeet.android.activity.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.letsmeet.android.R;
import com.letsmeet.android.activity.adapter.EventListRecyclerAdapter;
import com.letsmeet.android.apiclient.EventServiceClient;
import com.letsmeet.android.common.MainContentFragmentSelector;
import com.letsmeet.android.storage.LocalStore;
import com.letsmeet.server.eventService.model.ListEventsForUserResponse;

import java.io.IOException;

/**
 * Fragment to render events list.
 */
public class EventListFragment extends Fragment {

  private MainContentFragmentSelector mainContentFragmentSelector = MainContentFragmentSelector.UPCOMING_EVENTS;
  private RecyclerView eventListView;
  private SwipeRefreshLayout swipeRefresh;
  private long userId;

  public static EventListFragment newInstance(MainContentFragmentSelector mainContentFragmentSelector) {
    EventListFragment fragment = new EventListFragment();
    fragment.mainContentFragmentSelector = mainContentFragmentSelector;
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_events_list, container, false);
    renderEventList(view);
    return view;
  }

  @Override public void onResume() {
    super.onResume();
    if (eventListView != null) {
      listEvents(true /* useCached */);
    }
  }

  private void renderEventList(View view) {
    LocalStore localStore = LocalStore.getInstance(getContext());
    userId = localStore.getUserId();

    TextView placeholder = (TextView) view.findViewById(R.id.placeholder);
    placeholder.setText("Event list type: " + mainContentFragmentSelector.name());

    eventListView = (RecyclerView) view.findViewById(R.id.events_list);
    final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

    swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
    swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        listEvents(true /* useCached */);
      }
    });
    eventListView.setLayoutManager(layoutManager);
  }

  private void listEvents(final boolean useCached) {
    new AsyncTask<Long, Void, ListEventsForUserResponse>() {

      @Override protected ListEventsForUserResponse doInBackground(Long... params) {
        try {
          boolean shouldIgnorePastEvents = mainContentFragmentSelector.equals(
              MainContentFragmentSelector.UPCOMING_EVENTS);
          if (useCached) {
            return EventServiceClient.getInstance(getActivity()).listEventsFromCache(userId,
                shouldIgnorePastEvents);
          } else {
            return EventServiceClient.getInstance(getActivity()).listFreshEvents(userId,
                shouldIgnorePastEvents);
          }
        } catch (IOException e) {
          // TODO: Log to analytics.
        }
        return null;
      }

      @Override protected void onPostExecute(ListEventsForUserResponse response) {
        if (response == null) {
          Toast.makeText(getContext(),
              "Failed to connect server. Please check your network connection",
              Toast.LENGTH_SHORT).show();
          if (swipeRefresh != null) {
            swipeRefresh.setRefreshing(false);
          }
          return;
        }

        RecyclerView.Adapter adapter = eventListView.getAdapter();
        if (adapter != null && adapter instanceof EventListRecyclerAdapter) {
          EventListRecyclerAdapter eventListAdapter = (EventListRecyclerAdapter) adapter;
          eventListAdapter.updateEventList(response.getEventsList());
        } else {
          eventListView.setAdapter(new EventListRecyclerAdapter(response.getEventsList()));
        }
        if (swipeRefresh != null) {
          swipeRefresh.setRefreshing(false);
        }
      }
    }.execute();
  }
}
