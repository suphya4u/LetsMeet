package com.letsmeet.android.activity;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.letsmeet.android.R;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.letsmeet.android.activity.fragments.ShareOptionsDialogFragment;
import com.letsmeet.android.common.DateTimeUtils;
import com.letsmeet.android.config.Constants;
import com.letsmeet.android.widgets.datetime.DateTimePicker;
import com.letsmeet.android.widgets.contactselect.SelectContactFragment;
import com.letsmeet.android.apiclient.EventServiceClient;
import com.letsmeet.android.common.PhoneNumberHelper;
import com.letsmeet.android.storage.LocalStore;
import com.letsmeet.android.widgets.contactselect.ContactInfo;
import com.letsmeet.android.widgets.placeselect.PlaceInfo;
import com.letsmeet.android.widgets.placeselect.PlaceSelectView;
import com.letsmeet.server.eventService.model.CreateOrEditEventRequest;
import com.letsmeet.server.eventService.model.CreateOrEditEventResponse;
import com.letsmeet.server.eventService.model.EventDetails;
import com.letsmeet.server.eventService.model.EventLocation;
import com.letsmeet.server.eventService.model.Invitee;

import java.util.Calendar;
import java.util.List;

public class CreateEventActivity extends FragmentActivity {

  private static final int PLACE_PICKER_REQUEST_CODE = 1;

  private long eventTimeSelected = 0;
  private EventDetails eventDetails;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_event);

    long eventId = 0;
    try {
      String eventIdString = getIntent().getStringExtra(Constants.EVENT_ID_KEY);
      if (!Strings.isNullOrEmpty(eventIdString)) {
        eventId = Long.parseLong(eventIdString);
      }
    } catch (NumberFormatException e) {
    }
    if (eventId != 0) {
      // We are in edit mode. Fetch event and pre-populate all fields.
      LocalStore localStore = LocalStore.getInstance(this);
      fetchEvent(eventId, localStore.getUserId());
      // TODO(suhas): Update title, button and other string to reflect edit mode.
    }

    final PlaceSelectView placeSelectView = (PlaceSelectView) findViewById(R.id.place_autocomplete);
    placeSelectView.init(this);

    final Button pickAPlaceButton = (Button) findViewById(R.id.place_picker_button);
    pickAPlaceButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent placePickerIntent;
        try {
          placePickerIntent = new PlacePicker.IntentBuilder().build(CreateEventActivity.this);
        } catch (GooglePlayServicesRepairableException
            | GooglePlayServicesNotAvailableException e) {
          Toast.makeText(CreateEventActivity.this, "PlaceService exception", Toast.LENGTH_SHORT)
              .show();
          return;
        }
        startActivityForResult(placePickerIntent, PLACE_PICKER_REQUEST_CODE);
      }
    });

    final TextView selectedDateTime = (TextView) findViewById(R.id.selected_date_time);
    final Button pickTimeButton = (Button) findViewById(R.id.pick_time_button);
    pickTimeButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        DateTimePicker dateTimePicker = new DateTimePicker(
            getFragmentManager(), "EventDateTimePicker");
        dateTimePicker.setDateTimeSetListener(new DateTimePicker.OnDateTimeSetListener() {
          @Override public void onDateTimeSet(Calendar timeSelected) {
            setEventTime(timeSelected);
            selectedDateTime.setText(DateTimeUtils.getDisplayDateTime(
                CreateEventActivity.this, timeSelected));
            pickTimeButton.setText(R.string.change_date_time_button);
          }
        });
        dateTimePicker.show();
      }
    });

    final Button createEventButton = (Button) findViewById(R.id.create_event_button);
    createEventButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        // TODO(suhas): Too big click handler. re-factor.
        EditText nameEditText = (EditText) findViewById(R.id.create_event_name);
        EditText notesEditText = (EditText) findViewById(R.id.create_event_notes);
        FragmentManager fragmentManager = getFragmentManager();
        SelectContactFragment contactFragment =
            (SelectContactFragment) fragmentManager.findFragmentById(R.id.select_contact_fragment);
        String name = nameEditText.getText().toString();
        String notes = notesEditText.getText().toString();
        // TODO(suhas): Validate input.
        LocalStore localStore = LocalStore.getInstance(CreateEventActivity.this);
        long userId = localStore.getUserId();
        if (eventDetails == null) {
          eventDetails = new EventDetails();
        }
        eventDetails
            .setName(name)
            .setNotes(notes)
            .setOwnerId(userId);
        List<ContactInfo> selectedContacts = contactFragment.getSelectedContacts();

        // EventDetails#setInviteePhoneNumbers is deleted and replaced with addInviteePhoneNumber.
        // However client lib is somehow not updated. So continue using set.
        // TODO(suhas): Investigate and fix.
        List<Invitee> inviteePhoneNumbers = Lists.newArrayList();
        PhoneNumberHelper phoneNumberHelper = new PhoneNumberHelper(CreateEventActivity.this);
        for (ContactInfo contact : selectedContacts) {
          String phoneNumber = phoneNumberHelper.formatPhoneNumber(contact.getPhoneNumber());
          Invitee invitee = new Invitee().setPhoneNumber(phoneNumber);
          inviteePhoneNumbers.add(invitee);
        }
        eventDetails.setInviteePhoneNumbers(inviteePhoneNumbers);
        eventDetails.setEventTimeMillis(eventTimeSelected);
        PlaceInfo selectedPlace = placeSelectView.getSelectedPlace();
        EventLocation location = new EventLocation()
            .setPlaceId(selectedPlace.getPlaceId())
            .setPlaceAddress(selectedPlace.getAddress());
        eventDetails.setLocation(location);
        createEvent(eventDetails);
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_create_event, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }


  // TODO(suhas): This piece of code should be in the ApiClient.
  private void createEvent(final EventDetails eventDetails) {
    final CreateOrEditEventRequest request =
        new CreateOrEditEventRequest().setEventDetails(eventDetails);
    new AsyncTask<CreateOrEditEventRequest, Void, CreateOrEditEventResponse>() {
      @Override protected CreateOrEditEventResponse doInBackground(
          CreateOrEditEventRequest... params) {
        return  EventServiceClient.getInstance().createEvent(request);
      }

      @Override protected void onPostExecute(CreateOrEditEventResponse response) {
        List<String> phoneNumbersNotYetRegistered = response.getPhoneNumbersNotYetRegistered();
        if (phoneNumbersNotYetRegistered != null && !phoneNumbersNotYetRegistered.isEmpty()) {
          ShareOptionsDialogFragment dialog = new ShareOptionsDialogFragment();
          // TODO(suhas): Show names instead of phone numbers in dialog.
          dialog.setSharingDetails(phoneNumbersNotYetRegistered, eventDetails);
          dialog.show(getFragmentManager(), "ShareOptionsDialogFragment");
        } else {
          finish();
        }
      }
    }.execute(request);
  }

  private void fetchEvent(final long eventId, final long userId) {
    final ProgressDialog progressDialog = new ProgressDialog(this);
    progressDialog.setCancelable(false);
    progressDialog.show();

    new AsyncTask<Void, Void, EventDetails>() {

      @Override protected EventDetails doInBackground(Void... params) {
        return EventServiceClient.getInstance().GetEventDetails(eventId, userId);
      }

      @Override protected void onPostExecute(EventDetails eventDetails) {
        populateEventDetails(eventDetails);
        progressDialog.dismiss();
      }
    }.execute();
  }

  private void populateEventDetails(EventDetails eventDetails) {
    this.eventDetails = eventDetails;
    EditText nameEditText = (EditText) findViewById(R.id.create_event_name);
    EditText notesEditText = (EditText) findViewById(R.id.create_event_notes);

    nameEditText.setText(eventDetails.getName());
    notesEditText.setText(eventDetails.getNotes());

    setTitle(R.string.edit_event_title);

    final Button createEventButton = (Button) findViewById(R.id.create_event_button);
    createEventButton.setText(R.string.edit_event_button);

    // TODO(suhas): Populate all details.
  }

  private void setEventTime(Calendar timeSelected) {
    eventTimeSelected = timeSelected.getTimeInMillis();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (PLACE_PICKER_REQUEST_CODE == requestCode) {
      if (resultCode == RESULT_OK) {
        Place place = PlacePicker.getPlace(data, this);
        Toast.makeText(this, "Place picked: " + place.getAddress(), Toast.LENGTH_LONG).show();
      }
    }
  }
}
