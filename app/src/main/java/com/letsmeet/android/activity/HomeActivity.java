package com.letsmeet.android.activity;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.letsmeet.com.letsmeet.R;
import android.widget.Toast;

import com.letsmeet.android.activity.adapter.EventListRecyclerAdapter;
import com.letsmeet.android.apiclient.EventServiceClient;
import com.letsmeet.android.config.Config;
import com.letsmeet.android.config.Constants;
import com.letsmeet.android.storage.LocalStore;
import com.letsmeet.server.eventService.model.ListEventsForUserResponse;

public class HomeActivity extends AppCompatActivity {

  private RecyclerView eventListView;

  private BroadcastReceiver verificationCompleteReceiver = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      Toast.makeText(HomeActivity.this, "Verification complete", Toast.LENGTH_LONG).show();
      renderEventList();
    }
  };

  private long userId;

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
      if (Config.isEmulator() && !Config.isVerificationRequiredForEmulator()) {
        Toast.makeText(this,
            "Verification not yet complete, but continuing since you are in Emulator",
            Toast.LENGTH_LONG).show();
      } else {
        renderPendingVerification();
        return;
      }
    }

    renderEventList();
  }

  @Override
  protected void onResume() {
    super.onResume();
    // TODO(suhas): Prefer caching than fetching events on every resume.
    if (eventListView != null) {
      listEvents();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_home, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
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

  private void renderEventList() {
    LocalStore localStore = LocalStore.getInstance(this);
    userId = localStore.getUserId();
    setContentView(R.layout.activity_home);
    final Button createEventButton = (Button) findViewById(R.id.new_event_button);
    createEventButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        startActivity(new Intent(HomeActivity.this, CreateEventActivity.class));
      }
    });

    final Button btnListEvents = (Button) findViewById(R.id.list_events_button);
    btnListEvents.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        startActivity(new Intent(HomeActivity.this, EventListActivity.class));
      }
    });

    eventListView = (RecyclerView) findViewById(R.id.events_list);
    final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    eventListView.setLayoutManager(layoutManager);
    listEvents();
  }

  // TODO(suhas): Fetching event list code should be moved to ApiClient.
  private void listEvents() {
    new AsyncTask<Long, Void, ListEventsForUserResponse>() {

      @Override protected ListEventsForUserResponse doInBackground(Long... params) {
        return EventServiceClient.getInstance().listEvents(userId);
      }

      @Override protected void onPostExecute(ListEventsForUserResponse response) {
        // TODO(suhas): Do not create new adapter everytime. Update list in same adapter instead.
        eventListView.setAdapter(new EventListRecyclerAdapter(response.getEventsList()));
      }
    }.execute();
  }

  private void navigateToRegistration() {
    Intent intent = new Intent(this, RegisterActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
  }
}
