package com.letsmeet.android.activity;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import com.letsmeet.android.R;
import android.widget.Toast;

import com.letsmeet.android.config.Constants;
import com.letsmeet.android.storage.LocalStore;

public class HomeActivity extends AppCompatActivity {

  private BroadcastReceiver verificationCompleteReceiver = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      Toast.makeText(HomeActivity.this, "Verification complete", Toast.LENGTH_LONG).show();
      renderHomeScreen();
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    IntentFilter filter = new IntentFilter();
    filter.addAction(Constants.VERIFICATION_COMPLETE_BROADCAST);
    registerReceiver(verificationCompleteReceiver, filter);

    NotificationManager notificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.cancelAll();

    LocalStore localStore = LocalStore.getInstance(this);
    if (!localStore.isRegistered()) {
      navigateToRegistration();
      return;
    }

    if (!localStore.isPhoneVerified()) {
      renderPendingVerification();
      return;
    }

    renderHomeScreen();
  }

  @Override protected void onStop() {
    try {
      unregisterReceiver(verificationCompleteReceiver);
    } catch (IllegalArgumentException e) {
      // Ignore. Thrown when receiver is already unregistered.
    }
    super.onStop();
  }

  private void renderPendingVerification() {
    setContentView(R.layout.activity_home_pending_verification);
    final Button registerAgainButton = (Button) findViewById(R.id.register_again_button);
    registerAgainButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        navigateToRegistration();
      }
    });
  }

  private void renderHomeScreen() {
    setContentView(R.layout.activity_home);
    final Button createEventButton = (Button) findViewById(R.id.new_event_button);
    createEventButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        startActivity(new Intent(HomeActivity.this, CreateEventActivity.class));
      }
    });

    Button newHomeButton = (Button) findViewById(R.id.show_new_home);
    newHomeButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        startActivity(new Intent(HomeActivity.this, MainActivity.class));
      }
    });

    final Button btnListEvents = (Button) findViewById(R.id.list_events_button);
    btnListEvents.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        startActivity(new Intent(HomeActivity.this, EventListActivity.class));
      }
    });
  }

  private void navigateToRegistration() {
    Intent intent = new Intent(this, RegisterActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
  }
}
