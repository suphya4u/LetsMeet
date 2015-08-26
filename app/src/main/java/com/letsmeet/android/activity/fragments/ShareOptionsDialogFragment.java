package com.letsmeet.android.activity.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.letsmeet.com.letsmeet.R;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.letsmeet.android.verification.ShareInvitation;
import com.letsmeet.server.eventService.model.EventDetails;

import java.util.List;

/**
 * Dialog to show options to share event.
 */
public class ShareOptionsDialogFragment extends DialogFragment {

  private List<String> usersNotRegistered;
  private EventDetails eventDetails;

  public void setSharingDetails(List<String> usersNotRegistered, EventDetails eventDetails) {
    this.usersNotRegistered = usersNotRegistered;
    this.eventDetails = eventDetails;
  }

  @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    setCancelable(false);
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = getActivity().getLayoutInflater();
    View rootView = inflater.inflate(R.layout.share_options_dialog_fragment, null);
    builder.setView(rootView)
        .setCancelable(false)
        .setPositiveButton(R.string.share_by_sms, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int id) {
            Toast.makeText(getActivity(), "Sharing by Sms", Toast.LENGTH_LONG).show();
            new ShareInvitation().sendInvitationSms(usersNotRegistered, eventDetails);
            getActivity().finish();
          }
        })
        .setNegativeButton(R.string.share_other_channel, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            Toast.makeText(getActivity(), "Sharing on other channels", Toast.LENGTH_LONG).show();
            getActivity().finish();
          }
        });

    if (usersNotRegistered != null) {
      ListView userList = (ListView) rootView.findViewById(R.id.not_registered_users_list);
      userList.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,
          usersNotRegistered));
    }

    return builder.create();
  }
}
