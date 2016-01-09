package csc.project.interact;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import csc.project.BaseActivity;
import csc.project.R;
import csc.project.R.id;
import csc.project.R.layout;
import csc.project.R.string;
import csc.project.UserControl;
import csc.travel.SingleTravel;
import csc.travel.TravelType;
import csc.util.Constants;
import csc.util.TimeFormat;

import java.text.ParseException;
import java.util.Date;

/**
 * A class that is used when trying to edit a travel.
 */
public class EditTravelActivity extends BaseActivity {
  /**
   * The field of this activity containing the Id.
   */
  private EditText idId = null;
  /**
   * The field of this activity containing the Provider.
   */
  private EditText idProvider = null;
  /**
   * The field of this activity containing the Start Time.
   */
  private EditText idStart = null;
  /**
   * The field of this activity containing the End Time.
   */
  private EditText idEnd = null;
  /**
   * The field of this activity containing the Origin.
   */
  private EditText idOrigin = null;
  /**
   * The field of this activity containing the Destination.
   */
  private EditText idDestination = null;
  /**
   * The field of this activity containing the Capacity.
   */
  private EditText idSeats = null;
  /**
   * The field of this activity containing the Cost.
   */
  private EditText idCost = null;
  /**
   * The field of this activity containing the Travel Types.
   */
  private Spinner travelSpinner = null;
  /**
   * The button for cancelling.
   */
  private Button cancel = null;
  /**
   * The button for submitting.
   */
  private Button editButton = null;
  /**
   * The travel to associate with this activity.
   */
  private SingleTravel st = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(layout.activity_edit_travel);

    idSeats = (EditText) findViewById(id.r_editText2);
    idStart = (EditText) findViewById(id.r_editText3);
    idEnd = (EditText) findViewById(id.r_editText4);
    idProvider = (EditText) findViewById(id.r_editText);
    idId = (EditText) findViewById(id.r_editText5);
    idDestination = (EditText) findViewById(id.r_editText7);
    idOrigin = (EditText) findViewById(id.r_editText6);
    idCost = (EditText) findViewById(id.r_editText8);
    cancel = (Button) findViewById(id.button5);
    editButton = (Button) findViewById(id.r_button);

    travelSpinner = (Spinner) findViewById(R.id.spinner3);
    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this,
        android.R.layout.simple_spinner_item, TravelType.getUsableNames());
    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    travelSpinner.setAdapter(spinnerArrayAdapter);

    // get the Intent that launched me
    Intent intent = getIntent();
    if (intent.getSerializableExtra(Constants.TYPE_KEY) != null) {
      UserControl uc = (UserControl) getApplicationContext();
      st = uc.getDatabase().getTravel(
          (TravelType) intent.getSerializableExtra(Constants.TYPE_KEY),
          intent.getStringExtra(Constants.TRAVEL_KEY));
      // if st is null, we are adding a new one
    } else {
      cancel.setVisibility(View.GONE);
    }

    cancel(getCurrentFocus());
  }

  /**
   * Replaces the fields with the travel's.
   * 
   * @param view
   *          the current view
   */
  public void cancel(View view) {
    if (st == null) {
      return;
    }
    idProvider.setText(st.getProvider());
    idId.setText(st.getIdentifier());
    idOrigin.setText(st.getOrigin());
    idDestination.setText(st.getDestination());
    idStart.setText(st.formatStartTime());
    idEnd.setText(st.formatEndTime());
    idSeats.setText(String.valueOf(st.getAvailableCapacity()));
    idCost.setText(String.valueOf(st.getCost()));
    travelSpinner.setSelection(st.getType().ordinal());
  }

  /**
   * The action to take when submit is clicked.
   * 
   * @param view
   *          The current view.
   */
  public void submit(View view) {
    TravelType tt = TravelType.getByIndex(travelSpinner.getSelectedItemPosition());
    if (tt == null || !tt.isUsable()) {
      return;
    }
    // Reset errors.
    idProvider.setError(null);
    idId.setError(null);
    idOrigin.setError(null);
    idDestination.setError(null);
    idEnd.setError(null);
    idStart.setError(null);
    idSeats.setError(null);

    // Store values at the time of the login attempt.
    final String provider = idProvider.getText().toString();
    final String id = idId.getText().toString();
    final String origin = idOrigin.getText().toString();
    final String dest = idDestination.getText().toString();
    final String startSt = idStart.getText().toString();
    final String endSt = idEnd.getText().toString();
    final String seatSt = idSeats.getText().toString();
    final String costSt = idCost.getText().toString();

    int seats = 0;
    double cost = 0;
    Date start = null;
    Date end = null;

    View focusView = null;

    try {
      start = TimeFormat.DATE_TIME.parseString(startSt);
    } catch (ParseException ignore) {
      idStart.setError(getString(string.error_invalid));
      focusView = idStart;
    }
    try {
      end = TimeFormat.DATE_TIME.parseString(endSt);
      if (start != null && end.before(start)) {
        idEnd.setError(getString(string.error_invalid));
        focusView = idEnd;
      }
    } catch (ParseException ignore) {
      idEnd.setError(getString(string.error_invalid));
      focusView = idEnd;
    }
    try {
      seats = Integer.parseInt(seatSt);
      if (seats <= 0) {
        idSeats.setError(getString(string.error_invalid));
        focusView = idSeats;
      }
    } catch (NumberFormatException ignore) {
      idSeats.setError(getString(string.error_invalid));
      focusView = idSeats;
    }
    try {
      cost = Double.parseDouble(costSt);
      if (cost <= 0) {
        idCost.setError(getString(string.error_invalid));
        focusView = idCost;
      }
    } catch (NumberFormatException ignore) {
      idCost.setError(getString(string.error_invalid));
      focusView = idCost;
    }

    if (TextUtils.isEmpty(provider)) {
      idProvider.setError(getString(string.error_field_required));
      focusView = idProvider;
    }
    if (TextUtils.isEmpty(origin)) {
      idOrigin.setError(getString(string.error_field_required));
      focusView = idOrigin;
    }
    if (TextUtils.isEmpty(dest) || dest.equals(origin)) {
      idDestination.setError(getString(string.error_field_required));
      focusView = idDestination;
    }
    UserControl uc = (UserControl) getApplicationContext();
    // we are trying to change the travel type or id, check if it exists
    if (uc.getDatabase().getTravel(tt, id) != null
        && (st == null || tt != st.getType() || !id.equals(st.getIdentifier()))) {
      idId.setError(getString(string.error_id_exists));
      focusView = idId;
    }

    if (focusView != null) {
      // There was an error
      focusView.requestFocus();
    } else {
      if (st == null) {
        try {
          // Number,DepartureDateTime,ArrivalDateTime,Airline,Origin,Destination,Price,NumSeats
          st = tt.create(id, startSt, endSt, provider, origin, dest, costSt, seatSt);
        } catch (ParseException ignore) { // should not be possible
          idStart.setError(getString(string.error_invalid));
          idStart.requestFocus();
          return;
        }
        cancel.setVisibility(View.VISIBLE);
      } else {
        uc.getDatabase().removeTravel(st.getType(), st.getIdentifier()); // remove

        st.setOrigin(origin);
        st.setDestination(dest);
        st.setStartTime(start);
        st.setEndTime(end);
        st.setCapacity(seats);
        st.setType(tt);
        st.setProvider(provider);
        st.setIdentifier(id);
        st.setCost(cost);
      }
      if (st.isInvalid()) { // should not be possible
        idId.setError(getString(string.error_invalid));
        idId.requestFocus();
        return;
      }

      uc.getDatabase().addTravel(st); // re-add to clear itineraries and users
      cancel(view); // refresh date
      uc.save();

      editButton.setText(getString(R.string.saved));
    }
  }

}
