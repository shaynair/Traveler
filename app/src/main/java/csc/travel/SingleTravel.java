package csc.travel;

import csc.interfaces.Identifiable;
import csc.interfaces.Updatable;

import java.util.Date;

/**
 * A class that represents a single travel.
 */
public abstract class SingleTravel extends Travel
    implements Identifiable<String, TravelType>, Updatable<SingleTravel> {
  /**
   * Serializable implementation.
   */
  private static final long serialVersionUID = 4953963860520338679L;
  /**
   * The id (unique number) of this travel.
   */
  private String id;
  /**
   * The type of this travel.
   */
  private TravelType type; // Enums are Serializable by default
  /**
   * The start time of this travel.
   */
  private Date start;
  /**
   * The end time of this travel.
   */
  private Date end;
  /**
   * The origin location of this travel.
   */
  private String origin;
  /**
   * The destination location of this travel.
   */
  private String destination;
  /**
   * The capacity for this travel.
   */
  private int capacity;
  /**
   * The number of users who have booked this travel.
   */
  private int numUsers;
  /**
   * The provider for this travel.
   */
  private String provider;

  /**
   * Create a SingleTravel instance with given parameters.
   *
   * @param type
   *          the type of this SingleTravel
   * @param id
   *          the given travel id
   * @param start
   *          the departure date and time
   * @param end
   *          the arrival date and time
   * @param origin
   *          the given origin
   * @param destination
   *          the given destination
   * @param cost
   *          the given cost
   * @param capacity
   *          the given capacity
   * @param provider
   *          the given provider
   */
  protected SingleTravel(TravelType type, String id, Date start, Date end, String origin,
      String destination, double cost, int capacity, String provider) {
    super(cost);
    this.id = id;
    this.type = type;
    this.start = start;
    this.end = end;
    this.origin = origin;
    this.destination = destination;
    this.capacity = capacity;
    this.provider = provider;
    this.numUsers = 0;
  }

  @Override
  public String getIdentifier() {
    return id;
  }

  @Override
  public void setIdentifier(String id) {
    this.id = id;
  }

  @Override
  public TravelType getType() {
    return type;
  }

  @Override
  public void setType(TravelType type) {
    this.type = type;
  }

  @Override
  public Date getStartTime() {
    return start;
  }

  @Override
  public Date getEndTime() {
    return end;
  }

  @Override
  public String getOrigin() {
    return origin;
  }

  @Override
  public String getDestination() {
    return destination;
  }

  /**
   * Sets the provider.
   * 
   * @param provider
   *          the provider to set
   */
  public void setProvider(String provider) {
    this.provider = provider;
  }

  /**
   * Sets the end date.
   * 
   * @param end
   *          the date to set
   */
  public void setEndTime(Date end) {
    this.end = end;
  }

  /**
   * Sets the start date.
   * 
   * @param start
   *          the date to set
   */
  public void setStartTime(Date start) {
    this.start = start;
  }

  /**
   * Sets the capacity.
   * 
   * @param capacity
   *          the capacity to set
   */
  public void setCapacity(int capacity) {
    this.capacity = capacity;
  }

  /**
   * Sets the cost.
   * 
   * @param cost
   *          the cost to set
   */
  public void setCost(double cost) {
    this.cost = cost;
  }

  /**
   * Sets the origin.
   * 
   * @param origin
   *          the origin to set
   */
  public void setOrigin(String origin) {
    this.origin = origin;
  }

  /**
   * Sets the destination.
   * 
   * @param destination
   *          the destination to set
   */
  public void setDestination(String destination) {
    this.destination = destination;
  }

  /**
   * Gets the capacity for this travel.
   *
   * @return the capacity for this travel
   */
  public int getCapacity() {
    return capacity;
  }

  /**
   * Gets whether this travel is full or not.
   *
   * @return True if this travel is full (exceeds capacity)
   */
  public boolean isFull() {
    return numUsers >= capacity;
  }

  /**
   * Adds a user to the total number of users booking this travel. Does nothing
   * if this travel is full.
   */
  public void addUser() {
    if (!isFull()) {
      numUsers++;
    }
  }

  /**
   * Removes a user to the total number of users booking this travel. Does
   * nothing if this travel is empty.
   */
  public void removeUser() {
    if (numUsers >= 0) {
      numUsers--;
    }
  }

  /**
   * Resets the user count of this travel.
   */
  public void resetUsers() {
    numUsers = 0;
  }

  /**
   * Gets the amount of available seats for this travel.
   *
   * @return the available capacity
   */
  public int getAvailableCapacity() {
    return capacity - numUsers;
  }

  /**
   * Gets the provider for this travel.
   *
   * @return the provider for this travel
   */
  public String getProvider() {
    return provider;
  }

  /**
   * Gets whether this travel is valid or not.
   *
   * @return a boolean representing if this travel is invalid
   */
  public boolean isInvalid() {
    return getEndTime().before(getStartTime()) || getCapacity() < 0
        || getOrigin().equalsIgnoreCase(getDestination());
  }

  @Override
  public void update(SingleTravel other) {
    this.start = other.start;
    this.end = other.end;
    this.origin = other.origin;
    this.destination = other.destination;
    this.cost = other.cost;
    this.capacity = other.capacity;
    this.provider = other.provider;
  }

  @Override
  public String toString() {
    return toString(true);
  }

  /**
   * Formats this travel into a string representation.
   *
   * @param includePrice
   *          if the price of this travel should be included
   * @return a string representing this travel
   */
  public abstract String toString(boolean includePrice);

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp = Double.doubleToLongBits(cost);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + destination.hashCode();
    result = prime * result + end.hashCode();
    result = prime * result + id.hashCode();
    result = prime * result + origin.hashCode();
    result = prime * result + start.hashCode();
    result = prime * result + type.hashCode();
    result = prime * result + capacity;
    result = prime * result + provider.hashCode();
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
    SingleTravel other = (SingleTravel) oth;
    return Double.doubleToLongBits(cost) == Double.doubleToLongBits(other.cost)
        && type == other.type && provider.equals(other.provider) && origin.equals(other.origin)
        && start.equals(other.start) && destination.equals(other.destination)
        && end.equals(other.end) && id.equals(other.id) && capacity == other.capacity;
  }

}
