package csc.io;

import csc.interfaces.LineParse;
import csc.util.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class for input from files and formatting to console. Admins select the
 * file paths.
 */
public final class InputOperations {
  /**
   * The logger for this class.
   */
  private static final Logger log = Logger.getLogger(InputOperations.class.getName());

  /**
   * A string containing all possible characters in a password.
   */
  private static final String rand = "0123456789abcdefghijklmnopqrstuvwxyz"
      + "!#$%&*_,.ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  /**
   * Hex array for the bytesToHex method.
   */
  private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

  /**
   * Default empty constructor.
   */
  private InputOperations() {
  }

  /**
   * Parses data from a given file path, parsing objects line by line. The data
   * returned is of class T, and type is of class E, where E can parse lines to
   * create T.
   *
   * @param path
   *          the path of the file
   * @param type
   *          the type of the objects
   * @return a set containing all users parsed from the file. if the file does
   *         not exist, an empty set is returned instead.
   */
  public static <T, E extends LineParse<T>> Collection<T> parseData(String path, E type) {
    File file = new File(path);
    if (file.exists()) {
      try (FileInputStream in = new FileInputStream(path)) {
        return parseData(in, type);
      } catch (IOException e) {
        log.log(Level.SEVERE, e.toString(), e);
      }
    }
    return Collections.emptySet();
  }

  /**
   * Parses data from a given file, parsing objects line by line. The data
   * returned is of class T, and type is of class E, where E can parse lines to
   * create T.
   *
   * @param in
   *          the input stream
   * @param type
   *          the type of the objects
   * @return a set containing all users parsed from the file
   */
  public static <T, E extends LineParse<T>> Collection<T> parseData(FileInputStream in,
      E type) {
    Collection<T> ret = new LinkedHashSet<>();

    // open the file for reading with the given path
    try (
        InputStreamReader isr = new InputStreamReader(in,
            Charset.forName(Constants.FILE_ENCODING));
        BufferedReader reader = new BufferedReader(isr)) {
      // each line is one object
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        String[] split = line.split(type.getDelimiter());
        try {
          // invalid input -- wrong number of arguments
          if (split.length == type.getNumArguments()) {
            ret.add(type.create(split));
          } else {
            log.log(Level.WARNING, "A line had {0} arguments, but expected was {1}. Skipping.",
                new Object[] { split.length, type.getNumArguments() });
          }
        } catch (ParseException e) {
          log.log(Level.SEVERE, "A line had incorrect arguments. Skipping.", e);
        }
      }
    } catch (IOException e) {
      log.log(Level.SEVERE, e.toString(), e);
    }
    return ret;
  }

  /**
   * Formats a collection into line-by-line format.
   *
   * @param collection
   *          the collection of objects to parse
   * @return a String representing the collection
   */
  public static <T> String formatCollection(Iterable<T> collection) {
    StringBuilder sb = new StringBuilder();
    for (T it : collection) {
      sb.append(it).append('\n');
    }
    return sb.toString();
  }

  /**
   * Hashes an input string.
   *
   * @param input
   *          the input password
   * @return a hash of input
   */
  public static String hashPassword(String input) {
    try {
      // convert bytes to string
      return toHexString(cryptPassword(input));
    } catch (UnsupportedEncodingException e) {
      log.log(Level.SEVERE, "Error hashing password.", e);
      return input;
    }
  }

  /**
   * Generates a random password.
   * 
   * @param len
   *          the length of the password to generate
   * @return a string of the random password
   */
  public static String generatePassword(int len) {
    SecureRandom rnd = new SecureRandom();

    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; i++) {
      sb.append(rand.charAt(rnd.nextInt(rand.length())));
    }
    return sb.toString();
  }

  /**
   * Converts a byte array into a string.
   *
   * @param bytes
   *          the input byte array
   * @return a string representation of the bytes in hexadecimal format
   */
  private static String toHexString(byte[] bytes) {
    int length = bytes.length;
    char[] hexChars = new char[length * 2];
    for (int j = 0; j < length; j++) {
      int toAdd = bytes[j] & 0xFF;
      hexChars[j * 2] = hexArray[toAdd >>> 4]; // upper byte
      hexChars[j * 2 + 1] = hexArray[toAdd & 0x0F]; // lower byte
    }
    return new String(hexChars);
  }

  /**
   * Encrypts an input string.
   *
   * @param input
   *          the input password
   * @return a byte array of the encrypted string
   * @throws UnsupportedEncodingException
   *           when the encoding is invalid
   */
  public static byte[] cryptPassword(String input) throws UnsupportedEncodingException {
    if (Constants.PASS_ENCODING.isEmpty()) {
      // no hashing is wanted
      return input.getBytes(Constants.FILE_ENCODING);
    }
    try {
      MessageDigest md = MessageDigest.getInstance(Constants.PASS_ENCODING);
      return md.digest(input.getBytes(Constants.FILE_ENCODING));
    } catch (NoSuchAlgorithmException e) {
      log.log(Level.SEVERE, "Error encrypting password.", e);
      return input.getBytes(Constants.FILE_ENCODING);
    }
  }
}
