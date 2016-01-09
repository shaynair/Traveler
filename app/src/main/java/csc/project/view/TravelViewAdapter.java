package csc.project.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import csc.project.R;
import csc.travel.SingleTravel;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display items and makes a call to the
 * specified listener.
 */
public class TravelViewAdapter extends RecyclerView.Adapter<TravelViewHolder> {

  /**
   * The list of travels to show.
   */
  private final List<SingleTravel> values;
  /**
   * The listener for when user selects an item.
   */
  private final TravelInteractionListener listener;

  /**
   * Creates a new adapter.
   * 
   * @param items
   *          the items to show
   * @param listener
   *          the action to take
   */
  public TravelViewAdapter(List<SingleTravel> items, TravelInteractionListener listener) {
    values = items;
    this.listener = listener;
  }

  @Override
  public TravelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new TravelViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.fragment_travel, parent, false));
  }

  @Override
  public void onBindViewHolder(final TravelViewHolder holder, int position) {
    holder.setItem(values.get(position));

    holder.getView().setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (null != listener) {
          // Notify the active callbacks interface (the activity, if the
          // fragment is attached to one) that an item has been selected.
          listener.onInteraction(holder.getItem());
        }
      }
    });
  }

  @Override
  public int getItemCount() {
    return values.size();
  }
}
