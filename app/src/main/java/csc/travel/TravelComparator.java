package csc.travel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * An enum for comparing Travels. Provides multiple forms of comparisons.
 */
public enum TravelComparator implements Comparator<Travel> {
  Total_Cost {
    @Override
    public int compare(Travel lhs, Travel rhs) {
      return Double.compare(lhs.getCost(), rhs.getCost());
    }
  },
  Total_Travel_Time {
    @Override
    public int compare(Travel lhs, Travel rhs) {
      return Long.compare(lhs.getTravelTime(), rhs.getTravelTime());
    }
  },
  Start_Time {
    @Override
    public int compare(Travel lhs, Travel rhs) {
      return lhs.getStartTime().compareTo(rhs.getStartTime());
    }
  },
  End_Time {
    @Override
    public int compare(Travel lhs, Travel rhs) {
      return lhs.getEndTime().compareTo(rhs.getEndTime());
    }
  };

  private final String name;

  private TravelComparator() {
    this.name = name().replace("_", " ");
  }

  /**
   * Returns a Comparator of this type but in descending order.
   *
   * @return a Comparator of this type but in reverse order
   */
  public Comparator<Travel> reverse() {
    return Collections.reverseOrder(this);
  }

  @Override
  public String toString() {
    return name;
  }

  /**
   * Gets the names of all Travel Comparators.
   *
   * @return a list containing the names
   */
  public static List<String> getUsableNames() {
    List<String> travelTypes = new ArrayList<>(values().length * 2 + 1);
    travelTypes.add("None");
    for (TravelComparator ut : values()) {
      travelTypes.add(ut.toString());
      travelTypes.add(ut + " (Descending)");
    }
    return travelTypes;
  }

  /**
   * Gets a Travel Comparator by the given index.
   *
   * @param selected
   *          the selected index
   * @return a comparator if one is found; null otherwise
   */
  public static Comparator<Travel> getByIndex(int selected) {
    if (selected <= 0 || selected > values().length * 2) {
      return null;
    }
    // selected = 0 is None
    TravelComparator tc = values()[(selected - 1) / 2];
    if (selected % 2 == 0) { // Descending
      return tc.reverse();
    } else {
      return tc;
    }
  }
}
