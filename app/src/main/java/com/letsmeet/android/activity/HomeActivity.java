package com.letsmeet.android.activity;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.letsmeet.com.letsmeet.R;

import com.letsmeet.android.activity.adapter.EventListRecyclerAdapter;
import com.letsmeet.android.apiclient.EventServiceClient;
import com.letsmeet.android.storage.LocalStore;
import com.letsmeet.server.eventService.model.EventDetails;
import com.letsmeet.server.eventService.model.ListEventsForUserResponse;

public class HomeActivity extends AppCompatActivity {

  private RecyclerView eventListView;

  private long userId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    LocalStore localStore = LocalStore.getInstance(this);
    userId = localStore.getUserId();
    if (userId == 0) {
      Intent intent = new Intent(this, RegisterActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      startActivity(intent);
      return;
    }

    setContentView(R.layout.activity_home);
    final Button button = (Button) findViewById(R.id.new_event_button);
    button.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        startActivity(new Intent(HomeActivity.this, CreateEventActivity.class));
      }
    });

    eventListView = (RecyclerView) findViewById(R.id.events_list);
    eventListView.setLayoutManager(new LinearLayoutManager(this));
  }

  @Override
  protected void onResume() {
    super.onResume();
    // TODO(suhas): Prefer caching than fetching events on every resume.
    listEvents();
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

  // TODO(suhas): Fetching event list code should be moved to ApiClient.
  private void listEvents() {
    new AsyncTask<Long, Void, ListEventsForUserResponse>() {

      @Override protected ListEventsForUserResponse doInBackground(Long... params) {
        return EventServiceClient.getInstance().listEvents(userId);
      }

      @Override protected void onPostExecute(ListEventsForUserResponse response) {
        eventListView.setAdapter(new EventListRecyclerAdapter(response.getEventsList()));
      }
    }.execute();
  }
}
