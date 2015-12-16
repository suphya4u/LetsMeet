package com.letsmeet.android.activity;

import android.content.Intent;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.letsmeet.android.R;

import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.letsmeet.android.activity.adapter.EventDetailsGuestsListAdapter;
import com.letsmeet.android.apiclient.EventServiceClient;
import com.letsmeet.android.common.DateTimeUtils;
import com.letsmeet.android.common.GoogleApiHelper;
import com.letsmeet.android.config.Constants;
import com.letsmeet.android.storage.LocalStore;
import com.letsmeet.android.common.ContactFetcher;
import com.letsmeet.android.widgets.contactselect.ContactInfo;
import com.letsmeet.android.widgets.rsvp.RsvpButtonsView;
import com.letsmeet.server.eventService.model.EventDetails;
import com.letsmeet.server.eventService.model.Invitee;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Activity to show event details
 */
public class EventDetailsActivity extends AppCompatActivity {

  private EventDetails eventDetails;
  private MenuItem editEventMenuItem;
  private boolean isOwner = false;
  private static final ImmutableMap<String, Integer> RESPONSE_WEIGHT =
      ImmutableMap.<String, Integer>builder()
          .put("YES", 4)
          .put("MAYBE", 3)
          .put("NO_RESPONSE", 2)
          .put("NO", 1)
          // TODO: Do we need not yet using Lets meet here?
          .build();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_event_details);

    if (eventDetails == null) {
      long eventId = 0;
      try {
        String eventIdString = getIntent().getStringExtra(Constants.INTENT_EVENT_ID_KEY);
        if (!Strings.isNullOrEmpty(eventIdString)) {
          eventId = Long.parseLong(eventIdString);
        }
      } catch (NumberFormatException e) {
        // Log to analytics.
      }
      if (eventId == 0) {
        Toast.makeText(this, "Could not find event id. Returning.", Toast.LENGTH_LONG).show();
        finish();
      }
      LocalStore localStore = LocalStore.getInstance(this);
      fetchEvent(eventId, localStore.getUserId());
    } else {
      populateEventData();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_event_details, menu);
    editEventMenuItem = menu.findItem(R.id.action_edit_event);
    editEventMenuItem.setVisible(isOwner);
    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_edit_event) {
      if (eventDetails != null && eventDetails.getEventId() != 0) {
        Intent editEventIntent = new Intent(EventDetailsActivity.this, CreateEventActivity.class);
        editEventIntent.putExtra(Constants.INTENT_EVENT_ID_KEY,
            String.valueOf(eventDetails.getEventId()));
        startActivity(editEventIntent);
      }
    }
    return super.onOptionsItemSelected(item);
  }

  private void populateEventData() {
    isOwner = eventDetails.getIsOwner();

    Button sendMessageButton = (Button) findViewById(R.id.send_message_button);
    sendMessageButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent intent = new Intent(EventDetailsActivity.this, ChatActivity.class);
        intent.putExtra(Constants.INTENT_EVENT_ID_KEY, String.valueOf(eventDetails.getEventId()));
        intent.putExtra(Constants.INTENT_EVENT_NAME_KEY, String.valueOf(eventDetails.getName()));
        startActivity(intent);
      }
    });

    TextView eventNameView = (TextView) findViewById(R.id.event_details_name);
    eventNameView.setText(eventDetails.getName());

    LinearLayout eventNotesContainer =
        (LinearLayout) findViewById(R.id.event_details_notes_container);
    if (Strings.isNullOrEmpty(eventDetails.getNotes())) {
      eventNotesContainer.setVisibility(View.GONE);
    } else {
      eventNotesContainer.setVisibility(View.VISIBLE);
      TextView eventNotesView = (TextView) findViewById(R.id.event_details_notes);
      eventNotesView.setText(eventDetails.getNotes());
    }

    LinearLayout eventLocationContainer =
        (LinearLayout) findViewById(R.id.event_details_location_container);
    if (eventDetails.getLocation() != null
        && !Strings.isNullOrEmpty(eventDetails.getLocation().getPlaceAddress())) {
      eventLocationContainer.setVisibility(View.VISIBLE);
      TextView eventLocationView = (TextView) findViewById(R.id.event_details_location);
      eventLocationView.setText(eventDetails.getLocation().getPlaceAddress());

      eventLocationView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          GoogleApiHelper googleApiHelper = new GoogleApiHelper(EventDetailsActivity.this,
              EventDetailsActivity.this, 1 /* clientId */);
          Location lastLocation = googleApiHelper.getLastKnownLocation();
          double latitude = 0.0;
          double longitude = 0.0;
          if (lastLocation != null) {
            latitude = lastLocation.getLatitude();
            longitude = lastLocation.getLongitude();
          }

          Uri gmmIntentUri = Uri.parse("geo:"
              + latitude
              + ","
              + longitude
              + "?q="
              + eventDetails.getLocation().getPlaceAddress());
          Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
          mapIntent.setPackage("com.google.android.apps.maps");
          startActivity(mapIntent);
        }
      });
    } else {
      eventLocationContainer.setVisibility(View.GONE);
    }

    RsvpButtonsView rsvpButtonsView = (RsvpButtonsView) findViewById(R.id.rsvp_buttons);
    rsvpButtonsView.setEventId(eventDetails.getEventId());
    rsvpButtonsView.setSelection(eventDetails.getMyResponse());

    LinearLayout eventDateTimeContainer =
        (LinearLayout) findViewById(R.id.event_details_date_time_container);
    if (eventDetails.getEventTimeMillis() == 0) {
      eventDateTimeContainer.setVisibility(View.GONE);
    } else {
      eventDateTimeContainer.setVisibility(View.VISIBLE);
      TextView eventDateTimeView = (TextView) findViewById(R.id.event_details_date_time);
      eventDateTimeView.setText(DateTimeUtils.getDisplayDateTime(
          this, eventDetails.getEventTimeMillis()));
    }

    RecyclerView guestsListView = (RecyclerView) findViewById(R.id.guests_list);
    final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    guestsListView.setLayoutManager(layoutManager);

    EventDetailsGuestsListAdapter guestsListAdapter = new EventDetailsGuestsListAdapter();
    guestsListView.setAdapter(guestsListAdapter);

    LocalStore localStore = LocalStore.getInstance(this);
    String userPhoneNumber = localStore.getUserPhoneNumber();
    populateGuestsList(guestsListAdapter, eventDetails.getInviteePhoneNumbers(), userPhoneNumber);

    configureEditAction(isOwner);
  }

  private void configureEditAction(boolean isOwner) {
    if (editEventMenuItem != null) {
      editEventMenuItem.setVisible(isOwner);
    }
  }

  private void populateGuestsList(final EventDetailsGuestsListAdapter guestsListAdapter,
      final List<Invitee> inviteePhoneNumbers, final String userPhoneNumber) {
    new AsyncTask<Void, Void, List<Pair<ContactInfo, String>>>() {
      @Override protected List<Pair<ContactInfo, String>> doInBackground(Void... params) {
        return getGuestsWithResponse(inviteePhoneNumbers, userPhoneNumber);
      }

      @Override protected void onPostExecute(List<Pair<ContactInfo, String>> guestsList) {
        guestsListAdapter.setGuestsList(guestsList);
      }
    }.execute();
  }

  private List<Pair<ContactInfo, String>> getGuestsWithResponse(
      List<Invitee> invitees, final String userPhoneNumber) {
    List<Pair<ContactInfo, String>> guestsList = Lists.transform(invitees,
        new Function<Invitee, Pair<ContactInfo, String>>() {
          @Nullable @Override public Pair<ContactInfo, String> apply(Invitee invitee) {
            if (PhoneNumberUtils.compare(invitee.getPhoneNumber(), userPhoneNumber)) {
              // Ignoring user's phone number in guests list.
              return null;
            }
            ContactInfo contactInfo = ContactFetcher.getInstance()
                .getContactInfoByNumber(invitee.getPhoneNumber(), EventDetailsActivity.this);
            if (Strings.isNullOrEmpty(contactInfo.getDisplayName())) {
              contactInfo.setDisplayName(invitee.getPhoneNumber());
            }
            return Pair.create(contactInfo, invitee.getResponse());
          }
        });
    guestsList.removeAll(Collections.<Pair<ContactInfo, String>>singleton(null));
    List<Pair<ContactInfo, String>> filteredGuestsList = new ArrayList<>(guestsList);

    Collections.sort(filteredGuestsList, new Comparator<Pair<ContactInfo, String>>() {
      @Override public int compare(Pair<ContactInfo, String> lhs, Pair<ContactInfo, String> rhs) {
        int lhsInt = 0;
        if (RESPONSE_WEIGHT.containsKey(lhs.second)) {
          lhsInt = RESPONSE_WEIGHT.get(lhs.second);
        }
        int rhsInt = 0;
        if (RESPONSE_WEIGHT.containsKey(rhs.second)) {
          lhsInt = RESPONSE_WEIGHT.get(rhs.second);
        }
        return lhsInt < rhsInt ? -1 : (lhsInt == rhsInt ? 0 : 1);
      }
    });
    return filteredGuestsList;
  }

  private void fetchEvent(final long eventId, final long userId) {
    new AsyncTask<Void, Void, EventDetails>() {
      @Override protected EventDetails doInBackground(Void... params) {
        try {
          eventDetails = EventServiceClient.getInstance(EventDetailsActivity.this)
              .getEventDetailsWithCaching(eventId, userId);
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
