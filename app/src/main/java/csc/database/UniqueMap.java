package csc.database;

import csc.interfaces.Identifiable;
import csc.interfaces.Updatable;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A sorted map that maps an identifier to an identifiable and updatable object.
 *
 * @param <K>
 *          the identifier that will be sorted against
 * @param <V>
 *          the identifiable type
 */
public abstract class UniqueMap<K extends Comparable<K> & Serializable,
    V extends Identifiable<K, ?> & Updatable<V>> implements Serializable, Iterable<V> {
  /**
   * Serializable implementation.
   */
  private static final long serialVersionUID = -8007512760928250095L;
  /**
   * The map for implementing this class.
   */
  private final Map<K, V> map;

  protected UniqueMap() {
    this.map = new HashMap<>();
    // You could also use a TreeMap, but it's not needed
  }

  /**
   * Gets all objects in the database.
   *
   * @return a collection of all the objects
   */
  public Collection<V> getValues() {
    return map.values();
  }

  /**
   * Checks if the ID exists.
   *
   * @param id
   *          the ID to check.
   * @return True iff the ID exists in the database.
   */
  public boolean containsId(K id) {
    return id != null && map.containsKey(id);
  }

  /**
   * Adds info to this database. Returns the object that is in this collection.
   *
   * @param val
   *          the object to add to this database.
   * @return the old value that was in this database if one existed, otherwise
   *         returns val
   */
  public V add(V val) {
    V ret = map.get(val.getIdentifier());
    if (ret != null) {
      ret.update(val);
    } else {
      map.put(val.getIdentifier(), val);
      ret = val;
    }
    return ret;
  }

  /**
   * Changes an object's identifier if it is possible.
   * 
   * @param val
   *          the object to change the identifier of
   * @param newId
   *          the identifier to change to
   */
  public void changeId(V val, K newId) {
    if (containsId(newId)) {
      return;
    }
    map.remove(val.getIdentifier());
    val.setIdentifier(newId);
    add(val);
  }

  /**
   * Registers multiple objects.
   *
   * @param toAdd
   *          The objects to add.
   */
  public void addAll(Iterable<V> toAdd) {
    for (V add : toAdd) {
      add(add);
    }
  }

  /**
   * Removes the object of the specified id.
   * 
   * @param id
   *          the id to remove
   */
  public void remove(K id) {
    if (id != null) {
      map.remove(id);
    }
  }

  /**
   * Get an object of a specific id.
   *
   * @param id
   *          the id of the object
   * @return the object
   */
  public V get(K id) {
    if (id == null) {
      return null;
    }
    return map.get(id);
  }

  /**
   * Clears all information from this database.
   */
  public void clear() {
    map.clear();
  }

  @Override
  public Iterator<V> iterator() {
    return map.values().iterator();
  }
}
