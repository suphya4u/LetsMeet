package com.letsmeet.android.common;

import android.content.Context;
import android.text.format.DateFormat;

import java.util.Calendar;

/**
 * Utility class for date time functions.
 */
public class DateTimeUtils {

  public static String getDisplayDateTime(Context context, Calendar calendar) {
    String eventDateString = DateFormat.getDateFormat(
        context).format(calendar.getTime());
    String eventTimeString = DateFormat.getTimeFormat(
        context).format(calendar.getTime());
    return eventDateString + " " + eventTimeString;
  }
}
