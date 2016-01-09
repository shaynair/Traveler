package csc.project.interact;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import csc.project.BaseActivity;
import csc.project.R;
import csc.project.UserControl;
import csc.project.view.ItineraryInteractionListener;
import csc.project.view.ItineraryViewHolder;
import csc.project.view.TravelFragment;
import csc.project.view.TravelInteractionListener;
import csc.travel.Itinerary;
import csc.travel.SingleTravel;
import csc.users.RegisteredUser;
import csc.util.Constants;
import csc.util.Privileges;

import java.util.ArrayList;

/**
 * Activity for viewing a single itinerary.
 */
public class ViewItineraryActivity extends BaseActivity
    implements TravelInteractionListener, ItineraryInteractionListener {

  /**
   * The itinerary associated with this activity.
   */
  private Itinerary itin = null;
  /**
   * The user associated with this activity.
   */
  private RegisteredUser user = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_itinerary);

    Button button = (Button) findViewById(R.id.button4); // unbook / book button

    UserControl uc = (UserControl) getApplicationContext();

    Intent intent = getIntent();
    itin = (Itinerary) intent.getSerializableExtra(Constants.ITIN_KEY);
    itin.refresh(uc.getDatabase()); // make sure values are synchronized

    if (!uc.getUser().hasPrivilege(Privileges.BOOK_TRAVEL)) { // guest
      button.setVisibility(View.GONE); // guests cannot book travels
    } else {
      user = uc.getDatabase().getUser(intent.getStringExtra(Constants.USER_KEY));
      if (user.hasBooked(itin)) {
        button.setText(getString(R.string.it_unbook));
      }
    }
    ItineraryViewHolder viewHolder = new ItineraryViewHolder(
        findViewById(android.R.id.content));
    viewHolder.setItem(itin);

    // add travel list
    TravelFragment tf = TravelFragment.newInstance(new ArrayList<>(itin.getTravels()));
    getSupportFragmentManager().beginTransaction().add(R.id.layout, tf).commit();
  }

  /**
   * Action to take when book button is done.
   * 
   * @param view
   *          Current view.
   */
  public void book(View view) {
    if (user == null || itin == null) {
      return;
    }
    if (user.hasBooked(itin)) {
      user.removeItinerary(itin);
    } else {
      user.bookItinerary(itin);
    }
    // commit changes
    ((UserControl) getApplicationContext()).save();

    finish();
  }

  @Override
  public void onInteraction(SingleTravel travel) {
    UserControl uc = (UserControl) getApplicationContext();
    if (!uc.getUser().hasPrivilege(Privileges.EDIT_TRAVEL)) {
      return;
    }
    // pass the single travel to the edit class
    Intent intent = new Intent(this, EditTravelActivity.class);
    intent.putExtra(Constants.TRAVEL_KEY, travel.getIdentifier());
    intent.putExtra(Constants.TYPE_KEY, travel.getType());
    startActivity(intent);
  }

  @Override
  public void onInteraction(Itinerary selected) {
    if (selected.equals(this.itin)) {
      book(getCurrentFocus());
    }
  }
}
