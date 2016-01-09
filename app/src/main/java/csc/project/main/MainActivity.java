package csc.project.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import csc.project.BaseActivity;
import csc.project.R.id;
import csc.project.R.layout;
import csc.project.R.string;
import csc.project.UserControl;
import csc.project.interact.EditUserActivity;
import csc.project.interact.ViewItineraryActivity;
import csc.project.view.ItineraryInteractionListener;
import csc.project.view.UserInteractionListener;
import csc.travel.Itinerary;
import csc.users.RegisteredUser;
import csc.util.Constants;
import csc.util.Privileges;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends BaseActivity
    implements ItineraryInteractionListener, UserInteractionListener {

  /**
   * The {@link android.support.v4.view.PagerAdapter} that will provide
   * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
   * derivative, which will keep every loaded fragment in memory. If this
   * becomes too memory intensive, it may be best to switch to a
   * {@link android.support.v4.app.FragmentStatePagerAdapter}.
   */
  private SectionsPagerAdapter idSectionsPagerAdapter = null;

  /**
   * The {@link ViewPager} that will host the section contents.
   */
  private ViewPager idViewPager = null;

  /**
   * The first page fragment, containing the search bar.
   */
  private MainFragment mainFragment = null;
  /**
   * The second page fragment, containing the user info.
   */
  private UserInfoFragment infoFragment = null;
  /**
   * The third page fragment, containing the upload prompts.
   */
  private UploadFragment uploadFragment = null;
  /**
   * The last page fragment, containing the searching of users.
   */
  private SearchUserFragment searchFragment = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(layout.activity_tabs);
    // Create the adapter that will return a fragment for each of the three
    // primary sections of the activity.
    idSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

    // Set up the ViewPager with the sections adapter.
    idViewPager = (ViewPager) findViewById(id.pager);
    idViewPager.setAdapter(idSectionsPagerAdapter);
    createView();
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
   * Initializes the fragments for this view.
   */
  private void createView() {
    UserControl uc = (UserControl) getApplicationContext();

    final List<PagePrivileges> privileges = new ArrayList<>();

    if (uc.getUser() != null) {
      // check if the password was newly set
      boolean noPassword = getIntent().getBooleanExtra(getString(string.r_passKey), false);

      mainFragment = MainFragment.newInstance(noPassword);
      // find the TextView object for TextView with id display_id

      // welcome message, button for searching itineraries, button for searching
      // travels, fields
      privileges.add(new PagePrivileges(getString(string.title_section1), mainFragment));

      if (uc.getUser().hasPrivilege(Privileges.EDIT_SELF)) {
        // user fields, booked itineraries view; should have a button -> search
        // itineraries
        infoFragment = UserInfoFragment.newInstance(uc.getUser().getIdentifier());

        privileges.add(new PagePrivileges(getString(string.title_section2), infoFragment));
      }

      if (uc.getUser().hasPrivilege(Privileges.UPLOAD_TRAVEL)
          || uc.getUser().hasPrivilege(Privileges.UPLOAD_USER)) {
        // upload file, output view
        uploadFragment = UploadFragment.newInstance();

        privileges.add(new PagePrivileges(getString(string.title_section3), uploadFragment));
      }

      if (uc.getUser().hasPrivilege(Privileges.VIEW_OTHER)) {
        // search users -> click
        searchFragment = SearchUserFragment.newInstance();

        privileges.add(new PagePrivileges(getString(string.title_section4), searchFragment));
      }
    }
    idSectionsPagerAdapter.refresh(privileges);
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    if (((UserControl) getApplicationContext()).getUser() == null) {
      logout(getCurrentFocus());
    }
  }

  /**
   * Goes to search the travels.
   * 
   * @param view
   *          the current view
   */
  public void searchTravels(View view) {
    if (mainFragment != null) {
      mainFragment.searchTravels();
    }
  }

  /**
   * Goes to search the itineraries.
   * 
   * @param view
   *          the current view
   */
  public void searchItineraries(View view) {
    if (mainFragment != null) {
      mainFragment.searchItineraries();
    }
  }

  /**
   * The transition method to the login activity.
   *
   * @param view
   *          The current view.
   */
  public void logout(View view) {
    if (mainFragment != null) {
      mainFragment.logout();
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
   * Action for when the upload user file is pressed.
   *
   * @param view
   *          The current view.
   */
  public void uploadUser(View view) {
    if (uploadFragment != null) {
      uploadFragment.uploadUser();
    }
  }

  /**
   * Action for when the upload travel file is pressed.
   *
   * @param view
   *          The current view.
   */
  public void uploadTravel(View view) {
    if (uploadFragment != null) {
      uploadFragment.uploadTravel();
    }
  }

  /**
   * Goes to search the users by name..
   *
   * @param view
   *          the current view
   */
  public void searchName(View view) {
    if (searchFragment != null) {
      searchFragment.searchName();
    }
  }

  /**
   * Goes to search the users by email.
   *
   * @param view
   *          the current view
   */
  public void searchEmail(View view) {
    if (searchFragment != null) {
      searchFragment.searchEmail();
    }
  }

  /**
   * Goes to search the users by email and name.
   *
   * @param view
   *          the current view
   */
  public void searchBoth(View view) {
    if (searchFragment != null) {
      searchFragment.searchBoth();
    }
  }

  /**
   * Action to take when add user button is clicked.
   *
   * @param view
   *          the current view
   */
  public void addUser(View view) {
    UserControl uc = (UserControl) getApplicationContext();
    if (uc.getUser() == null || !uc.getUser().hasPrivilege(Privileges.EDIT_OTHER)) {
      return;
    }
    startActivity(new Intent(this, EditUserActivity.class));
  }

  @Override
  public void onInteraction(Itinerary itin) {
    UserControl uc = (UserControl) getApplicationContext();

    Intent intent = new Intent(this, ViewItineraryActivity.class);
    // pass the user and itinerary to the view class
    if (uc.getUser().getType().isUsable()) {
      intent.putExtra(Constants.USER_KEY, uc.getUser().getIdentifier());
    }
    intent.putExtra(Constants.ITIN_KEY, itin);
    startActivity(intent);
  }

  @Override
  public void onInteraction(RegisteredUser user) {
    UserControl uc = (UserControl) getApplicationContext();
    if (uc.getUser() == null || !uc.getUser().hasPrivilege(Privileges.EDIT_OTHER)) {
      return;
    }
    Intent intent = new Intent(this, EditUserActivity.class);
    // pass the user to the view class
    intent.putExtra(Constants.USER_KEY, user.getIdentifier());
    startActivity(intent);
  }

  /**
   * Class for storing fragments and their names.
   */
  private static final class PagePrivileges {
    private final String name;
    private final Fragment fm;

    private PagePrivileges(String name, Fragment fm) {
      this.name = name;
      this.fm = fm;
    }

    public String getName() {
      return name;
    }

    public Fragment getFragment() {
      return fm;
    }
  }

  /**
   * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one
   * of the sections/tabs/pages.
   */
  private static final class SectionsPagerAdapter extends FragmentPagerAdapter {
    private List<PagePrivileges> privileges;

    private SectionsPagerAdapter(FragmentManager fm) {
      super(fm);
      this.privileges = new ArrayList<>();
    }

    public void refresh(List<PagePrivileges> privilege) {
      this.privileges = privilege;
      notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
      if (position < 0 || position >= privileges.size()) {
        return null;
      }
      return privileges.get(position).getFragment();
    }

    @Override
    public int getCount() {
      return privileges.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
      if (position < 0 || position >= privileges.size()) {
        return null;
      }
      return privileges.get(position).getName().toUpperCase(Locale.getDefault());
    }
  }

}
