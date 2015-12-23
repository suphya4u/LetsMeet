package com.letsmeet.android.activity.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.letsmeet.android.R;
import com.letsmeet.android.widgets.contactselect.ContactInfo;

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

    ImageView thumbnailView;
    TextView phoneNumberView;
    TextView responseView;

    public GuestsListViewHolder(View itemView) {
      super(itemView);
      phoneNumberView = (TextView) itemView.findViewById(R.id.guest_contact_name);
      thumbnailView = (ImageView) itemView.findViewById(R.id.guest_contact_thumbnail);
      responseView = (TextView) itemView.findViewById(R.id.rsvp_response);
    }

    public void setGuest(Pair<ContactInfo, String> contactInfoWithResponse) {
      phoneNumberView.setText(contactInfoWithResponse.first.getDisplayName());
      setViewImage(thumbnailView, contactInfoWithResponse.first.getThumbnailUrl());
      responseView.setText(toDisplayString(contactInfoWithResponse.second));
    }

    private String toDisplayString(String responseEnumName) {
      switch (responseEnumName) {
        case "NO_RESPONSE":
          return "Waiting";
        case "YES":
          return "Yes";
        case "NO":
          return "No";
        case "MAYBE":
          return "Maybe";
      }
      return responseEnumName;
    }

    // TODO: Code duplication in SelectedContactsAdapter.
    private void setViewImage(ImageView v, String value) {
      if (Strings.isNullOrEmpty(value)) {
        v.setImageDrawable(null);
        return;
      }
      try {
        v.setImageResource(Integer.parseInt(value));
      } catch (NumberFormatException nfe) {
        v.setImageURI(Uri.parse(value));
      }
    }
  }
}
