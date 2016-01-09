package csc.project.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import csc.project.R;
import csc.travel.Itinerary;
import csc.util.Constants;

import java.io.Serializable;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the
 * {@link ItineraryInteractionListener} interface.
 */
public class ItineraryFragment extends Fragment {

  /**
   * The list to show.
   */
  private List<Itinerary> list = null;

  /**
   * The listener for when the user selects an itinerary.
   */
  private ItineraryInteractionListener listener = null;

  /**
   * Creates a list of travels.
   * 
   * @param items
   *          the list to display
   * @return a travel fragment with the list
   */
  public static <T extends List<Itinerary> & Serializable> ItineraryFragment newInstance(
      T items) {
    ItineraryFragment frag = new ItineraryFragment();
    Bundle args = new Bundle();
    args.putSerializable(Constants.ITIN_KEY, items);
    frag.setArguments(args);
    return frag;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void onCreate(Bundle saved) {
    super.onCreate(saved);
    if (this.getArguments() != null
        && this.getArguments().getSerializable(Constants.ITIN_KEY) != null) {
      list = (List<Itinerary>) this.getArguments().getSerializable(Constants.ITIN_KEY);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_travel_list,
        container, false);
    recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
    recyclerView.setAdapter(new ItineraryViewAdapter(list, listener));

    recyclerView.addOnItemTouchListener(new FragmentTouchListener());

    return recyclerView;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (activity instanceof ItineraryInteractionListener) {
      listener = (ItineraryInteractionListener) activity;
    } else {
      throw new IllegalArgumentException(
          "Activity must implement ItineraryInteractionListener");
    }
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof ItineraryInteractionListener) {
      listener = (ItineraryInteractionListener) context;
    } else {
      throw new IllegalArgumentException(
          "Context must implement ItineraryInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    listener = null;
  }
}
