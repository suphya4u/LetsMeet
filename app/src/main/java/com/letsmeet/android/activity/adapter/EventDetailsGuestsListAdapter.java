package com.letsmeet.android.activity.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.letsmeet.android.R;
import com.letsmeet.android.widgets.contactselect.ContactInfo;
import com.letsmeet.server.eventService.model.RsvpResponse;

import java.util.List;

/**
 * Adapter to display guests list in event details.
 */
public class EventDetailsGuestsListAdapter
    extends RecyclerView.Adapter<EventDetailsGuestsListAdapter.GuestsListViewHolder>{

  List<Pair<ContactInfo, String>> guestsList;

  public void setGuestsList(List<Pair<ContactInfo, String>> guestsList) {
    this.guestsList = guestsList;
    notifyDataSetChanged();
  }

  @Override
  public EventDetailsGuestsListAdapter.GuestsListViewHolder onCreateViewHolder(
      ViewGroup viewGroup, int viewType) {
    View view = LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.event_details_guest_item, viewGroup, false);
    return new GuestsListViewHolder(view);
  }

  @Override public void onBindViewHolder(EventDetailsGuestsListAdapter.GuestsListViewHolder holder,
      int position) {
    holder.setGuest(guestsList.get(position));
  }

  @Override public int getItemCount() {
    return guestsList == null ? 0 : guestsList.size();
  }

  public class GuestsListViewHolder extends RecyclerView.ViewHolder {

    TextView phoneNumberView;
    TextView responseView;

    public GuestsListViewHolder(View itemView) {
      super(itemView);
      phoneNumberView = (TextView) itemView.findViewById(R.id.phone_number);
      responseView = (TextView) itemView.findViewById(R.id.rsvp_response);
    }

    public void setGuest(Pair<ContactInfo, String> contactInfoWithResponse) {
      phoneNumberView.setText(contactInfoWithResponse.first.getPhoneNumber());
      responseView.setText(contactInfoWithResponse.second);
    }
  }
}
