package csc.travel;

import csc.database.MainDatabase;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A class representing an itinerary, a sequence of SingleTravel. This sequence
 * is strictly non-empty, contains non-null values, and is sequential.
 */
public class Itinerary extends Travel implements Iterable<SingleTravel> {
  /**
   * Serializable implementation.
   */
  private static final long serialVersionUID = -1524101019614716587L;
  /**
   * The ordered map to store each travel by their origin location.
   */
  private final LinkedHashMap<String, SingleTravel> travel;
  /**
   * The last travel origin in this itinerary, used for optimization.
   */
  private String last = null;

  /**
   * Creates a multiple-element Itinerary
   *
   * @param travel
   *          The travels to be represented by this itinerary.
   */
  private Itinerary(LinkedHashMap<String, SingleTravel> travel) {
    super(0.0);
    this.travel = travel;
    calculateCost();
  }

  /**
   * Creates an empty Itinerary.
   */
  public Itinerary() {
    this(new LinkedHashMap<String, SingleTravel>());
  }

  /**
   * Creates a single-element Itinerary.
   *
   * @param travel
   *          The single travel to be represented by this itinerary.
   */
  public Itinerary(SingleTravel travel) {
    this();
    addTravel(travel);
  }

  /**
   * Adds a trip to this itinerary.
   *
   * @param toAdd
   *          the travel to add to this itinerary
   */
  public void add(SingleTravel toAdd) {
    // it's invalid if the time and location aren't after the end of this path
    if (toAdd == null || toAdd.isFull()
        || (!travel.isEmpty()
            && (toAdd.getStartTime().before(getEndTime()) || containsOrigin(toAdd.getOrigin())
                || !toAdd.getOrigin().equalsIgnoreCase(getDestination())))) {
      throw new IllegalArgumentException("Travels should start after " + getEndTime()
          + " and should originate at " + getDestination());
    }
    addTravel(toAdd);
  }

  /**
   * Gets whether a travel is in this itinerary.
   *
   * @param check
   *          the travel to check
   * @return True if this itinerary contains the ID of check
   */
  public boolean containsTravel(SingleTravel check) {
    return travel.containsKey(check.getOrigin().toLowerCase()) && travel
        .get(check.getOrigin().toLowerCase()).getIdentifier().equals(check.getIdentifier());
  }

  /**
   * Gets whether a travel origin is in this itinerary.
   *
   * @param check
   *          the origin to check
   * @return True if this itinerary contains a travel originating from origin
   */
  public boolean containsOrigin(String check) {
    return travel.containsKey(check.toLowerCase());
  }

  /**
   * Gets how many travels are in this itinerary.
   *
   * @return the amount of travels
   */
  public int size() {
    return travel.size();
  }

  /**
   * Gets whether this itinerary is empty.
   *
   * @return True if this itinerary is empty
   */
  public boolean isEmpty() {
    return travel.isEmpty();
  }

  /**
   * Makes a copy of this Itinerary.
   *
   * @return a copy of this Itinerary
   */
  public Itinerary copy() {
    return new Itinerary(new LinkedHashMap<>(travel));
  }

  /**
   * Gets the travels in this itinerary.
   *
   * @return the travels in this itinerary in order
   */
  public Collection<SingleTravel> getTravels() {
    return travel.values();
  }

  /**
   * Adds a trip to this itinerary.
   *
   * @param toAdd
   *          the travel to add to this itinerary
   */
  private void addTravel(SingleTravel toAdd) {
    travel.put(toAdd.getOrigin().toLowerCase(), toAdd);
    this.last = toAdd.getOrigin().toLowerCase();
    cost += toAdd.getCost();
  }

  /**
   * Calculates the total cost from the trips in this itinerary.
   */
  private void calculateCost() {
    cost = 0.0;
    for (SingleTravel travels : getTravels()) {
      cost += travels.getCost();
      this.last = travels.getOrigin().toLowerCase(); // update
    }
  }

  /**
   * Refreshes all travels to the ones in the database.
   * 
   * @param md
   *          the database to read from
   */
  public void refresh(MainDatabase md) {
    Set<SingleTravel> travels = new LinkedHashSet<>(travel.values());
    for (SingleTravel st : travels) {
      travel.put(st.getOrigin().toLowerCase(), md.getTravel(st.getType(), st.getIdentifier()));
    }
  }

  /**
   * Adds users to each travel in this itinerary. Does nothing if already
   * booked.
   */
  public void book() {
    for (SingleTravel travels : getTravels()) {
      travels.addUser();
    }
  }

  /**
   * Removes users from each travel in this itinerary. Does nothing if already
   * not booked.
   */
  public void unbook() {
    for (SingleTravel travels : getTravels()) {
      travels.removeUser();
    }
  }

  /**
   * Gets the first travel in this itinerary.
   *
   * @return the first travel
   */
  private SingleTravel getFirst() {
    if (isEmpty()) {
      throw new IndexOutOfBoundsException("Empty itinerary tried to access first.");
    }
    return travel.values().iterator().next();
  }

  /**
   * Gets the last travel in this itinerary.
   *
   * @return the last travel
   */
  private SingleTravel getLast() {
    if (isEmpty() || last == null) {
      throw new IndexOutOfBoundsException("Empty itinerary tried to access last.");
    }
    return travel.get(last);
  }

  @Override
  public Date getStartTime() {
    return getFirst().getStartTime();
  }

  @Override
  public Date getEndTime() {
    return getLast().getEndTime();
  }

  @Override
  public String getOrigin() {
    return getFirst().getOrigin();
  }

  @Override
  public String getDestination() {
    return getLast().getDestination();
  }

  @Override
  public String toString() {
    // Number,DepartureDateTime,ArrivalDateTime,Airline,Origin,Destination
    // followed by total price (on its own line, exactly two decimal places),
    // followed by total duration (on its own line, in format HH:MM).
    StringBuilder builder = new StringBuilder();
    for (SingleTravel tr : getTravels()) {
      builder.append(tr.toString(false)).append('\n');
    }
    builder.append(String.format("%.2f", getCost())).append('\n');
    builder.append(formatTravelTime());
    return builder.toString();
  }

  @Override
  public Iterator<SingleTravel> iterator() {
    return getTravels().iterator();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + travel.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Itinerary other = (Itinerary) obj;
    return travel.equals(other.travel);
  }
}
