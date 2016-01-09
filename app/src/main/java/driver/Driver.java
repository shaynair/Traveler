package driver;

import csc.io.InputOperations;
import csc.travel.Travel;
import csc.travel.TravelComparator;
import csc.travel.TravelType;
import csc.users.RegisteredUser;
import csc.users.UserType;
import csc.util.TimeFormat;

import java.text.ParseException;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Driver used for autotesting the project backend.
 */
public class Driver {
  private static final Logger log = Logger.getLogger(Driver.class.getName());

  /**
   * Uploads client information to the application from the file at the given
   * path.
   *
   * @param path
   *          the path to an input csv file of client information with lines in
   *          the format:
   *          LastName,FirstNames,Email,Address,CreditCardNumber,ExpiryDate (the
   *          ExpiryDate is stored in the format YYYY-MM-DD)
   */
  public static void uploadClientInfo(String path) {
    // parse the data into a collection, then add it to the users
    TestDatabase.getDatabase().addUsers(InputOperations.parseData(path, UserType.Client));
  }

  /**
   * Uploads flight information to the application from the file at the given
   * path.
   *
   * @param path
   *          the path to an input csv file of flight information with lines in
   *          the format:
   *          Number,DepartureDateTime,ArrivalDateTime,Airline,Origin
   *          ,Destination,Price (the dates are in the format YYYY-MM-DD; the
   *          price has exactly two decimal places)
   */
  public static void uploadFlightInfo(String path) {
    // parse the data into a collection, then add it to the travels
    TestDatabase.getDatabase().addTravels(InputOperations.parseData(path, TravelType.Flight));
  }

  /**
   * Returns the information stored for the client with the given email.
   *
   * @param email
   *          the email address of a client
   * @return the information stored for the client with the given email in this
   *         format:
   *         LastName,FirstNames,Email,Address,CreditCardNumber,ExpiryDate (the
   *         ExpiryDate is stored in the format YYYY-MM-DD)
   */
  public static String getClient(String email) {
    RegisteredUser user = TestDatabase.getDatabase().getUser(email);
    if (user == null) {
      log.log(Level.WARNING, "A user with the email {0} was not found.", email);
      return "";
    }
    return user.toString();
  }

  /**
   * Returns all flights that depart from origin and arrive at destination on
   * the given date.
   *
   * @param date
   *          a departure date (in the format YYYY-MM-DD)
   * @param origin
   *          a flight origin
   * @param destination
   *          a flight destination
   * @return the flights that depart from origin and arrive at destination on
   *         the given date formatted with one flight per line in exactly this
   *         format:
   *         Number,DepartureDateTime,ArrivalDateTime,Airline,Origin,Destination
   *         ,Price (the dates are in the format YYYY-MM-DD; the price has
   *         exactly two decimal places)
   */
  public static String getFlights(String date, String origin, String destination) {
    try {
      // find all the travels, then format it into a string of multiple lines
      return InputOperations.formatCollection(TestDatabase.getDatabase().searchTravels(
          TimeFormat.DATE.parseString(date), origin, destination, TravelType.Flight));
    } catch (ParseException e) {
      log.log(Level.SEVERE, "Incorrect date or time format.", e);
    }
    return "";
  }

  /**
   * Returns all itineraries that depart from origin and arrive at destination
   * on the given date. If an itinerary contains two consecutive flights F1 and
   * F2, then the destination of F1 should match the origin of F2. To simplify
   * our task, if there are more than 6 hours between the arrival of F1 and the
   * departure of F2, then we do not consider this sequence for a possible
   * itinerary (we judge that the stopover is too long).
   *
   * @param date
   *          a departure date (in the format YYYY-MM-DD)
   * @param origin
   *          a flight original
   * @param destination
   *          a flight destination
   * @return itineraries that depart from origin and arrive at destination on
   *         the given date with stopovers at or under 6 hours. Each itinerary
   *         in the output should contain one line per flight, in the format:
   *         Number,DepartureDateTime,ArrivalDateTime,Airline,Origin,Destination
   *         followed by total price (on its own line, exactly two decimal
   *         places), followed by total duration (on its own line, in format
   *         HH:MM).
   */
  public static String getItineraries(String date, String origin, String destination) {
    return getItinerariesSorted(date, origin, destination, null);
  }

  /**
   * Returns the same itineraries as getItineraries produces, but sorted
   * according to total itinerary cost, in non-decreasing order.
   *
   * @param date
   *          a departure date (in the format YYYY-MM-DD)
   * @param origin
   *          a flight original
   * @param destination
   *          a flight destination
   * @return itineraries (sorted in non-decreasing order of total itinerary
   *         cost) that depart from origin and arrive at destination on the
   *         given date with stopovers at or under 6 hours. Each itinerary in
   *         the output should contain one line per flight, in the format:
   *         Number,DepartureDateTime,ArrivalDateTime,Airline,Origin,Destination
   *         followed by total price (on its own line, exactly two decimal
   *         places), followed by total duration (on its own line, in format
   *         HH:MM).
   */
  public static String getItinerariesSortedByCost(String date, String origin,
      String destination) {
    return getItinerariesSorted(date, origin, destination, TravelComparator.Total_Cost);
  }

  /**
   * Returns the same itineraries as getItineraries produces, but sorted
   * according to total itinerary travel time, in non-decreasing order.
   *
   * @param date
   *          a departure date (in the format YYYY-MM-DD)
   * @param origin
   *          a flight original
   * @param destination
   *          a flight destination
   * @return itineraries (sorted in non-decreasing order of travel itinerary
   *         travel time) that depart from origin and arrive at destination on
   *         the given date with stopovers at or under 6 hours. Each itinerary
   *         in the output should contain one line per flight, in the format:
   *         Number,DepartureDateTime,ArrivalDateTime,Airline,Origin,Destination
   *         followed by total price (on its own line, exactly two decimal
   *         places), followed by total duration (on its own line, in format
   *         HH:MM).
   */
  public static String getItinerariesSortedByTime(String date, String origin,
      String destination) {
    return getItinerariesSorted(date, origin, destination, TravelComparator.Total_Travel_Time);
  }

  /**
   * Returns all itineraries that depart from origin and arrive at destination
   * on the given date.
   *
   * @param date
   *          a departure date (in the format YYYY-MM-DD)
   * @param origin
   *          a flight original
   * @param destination
   *          a flight destination
   * @param order
   *          the order to sort the itineraries; null does not sort them
   * @return itineraries that depart from origin and arrive at destination on
   *         the given date with stopovers at or under 6 hours.
   */
  private static String getItinerariesSorted(String date, String origin, String destination,
      Comparator<Travel> order) {
    try {
      // find all the itineraries sorted (or not), then format it into a string
      return InputOperations.formatCollection(TestDatabase.getDatabase()
          .searchItineraries(TimeFormat.DATE.parseString(date), origin, destination, order));
    } catch (ParseException e) {
      log.log(Level.SEVERE, "Incorrect date/time format.", e);
    }
    return "";
  }
}