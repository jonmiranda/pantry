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

  public static String getDisplayableTime(long seconds) {
    final long minutes = seconds / 60;
    final long hours = minutes / 60;
    final long days = hours / 24;
    final long months = days / 31;
    final long years = days / 365;

    if (seconds < 86400) { // 24 * 60 * 60
      return "Today";
    } else if (seconds < 172800) { // 48 * 60 * 60
      return "Yesterday";
    } else if (seconds < 2592000) { // 30 * 24 * 60 * 60
      return days <= 1 ? "1 day ago" : days + " days ago";
    } else if (seconds < 31104000) { // 12 * 30 * 24 * 60 * 60
      return months <= 1 ? "1 month ago" : months + " months ago";
    } else {
      return years <= 1 ? "1 year ago" : years + " years ago";
    }
  }
}
