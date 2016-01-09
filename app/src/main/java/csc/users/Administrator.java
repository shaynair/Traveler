package csc.users;

import java.util.Date;

/**
 * A class representing the Administrators.
 */

public class Administrator extends RegisteredUser {
  /**
   * Serializable implementation.
   */
  private static final long serialVersionUID = -2803688123077625378L;

  /**
   * Create an administrator with the given info.
   *
   * @param email
   *          The email of this user.
   * @param privilege
   *          The privilege level of this user.
   * @param firstNames
   *          The first names of this user.
   * @param lastName
   *          The last name of this user.
   * @param address
   *          The address of this user.
   * @param creditCard
   *          The credit card number of this user.
   * @param expiryDate
   *          The expiry date of the credit card.
   */
  public Administrator(String email, UserType privilege, String firstNames, String lastName,
      String address, String creditCard, Date expiryDate) {
    super(email, privilege, firstNames, lastName, address, creditCard, expiryDate);
  }

}
