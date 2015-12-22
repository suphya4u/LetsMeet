package com.letsmeet.android.activity.adapter;

import android.content.Context;
import android.content.Intent;

import com.google.common.base.Strings;
import com.letsmeet.android.R;

import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.letsmeet.android.activity.ChatActivity;
import com.letsmeet.android.activity.EventDetailsActivity;
import com.letsmeet.android.common.DateTimeUtils;
import com.letsmeet.android.config.Constants;
import com.letsmeet.android.storage.chat.ChatStore;
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
    private TextView eventTimeView;
    private TextView eventLocationView;
    private RsvpButtonsView rsvpButtonsView;
    private ImageView chatIcon;
    private TextView unreadChatsView;
    private LinearLayout unreadChatsLayout;

    public EventCompactViewHolder(final View itemView) {
      super(itemView);
      nameView = (TextView) itemView.findViewById(R.id.event_name);
      eventTimeView = (TextView) itemView.findViewById(R.id.event_time);
      eventLocationView = (TextView) itemView.findViewById(R.id.event_location);
      rsvpButtonsView = (RsvpButtonsView) itemView.findViewById(R.id.rsvp_buttons);
      chatIcon = (ImageView) itemView.findViewById(R.id.chat_icon);
      unreadChatsView = (TextView) itemView.findViewById(R.id.unread_chats_count);
      unreadChatsLayout = (LinearLayout) itemView.findViewById(R.id.unread_chats_layout);
    }

    public void setEvent(final EventDetails eventDetails) {
      eventId = eventDetails.getEventId();
      nameView.setText(eventDetails.getName());
      if (eventDetails.getLocation() != null) {
        eventLocationView.setText(eventDetails.getLocation().getPlaceAddress());
      }
      if (eventDetails.getEventTimeMillis() > 0) {
        eventTimeView.setText(DateTimeUtils.getDisplayDateTime(itemView.getContext(),
            eventDetails.getEventTimeMillis()));
      }
      rsvpButtonsView.setEventId(eventId);
      rsvpButtonsView.setSelection(eventDetails.getMyResponse());

      int unreadChatCount = ChatStore.getUnreadCount(itemView.getContext(), eventId);
      if (unreadChatCount > 0) {
        unreadChatsView.setVisibility(View.VISIBLE);
        unreadChatsView.setText("(" + unreadChatCount + ")");
        chatIcon.setColorFilter(
            itemView.getContext().getResources().getColor(R.color.unread_chats));
      } else {
        unreadChatsView.setVisibility(View.GONE);
        chatIcon.setColorFilter(
            itemView.getContext().getResources().getColor(R.color.no_chats));
      }

      unreadChatsLayout.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          openChatActivity(v.getContext(), eventId, eventDetails.getName());
        }
      });

    }

    public void onClick(View view) {
      Intent intent = new Intent(view.getContext(), EventDetailsActivity.class);
      intent.putExtra(Constants.INTENT_EVENT_ID_KEY, String.valueOf(eventId));
      view.getContext().startActivity(intent);
    }

    private void openChatActivity(Context context, long eventId, String eventName) {
      TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
      stackBuilder.addParentStack(EventDetailsActivity.class);

      Intent eventDetailsIntent = new Intent(context, EventDetailsActivity.class);
      eventDetailsIntent.putExtra(Constants.INTENT_EVENT_ID_KEY, String.valueOf(eventId));
      stackBuilder.addNextIntent(eventDetailsIntent);

      Intent chatIntent = new Intent(context, ChatActivity.class);
      chatIntent.putExtra(Constants.INTENT_EVENT_ID_KEY, String.valueOf(eventId));
      chatIntent.putExtra(Constants.INTENT_EVENT_NAME_KEY,
          Strings.isNullOrEmpty(eventName) ? "Chats" : eventName);
      stackBuilder.addNextIntent(chatIntent);

      stackBuilder.startActivities();
    }
  }
}
