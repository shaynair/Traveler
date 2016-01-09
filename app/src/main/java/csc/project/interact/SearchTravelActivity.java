package csc.project.interact;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import csc.project.BaseActivity;
import csc.project.R;
import csc.project.UserControl;
import csc.project.view.TravelFragment;
import csc.project.view.TravelInteractionListener;
import csc.travel.SingleTravel;
import csc.travel.Travel;
import csc.travel.TravelComparator;
import csc.travel.TravelType;
import csc.util.Constants;
import csc.util.Privileges;
import csc.util.TimeFormat;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Activity for searching travels.
 */
public class SearchTravelActivity extends BaseActivity implements TravelInteractionListener {

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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search_travel);

    idDate = (EditText) findViewById(R.id.date);
    idOrigin = (EditText) findViewById(R.id.email);
    idDest = (EditText) findViewById(R.id.name);

    // populate the entries
    spinner = (Spinner) findViewById(R.id.spinner4);
    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this,
        android.R.layout.simple_spinner_item, TravelComparator.getUsableNames());
    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(spinnerArrayAdapter);

    List<String> names = TravelType.getUsableNames();
    names.add("All");
    spinnerType = (Spinner) findViewById(R.id.spinner5);
    spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
        names);
    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinnerType.setAdapter(spinnerArrayAdapter);
    spinnerType.setSelection(names.size() - 1); // Set to "All"

    // launch fields from intent
    Intent intent = getIntent();
    idDate.setText(intent.getStringExtra(getString(R.string.date_key)));
    idOrigin.setText(intent.getStringExtra(getString(R.string.origin_key)));
    idDest.setText(intent.getStringExtra(getString(R.string.dest_key)));
    spinner.setSelection(
        intent.getIntExtra(getString(R.string.order_key), spinner.getSelectedItemPosition()));
    spinnerType.setSelection(intent.getIntExtra(getString(R.string.type_key),
        spinnerType.getSelectedItemPosition()));

    UserControl uc = (UserControl) getApplicationContext();
    if (uc.getUser() == null || !uc.getUser().hasPrivilege(Privileges.EDIT_TRAVEL)) {
      // hide the add button
      findViewById(R.id.add).setVisibility(View.GONE);
    }

    // add travel list
    TravelFragment frag = TravelFragment.newInstance(new ArrayList<SingleTravel>());
    getSupportFragmentManager().beginTransaction().add(R.id.layout, frag).commit();
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);

    // if intent filled out fields, search
    if (!idDate.getText().toString().isEmpty() || !idOrigin.getText().toString().isEmpty()
        || !idDest.getText().toString().isEmpty()) {
      search(getCurrentFocus());
    }
  }

  /**
   * Action to take when add is pressed.
   * 
   * @param view
   *          the current view
   */
  public void add(View view) {
    onInteraction(null); // goes to create a new travel
  }

  /**
   * Action to take when search is pressed.
   * 
   * @param view
   *          the current view
   */
  public void search(View view) {
    idDate.setError(null);
    idOrigin.setError(null);
    idDest.setError(null);

    String origin = idOrigin.getText().toString();
    String dest = idDest.getText().toString();
    if (origin.isEmpty()) {
      origin = null; // omit from the search
    }
    if (dest.isEmpty()) {
      dest = null; // omit from the search
    }
    if (origin != null && dest != null && origin.equalsIgnoreCase(dest)) {
      idDest.setError(getString(R.string.error_invalid));
      idDest.requestFocus();
      return;
    }
    Date date = null;

    if (!idDate.getText().toString().isEmpty()) {
      try {
        date = TimeFormat.DATE.parseString(idDate.getText().toString());
      } catch (ParseException ignore) {
        idDate.setError(getString(R.string.error_invalid));
        idDate.requestFocus();
        return;
      }
    }
    // date = null means no comparison

    Comparator<Travel> tt = TravelComparator.getByIndex(spinner.getSelectedItemPosition());
    // tt = null means no comparison

    TravelType type = TravelType.getByIndex(spinnerType.getSelectedItemPosition());
    // type = null means all

    UserControl uc = (UserControl) getApplicationContext();
    ArrayList<SingleTravel> list = uc.getDatabase().searchTravels(date, origin, dest, type,
        tt);
    if (list.isEmpty()) {
      idOrigin.setError(getString(R.string.error_none));
      idOrigin.requestFocus();
    } else {
      TravelFragment frag = TravelFragment.newInstance(list);
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
      ft.replace(R.id.layout, frag).commit();
    }
  }

  @Override
  public void onInteraction(SingleTravel travel) {
    UserControl uc = (UserControl) getApplicationContext();
    if (uc.getUser() == null || !uc.getUser().hasPrivilege(Privileges.EDIT_TRAVEL)) {
      return;
    }
    Intent intent = new Intent(this, EditTravelActivity.class);
    // pass the travel to the edit class
    if (travel != null) {
      intent.putExtra(Constants.TRAVEL_KEY, travel.getIdentifier());
      intent.putExtra(Constants.TYPE_KEY, travel.getType());
    }
    startActivity(intent);
  }
}
