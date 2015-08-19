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

  public void setOnTimeSetListener(TimePickerDialog.OnTimeSetListener onTimeSetListener) {
    this.onTimeSetListener = onTimeSetListener;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final Calendar c = Calendar.getInstance();
    int hour = c.get(Calendar.HOUR_OF_DAY);
    int minute = c.get(Calendar.MINUTE);

    return new TimePickerDialog(getActivity(), onTimeSetListener, hour, minute,
        DateFormat.is24HourFormat(getActivity()));
  }
}
