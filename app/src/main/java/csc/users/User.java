package csc.users;

import csc.interfaces.Identifiable;

/**
 * An interface for User, with a unique string identifier and privilege.
 */

public abstract class User implements Identifiable<String, UserType> {
  /**
   * Serializable implementation.
   */
  private static final long serialVersionUID = -24655964873062859L;
  /**
   * The email (unique identifier) of this user.
   */
  private String id;
  /**
   * The privilege level of this user.
   */
  private UserType privilege;

  /**
   * Creates a User.
   *
   * @param id
   *          the id to set
   * @param privilege
   *          the privilege to set
   */
  protected User(String id, UserType privilege) {
    this.id = id;
    this.privilege = privilege;
  }

  @Override
  public String getIdentifier() {
    return id;
  }

  @Override
  public void setIdentifier(String id) {
    this.id = id;
  }

  /**
   * Checks if this user's privilege level is high enough.
   *
   * @param check
   *          the privilege level to check against this user
   * @return True iff this user's privilege level is greater than or equal to
   *         check.
   */
  public boolean hasPrivilege(int check) {
    return privilege.hasPrivilege(check);
  }

  @Override
  public UserType getType() {
    return privilege;
  }

  @Override
  public void setType(UserType type) {
    this.privilege = type;
  }

  /**
   * Gets the name of this User.
   *
   * @return The formatted name of this User.
   */
  public abstract String getName();

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + privilege.hashCode();
    result = prime * result + id.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object oth) {
    if (this == oth) {
      return true;
    }
    if (oth == null || getClass() != oth.getClass()) {
      return false;
    }
    User other = (User) oth;
    return id.equals(other.id) && privilege == other.privilege;
  }
}
