package csc.users;

import java.util.Date;

/**
 * A class representing the Moderators.
 */

public class Moderator extends RegisteredUser {
  /**
   * Serializable implementation.
   */
  private static final long serialVersionUID = -280368812077625378L;

  /**
   * Create a moderator with the given info.
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
  public Moderator(String email, UserType privilege, String firstNames, String lastName,
      String address, String creditCard, Date expiryDate) {
    super(email, privilege, firstNames, lastName, address, creditCard, expiryDate);
  }

}
