package csc.util;

/**
 * A class that lists all privilege constants.
 */
public final class Privileges {
  // --------------Constants----------------
  /**
   * The privilege level of a guest.
   */
  public static final int GUEST_LEVEL = 0;
  /**
   * The privilege level of a client.
   */
  public static final int CLIENT_LEVEL = 1;
  /**
   * The privilege level of a moderator.
   */
  public static final int MOD_LEVEL = 5;
  /**
   * The privilege level of an administrator.
   */
  public static final int ADMIN_LEVEL = 10;

  // -----------------All-------------------
  /**
   * The privilege level needed to search itineraries.
   */
  public static final int SEARCH_MULTI = 0; // Itineraries
  /**
   * The privilege level needed to search travels.
   */
  public static final int SEARCH_SINGLE = 0; // SingleTravels
  /**
   * The privilege level needed to sort itineraries and travels.
   */
  public static final int SORT_TRAVEL = 0;

  // ----------------Client-----------------
  /**
   * The privilege level needed to book itineraries.
   */
  public static final int BOOK_TRAVEL = 1;
  /**
   * The privilege level needed to view booked itineraries.
   */
  public static final int VIEW_BOOK = 1;
  /**
   * The privilege level needed to edit the user's own information.
   */
  public static final int EDIT_SELF = 1;
  // -----------------Mod-------------------
  /**
   * The privilege level needed to edit travel information..
   */
  public static final int EDIT_TRAVEL = 5;
  /**
   * The privilege level needed to update travel data from a file.
   */
  public static final int UPLOAD_TRAVEL = 5;

  // ----------------Admin------------------
  /**
   * The privilege level needed to edit other users' information.
   */
  public static final int EDIT_OTHER = 10;
  /**
   * The privilege level needed to view other users' information.
   */
  public static final int VIEW_OTHER = 10;
  /**
   * The privilege level needed to update user data from a file.
   */
  public static final int UPLOAD_USER = 10;
  /**
   * The privilege level needed to view other users' bookings.
   */
  public static final int EDIT_OTHER_BOOK = 10;
  /**
   * The privilege level needed to edit other users' bookings.
   */
  public static final int VIEW_OTHER_BOOK = 10;

  /**
   * Empty constructor.
   */
  private Privileges() {
  }
}
