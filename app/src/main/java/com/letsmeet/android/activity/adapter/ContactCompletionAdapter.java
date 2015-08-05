package com.letsmeet.android.activity.adapter;

import android.app.Activity;
import android.content.Context;
import android.letsmeet.com.letsmeet.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suhas on 8/4/15.
 */
public class ContactCompletionAdapter extends BaseAdapter implements Filterable {

  private final Context context;

  // TODO(suhas): Read contacts instead of this auto complete test data.
  private static String[] testData = {"one", "two", "three", "four", "five"};

  public ContactCompletionAdapter(Context context) {
    this.context = context;
  }

  @Override public int getCount() {
    return testData.length;
  }

  @Override public String getItem(int position) {
    return testData[position];
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    View contactRow;
    if (convertView == null) {
      LayoutInflater inflater = (LayoutInflater)context.getSystemService(
          Context.LAYOUT_INFLATER_SERVICE);
      contactRow = inflater.inflate(R.layout.contact_item, parent, false);
    } else {
      contactRow = convertView;
    }
    TextView contactName = (TextView) contactRow.findViewById(R.id.contact_name);
    contactName.setText(testData[position]);
    return contactRow;
  }

  @Override public Filter getFilter() {
    return new ArrayFilter();
  }


  private class ArrayFilter extends Filter {
    @Override
    protected FilterResults performFiltering(CharSequence prefix) {

      // TODO(suhas): Filter contacts.
      FilterResults results = new FilterResults();

        results.values = testData;
        results.count = testData.length;

      return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
    }
  }
}
