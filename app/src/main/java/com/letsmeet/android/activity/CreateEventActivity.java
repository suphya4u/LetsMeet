package com.letsmeet.android.activity;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.common.base.Function;
import com.letsmeet.android.R;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.letsmeet.android.activity.fragments.ShareOptionsDialogFragment;
import com.letsmeet.android.common.ContactFetcher;
import com.letsmeet.android.common.DateTimeUtils;
import com.letsmeet.android.config.Constants;
import com.letsmeet.android.widgets.datetime.DateTimePicker;
import com.letsmeet.android.widgets.contactselect.SelectContactFragment;
import com.letsmeet.android.apiclient.EventServiceClient;
import com.letsmeet.android.common.PhoneNumberHelper;
import com.letsmeet.android.storage.LocalStore;
import com.letsmeet.android.widgets.contactselect.ContactInfo;
import com.letsmeet.android.widgets.datetime.DateTimePickerView;
import com.letsmeet.android.widgets.placeselect.PlaceInfo;
import com.letsmeet.android.widgets.placeselect.PlaceSelectView;
import com.letsmeet.server.eventService.model.CreateOrEditEventRequest;
import com.letsmeet.server.eventService.model.CreateOrEditEventResponse;
import com.letsmeet.server.eventService.model.EventDetails;
import com.letsmeet.server.eventService.model.EventLocation;
import com.letsmeet.server.eventService.model.Invitee;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

public class CreateEventActivity extends AppCompatActivity {

  private EventDetails eventDetails;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_event);

    long eventId = 0;
    try {
      String eventIdString = getIntent().getStringExtra(Constants.INTENT_EVENT_ID_KEY);
      if (!Strings.isNullOrEmpty(eventIdString)) {
        eventId = Long.parseLong(eventIdString);
      }
    } catch (NumberFormatException e) {
      // Log to analytics.
    }
    if (eventId != 0) {
      // We are in edit mode. Fetch event and pre-populate all fields.
      LocalStore localStore = LocalStore.getInstance(this);
      fetchEvent(eventId, localStore.getUserId());
      // TODO(suhas): Update title, button and other string to reflect edit mode.
    }

    final PlaceSelectView placeSelectView = (PlaceSelectView) findViewById(R.id.place_autocomplete);
    placeSelectView.init(this);

    final Button createEventButton = (Button) findViewById(R.id.create_event_button);
    createEventButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        // TODO(suhas): Too big click handler. re-factor.
        boolean hasErrors = false;

        EditText nameEditText = (EditText) findViewById(R.id.create_event_name);
        String name = nameEditText.getText().toString();
        if (Strings.isNullOrEmpty(name)) {
          hasErrors = true;
          nameEditText.setError(getString(R.string.cannot_be_empty));
        }

        EditText notesEditText = (EditText) findViewById(R.id.create_event_notes);
        String notes = notesEditText.getText().toString();

        LocalStore localStore = LocalStore.getInstance(CreateEventActivity.this);
        long userId = localStore.getUserId();
        if (eventDetails == null) {
          eventDetails = new EventDetails();
        }
        eventDetails
            .setName(name)
            .setNotes(notes)
            .setOwnerId(userId);

        FragmentManager fragmentManager = getFragmentManager();
        SelectContactFragment contactFragment =
            (SelectContactFragment) fragmentManager.findFragmentById(R.id.select_contact_fragment);
        List<Invitee> inviteePhoneNumbers = getInviteePhoneNumbers(contactFragment);
        eventDetails.setInviteePhoneNumbers(inviteePhoneNumbers);
        if (inviteePhoneNumbers.isEmpty()) {
          hasErrors = true;
          contactFragment.setError(getString(R.string.must_add_guests));
        }

        DateTimePickerView dateTimePickerView = (DateTimePickerView) getFragmentManager()
            .findFragmentById(R.id.date_time_picker_fragment);
        long selectedTime = dateTimePickerView.getSelectedTime();
        if (selectedTime < new Date().getTime()) {
          hasErrors = true;
          Button selectedTimeView = (Button) findViewById(R.id.selected_time);
          selectedTimeView.setError(getString(R.string.cannot_be_past));
        }

        PlaceInfo selectedPlace = placeSelectView.getSelectedPlace();
        if (Strings.isNullOrEmpty(selectedPlace.getAddress())) {
          hasErrors = true;
          placeSelectView.setError(getString(R.string.cannot_be_empty));
        }
        EventLocation location = new EventLocation()
            .setPlaceId(selectedPlace.getPlaceId())
            .setPlaceAddress(selectedPlace.getAddress());
        eventDetails.setLocation(location);

        if (!hasErrors) {
          createEvent(eventDetails);
        }
      }
    });
  }

  private void createEvent(final EventDetails eventDetails) {
    final CreateOrEditEventRequest request =
        new CreateOrEditEventRequest().setEventDetails(eventDetails);
    new AsyncTask<CreateOrEditEventRequest, Void, CreateOrEditEventResponse>() {
      @Override protected CreateOrEditEventResponse doInBackground(
          CreateOrEditEventRequest... params) {
        try {
          return  EventServiceClient.getInstance(CreateEventActivity.this).createEvent(request);
        } catch (IOException e) {
          // Log to analytics
        }
        return null;
      }

      @Override protected void onPostExecute(CreateOrEditEventResponse response) {
        if (response == null) {
          Toast.makeText(CreateEventActivity.this,
              "Failed to connect server. Please check your network connection",
              Toast.LENGTH_SHORT).show();
          finish();
          return;
        }

        List<String> phoneNumbersNotYetRegistered = response.getPhoneNumbersNotYetRegistered();
        if (phoneNumbersNotYetRegistered != null && !phoneNumbersNotYetRegistered.isEmpty()) {
          ShareOptionsDialogFragment dialog = new ShareOptionsDialogFragment();
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
        try {
          return EventServiceClient.getInstance(CreateEventActivity.this)
              .getEventDetailsWithCaching(eventId, userId);
        } catch (IOException e) {
          // Log to analytics.
        }
        return null;
      }

      @Override protected void onPostExecute(EventDetails eventDetails) {
        if (eventDetails == null) {
          Toast.makeText(CreateEventActivity.this,
              "Failed to connect server. Please check your network connection",
              Toast.LENGTH_SHORT).show();
        } else {
          populateEventDetails(eventDetails);
        }
        progressDialog.dismiss();
      }
    }.execute();
  }

  private void populateEventDetails(EventDetails eventDetails) {
    this.eventDetails = eventDetails;
    EditText nameEditText = (EditText) findViewById(R.id.create_event_name);
    EditText notesEditText = (EditText) findViewById(R.id.create_event_notes);
    final PlaceSelectView placeSelectView = (PlaceSelectView) findViewById(R.id.place_autocomplete);

    SelectContactFragment contactFragment = (SelectContactFragment)
        getFragmentManager().findFragmentById(R.id.select_contact_fragment);

    nameEditText.setText(eventDetails.getName());
    notesEditText.setText(eventDetails.getNotes());
    if (eventDetails.getLocation() != null) {
      PlaceInfo placeInfo = new PlaceInfo()
          .setPlaceId(eventDetails.getLocation().getPlaceId())
          .setAddress(eventDetails.getLocation().getPlaceAddress());
      placeSelectView.setSelectedPlace(placeInfo);
    }
    if (eventDetails.getInviteePhoneNumbers() != null
        && !eventDetails.getInviteePhoneNumbers().isEmpty()) {
      // Ignoring owner phone number as assumption is that only owner of the event will be in
      // this code path.
      contactFragment.setSelectedContacts(
          transformToContactInfos(eventDetails.getOwnerPhoneNumber(),
              eventDetails.getInviteePhoneNumbers()));
    }

    setTitle(R.string.edit_event_title);

    final Button createEventButton = (Button) findViewById(R.id.create_event_button);
    createEventButton.setText(R.string.edit_event_button);

    DateTimePickerView dateTimePickerView = (DateTimePickerView) getFragmentManager()
        .findFragmentById(R.id.date_time_picker_fragment);
    Calendar eventTimeCalendar = Calendar.getInstance();
    eventTimeCalendar.setTimeInMillis(eventDetails.getEventTimeMillis());
    dateTimePickerView.setDateTime(eventTimeCalendar);
  }

  private List<ContactInfo> transformToContactInfos(final String ignoreNumber,
      List<Invitee> invitees) {
    List<ContactInfo> contactInfos = Lists.transform(
        invitees, new Function<Invitee, ContactInfo>() {
          @Nullable @Override public ContactInfo apply(Invitee invitee) {
            if (invitee.getPhoneNumber() == null
                || invitee.getPhoneNumber().equals(ignoreNumber)) {
              return null;
            }
            return ContactFetcher.getInstance().getContactInfoByNumber(
                invitee.getPhoneNumber(), CreateEventActivity.this);
          }
        });
    contactInfos.removeAll(Collections.<ContactInfo>singleton(null));
    return contactInfos;
  }

  private List<Invitee> getInviteePhoneNumbers(SelectContactFragment contactFragment) {
    List<ContactInfo> selectedContacts = contactFragment.getSelectedContacts();

    // EventDetails#setInviteePhoneNumbers is deleted and replaced with addInviteePhoneNumber.
    // However client lib is somehow not updated. So continue using set.
    List<Invitee> inviteePhoneNumbers = Lists.newArrayList();
    PhoneNumberHelper phoneNumberHelper = new PhoneNumberHelper(CreateEventActivity.this);
    for (ContactInfo contact : selectedContacts) {
      String phoneNumber = phoneNumberHelper.formatPhoneNumber(contact.getPhoneNumber());
      Invitee invitee = new Invitee().setPhoneNumber(phoneNumber);
      inviteePhoneNumbers.add(invitee);
    }
    return inviteePhoneNumbers;
  }
}
