package csc.database;

import csc.travel.SingleTravel;
import csc.travel.TravelType;

import java.util.ArrayList;
import java.util.Date;

/**
 * Storage of all travels of a specific type in the system.
 */
public class TravelDatabase extends UniqueMap<String, SingleTravel> {
  /**
   * Serializable implementation.
   */
  private static final long serialVersionUID = 9124880330389237676L;
  /**
   * The type of this travel database.
   */
  private final TravelType type;

  /**
   * Creates a new empty TravelDatabase.
   */
  public TravelDatabase(TravelType type) {
    this.type = type;
  }

  /**
   * Gets the type of this TravelDatabase.
   */
  public TravelType getType() {
    return type;
  }

  /**
   * Returns all travels that depart from origin and arrive at destination on
   * the given date, of this type.
   *
   * @param date
   *          the date to check for; null checks all values
   * @param origin
   *          the origin to check for; null checks all values
   * @param destination
   *          the destination to check for; a null value will not check it
   * @return a set of the travels that match the specifications
   */
  public ArrayList<SingleTravel> searchTravels(Date date, String origin, String destination,
      boolean includeFull) {
    ArrayList<SingleTravel> ret = new ArrayList<>();
    for (SingleTravel travel : getValues()) {
      if ((date == null || travel.startsWithin(date))
          && (origin == null || travel.getOrigin().equalsIgnoreCase(origin))
          && (includeFull || travel.getAvailableCapacity() > 0)
          && (destination == null || travel.getDestination().equalsIgnoreCase(destination))) {
        ret.add(travel);
      }
    }
    return ret;
  }

}
