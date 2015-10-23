package net.jonmiranda.pantry;

public class Utils {

  public static Object firstNonNull(Object... objects) {
    for (int i = 0; i < objects.length; ++i) {
      if (objects[i] != null) {
        return objects[i];
      }
    }
    return null;
  }

  public static String getDisplayableTime(long milliseconds) {
    final long seconds = milliseconds / 1000;
    final long minutes = seconds / 60;
    final long hours = minutes / 60;
    final long days = hours / 24;
    final long months = days / 31;
    final long years = days / 365;

    if (milliseconds < 0) {
      return "Future";
    } else if (days <= 1) { // 24 * 60 * 60
      return "Today";
    } else if (days <= 2) { // 48 * 60 * 60
      return "Yesterday";
    } else if (days < 31) { // 30 * 24 * 60 * 60
      return days <= 1 ? "1 day ago" : days + " days ago";
    } else if (months <= 1) { // 12 * 30 * 24 * 60 * 60
      return months <= 1 ? "1 month ago" : months + " months ago";
    } else {
      return years <= 1 ? "1 year ago" : years + " years ago";
    }
  }
}
