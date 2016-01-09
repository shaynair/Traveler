package csc.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * A class for formatting dates and times into strings.
 */
public enum TimeFormat {
  DATE("yyyy-MM-dd"), TIME("HH:mm"), DATE_TIME("yyyy-MM-dd HH:mm");

  /**
   * The format to use when parsing times and dates.
   */
  private final SimpleDateFormat format;

  /**
   * Creates a TimeFormat from the given string.
   *
   * @param format
   *          the string to format dates into
   */
  private TimeFormat(String format) {
    this.format = new SimpleDateFormat(format, Locale.getDefault());
  }

  /**
   * Formats a Date into a string.
   *
   * @param date
   *          the date to parse
   * @return a string representation of the Date
   */
  public String formatDate(Date date) {
    return format.format(date);
  }

  /**
   * Gets a Date from a String.
   *
   * @param str
   *          the string to parse
   * @return a Date representing str
   * @throws ParseException
   *           if the string is in the wrong format
   */
  public Date parseString(String str) throws ParseException {
    return format.parse(str);
  }

  /**
   * Formats a UNIX timestamp difference into a string.
   *
   * @param millis
   *          the timestamp difference to parse
   * @return a string representation in hh:mm of the timestamp difference
   */
  public static String formatMillis(long millis) {
    long hours = TimeUnit.MILLISECONDS.toHours(millis);
    long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);

    return String.format("%02d:%02d", hours, minutes - TimeUnit.HOURS.toMinutes(hours));
  }
}
