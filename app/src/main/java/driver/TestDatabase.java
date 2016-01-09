package driver;

import csc.database.MainDatabase;

/**
 * Used for testing the back-end.
 */
public class TestDatabase {

  /**
   * The singleton main database for testing.
   */
  private static final MainDatabase database = new MainDatabase();

  /**
   * Gets the main database.
   * 
   * @return the main database
   */
  public static MainDatabase getDatabase() {
    return database;
  }
}
