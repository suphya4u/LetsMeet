package com.letsmeet.android.activity.adapter;

import android.content.Intent;
import com.letsmeet.android.R;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.letsmeet.android.activity.EventDetailsActivity;
import com.letsmeet.android.apiclient.EventServiceClient;
import com.letsmeet.android.common.DateTimeUtils;
import com.letsmeet.android.config.Constants;
import com.letsmeet.android.storage.LocalStore;
import com.letsmeet.server.eventService.model.EventDetails;
import com.letsmeet.server.eventService.model.RsvpRequest;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Recycle view adapter for events list.
 */
public class EventListRecyclerAdapter
    extends RecyclerView.Adapter<EventListRecyclerAdapter.EventCompactViewHolder> {

  private final List<EventDetails> eventList;

  public EventListRecyclerAdapter(List<EventDetails> eventList) {
    this.eventList = eventList;
  }

  @Override
  public EventListRecyclerAdapter.EventCompactViewHolder onCreateViewHolder(
      ViewGroup viewGroup, int i) {
    View view = LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.event_compact_view, viewGroup, false);
    final EventCompactViewHolder viewHolder = new EventCompactViewHolder(view);
    view.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        viewHolder.onClick(v);
      }
    });
    return viewHolder;
  }

  @Override public void onBindViewHolder(
      EventListRecyclerAdapter.EventCompactViewHolder viewHolder, int i) {
    viewHolder.setEvent(eventList.get(i));
  }

  @Override public int getItemCount() {
    if (eventList == null) {
      return 0;
    }
    return eventList.size();
  }

  public class EventCompactViewHolder extends RecyclerView.ViewHolder {

    private long eventId;
    private TextView nameView;
    private TextView notesView;
    private TextView eventTimeView;
    private TextView eventLocationView;
    private TextView myResponseView;

    public EventCompactViewHolder(View itemView) {
      super(itemView);
      nameView = (TextView) itemView.findViewById(R.id.event_name);
      notesView = (TextView) itemView.findViewById(R.id.event_notes);
      eventTimeView = (TextView) itemView.findViewById(R.id.event_time);
      eventLocationView = (TextView) itemView.findViewById(R.id.event_location);
      myResponseView = (TextView) itemView.findViewById(R.id.event_my_response);

      Button rsvpYesButton = (Button) itemView.findViewById(R.id.rsvp_yes);
      rsvpYesButton.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          rsvp("YES");
        }
      });

      Button rsvpMaybeButton = (Button) itemView.findViewById(R.id.rsvp_maybe);
      rsvpMaybeButton.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          rsvp("MAYBE");
        }
      });

      Button rsvpNoButton = (Button) itemView.findViewById(R.id.rsvp_no);
      rsvpNoButton.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          rsvp("NO");
        }
      });
    }

    public void setEvent(EventDetails eventDetails) {
      eventId = eventDetails.getEventId();
      nameView.setText(eventDetails.getName());
      notesView.setText(eventDetails.getNotes());
      if (eventDetails.getLocation() != null) {
        eventLocationView.setText(eventDetails.getLocation().getPlaceAddress());
      }
      myResponseView.setText("My Response: " + eventDetails.getMyResponse());

      Calendar eventTime = Calendar.getInstance(TimeZone.getDefault());
      eventTime.setTimeInMillis(eventDetails.getEventTimeMillis());
      eventTimeView.setText(DateTimeUtils.getDisplayDateTime(itemView.getContext(), eventTime));
    }

    public void onClick(View view) {
      Intent intent = new Intent(view.getContext(), EventDetailsActivity.class);
      intent.putExtra(Constants.EVENT_ID_KEY, String.valueOf(eventId));
      view.getContext().startActivity(intent);
    }

    private void rsvp(final String response) {
      LocalStore localStore = LocalStore.getInstance(itemView.getContext());
      final long userId = localStore.getUserId();
      new AsyncTask<Void, Void, Void>() {
        @Override protected Void doInBackground(Void... params) {
          RsvpRequest request = new RsvpRequest()
              .setUserId(userId)
              .setEventId(eventId)
              .setResponse(response);
          EventServiceClient.getInstance().rsvpEvent(request);
          return null;
        }
      }.execute();
    }
  }
}
