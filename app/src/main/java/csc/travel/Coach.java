package csc.travel;

import java.util.Date;

/**
 * A class that represents a bus ride.
 */
public class Coach extends SingleTravel {
  /**
   * Serializable implementation.
   */
  private static final long serialVersionUID = 7821628923036367887L;

  /**
   * Create a SingleTravel instance with given parameters.
   *
   * @param type
   *          the type of this travel
   * @param id
   *          the given travel id
   * @param start
   *          the departure date and time
   * @param end
   *          the arrival date and time
   * @param origin
   *          the given origin
   * @param destination
   *          the given destination
   * @param cost
   *          the given cost
   * @param provider
   *          the given provider
   * @param capacity
   *          the given capacity
   */
  public Coach(TravelType type, String id, Date start, Date end, String origin,
      String destination, double cost, String provider, int capacity) {
    super(type, id, start, end, origin, destination, cost, capacity, provider);
  }

  @Override
  public String toString(boolean includePrice) {
    // Number,DepartureDateTime,ArrivalDateTime,Airline,Origin,Destination(,Price),NumSeats
    String ret = String.format("%s,%s,%s,%s,%s,%s", getIdentifier(), formatStartTime(),
        formatEndTime(), getProvider(), getOrigin(), getDestination());
    if (includePrice) {
      ret += String.format(",%.2f", getCost());
    }
    return ret;
  }

}
