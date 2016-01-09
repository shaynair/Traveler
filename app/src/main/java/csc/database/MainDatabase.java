package csc.database;

import csc.travel.Itinerary;
import csc.travel.SingleTravel;
import csc.travel.Travel;
import csc.travel.TravelType;
import csc.users.RegisteredUser;
import csc.util.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main database, storing the user and travel databases.
 */
public class MainDatabase implements Serializable {
  /**
   * The logger for this class.
   */
  private static final Logger log = Logger.getLogger(MainDatabase.class.getName());
  /**
   * Serializable implmentation.
   */
  private static final long serialVersionUID = 6719754597423119338L;

  /**
   * A collection of the users in this database.
   */
  private final UserDatabase users;
  /**
   * A collection of each possible TravelType and the corresponding travel
   * database.
   */
  private final Map<TravelType, TravelDatabase> travels;
  /**
   * An indexed collection of the travels originating from each location. Used
   * for searching.
   */
  private final Map<String, List<SingleTravel>> travelSearch;

  /**
   * Creates a new empty main database.
   */
  public MainDatabase() {
    this.users = new UserDatabase();
    this.travelSearch = new HashMap<>();

    // initialize completely and make final
    Map<TravelType, TravelDatabase> travel = new EnumMap<>(TravelType.class);
    for (TravelType tt : TravelType.values()) {
      travel.put(tt, new TravelDatabase(tt));
    }
    this.travels = Collections.unmodifiableMap(travel);
  }

  /**
   * Changes the email of a user.
   * 
   * @param ru
   *          the user to change
   * @param email
   *          the email to change to
   */
  public void changeEmail(RegisteredUser ru, String email) {
    users.changeId(ru, email);
  }

  /**
   * Changes the id of a travel.
   * 
   * @param st
   *          the travel to change
   * @param id
   *          the id to change to
   */
  public void changeIdentifier(SingleTravel st, String id) {
    travels.get(st.getType()).changeId(st, id);
  }

  /**
   * Adds travel info to this database.
   *
   * @param travel
   *          the travel to add to this database.
   */
  public void addTravel(SingleTravel travel) {
    // it's invalid if it's expired, or cyclic, or has invalid time
    if (travel.isInvalid()) {
      log.log(Level.INFO, "A travel of ID {0} and type {1} has invalid data. Skipping.",
          new Object[] { travel.getIdentifier(), travel.getType() });
      return;
    }
    SingleTravel old = getTravel(travel.getType(), travel.getIdentifier());
    if (old != null) {
      if (old.equals(travel)) { // nothing updated
        return;
      }
      removeTravelIndex(old);
    }
    travel.resetUsers();
    // get the persisting object, then add it
    // if there exists one, it will be updated and gotten
    addTravelIndex(travels.get(travel.getType()).add(travel));
  }

  /**
   * Removes a travel from the database.
   * 
   * @param type
   *          the type of the travel
   * @param id
   *          the id of the travel
   */
  public void removeTravel(TravelType type, String id) {
    SingleTravel old = getTravel(type, id);
    if (old != null) {
      removeTravelIndex(old);
      travels.get(type).remove(id);
    }
  }

  /**
   * Removes the index of a travel for searching.
   *
   * @param travel
   *          the travel object to remove
   */
  private void removeTravelIndex(SingleTravel travel) {
    // if first occurrence, add an empty set
    String origin = travel.getOrigin().toLowerCase();
    if (travelSearch.containsKey(origin)) {
      List<SingleTravel> index = travelSearch.get(origin);
      index.remove(travel);
      if (index.isEmpty()) { // remove it altogether
        travelSearch.remove(origin);
      }
    }

    // to remove this travel completely, we remove it from the itineraries as
    // well (thus making the itinerary invalid and needed to be replaced)
    for (RegisteredUser ru : users.getValues()) {
      ru.removeTravel(travel);
    }
    travel.resetUsers();
  }

  /**
   * Indexes a travel for searching.
   *
   * @param travel
   *          the travel object to index
   */
  private void addTravelIndex(SingleTravel travel) {
    // if first occurrence, add an empty set
    String origin = travel.getOrigin().toLowerCase();
    if (!travelSearch.containsKey(origin)) {
      travelSearch.put(origin, new ArrayList<SingleTravel>());
    }
    travelSearch.get(origin).add(travel);
  }

  /**
   * Adds travel info to this database.
   *
   * @param toAdd
   *          the travels to add to this database.
   */
  public void addTravels(Iterable<SingleTravel> toAdd) {
    for (SingleTravel tr : toAdd) {
      addTravel(tr);
    }
  }

  /**
   * Adds user info to this database.
   *
   * @param user
   *          the user to add to this database. Existing ones are replaced.
   */
  public void addUser(RegisteredUser user) {
    users.add(user);
  }

  /**
   * Adds user info to this database.
   *
   * @param user
   *          the users to add to this database. Existing ones are replaced.
   */
  public void addUsers(Iterable<RegisteredUser> user) {
    users.addAll(user);
  }

  /**
   * Gets user info from this database.
   *
   * @param id
   *          the identifier to look for
   */
  public RegisteredUser getUser(String id) {
    return users.get(id);
  }

  /**
   * Gets all users from this database.
   *
   * @return a collection of all the users
   */
  public Collection<RegisteredUser> getAllUsers() {
    return users.getValues();
  }

  /**
   * Gets all travels in the database.
   *
   * @param type
   *          the type to get all travels for
   * @return a collection of all the travels
   */
  public Collection<SingleTravel> getAllTravels(TravelType type) {
    return travels.get(type).getValues();
  }

  /**
   * Gets a travel with the corresponding id and type.
   *
   * @param tt
   *          the type of travel
   * @param id
   *          the id of the travel
   * @return the travel with the respective data
   */
  public SingleTravel getTravel(TravelType tt, String id) {
    return travels.get(tt).get(id);
  }

  /**
   * Clears all information from this database.
   */
  public void clear() {
    users.clear();
    for (TravelType tt : TravelType.values()) {
      travels.get(tt).clear();
    }
    travelSearch.clear();
  }

  /**
   * Returns all users of the given name and email.
   *
   * @param name
   *          the names to check for
   * @param email
   *          the email to check for
   * @return a set of the users that contain the names provided
   */
  public ArrayList<RegisteredUser> searchUsers(String name, String email) {
    return users.searchUsers(name, email);
  }

  /**
   * Returns all travels that depart from origin and arrive at destination on
   * the given date, of the given type.
   *
   * @param date
   *          the date to start the travel; a null value will return all travels
   * @param origin
   *          the location to start the travel; a null value will return all
   *          travels
   * @param destination
   *          the location to end the travel; a null value will return all
   *          travels
   * @param type
   *          the type of travel; a null value will return all travels
   * @return a set of the travels that match the specifications
   */
  public Iterable<SingleTravel> searchTravels(Date date, String origin, String destination,
      TravelType type) {
    return searchTravels(date, origin, destination, type, null);
  }

  /**
   * Returns all travels that depart from origin and arrive at destination on
   * the given date, in the order specified.
   *
   * @param date
   *          the date to start the travel; a null value will return all travels
   * @param origin
   *          the location to start the travel; a null value will return all
   *          travels
   * @param destination
   *          the location to end the travel; a null value will not search
   * @param type
   *          the type of travel; a null value will return all travels
   * @param order
   *          the order to set the travels in; a null value will do nothing
   * @return a set of the travels that match the specifications
   */
  public ArrayList<SingleTravel> searchTravels(Date date, String origin, String destination,
      TravelType type, Comparator<Travel> order) {
    ArrayList<SingleTravel> ret;
    if (type != null) { // search only type
      ret = travels.get(type).searchTravels(date, origin, destination, true);
    } else if (origin == null) {
      // we can't use the index; just search all types
      ret = new ArrayList<>();
      for (TravelType typ : TravelType.values()) {
        ret.addAll(travels.get(typ).searchTravels(date, null, destination, true));
      }
    } else { // search all
      ret = listTravels(date, null, origin, destination, true);
    }
    if (order != null) {
      Collections.sort(ret, order);
    }
    return ret;
  }

  /**
   * Finds travel information matching the specified arguments, and puts them
   * into a list. THe list is then copied if needed.
   *
   * @param lower
   *          the date to start the travel; lower bound; a null value will
   *          return all travels
   * @param upper
   *          the date to start the travel; upper bound; null value skips check
   * @param origin
   *          the location to start the travel
   * @param destination
   *          the destination to match; null will match all
   * @param includeFull
   *          whether to include full travels or not
   * @return a list containing the travel information matched.
   */
  private ArrayList<SingleTravel> listTravels(Date lower, Date upper, String origin,
      String destination, boolean includeFull) {

    ArrayList<SingleTravel> ret = new ArrayList<>();
    if (travelSearch.containsKey(origin.toLowerCase())) {
      for (SingleTravel travel : travelSearch.get(origin.toLowerCase())) {
        if ((destination == null || travel.getDestination().equalsIgnoreCase(destination))
            && (includeFull || travel.getAvailableCapacity() > 0)
            && (lower == null || travel.startsWithin(lower, upper))) {
          ret.add(travel);
        }
      }
      return ret;
    }
    return ret;
  }

  /**
   * Returns all itineraries that depart from origin and arrive at destination
   * on the given date.
   *
   * @param date
   *          the date to start the sequence
   * @param origin
   *          the location to start the sequence
   * @param destination
   *          the location to end the sequence
   * @return a set of the itineraries that match the specifications
   */
  public ArrayList<Itinerary> searchItineraries(Date date, String origin, String destination) {
    return searchItineraries(date, origin, destination, null);
  }

  /**
   * Returns all itineraries that depart from origin and arrive at destination
   * on the given date, in the order specified.
   *
   * @param date
   *          the date to start the sequence
   * @param origin
   *          the location to start the sequence
   * @param destination
   *          the location to end the sequence
   * @param order
   *          the order to set the itineraries in; a null value will do nothing
   * @return a set of the itineraries that match the specifications
   */
  public ArrayList<Itinerary> searchItineraries(Date date, String origin, String destination,
      Comparator<Travel> order) {
    if (origin.equalsIgnoreCase(destination)) {
      log.log(Level.WARNING, "Incorrect input. origin and destination are the same.");
      return new ArrayList<>();
    }
    ArrayList<Itinerary> ret = generateItineraries(date, origin, destination);

    if (order != null) {
      Collections.sort(ret, order);
    }
    return ret;
  }

  /**
   * Generates all itineraries that depart from origin and arrive at destination
   * on the given date.
   *
   * @param date
   *          the date to start the sequence
   * @param origin
   *          the location to start the sequence
   * @param destination
   *          the location to end the sequence
   * @return a list of the itineraries that match the specifications
   */
  private ArrayList<Itinerary> generateItineraries(Date date, String origin,
      String destination) {
    ArrayList<Itinerary> list = new ArrayList<>();
    generateItineraries(date, null, origin, destination, list, new Itinerary());
    return list;
  }

  /**
   * Generates all itineraries that start with itin and arrive at destination on
   * the given date, and adds them to an existing list.
   *
   * @param lower
   *          the date to start the sequence
   * @param upper
   *          the date to start the sequence, upper bound; null will skip check
   * @param origin
   *          the location to start the sequence
   * @param destination
   *          the location to end the sequence
   * @param list
   *          the list to add to
   * @param itin
   *          the running itinerary
   */
  private void generateItineraries(Date lower, Date upper, String origin, String destination,
      List<Itinerary> list, Itinerary itin) {

    if (origin.equalsIgnoreCase(destination)) {
      // no more itineraries should be made, we've reached the destination
      list.add(itin);
      return;
    }
    // get all travels originating from origin and in the appropriate time range
    List<SingleTravel> found = listTravels(lower, upper, origin, null, false);

    for (SingleTravel st : found) {
      // if we haven't visited this location...
      if (!itin.containsOrigin(st.getDestination())) {
        // repeat process with new date range and origin
        Date newLower = new Date(st.getEndTime().getTime() + Constants.MIN_STOPOVER);
        Date newUpper = new Date(st.getEndTime().getTime() + Constants.MAX_STOPOVER);

        Itinerary itinCopy = itin.copy();
        itinCopy.add(st);

        generateItineraries(newLower, newUpper, st.getDestination(), destination, list,
            itinCopy);
      }
    }
    // if no travels are found, an itinerary from this location cannot be made
  }
}
