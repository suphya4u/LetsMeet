package com.letsmeet.android.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.letsmeet.com.letsmeet.R;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.letsmeet.android.activity.adapter.ContactCompletionAdapter;
import com.letsmeet.android.apiclient.EventServiceClient;
import com.letsmeet.android.storage.LocalStore;
import com.letsmeet.server.eventService.model.CreateEventRequest;
import com.letsmeet.server.eventService.model.CreateEventResponse;
import com.letsmeet.server.eventService.model.EventDetails;

public class CreateEventActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_event);
    final Button button = (Button) findViewById(R.id.create_event_button);

    AutoCompleteTextView contactAutoComplete =
        (AutoCompleteTextView) findViewById(R.id.contact_autocomplete);
    ContactCompletionAdapter contactAdapter = new ContactCompletionAdapter(this);
    contactAutoComplete.setAdapter(contactAdapter);

    button.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        EditText nameEditText = (EditText) findViewById(R.id.create_event_name);
        EditText notesEditText = (EditText) findViewById(R.id.create_event_notes);
        String name = nameEditText.getText().toString();
        String notes = notesEditText.getText().toString();
        // TODO(suhas): Validate input.
        LocalStore localStore = LocalStore.getInstance(CreateEventActivity.this);
        long userId = localStore.getUserId();
        EventDetails eventDetails = new EventDetails()
            .setName(name)
            .setNotes(notes)
            .setOwnerId(userId);
        createEvent(new CreateEventRequest().setEventDetails(eventDetails));
        finish();
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
        return EventServiceClient.getInstance().createEvent(request);
      }
    }.execute(request);
  }
}
