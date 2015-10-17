package com.letsmeet.android.activity.adapter;

import android.content.Intent;
import com.letsmeet.android.R;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.letsmeet.android.activity.EventDetailsActivity;
import com.letsmeet.android.common.DateTimeUtils;
import com.letsmeet.android.config.Constants;
import com.letsmeet.android.widgets.rsvp.RsvpButtonsView;
import com.letsmeet.server.eventService.model.EventDetails;

import java.util.List;

/**
 * Recycle view adapter for events list.
 */
public class EventListRecyclerAdapter
    extends RecyclerView.Adapter<EventListRecyclerAdapter.EventCompactViewHolder> {

  private List<EventDetails> eventList;

  public EventListRecyclerAdapter(List<EventDetails> eventList) {
    this.eventList = eventList;
  }

  public void updateEventList(List<EventDetails> eventList) {
    this.eventList = eventList;
    notifyDataSetChanged();
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
    private RsvpButtonsView rsvpButtonsView;

    public EventCompactViewHolder(final View itemView) {
      super(itemView);
      nameView = (TextView) itemView.findViewById(R.id.event_name);
      notesView = (TextView) itemView.findViewById(R.id.event_notes);
      eventTimeView = (TextView) itemView.findViewById(R.id.event_time);
      eventLocationView = (TextView) itemView.findViewById(R.id.event_location);
      myResponseView = (TextView) itemView.findViewById(R.id.event_my_response);
      rsvpButtonsView = (RsvpButtonsView) itemView.findViewById(R.id.rsvp_buttons);
    }

    public void setEvent(EventDetails eventDetails) {
      eventId = eventDetails.getEventId();
      nameView.setText(eventDetails.getName());
      notesView.setText(eventDetails.getNotes());
      if (eventDetails.getLocation() != null) {
        eventLocationView.setText(eventDetails.getLocation().getPlaceAddress());
      }
      myResponseView.setText("My Response: " + eventDetails.getMyResponse());
      eventTimeView.setText(DateTimeUtils.getDisplayDateTime(itemView.getContext(),
          eventDetails.getEventTimeMillis()));
      rsvpButtonsView.setEventId(eventId);
    }

    public void onClick(View view) {
      Intent intent = new Intent(view.getContext(), EventDetailsActivity.class);
      intent.putExtra(Constants.EVENT_ID_KEY, String.valueOf(eventId));
      view.getContext().startActivity(intent);
    }
  }
}
