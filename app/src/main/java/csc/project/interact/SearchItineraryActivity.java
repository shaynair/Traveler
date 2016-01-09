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
import csc.project.view.ItineraryFragment;
import csc.project.view.ItineraryInteractionListener;
import csc.travel.Itinerary;
import csc.travel.Travel;
import csc.travel.TravelComparator;
import csc.util.Constants;
import csc.util.TimeFormat;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

/**
 * Activity for searching itineraries.
 */
public class SearchItineraryActivity extends BaseActivity
    implements ItineraryInteractionListener {

  /**
   * The user associated with this class.
   */
  private String user = null;

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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search_itinerary);

    user = getIntent().getStringExtra(Constants.USER_KEY);

    idDate = (EditText) findViewById(R.id.date);
    idOrigin = (EditText) findViewById(R.id.email);
    idDest = (EditText) findViewById(R.id.name);

    // populate the entries
    spinner = (Spinner) findViewById(R.id.spinner4);
    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this,
        android.R.layout.simple_spinner_item, TravelComparator.getUsableNames());
    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(spinnerArrayAdapter);

    // launch fields from intent
    Intent intent = getIntent();
    idDate.setText(intent.getStringExtra(getString(R.string.date_key)));
    idOrigin.setText(intent.getStringExtra(getString(R.string.origin_key)));
    idDest.setText(intent.getStringExtra(getString(R.string.dest_key)));
    spinner.setSelection(
        intent.getIntExtra(getString(R.string.order_key), spinner.getSelectedItemPosition()));

    // add travel list
    ItineraryFragment frag = ItineraryFragment.newInstance(new ArrayList<Itinerary>());
    getSupportFragmentManager().beginTransaction().add(R.id.layout, frag).commit();
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);

    // if intent filled out fields, search
    if (!idDate.getText().toString().isEmpty() && !idOrigin.getText().toString().isEmpty()
        && !idDest.getText().toString().isEmpty()) {
      search(getCurrentFocus());
    }
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

    final String origin = idOrigin.getText().toString();
    final String dest = idDest.getText().toString();
    if (origin.equalsIgnoreCase(dest)) {
      idDest.setError(getString(R.string.error_invalid));
      idDest.requestFocus();
      return;
    }
    Date date;

    try {
      date = TimeFormat.DATE.parseString(idDate.getText().toString());
    } catch (ParseException ignore) {
      idDate.setError(getString(R.string.error_invalid));
      idDate.requestFocus();
      return;
    }
    Comparator<Travel> tt = TravelComparator.getByIndex(spinner.getSelectedItemPosition());
    // tt = null means no comparison

    UserControl uc = (UserControl) getApplicationContext();
    ArrayList<Itinerary> list = uc.getDatabase().searchItineraries(date, origin, dest, tt);
    if (list.isEmpty()) {
      idOrigin.setError(getString(R.string.error_none));
      idOrigin.requestFocus();
    } else {
      ItineraryFragment frag = ItineraryFragment.newInstance(list);
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
      ft.replace(R.id.layout, frag).commit();
    }
  }

  @Override
  public void onInteraction(Itinerary itin) {
    Intent intent = new Intent(this, ViewItineraryActivity.class);
    // pass the user and itinerary to the view class
    intent.putExtra(Constants.USER_KEY, user);
    intent.putExtra(Constants.ITIN_KEY, itin);
    startActivity(intent);
  }
}
