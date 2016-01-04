package com.letsmeet.android.widgets.datetime;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.letsmeet.android.R;
import com.letsmeet.android.common.DateTimeUtils;

import java.util.Calendar;


/**
 * Date time picker fragment
 */
public class DateTimePickerView extends Fragment implements TimePickerDialog.OnTimeSetListener,
    DatePickerDialog.OnDateSetListener {

  private Button dateButton;
  private Button timeButton;
  private int year = -1;
  private int monthOfYear = -1;
  private int dayOfMonth = -1;
  private int hourOfDay = -1;
  private int minute = -1;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_date_time_picker, container, false);

    dateButton = (Button) view.findViewById(R.id.selected_date);
    dateButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setOnDateSetListener(DateTimePickerView.this);
        datePickerFragment.updateTime(year, monthOfYear, dayOfMonth);
        datePickerFragment.show(getFragmentManager(), "DatePicker");
      }
    });

    timeButton = (Button) view.findViewById(R.id.selected_time);
    timeButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setOnTimeSetListener(DateTimePickerView.this);
        timePickerFragment.updateTime(hourOfDay, minute);
        timePickerFragment.show(getFragmentManager(), "TimePicker");
      }
    });

    setDefaultDateTime();
    return view;
  }

  public void setDateTime(Calendar dateTime) {
    onDateSet(null /* DateView */,
        dateTime.get(Calendar.YEAR),
        dateTime.get(Calendar.MONTH),
        dateTime.get(Calendar.DAY_OF_MONTH));
    onTimeSet(null /* TimeView */,
        dateTime.get(Calendar.HOUR_OF_DAY),
        dateTime.get(Calendar.MINUTE));
  }

  @Override public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
    this.year = year;
    this.monthOfYear = monthOfYear;
    this.dayOfMonth = dayOfMonth;

    if (dateButton != null) {
      Calendar calendar = Calendar.getInstance();
      calendar.set(year, monthOfYear, dayOfMonth);
      String dateStr = DateTimeUtils.getDisplayDate(getActivity(), calendar, false /* useShort */);
      dateButton.setText(dateStr);
      clearErrors();
    }
  }

  @Override public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
    this.hourOfDay = hourOfDay;
    this.minute = minute;

    if (timeButton != null) {
      Calendar calendar = Calendar.getInstance();
      calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
      calendar.set(Calendar.MINUTE, minute);
      String timeStr = DateTimeUtils.getDisplayTime(getActivity(), calendar, false /* useShort */);
      timeButton.setText(timeStr);
      clearErrors();
    }
  }

  public long getSelectedTime() {
    Calendar selectedTime = Calendar.getInstance();
    selectedTime.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
    return selectedTime.getTimeInMillis();
  }

  private void setDefaultDateTime() {
    Calendar now = Calendar.getInstance();

    if (now.get(Calendar.HOUR_OF_DAY) > 18) {
      now.add(Calendar.DAY_OF_YEAR, 1);
    }
    // TODO: This doesn't work. Maybe switch to DateTime?
    now.set(Calendar.HOUR_OF_DAY, 20);
    now.set(Calendar.MINUTE, 0);

    setDateTime(now);
  }

  private void clearErrors() {
    if (dateButton != null) {
      dateButton.setError(null);
    }
    if (timeButton != null) {
      timeButton.setError(null);
    }
  }
}
