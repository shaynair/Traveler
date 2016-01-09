package csc.users;

import csc.interfaces.Updatable;
import csc.travel.Itinerary;
import csc.travel.SingleTravel;
import csc.util.TimeFormat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * An abstract class for registered users (in the database).
 */

public abstract class RegisteredUser extends User implements Updatable<RegisteredUser> {
  /**
   * Serializable implementation.
   */
  private static final long serialVersionUID = -2465596487306282569L;
  /**
   * The password of this user (encrypted if specified in Constants).
   */
  private String password;
  /**
   * The booked itineraries of this user.
   */
  private transient ArrayList<Itinerary> booked;
  /**
   * The first names of this user.
   */
  private String firstNames;
  /**
   * The last names of this user.
   */
  private String lastName;
  /**
   * The address of this user.
   */
  private String address;
  /**
   * The credit card number of this user.
   */
  private String creditCard;
  /**
   * The expiry date of the credit card of this user.
   */
  private Date expiryDate;

  /**
   * Creates a RegisteredUser with the given information.
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
  protected RegisteredUser(String email, UserType privilege, String firstNames,
      String lastName, String address, String creditCard, Date expiryDate) {
    super(email, privilege);
    this.firstNames = firstNames;
    this.lastName = lastName;
    this.address = address;
    this.creditCard = creditCard;
    this.expiryDate = expiryDate;
    this.password = ""; // no password by default

    this.booked = new ArrayList<>();
  }

  @Override
  public String getName() {
    return firstNames + ' ' + lastName;
  }

  /**
   * Gets the password for this user, if there is one.
   *
   * @return the password; empty string if there is no password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets a password for this user.
   *
   * @param password
   *          the password to set; empty string removes the password
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Gets a view of the booked itineraries of this user.
   *
   * @return the booked itineraries
   */
  public ArrayList<Itinerary> getBookedItineraries() {
    return booked;
  }

  /**
   * Books an itinerary for this user.
   *
   * @param it
   *          the itinerary to book.
   */
  public void bookItinerary(Itinerary it) {
    if (!hasBooked(it)) {
      booked.add(it);
      it.book();
    }
  }

  /**
   * Removes an itinerary for this user.
   *
   * @param it
   *          the itinerary to remove.
   */
  public void removeItinerary(Itinerary it) {
    if (hasBooked(it)) {
      booked.remove(it);
      it.unbook();
    }
  }

  /**
   * Removes any itineraries with the specified travel.
   *
   * @param st
   *          the travel to remove
   */
  public void removeTravel(SingleTravel st) {
    Iterator<Itinerary> itinIterator = booked.iterator();
    while (itinIterator.hasNext()) {
      Itinerary it = itinIterator.next();
      if (it.containsTravel(st)) {
        itinIterator.remove();
        it.unbook();
      }
    }
  }

  /**
   * Gets the first names of this user.
   *
   * @return the firstNames
   */
  public String getFirstNames() {
    return firstNames;
  }

  /**
   * Sets the first names of this user.
   *
   * @param firstNames
   *          the firstNames to set
   */
  public void setFirstNames(String firstNames) {
    this.firstNames = firstNames;
  }

  /**
   * Gets the last name of this user.
   *
   * @return the lastName
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * Sets the last name of this user.
   *
   * @param lastName
   *          the lastName to set
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * Gets the address of this user.
   *
   * @return the address
   */
  public String getAddress() {
    return address;
  }

  /**
   * Sets the address of this user.
   *
   * @param address
   *          the address to set
   */
  public void setAddress(String address) {
    this.address = address;
  }

  /**
   * Gets the credit card of this user.
   *
   * @return the creditCard
   */
  public String getCreditCard() {
    return creditCard;
  }

  /**
   * Sets the credit card of this user.
   *
   * @param creditCard
   *          the creditCard to set
   */
  public void setCreditCard(String creditCard) {
    this.creditCard = creditCard;
  }

  /**
   * Gets the expiry date of this user.
   *
   * @return the expiryDate as a string
   */
  public String getExpiryDate() {
    return TimeFormat.DATE.formatDate(expiryDate);
  }

  /**
   * Sets the expiry date of this user.
   *
   * @param expiryDate
   *          the expiryDate to set
   */
  public void setExpiryDate(Date expiryDate) {
    this.expiryDate = expiryDate;
  }

  /**
   * Checks if this user has booked an itinerary.
   * 
   * @param itin
   *          the itinerary to check
   * @return whether it is booked or not.
   */
  public boolean hasBooked(Itinerary itin) {
    return booked.contains(itin);
  }

  @Override
  public void update(RegisteredUser other) {
    this.firstNames = other.firstNames;
    this.lastName = other.lastName;
    this.address = other.address;
    this.creditCard = other.creditCard;
    this.expiryDate = other.expiryDate;
    if (!other.getPassword().isEmpty()) {
      this.password = other.password;
    }
    // booked itineraries do not get updated
  }

  @Override
  public String toString() {
    // LastName,FirstNames,Email,Address,CreditCardNumber,ExpiryDate
    return String.format("%s,%s,%s,%s,%s,%s", getLastName(), getFirstNames(), getIdentifier(),
        getAddress(), getCreditCard(), getExpiryDate());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + address.hashCode();
    result = prime * result + creditCard.hashCode();
    result = prime * result + expiryDate.hashCode();
    result = prime * result + firstNames.hashCode();
    result = prime * result + lastName.hashCode();
    result = prime * result + password.hashCode();
    return result;
  }

  /**
   * Provides a custom reading method for de-serializing.
   *
   * @param ois
   *          the stream to read this object
   * @throws IOException
   *           if there is an error in reading
   * @throws ClassNotFoundException
   *           if a class is not found
   */
  private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
    // read all non-transient fields
    ois.defaultReadObject();

    // booked is transient because we want to do itinerary reading ourselves
    this.booked = new ArrayList<>();

    // booked is read by the SaveOperations class
  }

  @Override
  public boolean equals(Object oth) {
    if (this == oth) {
      return true;
    }
    if (!super.equals(oth)) {
      return false;
    }
    RegisteredUser other = (RegisteredUser) oth;
    return address.equals(other.address) && booked.equals(other.booked)
        && creditCard.equals(other.creditCard) && expiryDate.equals(other.expiryDate)
        && firstNames.equals(other.firstNames) && lastName.equals(other.lastName)
        && password.equals(other.password);
  }
}
