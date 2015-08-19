package com.letsmeet.android.widgets.datetime;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Fragment to pick a date.
 */
public class DatePickerFragment extends DialogFragment {

  private DatePickerDialog.OnDateSetListener onDateSetListener;

  public void setOnDateSetListener(DatePickerDialog.OnDateSetListener onDateSetListener) {
    this.onDateSetListener = onDateSetListener;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final Calendar c = Calendar.getInstance();
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    int day = c.get(Calendar.DAY_OF_MONTH);

    return new DatePickerDialog(getActivity(), onDateSetListener, year, month, day);
  }
}
