package driver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SampleTests {

  // NOTE: set the PATH variable to the location of the input csv files.
  public static final String PATH = "";
  public static final String CLIENTS = PATH + "clients.txt";
  public static final String FLIGHTS1 = PATH + "flights1.txt";
  public static final String FLIGHTS2 = PATH + "flights2.txt";

  public static final int TIMEOUT = 500;

  @Test(timeout = TIMEOUT)
  public void testGetClient() throws Exception {

    Driver.uploadClientInfo(CLIENTS);

    String expected = "Roe,Roe,richard@email.com,21 First Lane Way,9999888877776666,2017-10-01";
    String found = Driver.getClient("richard@email.com").trim();
    String msg = "Unexpected (incorrect or incorrectly formatted) "
        + "client information or string output was returned.";

    assertFalse("getClient(String) didn't find any clients.", found.isEmpty());
    assertEquals(msg, expected, found);
  }

  @Test(timeout = TIMEOUT)
  public void testGetFlights() throws Exception {

    Driver.uploadFlightInfo(FLIGHTS1);

    String expected = "490,2016-09-30 22:40,2016-10-01 01:59,Go Airline,New York,Boston,532.00";
    String found = Driver.getFlights("2016-09-30", "New York", "Boston").trim();
    String msg = "Unexpected (incorrect or incorrectly formatted) "
        + "flight information or string output was returned.";

    assertFalse("getFlights(String, String, String) didn't find any flights.",
        found.isEmpty());
    assertEquals(msg, expected, found);
  }

  @Test(timeout = TIMEOUT)
  public void testGetItineraries() throws Exception {

    Driver.uploadFlightInfo(FLIGHTS2);

    String expected = "490,2016-09-30 22:40,2016-10-01 01:59,Go Airline,London,Rome\n532.99\n03:19"
        + "\n102,2016-09-30 16:37,2016-09-30 17:22,Go Airline,London,Paris\n"
        + "249,2016-09-30 19:22,2016-09-30 22:40,Go Airline,Paris,Rome\n580.00\n06:03";
    String expected2 = "102,2016-09-30 16:37,2016-09-30 17:22,Go Airline,London,Paris\n"
        + "249,2016-09-30 19:22,2016-09-30 22:40,Go Airline,Paris,Rome\n580.00\n06:03\n"
        + "490,2016-09-30 22:40,2016-10-01 01:59,Go Airline,London,Rome\n532.99\n03:19";
    String found = Driver.getItineraries("2016-09-30", "London", "Rome").trim();

    String msg = "Unexpected (incorrect or incorrectly formatted) "
        + "itinerary information or string output was returned.";

    assertFalse("getIntineraries(String, String, String) didn't find any flights.",
        found.isEmpty());

    boolean match1 = expected.equals(found);
    boolean match2 = expected2.equals(found);
    assertTrue(msg, match1 || match2);
  }
}
