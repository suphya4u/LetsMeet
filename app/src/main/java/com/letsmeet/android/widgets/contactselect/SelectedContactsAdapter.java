package com.letsmeet.android.widgets.contactselect;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.letsmeet.android.R;

import java.util.List;

/**
 * Adapter to manage selected contacts.
 */
public class SelectedContactsAdapter
    extends RecyclerView.Adapter<SelectedContactsAdapter.ContactInfoViewHolder> {

  private interface RemoveContactListener {
    void onContactRemove();
  }

  List<ContactInfo> selectedContacts;

  public SelectedContactsAdapter() {
    selectedContacts = Lists.newArrayList();
  }

  public List<ContactInfo> getSelectedContacts() {
    return ImmutableList.copyOf(selectedContacts);
  }

  public void addContact(ContactInfo contactInfo) {
    selectedContacts.add(contactInfo);
    notifyDataSetChanged();
  }

  @Override
  public SelectedContactsAdapter.ContactInfoViewHolder onCreateViewHolder(
      ViewGroup viewGroup, int viewType) {
    View view = LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.selected_contact_item, viewGroup, false);
    return new ContactInfoViewHolder(view);
  }

  @Override
  public void onBindViewHolder(SelectedContactsAdapter.ContactInfoViewHolder holder, final int position) {
    ContactInfo contactInfo = selectedContacts.get(position);
    holder.setContactInfo(contactInfo, new View.OnClickListener() {
      @Override public void onClick(View v) {
        selectedContacts.remove(position);
        notifyDataSetChanged();
      }
    });
  }

  @Override public int getItemCount() {
    return selectedContacts.size();
  }

  public class ContactInfoViewHolder extends RecyclerView.ViewHolder {

    private TextView nameView;
    private TextView phoneNumberView;
    private ImageView thumbnailView;
    private Button removeButton;

    public ContactInfoViewHolder(View itemView) {
      super(itemView);
      nameView = (TextView) itemView.findViewById(R.id.selected_contact_name);
      phoneNumberView = (TextView) itemView.findViewById(R.id.selected_contact_number);
      thumbnailView = (ImageView) itemView.findViewById(R.id.selected_contact_thumbnail);
      removeButton = (Button) itemView.findViewById(R.id.remove_selected_contact);
    }

    public void setContactInfo(ContactInfo contactInfo, View.OnClickListener removeListener) {
      nameView.setText(contactInfo.getDisplayName());
      phoneNumberView.setText(contactInfo.getPhoneNumber());
      setViewImage(thumbnailView, contactInfo.getThumbnailUrl());
      removeButton.setOnClickListener(removeListener);
    }

    private void setViewImage(ImageView v, String value) {
      if (Strings.isNullOrEmpty(value)) {
        // TODO(suhas): Set default image.
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
