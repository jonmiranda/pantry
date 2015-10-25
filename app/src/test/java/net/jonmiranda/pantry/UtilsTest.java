package net.jonmiranda.pantry;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class UtilsTest {

  @Test
  public void testFirstNonNull() {
    Object notNull = new Object();
    Object nullObject = null;

    assertEquals(Utils.firstNonNull(notNull, nullObject), notNull);
    assertEquals(Utils.firstNonNull(nullObject, notNull), notNull);
  }

  @Test
  public void testGetDisplayableTime() {
    String displayTime = Utils.getDisplayableTime(0);
    assertEquals("Today", displayTime);

    displayTime = Utils.getDisplayableTime(TimeUnit.DAYS.toMillis(1));
    assertEquals("Yesterday", displayTime);

    displayTime = Utils.getDisplayableTime(TimeUnit.DAYS.toMillis(2));
    assertEquals("2 days ago", displayTime);

    displayTime = Utils.getDisplayableTime(TimeUnit.DAYS.toMillis(31));
    assertEquals("1 month ago", displayTime);

    displayTime = Utils.getDisplayableTime(TimeUnit.DAYS.toMillis(62));
    assertEquals("2 months ago", displayTime);

    displayTime = Utils.getDisplayableTime(TimeUnit.DAYS.toMillis(365));
    assertEquals("1 year ago", displayTime);

    displayTime = Utils.getDisplayableTime(TimeUnit.DAYS.toMillis(365 * 2));
    assertEquals("2 years ago", displayTime);
  }
}
