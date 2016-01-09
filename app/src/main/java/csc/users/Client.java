package csc.users;

import java.util.Date;

/**
 * A class that represents an client.
 */

public class Client extends RegisteredUser {
  /**
   * Serializable implementation.
   */
  private static final long serialVersionUID = 2459059241137294064L;

  /**
   * Create an client with the given info.
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
  public Client(String email, UserType privilege, String firstNames, String lastName,
      String address, String creditCard, Date expiryDate) {
    super(email, privilege, firstNames, lastName, address, creditCard, expiryDate);
  }

}
