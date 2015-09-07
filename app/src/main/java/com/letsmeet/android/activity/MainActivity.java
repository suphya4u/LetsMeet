package com.letsmeet.android.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.widget.DrawerLayout;

import com.google.common.collect.Maps;
import com.letsmeet.android.R;
import com.letsmeet.android.activity.fragments.EventListFragment;
import com.letsmeet.android.common.EventListType;

import java.util.Map;

public class MainActivity extends AppCompatActivity
    implements NavigationDrawerFragment.NavigationDrawerCallbacks {

  Map<EventListType, Fragment> fragmentMap;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    fragmentMap = Maps.newHashMap();
    fragmentMap.put(EventListType.UPCOMING, EventListFragment.newInstance(EventListType.UPCOMING));
    fragmentMap.put(EventListType.ALL, EventListFragment.newInstance(EventListType.ALL));
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
  public void onNavigationDrawerItemSelected(EventListType eventListType) {
    // update the main content by replacing fragments
    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentManager.beginTransaction()
        .replace(R.id.container, fragmentMap.get(eventListType))
        .commit();
  }
}
