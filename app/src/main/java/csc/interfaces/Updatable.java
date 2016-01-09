package csc.interfaces;

/**
 * An interface that specifies that objects can be updated with the fields of
 * another.
 *
 * @param <K>
 *          the type of the object to be updated to
 */
public interface Updatable<K> {

  /**
   * Updates this object with the fields of other.
   *
   * @param other
   *          the object to take the information from
   */
  public void update(K other);
}
