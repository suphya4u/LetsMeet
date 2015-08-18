package com.letsmeet.android.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.letsmeet.com.letsmeet.R;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventListActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_event_list);
    String[] eventArray = {
        "Wed 19-Aug - Smoke Eaters [YES] [NO] [MAYBE]",
        "Thu 20-Aug - BJs [YES] [NO] [MAYBE]",
        "Fri 21-Aug - CheeseCake Factory [YES] [NO] [MAYBE]",
        "Sat 22-Aug - Bawarchi [YES] [NO] [MAYBE]",
        "Sun 23-Aug - Smoke Eaters [YES] [NO] [MAYBE]",
        "Mon 24-Aug - Red Robin [YES] [NO] [MAYBE]",
        "Tue 25-Aug - Gap Chup Gharat [YES] [NO] [MAYBE]"
    };

    List<String> events = new ArrayList<String>(Arrays.asList(eventArray));

    ArrayAdapter<String> mEventAdapter = new ArrayAdapter<String>(
        this,
        R.layout.list_item_event,
        R.id.list_item_event_textview,
        events);

    ListView listView = (ListView) findViewById(R.id.listview_event);
    listView.setAdapter(mEventAdapter);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_event_list, menu);
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
}
