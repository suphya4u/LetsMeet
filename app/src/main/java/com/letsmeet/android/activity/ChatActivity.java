package com.letsmeet.android.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.letsmeet.android.R;
import com.letsmeet.android.config.Constants;

public class ChatActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat);

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
      Toast.makeText(this, "Could not find event id. Returning.", Toast.LENGTH_LONG).show();
      finish();
    }

    populateChatData(eventId);

    TextView view = (TextView) findViewById(R.id.test_content);
    view.setText(String.valueOf(eventId));
  }

  private void populateChatData(long eventId) {

  }
}
