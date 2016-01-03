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
  private int year = -1;
  private int monthOfYear = -1;
  private int dayOfMonth = -1;

  public void setOnDateSetListener(DatePickerDialog.OnDateSetListener onDateSetListener) {
    this.onDateSetListener = onDateSetListener;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    if (year < 0 || monthOfYear < 0 || dayOfMonth < 0) {
      final Calendar c = Calendar.getInstance();
      year = c.get(Calendar.YEAR);
      monthOfYear = c.get(Calendar.MONTH);
      dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
    }

    return new DatePickerDialog(getActivity(), onDateSetListener, year, monthOfYear, dayOfMonth);
  }

  public void updateTime(int year, int monthOfYear, int dayOfMonth) {
    this.year = year;
    this.monthOfYear = monthOfYear;
    this.dayOfMonth = dayOfMonth;

    DatePickerDialog dialog = (DatePickerDialog) getDialog();
    if (dialog != null) {
      dialog.updateDate(year, monthOfYear, dayOfMonth);
    }
  }
}
