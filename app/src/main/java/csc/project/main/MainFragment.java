package csc.project.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import csc.project.R;
import csc.project.UserControl;
import csc.project.interact.SearchItineraryActivity;
import csc.project.interact.SearchTravelActivity;
import csc.project.login.LoginActivity;
import csc.travel.TravelComparator;
import csc.travel.TravelType;
import csc.util.Constants;

import java.util.List;

/**
 * Class for the first screen in the main activity.
 */
public class MainFragment extends Fragment {
  /**
   * A key string for the noPassword arguments of this fragment.
   */
  private static final String PASS_KEY = "noPassword";

  /**
   * The field for the date.
   */
  private EditText idDate = null;

  /**
   * The field for the origin.
   */
  private EditText idOrigin = null;

  /**
   * The field for the destination.
   */
  private EditText idDest = null;

  /**
   * The comparator choice.
   */
  private Spinner spinner = null;

  /**
   * The type choice.
   */
  private Spinner spinnerType = null;

  /**
   * Returns a new instance of this fragment.
   *
   * @param noPassword
   *          if the user's password was newly set
   */
  public static MainFragment newInstance(boolean noPassword) {
    MainFragment fragment = new MainFragment();
    Bundle args = new Bundle();
    args.putBoolean(PASS_KEY, noPassword);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_main, container, false);
    TextView newly = (TextView) rootView.findViewById(R.id.welcome);
    // specify text to be displayed in the TextView
    if (getArguments().getBoolean(PASS_KEY)) {
      TextView pass = (TextView) rootView.findViewById(R.id.password);
      pass.setVisibility(View.VISIBLE);
    }
    newly.setText(getString(R.string.hello_msg));

    idDate = (EditText) rootView.findViewById(R.id.date);
    idOrigin = (EditText) rootView.findViewById(R.id.email);
    idDest = (EditText) rootView.findViewById(R.id.name);

    // populate the entries
    spinner = (Spinner) rootView.findViewById(R.id.spinner6);
    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(),
        android.R.layout.simple_spinner_item, TravelComparator.getUsableNames());
    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(spinnerArrayAdapter);

    List<String> names = TravelType.getUsableNames();
    names.add("All");
    spinnerType = (Spinner) rootView.findViewById(R.id.spinner7);
    spinnerArrayAdapter = new ArrayAdapter<>(getActivity(),
        android.R.layout.simple_spinner_item, names);
    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinnerType.setAdapter(spinnerArrayAdapter);
    spinnerType.setSelection(names.size() - 1); // Set to "All"

    return rootView;
  }

  /**
   * Transition to search travels.
   */
  public void searchTravels() {
    // pass fields on to search class
    Intent intent = new Intent(getActivity(), SearchTravelActivity.class);
    intent.putExtra(getString(R.string.date_key), idDate.getText().toString());
    intent.putExtra(getString(R.string.order_key), spinner.getSelectedItemPosition());
    intent.putExtra(getString(R.string.type_key), spinnerType.getSelectedItemPosition());
    intent.putExtra(getString(R.string.dest_key), idDest.getText().toString());
    intent.putExtra(getString(R.string.origin_key), idOrigin.getText().toString());
    startActivity(intent);
  }

  /**
   * Transition to search itineraries.
   */
  public void searchItineraries() {
    // pass fields on to search class
    Intent intent = new Intent(getActivity(), SearchItineraryActivity.class);
    intent.putExtra(getString(R.string.date_key), idDate.getText().toString());
    intent.putExtra(getString(R.string.order_key), spinner.getSelectedItemPosition());
    UserControl uc = ((UserControl) getActivity().getApplicationContext());
    if (uc.getUser().getType().isUsable()) {
      intent.putExtra(Constants.USER_KEY, uc.getUser().getIdentifier());
    }
    intent.putExtra(getString(R.string.dest_key), idDest.getText().toString());
    intent.putExtra(getString(R.string.origin_key), idOrigin.getText().toString());
    startActivity(intent);
  }

  /**
   * The transition method to the login activity.
   */
  public void logout() {
    getActivity().finish();

    UserControl uc = ((UserControl) getActivity().getApplicationContext());

    // remove auto login (otherwise we will jump back here)
    SharedPreferences.Editor edit = uc.getPreferences().edit();
    edit.putBoolean(getString(R.string.pref_autolog), false);
    edit.apply();

    // clear this activity before going backwards
    Intent intent = new Intent(getActivity(), LoginActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    if (uc.getUser() != null) {
      intent.putExtra(getString(R.string.r_emailKey), uc.getUser().getIdentifier());
    }
    uc.setUser(null);
    startActivity(intent);
  }
}
