package csc.util;

/**
 * A class for listing any miscellaneous constants.
 */
public final class Constants {

  /**
   * The minimum length for passwords.
   */
  public static final int MIN_PASSWORD = 6;

  /**
   * The maximum length for passwords.
   */
  public static final int MAX_PASSWORD = 16;

  /**
   * Indicates the mimimum amount of stopover between travels, in milliseconds.
   */
  public static final long MIN_STOPOVER = 30L * 60L * 1000L;

  /**
   * Indicates the maximum amount of stopover between travels, in milliseconds.
   */
  public static final long MAX_STOPOVER = 6L * 60L * 60L * 1000L;

  /**
   * Indicates the encoding of files. (UTF-8, US-ASCII, etc.)
   */
  public static final String FILE_ENCODING = "US-ASCII";

  /**
   * The hashing algorithm to use for passwords. Other values include MD5
   * (insecure), SHA-256, SHA-1 (insecure), or empty string (none)
   */
  public static final String PASS_ENCODING = "SHA-256";

  /**
   * The file name to use for internally loading and saving data.
   */
  public static final String SAVE_FILE = "passwords.txt";

  /**
   * The name of the directory to read files from.
   */
  public static final String SAVE_DIR = "record_data";

  /**
   * The name of the database table.
   */
  public static final String DATABASE_TABLE = "save";

  /**
   * The name of the database column to store the byte array in.
   */
  public static final String DATABASE_COLUMN = "serialized";

  /**
   * The version of the database.
   */
  public static final int DATABASE_VERSION = 1;

  /**
   * The encryption key to use for internally loading and saving data. Must be
   * at least 16 characters in length.
   */
  public static final String CIPHER_PASSWORD = "CSCB07H3F-2015-ProjectP3-T0003-1";

  /**
   * The encryption algorithm for internally loading and saving data. Other
   * values include DES (insecure, outdated), and AES.
   */
  public static final String CIPHER = "AES";

  /**
   * The padding algorithm to use for internally saving and loading data. Must
   * match CIPHER.
   */
  public static final String CIPHER_PADDING = "AES/GCM/NoPadding";

  /**
   * The length of the initialization vector for the encryption algorithm. Must
   * be exactly 16.
   */
  public static final int CIPHER_IV_LENGTH = 16;

  /**
   * The length of the additional random bytes to add to the encryption
   * password. Recommended value is anything above 8.
   */
  public static final int CIPHER_SALT_LENGTH = 16;

  /**
   * The key to use for all itinerary-related arguments.
   */
  public static final String ITIN_KEY = "ItineraryKey";

  /**
   * The key to use for all user-related arguments.
   */
  public static final String USER_KEY = "userKey";

  /**
   * Key for all single travel-related arguments.
   */
  public static final String TRAVEL_KEY = "TravelKey";

  /**
   * Key for all type-related arguments.
   */
  public static final String TYPE_KEY = "TravelTypeKey";

  /**
   * Checks if an email is valid or not.
   *
   * @param email
   *          the email to check
   * @return whether the email is invalid or not
   */
  public static boolean isEmailInvalid(String email) {
    // contains exactly one @ and a . is after the @
    return !email.contains("@") || email.lastIndexOf('.') <= email.indexOf('@')
        || email.lastIndexOf('@') != email.indexOf('@');
  }

  /**
   * Checks whether a password is valid or not.
   *
   * @param password
   *          the password to check
   * @return whether the password is invalid or not.
   */
  public static boolean isPasswordInvalid(CharSequence password) {
    return password.length() < MIN_PASSWORD || password.length() > MAX_PASSWORD;
  }

  /**
   * Empty constructor.
   */
  private Constants() {
  }
}
