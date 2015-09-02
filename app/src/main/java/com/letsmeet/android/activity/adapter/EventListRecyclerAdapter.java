package com.letsmeet.android.activity.adapter;

import android.content.Intent;
import com.letsmeet.android.R;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.letsmeet.android.activity.EventDetailsActivity;
import com.letsmeet.android.config.Constants;
import com.letsmeet.server.eventService.model.EventDetails;

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
    EventDetails event = eventList.get(i);
    viewHolder.setEventName(event.getName());
    viewHolder.setEventNotes(event.getNotes());
    viewHolder.setEventTime(event.getEventTimeMillis());
    viewHolder.setEventId(event.getEventId());
  }

  @Override public int getItemCount() {
    if (eventList == null) {
      return 0;
    }
    return eventList.size();
  }

  public class EventCompactViewHolder extends RecyclerView.ViewHolder {

    private TextView nameView;
    private TextView notesView;
    private TextView eventTimeView;
    private long eventId;

    public EventCompactViewHolder(View itemView) {
      super(itemView);
      nameView = (TextView) itemView.findViewById(R.id.event_name);
      notesView = (TextView) itemView.findViewById(R.id.event_notes);
      eventTimeView = (TextView) itemView.findViewById(R.id.event_time);
    }

    // TODO(suhas): Maybe replace all below set methods with one method - setEventDetails()
    public void setEventTime(long eventTimeMillis) {
      Calendar eventTime = Calendar.getInstance(TimeZone.getDefault());
      eventTime.setTimeInMillis(eventTimeMillis);
      String eventDateString = DateFormat.getDateFormat(itemView.getContext())
          .format(eventTime.getTime());
      String eventTimeString = DateFormat.getTimeFormat(itemView.getContext())
          .format(eventTime.getTime());
      eventTimeView.setText(eventDateString + " " + eventTimeString);
    }

    public void setEventName(String eventName) {
      nameView.setText(eventName);
    }

    public void setEventNotes(String eventNotes) {
      notesView.setText(eventNotes);
    }

    public void setEventId(long eventId) {
      this.eventId = eventId;
    }

    public void onClick(View view) {
      Intent intent = new Intent(view.getContext(), EventDetailsActivity.class);
      intent.putExtra(Constants.EVENT_ID_KEY, String.valueOf(eventId));
      view.getContext().startActivity(intent);
    }
  }
}
