package com.letsmeet.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import com.letsmeet.android.R;

public class HomeActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
    final Button createEventButton = (Button) findViewById(R.id.new_event_button);
    createEventButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        startActivity(new Intent(HomeActivity.this, CreateEventActivity.class));
      }
    });

    Button newHomeButton = (Button) findViewById(R.id.show_new_home);
    newHomeButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        startActivity(new Intent(HomeActivity.this, MainActivity.class));
      }
    });

    final Button btnListEvents = (Button) findViewById(R.id.list_events_button);
    btnListEvents.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        startActivity(new Intent(HomeActivity.this, EventListActivity.class));
      }
    });
  }

}
