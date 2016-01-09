package csc.project.interact;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import csc.project.BaseActivity;
import csc.project.R;
import csc.project.main.UserInfoFragment;
import csc.project.view.ItineraryInteractionListener;
import csc.travel.Itinerary;
import csc.util.Constants;

/**
 * Activity for editing users from administrator.
 */
public class EditUserActivity extends BaseActivity implements ItineraryInteractionListener {

  /**
   * The user associated with this activity.
   */
  private String user = null;
  /**
   * The user info editor.
   */
  private UserInfoFragment infoFragment = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_user);

    Intent intent = getIntent();
    if (intent.getStringExtra(Constants.USER_KEY) != null) {
      user = intent.getStringExtra(Constants.USER_KEY);
    } else {
      // if user is null, we are adding a new one
      // hide the book button
      findViewById(R.id.button6).setVisibility(View.GONE);
    }

    // add travel list
    infoFragment = UserInfoFragment.newInstance(user);
    getSupportFragmentManager().beginTransaction().replace(R.id.layout, infoFragment).commit();
  }

  @Override
  protected void onPostResume() {
    super.onPostResume();

    // re make
    if (infoFragment != null) {
      infoFragment.refresh();
    }
  }

  @Override
  protected void onResumeFragments() {
    super.onResumeFragments();

    // re make
    if (infoFragment != null) {
      infoFragment.refresh();
    }
  }

  /**
   * Action for when the submit button is pressed.
   *
   * @param view
   *          The current view.
   */
  public void submitEdit(View view) {
    if (infoFragment != null) {
      infoFragment.submit();
    }
  }

  /**
   * Action for when the cancel button is pressed.
   *
   * @param view
   *          The current view.
   */
  public void cancelEdit(View view) {
    if (infoFragment != null) {
      infoFragment.cancel();
    }
  }

  /**
   * Action for when the submit button is pressed.
   *
   * @param view
   *          The current view.
   */
  public void submitPass(View view) {
    if (infoFragment != null) {
      infoFragment.submitPass();
    }
  }

  /**
   * Action to take when book for user button is pressed.
   * 
   * @param view
   *          the current view
   */
  public void bookForUser(View view) {
    if (user == null) {
      return;
    }
    // go to search itineraries with this user
    Intent intent = new Intent(this, SearchItineraryActivity.class);
    intent.putExtra(Constants.USER_KEY, user);
    startActivity(intent);
  }

  @Override
  public void onInteraction(Itinerary itin) {
    Intent intent = new Intent(this, ViewItineraryActivity.class);
    // pass the user and the itinerary to the view class
    intent.putExtra(Constants.USER_KEY, user);
    intent.putExtra(Constants.ITIN_KEY, itin);
    startActivity(intent);
  }
}
