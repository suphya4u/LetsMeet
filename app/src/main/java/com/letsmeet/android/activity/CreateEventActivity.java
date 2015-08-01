package com.letsmeet.android.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.letsmeet.com.letsmeet.R;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.letsmeet.android.gcm.GcmRegistrationAsyncTask;

public class CreateEventActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_event);
    final Button button = (Button) findViewById(R.id.create_event_button);
    button.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        EditText nameEditText = (EditText) findViewById(R.id.create_event_name);
        EditText notesEditText = (EditText) findViewById(R.id.create_event_notes);
        String name = nameEditText.getText().toString();
        String notes = notesEditText.getText().toString();
        // TODO(suhas): Validate input.
        // Fire create event API.
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
}
