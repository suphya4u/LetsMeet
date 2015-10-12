package com.letsmeet.android.activity;

import android.content.Intent;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.letsmeet.android.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.letsmeet.android.activity.adapter.EventDetailsGuestsListAdapter;
import com.letsmeet.android.apiclient.EventServiceClient;
import com.letsmeet.android.common.DateTimeUtils;
import com.letsmeet.android.config.Constants;
import com.letsmeet.android.storage.LocalStore;
import com.letsmeet.android.common.ContactFetcher;
import com.letsmeet.android.widgets.contactselect.ContactInfo;
import com.letsmeet.server.eventService.model.EventDetails;
import com.letsmeet.server.eventService.model.Invitee;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Activity to show event details
 */
public class EventDetailsActivity extends AppCompatActivity {

  private EventDetails eventDetails;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_event_details);

    if (eventDetails == null) {
      long eventId = 0;
      try {
        String eventIdString = getIntent().getStringExtra(Constants.EVENT_ID_KEY);
        if (!Strings.isNullOrEmpty(eventIdString)) {
          eventId = Long.parseLong(eventIdString);
        }
      } catch (NumberFormatException e) {
        // Log to analytics.
      }
      if (eventId == 0) {
        Toast.makeText(this, "Could not event id. Returning.", Toast.LENGTH_LONG).show();
        finish();
      }
      LocalStore localStore = LocalStore.getInstance(this);
      fetchEvent(eventId, localStore.getUserId());
    } else {
      populateEventData();
    }
  }

  @Override protected void onResume() {
    super.onResume();
    // TODO(suhas): Cache!!! Don't fetch everytime unless cache expire or updated in some flow.
    if (eventDetails != null) {
      LocalStore localStore = LocalStore.getInstance(this);
      fetchEvent(eventDetails.getEventId(), localStore.getUserId());
    }
  }

  private void populateEventData() {
    TextView eventNameView = (TextView) findViewById(R.id.event_details_name);
    eventNameView.setText(eventDetails.getName());

    TextView eventNotesView = (TextView) findViewById(R.id.event_details_notes);
    eventNotesView.setText(eventDetails.getNotes());

    if (eventDetails.getLocation() != null) {
      TextView eventLocationView = (TextView) findViewById(R.id.event_details_location);
      eventLocationView.setText(eventDetails.getLocation().getPlaceAddress());
    }

    TextView eventDateTimeView = (TextView) findViewById(R.id.event_details_date_time);
    eventDateTimeView.setText(DateTimeUtils.getDisplayDateTime(
        this, eventDetails.getEventTimeMillis()));

    RecyclerView guestsListView = (RecyclerView) findViewById(R.id.guests_list);
    EventDetailsGuestsListAdapter guestsListAdapter = new EventDetailsGuestsListAdapter();
    final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    guestsListView.setLayoutManager(layoutManager);
    guestsListView.setAdapter(guestsListAdapter);
    guestsListAdapter.setGuestsList(getGuestsWithResponse(eventDetails.getInviteePhoneNumbers()));

    if (eventDetails.getIsOwner()) {
      Button editEventButton = (Button) findViewById(R.id.edit_event_button);
      editEventButton.setVisibility(View.VISIBLE);
      editEventButton.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          Intent editEventIntent = new Intent(EventDetailsActivity.this, CreateEventActivity.class);
          editEventIntent.putExtra(Constants.EVENT_ID_KEY,
              String.valueOf(eventDetails.getEventId()));
          startActivity(editEventIntent);
        }
      });
    }
  }

  private List<Pair<ContactInfo, String>> getGuestsWithResponse(
      List<Invitee> invitees) {
    return Lists.transform(invitees, new Function<Invitee, Pair<ContactInfo, String>>() {
      @Nullable @Override public Pair<ContactInfo, String> apply(Invitee invitee) {
        ContactInfo contactInfo = ContactFetcher.getInstance()
            .getContactInfoByNumber(invitee.getPhoneNumber(), EventDetailsActivity.this);
        if (Strings.isNullOrEmpty(contactInfo.getDisplayName())) {
          contactInfo.setDisplayName(invitee.getPhoneNumber());
        }
        return Pair.create(contactInfo, invitee.getResponse());
      }
    });
  }

  private void fetchEvent(final long eventId, final long userId) {
    new AsyncTask<Void, Void, EventDetails>() {
      @Override protected EventDetails doInBackground(Void... params) {
        try {
          eventDetails = EventServiceClient.getInstance().GetEventDetails(eventId, userId);
        } catch (IOException e) {
          // Log to analytics.
        }
        return eventDetails;
      }

      @Override protected void onPostExecute(EventDetails serverResponse) {
        if (serverResponse == null) {
          Toast.makeText(EventDetailsActivity.this,
              "Failed to connect server. Please check your network connection",
              Toast.LENGTH_SHORT).show();
        } else {
          populateEventData();
        }
      }
    }.execute();
  }
}
