package com.letsmeet.android.activity.adapter;

import android.letsmeet.com.letsmeet.R;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.letsmeet.server.eventService.model.EventDetails;

import java.util.List;

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
    viewHolder.nameView.setText(event.getName());
    viewHolder.notesView.setText(event.getNotes());
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

    public EventCompactViewHolder(View itemView) {
      super(itemView);
      nameView = (TextView) itemView.findViewById(R.id.event_name);
      notesView = (TextView) itemView.findViewById(R.id.event_notes);
    }

    public void onClick(View view) {
      // TODO(suhas): Implement this.
    }
  }
}
