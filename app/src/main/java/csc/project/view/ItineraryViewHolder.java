package csc.project.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import csc.project.R;
import csc.travel.Itinerary;

/**
 * Holds a view of an item.
 */
public class ItineraryViewHolder extends RecyclerView.ViewHolder {
  /**
   * The itinerary associated with this item.
   */
  private Itinerary item = null;
  /**
   * The whole view of this item.
   */
  private final View view;
  /**
   * The origin field to show.
   */
  private final TextView idOrigin;
  /**
   * The destination field to show.
   */
  private final TextView idDestination;
  /**
   * The start time field to show.
   */
  private final TextView idStart;
  /**
   * The end time field to show.
   */
  private final TextView idEnd;
  /**
   * The cost field to show.
   */
  private final TextView idCost;
  /**
   * The time field to show.
   */
  private final TextView idTime;

  /**
   * Creates a new ViewHolder with the view.
   *
   * @param view
   *          the view to hold
   */
  public ItineraryViewHolder(View view) {
    super(view);
    this.view = view;
    idOrigin = (TextView) view.findViewById(R.id.email);
    idDestination = (TextView) view.findViewById(R.id.destination);
    idStart = (TextView) view.findViewById(R.id.start_date_time);
    idEnd = (TextView) view.findViewById(R.id.arrival_date_time);
    idCost = (TextView) view.findViewById(R.id.cost);
    idTime = (TextView) view.findViewById(R.id.travel_time);
  }

  /**
   * Gets the item of this view.
   *
   * @return the item
   */
  public Itinerary getItem() {
    return item;
  }

  /**
   * Sets the item of this view.
   *
   * @param item
   *          the item to set
   */
  public void setItem(Itinerary item) {
    this.item = item;

    idOrigin.setText("From " + item.getOrigin());
    idDestination.setText("To " + item.getDestination());
    idStart.setText("Departs on " + item.formatStartTime());
    idEnd.setText("Arrives on " + item.formatEndTime());
    idCost.setText(String.format("Costs $%.2f", item.getCost()));
    idTime.setText("Duration: " + item.formatTravelTime());
  }

  /**
   * Gets the view.
   *
   * @return the view
   */
  public View getView() {
    return view;
  }
}