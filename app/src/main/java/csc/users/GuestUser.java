package csc.users;

/**
 * A class for guest users (not in the database).
 */
public class GuestUser extends User {
  /**
   * Serializable implementation.
   */
  private static final long serialVersionUID = -246559648730628569L;

  /**
   * Creates a GuestUser.
   */
  public GuestUser() {
    super("Guest", UserType.Guest);
  }

  @Override
  public String getName() {
    return getIdentifier();
  }

  @Override
  public String toString() {
    return getIdentifier();
  }
}
