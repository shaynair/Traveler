package csc.travel;

import csc.interfaces.LineParse;
import csc.util.TimeFormat;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * An enumeration of the possible types of travel.
 */
public enum TravelType implements LineParse<SingleTravel> {
  Flight(8) {
    @Override
    public SingleTravel create(String... args) throws ParseException {
      // Number,DepartureDateTime,ArrivalDateTime,Airline,Origin,Destination,Price,NumSeats
      return new Flight(this, args[0], TimeFormat.DATE_TIME.parseString(args[1]),
          TimeFormat.DATE_TIME.parseString(args[2]), args[4], args[5],
          Double.parseDouble(args[6]), args[3], Integer.parseInt(args[7]));
    }
  },
  Railroad(8) {
    @Override
    public SingleTravel create(String... args) throws ParseException {
      // Number,DepartureDateTime,ArrivalDateTime,Airline,Origin,Destination,Price,NumSeats
      return new Railroad(this, args[0], TimeFormat.DATE_TIME.parseString(args[1]),
          TimeFormat.DATE_TIME.parseString(args[2]), args[4], args[5],
          Double.parseDouble(args[6]), args[3], Integer.parseInt(args[7]));
    }
  },
  Coach(8) {
    @Override
    public SingleTravel create(String... args) throws ParseException {
      // Number,DepartureDateTime,ArrivalDateTime,Airline,Origin,Destination,Price,NumSeats
      return new Coach(this, args[0], TimeFormat.DATE_TIME.parseString(args[1]),
          TimeFormat.DATE_TIME.parseString(args[2]), args[4], args[5],
          Double.parseDouble(args[6]), args[3], Integer.parseInt(args[7]));
    }
  },
  Ferry(8) {
    @Override
    public SingleTravel create(String... args) throws ParseException {
      // Number,DepartureDateTime,ArrivalDateTime,Airline,Origin,Destination,Price,NumSeats
      return new Ferry(this, args[0], TimeFormat.DATE_TIME.parseString(args[1]),
          TimeFormat.DATE_TIME.parseString(args[2]), args[4], args[5],
          Double.parseDouble(args[6]), args[3], Integer.parseInt(args[7]));
    }
  };

  /**
   * The number of arguments needed for parsing from a file.
   */
  private final int numArgs;

  /**
   * Creates a new TravelType with the specified arguments.
   *
   * @param numArgs
   *          the number of expected arguments in a file
   */
  private TravelType(int numArgs) {
    this.numArgs = numArgs;
  }

  @Override
  public int getNumArguments() {
    return numArgs;
  }

  @Override
  public String getDelimiter() {
    return ",";
  }

  @Override
  public boolean isUsable() {
    return true;
  }

  /**
   * Gets the names of all Travel Types.
   * 
   * @return a list containing the names
   */
  public static List<String> getUsableNames() {
    List<String> travelTypes = new ArrayList<>(values().length);
    for (TravelType ut : TravelType.values()) {
      travelTypes.add(ut.name());
    }
    return travelTypes;
  }

  /**
   * Gets a Travel Type by the given index.
   * 
   * @param selected
   *          the selected index
   * @return a Travel Type if one is found; null otherwise
   */
  public static TravelType getByIndex(int selected) {
    if (selected < 0 || selected >= values().length) {
      return null;
    }
    return values()[selected];
  }
}
