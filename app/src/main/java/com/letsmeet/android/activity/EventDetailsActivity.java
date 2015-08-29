package com.letsmeet.android.activity;

import android.content.Intent;
import android.letsmeet.com.letsmeet.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.letsmeet.android.apiclient.EventServiceClient;
import com.letsmeet.android.config.Constants;
import com.letsmeet.android.storage.LocalStore;
import com.letsmeet.server.eventService.model.EventDetails;

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

  private void populateEventData() {
    TextView eventNameView = (TextView) findViewById(R.id.event_name);
    eventNameView.setText(eventDetails.getName());

    TextView eventNotesView = (TextView) findViewById(R.id.event_notes);
    eventNotesView.setText(eventDetails.getNotes());

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

  private void fetchEvent(final long eventId, final long userId) {
    new AsyncTask<Void, Void, Void>() {
      @Override protected Void doInBackground(Void... params) {
        eventDetails = EventServiceClient.getInstance().GetEventDetails(eventId, userId);
        return null;
      }

      @Override protected void onPostExecute(Void aVoid) {
        populateEventData();
      }
    }.execute();
  }
}
