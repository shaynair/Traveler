package csc.travel;

import csc.util.TimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * A Travel class used to implement general travel methods. Both single trips
 * and itineraries (sequences of trips) have these methods.
 */
public abstract class Travel implements Serializable {

  /**
   * Serializable implementation.
   */
  private static final long serialVersionUID = 3333515126760238035L;
  /**
   * The total cost.
   */
  protected double cost;

  /**
   * Creates a Travel.
   * 
   * @param cost
   *          the cost of the travel
   */
  protected Travel(double cost) {
    this.cost = cost;
  }

  /**
   * Get the arrival date and time of the travel.
   *
   * @return a Date representative of the arrival date and time of the travel
   */
  public abstract Date getStartTime();

  /**
   * Get the departure date and time of the travel.
   *
   * @return a string representative of the departure date and time of the
   *         travel
   */
  public abstract Date getEndTime();

  /**
   * Get the origin of the travel.
   *
   * @return a string representative of the origin of the travel
   */
  public abstract String getOrigin();

  /**
   * Get the destination of the travel.
   *
   * @return a string representative of the destination of the travel
   */
  public abstract String getDestination();

  /**
   * Get the cost of the travel.
   *
   * @return the cost of the travel
   */
  public double getCost() {
    return cost;
  }

  /**
   * Get the travel time (in milliseconds) of the travel.
   *
   * @return the travel time of the travel
   */
  public long getTravelTime() {

    return getEndTime().getTime() - getStartTime().getTime();
  }

  /**
   * Formats the travel time into a proper string format.
   *
   * @return a string representing the travel time
   */
  public String formatTravelTime() {

    return TimeFormat.formatMillis(getTravelTime());
  }

  /**
   * Formats the departure time into a proper string format.
   *
   * @return a string representing the departure time
   */
  public String formatStartTime() {

    return TimeFormat.DATE_TIME.formatDate(getStartTime());
  }

  /**
   * Formats the arrival time into a proper string format.
   *
   * @return a string representing the arrival time
   */
  public String formatEndTime() {

    return TimeFormat.DATE_TIME.formatDate(getEndTime());
  }

  /**
   * Returns whether this travel starts on the specified day.
   *
   * @param date
   *          the date to check
   * @return True if this travel matches the date
   */
  public boolean startsWithin(Date date) {

    return startsWithin(date, null);
  }

  /**
   * Returns whether this travel starts between the two dates, inclusively.
   *
   * @param dateLower
   *          the date to check as a lower bound
   * @param dateUpper
   *          the date to check as an upper bound; null value skips check and
   *          only checks if the date is on the same date as dateLower
   * @return True if this travel starts in between dateLower and dateUpper
   */
  public boolean startsWithin(Date dateLower, Date dateUpper) {
    if (getStartTime().after(dateLower) || getStartTime().equals(dateLower)) {
      if (dateUpper != null) {
        return getStartTime().before(dateUpper) || getStartTime().equals(dateUpper);
      } else {
        return TimeFormat.DATE.formatDate(getStartTime())
            .equals(TimeFormat.DATE.formatDate(dateLower));
      }
    }
    return false;
  }

  @Override
  public abstract String toString();

  @Override
  public abstract int hashCode();

  @Override
  public abstract boolean equals(Object oth);

}
