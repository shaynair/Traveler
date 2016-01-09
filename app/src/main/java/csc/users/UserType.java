package csc.users;

import csc.interfaces.LineParse;
import csc.util.Privileges;
import csc.util.TimeFormat;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * A class for storing user types.
 */
public enum UserType implements LineParse<RegisteredUser> {
  Guest(Privileges.GUEST_LEVEL, -1), Client(Privileges.CLIENT_LEVEL, 6) {
    @Override
    public RegisteredUser create(String... args) throws ParseException {
      // LastName,FirstNames,Email,Address,CreditCardNumber,ExpiryDate
      return new Client(args[2], this, args[1], args[0], args[3], args[4],
          TimeFormat.DATE.parseString(args[5]));
    }
  },
  Moderator(Privileges.MOD_LEVEL, 6) {
    @Override
    public RegisteredUser create(String... args) throws ParseException {
      // LastName,FirstNames,Email,Address,CreditCardNumber,ExpiryDate
      return new Moderator(args[2], this, args[1], args[0], args[3], args[4],
          TimeFormat.DATE.parseString(args[5]));
    }
  },
  Administrator(Privileges.ADMIN_LEVEL, 6) {
    @Override
    public RegisteredUser create(String... args) throws ParseException {
      // LastName,FirstNames,Email,Address,CreditCardNumber,ExpiryDate
      return new Administrator(args[2], this, args[1], args[0], args[3], args[4],
          TimeFormat.DATE.parseString(args[5]));
    }

  };

  /**
   * The privilege level of this user type.
   */
  private final int level;
  /**
   * The number of arguments expected when parsing from a file.
   */
  private final int numArgs;

  /**
   * Defines a new UserType.
   *
   * @param level
   *          the privileges of the user type
   * @param numArgs
   *          the number of expected arguments in a file
   */
  private UserType(int level, int numArgs) {
    this.level = level;
    this.numArgs = numArgs;
  }

  @Override
  public int getNumArguments() {
    return numArgs;
  }

  @Override
  public String getDelimiter() {
    return ",";
  }

  /**
   * Gets the privilege level constant.
   *
   * @return the privilege level
   */
  public int getPrivilege() {
    return level;
  }

  /**
   * Checks if this privilege level is high enough.
   *
   * @param check
   *          the privilege level to check against this privilege
   * @return True iff this privilege level is greater than or equal to check.
   */
  public boolean hasPrivilege(int check) {
    return level >= check;
  }

  @Override
  public boolean isUsable() {
    return this != Guest;
  }

  @Override
  public RegisteredUser create(String... args) throws ParseException {
    // default -- if not specified
    throw new UnsupportedOperationException("This type does not support creation.");
  }

  /**
   * Gets the names of all usable User Types (registered ones).
   * 
   * @return an array list containing the names
   */
  public static List<String> getUsableNames() {
    List<String> strings = new ArrayList<>(values().length);
    for (UserType ut : UserType.values()) {
      strings.add(ut.name());
    }
    return strings;
  }

  /**
   * Gets a usable User Type by the given index.
   * 
   * @param selected
   *          the selected index
   * @return a User Type if one is found; null otherwise
   */
  public static UserType getByIndex(int selected) {
    if (selected < 0 || selected >= values().length) {
      return null;
    }
    return values()[selected];
  }
}
