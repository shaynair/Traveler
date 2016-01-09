package csc.interfaces;

import java.io.Serializable;

/**
 * A class for storing a unique identifier. Subclasses must implement equals,
 * hashCode and toString.
 *
 * @param <K>
 *          the type of identifier, can be ordered
 * @param <T>
 *          the type of the object, an enum
 */
public interface Identifiable<K extends Comparable<K> & Serializable, T extends Enum<T>>
    extends Serializable {

  /**
   * Get the identifier of this object.
   *
   * @return the identifier
   */
  public K getIdentifier();

  /**
   * Set the identifier of this object.
   *
   * @param id
   *          the identifier to set
   */
  public void setIdentifier(K id);

  /**
   * Gets the type of this object.
   *
   * @return the type of this object
   */
  public T getType();

  /**
   * Sets the type of this object.
   *
   * @param type
   *          the type to set
   */
  public void setType(T type);

  @Override
  public boolean equals(Object oth);

  @Override
  public String toString();

  @Override
  public int hashCode();
}
