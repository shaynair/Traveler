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
import csc.project.UserControl;
import csc.travel.SingleTravel;
import csc.travel.TravelType;
import csc.util.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the
 * {@link TravelInteractionListener} interface.
 */
public class TravelFragment extends Fragment {

  /**
   * The list to show.
   */
  private List<IdentifierTypePair> list = null;

  /**
   * Listener for when the user selects a travel.
   */
  private TravelInteractionListener listener = null;

  /**
   * Creates a list of travels.
   * 
   * @param items
   *          the list to display
   * @return a travel fragment with the list
   */
  public static <T extends List<SingleTravel> & Serializable> TravelFragment newInstance(
      T items) {
    TravelFragment frag = new TravelFragment();
    Bundle args = new Bundle();

    ArrayList<IdentifierTypePair> pairs = new ArrayList<>(items.size());
    for (SingleTravel st : items) {
      pairs.add(new IdentifierTypePair(st.getIdentifier(), st.getType()));
    }
    args.putSerializable(Constants.TRAVEL_KEY, pairs);
    frag.setArguments(args);
    return frag;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void onCreate(Bundle saved) {
    super.onCreate(saved);
    if (this.getArguments() != null
        && this.getArguments().getSerializable(Constants.TRAVEL_KEY) != null) {
      list = (List<IdentifierTypePair>) this.getArguments()
          .getSerializable(Constants.TRAVEL_KEY);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_travel_list,
        container, false);
    recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
    recyclerView.addOnItemTouchListener(new FragmentTouchListener());

    UserControl uc = (UserControl) getActivity().getApplicationContext();
    List<SingleTravel> st = new ArrayList<>(list.size());
    for (IdentifierTypePair pair : list) {
      st.add(uc.getDatabase().getTravel(pair.getType(), pair.getIdentifier()));
    }
    recyclerView.setAdapter(new TravelViewAdapter(st, listener));

    return recyclerView;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (activity instanceof TravelInteractionListener) {
      listener = (TravelInteractionListener) activity;
    } else {
      throw new IllegalArgumentException("Activity must implement TravelInteractionListener");
    }
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof TravelInteractionListener) {
      listener = (TravelInteractionListener) context;
    } else {
      throw new IllegalArgumentException("Context must implement TravelInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    listener = null;
  }

  /**
   * Class for serializing single travels.
   */
  private static final class IdentifierTypePair implements Serializable {
    /**
     * Serializable.
     */
    private static final long serialVersionUID = -2055850308091369578L;
    /**
     * The ID of the travel.
     */
    private final String identifier;
    /**
     * The type of the travel.
     */
    private final TravelType type;

    /**
     * Creates a new pair.
     * 
     * @param identifier
     *          the ID of the travel
     * @param type
     *          the type of the travel
     */
    private IdentifierTypePair(String identifier, TravelType type) {
      this.identifier = identifier;
      this.type = type;
    }

    /**
     * Gets the ID.
     * 
     * @return the ID of the travel.
     */
    public String getIdentifier() {
      return identifier;
    }

    /**
     * Gets the type.
     * 
     * @return the type of the travel.
     */
    public TravelType getType() {
      return type;
    }
  }
}
