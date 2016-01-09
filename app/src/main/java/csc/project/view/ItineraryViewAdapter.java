package csc.project.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import csc.project.R;
import csc.travel.Itinerary;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display items and makes a call to the
 * specified listener.
 */
public class ItineraryViewAdapter extends RecyclerView.Adapter<ItineraryViewHolder> {

  /**
   * The list of itineraries to show.
   */
  private final List<Itinerary> values;
  /**
   * The listener for when the user selects an itinerary.
   */
  private final ItineraryInteractionListener listener;

  /**
   * Creates a new adapter.
   * 
   * @param items
   *          the items to show
   * @param listener
   *          the action to take
   */
  public ItineraryViewAdapter(List<Itinerary> items, ItineraryInteractionListener listener) {
    values = items;
    this.listener = listener;
  }

  @Override
  public ItineraryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ItineraryViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.fragment_itinerary, parent, false));
  }

  @Override
  public void onBindViewHolder(final ItineraryViewHolder holder, int position) {
    holder.setItem(values.get(position));

    holder.getView().setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        listener.onInteraction(holder.getItem());
      }
    });
  }

  @Override
  public int getItemCount() {
    return values.size();
  }
}
