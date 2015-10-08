package com.letsmeet.android.activity.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.letsmeet.android.R;
import com.letsmeet.android.apiclient.FeedbackServiceClient;
import com.letsmeet.android.storage.LocalStore;

/**
 * Fragment to send feedback.
 */
public class SendFeedbackFragment extends Fragment {

  public SendFeedbackFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    LocalStore localStore = LocalStore.getInstance(getActivity());
    final long userId = localStore.getUserId();

    PackageInfo pInfo = null;
    try {
      pInfo = getActivity().getPackageManager()
          .getPackageInfo(getActivity().getPackageName(), 0);
    } catch (PackageManager.NameNotFoundException e) {
      // TODO: Handle?
    }
    final String appVersion = pInfo == null ? "unknown" : pInfo.versionName;

    View rootView = inflater.inflate(R.layout.fragment_send_feedback, container, false);
    final EditText sendFeedbackText = (EditText) rootView.findViewById(R.id.send_feedback_text);
    Button sendFeedbackButton = (Button) rootView.findViewById(R.id.send_feedback_button);
    sendFeedbackButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        sendFeedback(userId, sendFeedbackText.getText().toString(), appVersion);
      }
    });
    return rootView;
  }

  private void sendFeedback(final long userId, final String feedback, final String appVersion) {
    final ProgressDialog progressDialog = new ProgressDialog(getActivity());
    progressDialog.setCancelable(false);
    progressDialog.show();

    new AsyncTask<Void, Void, Void>() {
      @Override protected Void doInBackground(Void... params) {
        FeedbackServiceClient.getInstance().sendFeedback(userId, feedback, appVersion);
        return null;
      }

      @Override protected void onPostExecute(Void aVoid) {
        progressDialog.dismiss();
        // TODO: Show your feedback received successfully message.
        // Hide edittext and send feedback button. Unhide it in createView method and hide the
        // message there.

      }
    }.execute();
  }
}
