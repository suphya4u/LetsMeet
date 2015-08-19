package com.letsmeet.android.widgets.datetime;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Widget to pick a date and time.
 */
public class DateTimePicker implements TimePickerDialog.OnTimeSetListener,
    DatePickerDialog.OnDateSetListener {

  private final FragmentManager fragmentManager;
  private final String uniqueTag;
  private OnDateTimeSetListener listener;

  public static interface OnDateTimeSetListener {
    void onDateTimeSet(Calendar calendar);
  }

  public DateTimePicker(FragmentManager fragmentManager, String uniqueTag) {
    this.fragmentManager = fragmentManager;
    this.uniqueTag = uniqueTag;
  }

  public void setDateTimeSetListener(OnDateTimeSetListener listener) {
    this.listener = listener;
  }

  public void show() {
    DatePickerFragment datePickerFragment = new DatePickerFragment();
    datePickerFragment.setOnDateSetListener(this);
    datePickerFragment.show(fragmentManager, uniqueTag);
  }

  @Override public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
    TimePickerFragment timePickerFragment = new TimePickerFragment();
    timePickerFragment.setOnTimeSetListener(this);
    timePickerFragment.show(fragmentManager, uniqueTag);
  }

  @Override public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
    if (listener != null) {
      Calendar calendar = Calendar.getInstance();
      // TODO(suhas): Set all date time fields.
      listener.onDateTimeSet(calendar);
    }
  }
}
