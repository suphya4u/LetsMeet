package com.letsmeet.android.widgets.rsvp;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.letsmeet.android.R;
import com.letsmeet.android.apiclient.EventServiceClient;
import com.letsmeet.android.storage.LocalStore;
import com.letsmeet.server.eventService.model.RsvpRequest;
import com.letsmeet.server.eventService.model.RsvpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * View to render and handle rsvp buttons.
 */
public class RsvpButtonsView extends LinearLayout {

  private long eventId = 0;
  private Map<String, Button> buttonMap = new HashMap<>();

  public RsvpButtonsView(Context context) {
    super(context);
    init();
  }

  public RsvpButtonsView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public RsvpButtonsView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  public void setEventId(long eventId) {
    this.eventId = eventId;
  }

  private void init() {
    inflate(getContext(), R.layout.fragment_rsvp_buttons, this);

    Button rsvpYesButton = (Button) findViewById(R.id.rsvp_yes);
    rsvpYesButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        rsvp(getContext(), "YES");
      }
    });
    buttonMap.put("YES", rsvpYesButton);

    Button rsvpMaybeButton = (Button) findViewById(R.id.rsvp_maybe);
    rsvpMaybeButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        rsvp(getContext(), "MAYBE");
      }
    });
    buttonMap.put("MAYBE", rsvpMaybeButton);

    Button rsvpNoButton = (Button) findViewById(R.id.rsvp_no);
    rsvpNoButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        rsvp(getContext(), "NO");
      }
    });
    buttonMap.put("NO", rsvpNoButton);
  }

  private void rsvp(final Context context, final String response) {
    LocalStore localStore = LocalStore.getInstance(getContext());
    final long userId = localStore.getUserId();
    new AsyncTask<Void, Void, RsvpResponse>() {
      @Override protected RsvpResponse doInBackground(Void... params) {
        RsvpRequest request = new RsvpRequest()
            .setUserId(userId)
            .setEventId(eventId)
            .setResponse(response);
        try {
          return EventServiceClient.getInstance(context).rsvpEvent(request);
        } catch (IOException e) {
          // Long to analytics.
        }
        return null;
      }

      @Override protected void onPostExecute(RsvpResponse serverResponse) {
        if (serverResponse == null) {
          Toast.makeText(context,
              "Failed to connect server. Please check your network connection",
              Toast.LENGTH_SHORT).show();
        }
        setSelection(response);
      }
    }.execute();
  }

  public void setSelection(String response) {
    for (Map.Entry<String, Button> buttonEntry : buttonMap.entrySet()) {
      String responseVal = buttonEntry.getKey();
      Button button = buttonEntry.getValue();
      if (responseVal.equals(response)) {
        button.setTextColor(getResources().getColor(R.color.selected_rsvp_button));
      } else {
        button.setTextColor(getResources().getColor(R.color.unselected_rsvp_button));
      }
    }
  }
}
