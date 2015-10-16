package com.letsmeet.android.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.letsmeet.android.R;

import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.letsmeet.android.common.PhoneNumberHelper;
import com.letsmeet.android.config.Config;
import com.letsmeet.android.config.Constants;
import com.letsmeet.android.gcm.GcmRegistrationHandler;
import com.letsmeet.android.storage.LocalStore;
import com.letsmeet.android.verification.SmsVerifier;

import java.io.IOException;


public class RegisterActivity extends AppCompatActivity {

  private BroadcastReceiver verificationCompleteReceiver = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      Toast.makeText(RegisterActivity.this, "Verification complete", Toast.LENGTH_LONG).show();
      navigateToMain();
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override protected void onStart() {
    super.onStart();
    IntentFilter filter = new IntentFilter();
    filter.addAction(Constants.VERIFICATION_COMPLETE_BROADCAST);
    registerReceiver(verificationCompleteReceiver, filter);

    LocalStore localStore = LocalStore.getInstance(this);
    if (localStore.isRegistered()) {
      if (localStore.isPhoneVerified()) {
        navigateToMain();
      } else {
        renderPendingVerification();
      }
    } else {
      renderRegistration();
    }
  }

  @Override protected void onStop() {
    try {
      unregisterReceiver(verificationCompleteReceiver);
    } catch (IllegalArgumentException e) {
      // Ignore. Thrown when receiver is already unregistered.
    }
    super.onStop();
  }

  private void renderRegistration() {
    setContentView(R.layout.activity_register);

    final Button button = (Button) findViewById(R.id.register_button);
    button.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        EditText nameEditText = (EditText) findViewById(R.id.registration_name);
        EditText phoneEditText = (EditText) findViewById(R.id.registration_phone);
        String name = nameEditText.getText().toString();
        String formattedPhoneNumber;
        if (Config.isEmulator()) {
          formattedPhoneNumber = phoneEditText.getText().toString();
        } else {
          PhoneNumberHelper phoneNumberHelper = new PhoneNumberHelper(RegisterActivity.this);
          formattedPhoneNumber = phoneNumberHelper
              .formatPhoneNumber(phoneEditText.getText().toString());
        }
        registerUser(name, formattedPhoneNumber);
      }
    });
  }

  private void registerUser(final String name, final String phone) {
    final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
    progressDialog.setCancelable(false);
    progressDialog.show();

    new AsyncTask<Void, Void, Void>() {

      @Override protected Void doInBackground(Void... params) {
        try {
          String registrationId = GcmRegistrationHandler.getRegistrationId(RegisterActivity.this);
          LocalStore localStore = LocalStore.getInstance(RegisterActivity.this);
          localStore.saveUserData(name, phone, registrationId);
          localStore.setVerificationStarted();
          SmsVerifier.getInstance().verifyPhoneNumber(RegisterActivity.this, phone);
        } catch (IOException e) {
          // TODO(suhas): Handle gracefully.
          throw new RuntimeException(e);
        }
        return null;
      }

      @Override
      protected void onPostExecute(Void response) {
        progressDialog.dismiss();
        renderPendingVerification();
      }
    }.execute();
  }

  private void renderPendingVerification() {
    setContentView(R.layout.activity_home_pending_verification);
    final Button registerAgainButton = (Button) findViewById(R.id.register_again_button);
    registerAgainButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        renderRegistration();
      }
    });
  }

  private void navigateToMain() {
    Intent intent = new Intent(this, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
  }
}
