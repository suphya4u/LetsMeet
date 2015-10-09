package com.letsmeet.android.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;

import com.google.common.collect.Maps;
import com.letsmeet.android.R;
import com.letsmeet.android.activity.fragments.EventListFragment;
import com.letsmeet.android.activity.fragments.NavigationDrawerFragment;
import com.letsmeet.android.activity.fragments.SendFeedbackFragment;
import com.letsmeet.android.common.MainContentFragmentSelector;
import com.letsmeet.android.storage.LocalStore;

import java.util.Map;

public class MainActivity extends AppCompatActivity
    implements NavigationDrawerFragment.NavigationDrawerCallbacks {

  private Map<MainContentFragmentSelector, Fragment> fragmentMap;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    LocalStore localStore = LocalStore.getInstance(this);
    if (!localStore.isPhoneVerified()) {
      navigateToRegistration();
      return;
    }

    renderEventsList();
  }

  @Override protected void onStart() {
    super.onStart();

    NotificationManager notificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.cancelAll();
  }

  private void renderEventsList() {
    fragmentMap = Maps.newHashMap();
    fragmentMap.put(MainContentFragmentSelector.UPCOMING_EVENTS, EventListFragment.newInstance(
        MainContentFragmentSelector.UPCOMING_EVENTS));
    fragmentMap.put(
        MainContentFragmentSelector.ALL_EVENTS, EventListFragment.newInstance(MainContentFragmentSelector.ALL_EVENTS));
    setContentView(R.layout.activity_main);

    final Button createEventButton = (Button) findViewById(R.id.new_event_button);
    createEventButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        startActivity(new Intent(MainActivity.this, CreateEventActivity.class));
      }
    });

    // Fragment managing the behaviors, interactions and presentation of the navigation drawer.
    NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)
        getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

    // Set up the drawer.
    mNavigationDrawerFragment.setUp(
        R.id.navigation_drawer,
        (DrawerLayout) findViewById(R.id.drawer_layout));
  }

  @Override
  public void onNavigationDrawerItemSelected(
      MainContentFragmentSelector mainContentFragmentSelector) {
    // update the main content by replacing fragments
    FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment selectedFragment = fragmentMap.get(MainContentFragmentSelector.UPCOMING_EVENTS);
    int titleId = R.string.app_name;
    switch (mainContentFragmentSelector) {
      case SEND_FEEDBACK:
        titleId = R.string.drawer_menu_title_send_feedback;
        selectedFragment = new SendFeedbackFragment();
        break;
      case ALL_EVENTS:
        titleId = R.string.drawer_menu_title_all_events;
        selectedFragment = fragmentMap.get(MainContentFragmentSelector.ALL_EVENTS);
        break;
      case UPCOMING_EVENTS:
      default:
        titleId = R.string.drawer_menu_title_upcoming_events;
    }

    setTitle(titleId);
    fragmentManager.beginTransaction()
        .replace(R.id.container, selectedFragment)
        .commit();
  }

  public void navigateToRegistration() {
    Intent intent = new Intent(this, RegisterActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
  }
}
