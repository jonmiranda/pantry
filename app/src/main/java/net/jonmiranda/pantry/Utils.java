package net.jonmiranda.pantry;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Utils {

  public static Object firstNonNull(Object... objects) {
    for (int i = 0; i < objects.length; ++i) {
      if (objects[i] != null) {
        return objects[i];
      }
    }
    return null;
  }

  public static String getDisplayableTime(long delta_milliseconds) {
    final long seconds = delta_milliseconds / 1000;
    final long minutes = seconds / 60;
    final long hours = minutes / 60;
    final long days = hours / 24;
    final long months = days / 31;
    final long years = days / 365;

    if (delta_milliseconds < 0) {
      return "Future";
    } else if (delta_milliseconds < TimeUnit.DAYS.toMillis(1)) {
      return "Today";
    } else if (delta_milliseconds < TimeUnit.DAYS.toMillis(2)) {
      return "Yesterday";
    } else if (delta_milliseconds < TimeUnit.DAYS.toMillis(31)) {
      return days + " days ago";
    } else if (delta_milliseconds < TimeUnit.DAYS.toMillis(365)) {
      return months <= 1 ? "1 month ago" : months + " months ago";
    } else {
      return years <= 1 ? "1 year ago" : years + " years ago";
    }
  }

  public static Date getTodaysDate() {
    Calendar now = Calendar.getInstance();
    now.set(Calendar.HOUR, 0);
    now.set(Calendar.MINUTE, 0);
    now.set(Calendar.SECOND, 0);
    now.set(Calendar.MILLISECOND, 0);
    return now.getTime();
  }
}
