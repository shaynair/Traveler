package csc.interfaces;

import java.text.ParseException;

/**
 * An interface for parsing objects from a single line.
 *
 * @param <T>
 *          the type of object to be parsed
 */
public interface LineParse<T> {
  /**
   * Creates a new object of the appropriate type, from the file.
   *
   * @return a new object of the appropriate type
   * @throws ParseException
   *           when a date argument is invalid
   */
  public T create(String... args) throws ParseException;

  /**
   * Gets the number of arguments expected for parsing from a file.
   *
   * @return the number of arguments
   */
  public int getNumArguments();

  /**
   * Gets the delimiter for parsing the line.
   *
   * @return the delimiter
   */
  public String getDelimiter();

  /**
   * Gets whether this is usable or not.
   *
   * @return whether this can be used to create or not
   */
  public boolean isUsable();
}
