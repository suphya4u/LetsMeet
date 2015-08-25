package com.letsmeet.android.activity;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.letsmeet.com.letsmeet.R;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.letsmeet.android.activity.fragments.ShareOptionsDialogFragment;
import com.letsmeet.android.widgets.datetime.DateTimePicker;
import com.letsmeet.android.activity.fragments.SelectContactFragment;
import com.letsmeet.android.apiclient.EventServiceClient;
import com.letsmeet.android.common.PhoneNumberHelper;
import com.letsmeet.android.storage.LocalStore;
import com.letsmeet.android.widgets.ContactInfo;
import com.letsmeet.server.eventService.model.CreateEventRequest;
import com.letsmeet.server.eventService.model.CreateEventResponse;
import com.letsmeet.server.eventService.model.EventDetails;
import com.letsmeet.server.eventService.model.Invitee;

import java.util.Calendar;
import java.util.List;

public class CreateEventActivity extends AppCompatActivity {

  private long eventTimeSelected = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_event);

    final Button pickTimeButton = (Button) findViewById(R.id.pick_time_button);
    pickTimeButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        DateTimePicker dateTimePicker = new DateTimePicker(
            getFragmentManager(), "EventDateTimePicker");
        dateTimePicker.setDateTimeSetListener(new DateTimePicker.OnDateTimeSetListener() {
          @Override public void onDateTimeSet(Calendar timeSelected) {
            setEventTime(timeSelected);
          }
        });
        dateTimePicker.show();
      }
    });

    final Button createEventButton = (Button) findViewById(R.id.create_event_button);
    createEventButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
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
        EventDetails eventDetails = new EventDetails()
            .setName(name)
            .setNotes(notes)
            .setOwnerId(userId);
        ImmutableList<ContactInfo> selectedContacts = contactFragment.getSelectedContacts();

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
        createEvent(new CreateEventRequest().setEventDetails(eventDetails));
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
  private void createEvent(final CreateEventRequest request) {
    new AsyncTask<CreateEventRequest, Void, CreateEventResponse>() {
      @Override protected CreateEventResponse doInBackground(CreateEventRequest... params) {
        return  EventServiceClient.getInstance().createEvent(request);
      }

      @Override protected void onPostExecute(CreateEventResponse response) {
        List<String> phoneNumbersNotYetRegistered = response.getPhoneNumbersNotYetRegistered();
        if (!phoneNumbersNotYetRegistered.isEmpty()) {
          DialogFragment dialog = new ShareOptionsDialogFragment();
          dialog.show(getFragmentManager(), "ShareOptionsDialogFragment");
        }
      }
    }.execute(request);
  }

  private void setEventTime(Calendar timeSelected) {
    eventTimeSelected = timeSelected.getTimeInMillis();
  }
}
