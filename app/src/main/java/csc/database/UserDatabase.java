package csc.database;

import csc.users.RegisteredUser;

import java.util.ArrayList;

/**
 * Storage of all registered users in the system.
 */
public class UserDatabase extends UniqueMap<String, RegisteredUser> {
  /**
   * Serializable implementation.
   */
  private static final long serialVersionUID = -8268532265982086570L;

  /**
   * Returns all users of the given names.
   *
   * @param names
   *          the names to check for
   * @param email
   *          the email to check for
   * @return a set of the users that contain the names provided
   */
  public ArrayList<RegisteredUser> searchUsers(String names, String email) {
    ArrayList<RegisteredUser> ret = new ArrayList<>();
    for (RegisteredUser ru : getValues()) {
      if (ru.getName().toLowerCase().contains(names.toLowerCase())
          && ru.getIdentifier().toLowerCase().contains(email.toLowerCase())) {
        ret.add(ru);
      }
    }
    return ret;
  }
}
