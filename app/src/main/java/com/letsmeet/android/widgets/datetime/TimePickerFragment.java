package com.letsmeet.android.widgets.datetime;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Fragment to pick time.
 */
public class TimePickerFragment extends DialogFragment {

  private TimePickerDialog.OnTimeSetListener onTimeSetListener;
  private int hourOfDay = -1;
  private int minute = -1;

  public void setOnTimeSetListener(TimePickerDialog.OnTimeSetListener onTimeSetListener) {
    this.onTimeSetListener = onTimeSetListener;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    if (hourOfDay < 0 || minute < 0) {
      final Calendar c = Calendar.getInstance();
      hourOfDay = c.get(Calendar.HOUR_OF_DAY);
      minute = c.get(Calendar.MINUTE);
    }

    return new TimePickerDialog(getActivity(), onTimeSetListener, hourOfDay, minute,
        DateFormat.is24HourFormat(getActivity()));
  }

  public void updateTime(int hourOfDay, int minute) {
    this.hourOfDay = hourOfDay;
    this.minute = minute;

    TimePickerDialog dialog = (TimePickerDialog) getDialog();
    if (dialog != null) {
      dialog.updateTime(hourOfDay, minute);
    }
  }
}
