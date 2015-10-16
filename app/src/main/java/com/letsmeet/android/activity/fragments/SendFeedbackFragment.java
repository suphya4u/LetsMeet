package com.letsmeet.android.activity.fragments;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.letsmeet.android.R;
import com.letsmeet.android.apiclient.FeedbackServiceClient;
import com.letsmeet.android.storage.LocalStore;
import com.letsmeet.server.feedbackService.model.SendFeedbackResponse;

import java.io.IOException;

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

  @Override public void onResume() {
    switchToFeedbackMode();
  }

  private void switchToFeedbackMode() {
    getActivity().findViewById(R.id.send_feedback_text).setVisibility(View.GONE);
    getActivity().findViewById(R.id.feedback_form).setVisibility(View.VISIBLE);
  }

  private void switchToThankYouNote() {
    getActivity().findViewById(R.id.send_feedback_text).setVisibility(View.VISIBLE);
    getActivity().findViewById(R.id.feedback_form).setVisibility(View.GONE);
  }

  private void sendFeedback(final long userId, final String feedback, final String appVersion) {
    final ProgressDialog progressDialog = new ProgressDialog(getActivity());
    progressDialog.setCancelable(false);
    progressDialog.show();

    new AsyncTask<Void, Void, SendFeedbackResponse>() {
      @Override protected SendFeedbackResponse doInBackground(Void... params) {
        try {
          return FeedbackServiceClient.getInstance().sendFeedback(userId, feedback, appVersion);
        } catch (IOException e) {
          // Log to analytics.
        }
        return null;
      }

      @Override protected void onPostExecute(SendFeedbackResponse serverResponse) {
        progressDialog.dismiss();
        if (serverResponse == null) {
          Toast.makeText(getContext(),
              "Failed to connect server. Please check your network connection",
              Toast.LENGTH_SHORT).show();
        }
        switchToThankYouNote();
      }
    }.execute();
  }
}
