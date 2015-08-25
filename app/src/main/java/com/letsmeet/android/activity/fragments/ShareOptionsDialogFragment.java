package com.letsmeet.android.activity.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.letsmeet.com.letsmeet.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

/**
 * Dialog to show options to share event.
 */
public class ShareOptionsDialogFragment extends DialogFragment {

  @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    setCancelable(false);
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = getActivity().getLayoutInflater();
    builder.setView(inflater.inflate(R.layout.share_options_dialog_fragment, null))
        .setCancelable(false)
        .setPositiveButton(R.string.share_by_sms, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int id) {
            Toast.makeText(getActivity(), "Sharing by Sms", Toast.LENGTH_LONG).show();
            getActivity().finish();
          }
        })
        .setNegativeButton(R.string.share_other_channel, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            Toast.makeText(getActivity(), "Sharing on other channels", Toast.LENGTH_LONG).show();
            getActivity().finish();
          }
        });
    return builder.create();
  }
}
