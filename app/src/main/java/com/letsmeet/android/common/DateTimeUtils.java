package com.letsmeet.android.common;

import android.content.Context;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Utility class for date time functions.
 */
public class DateTimeUtils {

  private static final String WITHIN_WEEK_SHORT_DATE_FORMAT = "EEEE";
  private static final String WITHIN_WEEK_LONG_DATE_FORMAT = "EEE, dd MMM";

  public static String getDisplayDateTime(Context context, long timeInMillis) {
    return getDisplayDateTime(context, timeInMillis, false /* useShort */);
  }

  public static String getDisplayDateTime(Context context, long timeInMillis, boolean useShort) {
    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
    calendar.setTimeInMillis(timeInMillis);
    String eventDateString = getDisplayDate(context, calendar, useShort);
    String eventTimeString = getDisplayTime(context, calendar, useShort);
    return eventDateString + " " + eventTimeString;
  }

  public static String getDisplayDate(Context context, Calendar calendar, boolean useShort) {
    Calendar now = Calendar.getInstance();
    if (now.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
      if (now.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)) {
        // TODO: Move this to R.string for internationalization.
        return "Today";
      }
      if (now.get(Calendar.DAY_OF_YEAR) + 1 == calendar.get(Calendar.DAY_OF_YEAR)) {
        return "Tomorrow";
      }
      if (Math.abs(now.get(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR)) < 6) {
        if (useShort) {
          return DateFormat.format(WITHIN_WEEK_SHORT_DATE_FORMAT, calendar).toString();
        } else {
          return DateFormat.format(WITHIN_WEEK_LONG_DATE_FORMAT, calendar).toString();
        }
      }
    }
    return DateFormat.format(WITHIN_WEEK_LONG_DATE_FORMAT, calendar).toString();
  }

  public static String getDisplayTime(Context context, Calendar calendar, boolean useShort) {
    return DateFormat.getTimeFormat(context).format(calendar.getTime());
  }
}
