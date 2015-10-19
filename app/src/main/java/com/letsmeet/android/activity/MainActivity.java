package com.letsmeet.android.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
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
    setContentView(R.layout.activity_main);

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

    fragmentManager.beginTransaction()
        .replace(R.id.container, getFragment(mainContentFragmentSelector))
        .commit();
  }

  private Fragment getFragment(MainContentFragmentSelector selector) {
    Fragment fragment = fragmentMap.get(selector);
    if (fragment == null) {
      switch (selector) {
        case SEND_FEEDBACK:
          return new SendFeedbackFragment();
        default:
          selector = MainContentFragmentSelector.UPCOMING_EVENTS;
        case UPCOMING_EVENTS:
        case ALL_EVENTS:
          fragment = EventListFragment.newInstance(selector);
          fragmentMap.put(selector, fragment);
      }
    }
    return fragment;
  }

  public void navigateToRegistration() {
    Intent intent = new Intent(this, RegisterActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
  }
}
