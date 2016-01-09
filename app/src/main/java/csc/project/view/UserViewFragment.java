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
import csc.users.RegisteredUser;
import csc.util.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the
 * {@link UserInteractionListener} interface.
 */
public class UserViewFragment extends Fragment {
  /**
   * The list to show.
   */
  private List<String> list = null;
  /**
   * The listener for when the user selects an user.
   */
  private UserInteractionListener listener = null;

  /**
   * Creates a list of users.
   * 
   * @param items
   *          the list to display
   * @return a user fragment with the list
   */
  public static <T extends List<RegisteredUser> & Serializable> UserViewFragment newInstance(
      T items) {
    UserViewFragment frag = new UserViewFragment();
    Bundle args = new Bundle();

    ArrayList<String> serial = new ArrayList<>(items.size());
    for (RegisteredUser ru : items) {
      serial.add(ru.getIdentifier());
    }
    args.putSerializable(Constants.USER_KEY, serial);
    frag.setArguments(args);
    return frag;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void onCreate(Bundle saved) {
    super.onCreate(saved);
    if (this.getArguments() != null
        && this.getArguments().getSerializable(Constants.USER_KEY) != null) {
      list = (List<String>) this.getArguments().getSerializable(Constants.USER_KEY);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_user_list,
        container, false);
    recyclerView.addOnItemTouchListener(new FragmentTouchListener());
    recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

    // recreate
    UserControl uc = (UserControl) getActivity().getApplicationContext();
    List<RegisteredUser> ru = new ArrayList<>(list.size());
    for (String id : list) {
      ru.add(uc.getDatabase().getUser(id));
    }

    recyclerView.setAdapter(new UserViewAdapter(ru, listener));

    return recyclerView;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (activity instanceof UserInteractionListener) {
      listener = (UserInteractionListener) activity;
    } else {
      throw new IllegalArgumentException("Activity must implement UserInteractionListener");
    }
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof UserInteractionListener) {
      listener = (UserInteractionListener) context;
    } else {
      throw new IllegalArgumentException("Context must implement UserInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    listener = null;
  }
}
