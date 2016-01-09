package csc.project.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import csc.io.InputOperations;
import csc.project.R;
import csc.project.R.id;
import csc.project.R.string;
import csc.project.UserControl;
import csc.project.view.ItineraryFragment;
import csc.users.RegisteredUser;
import csc.users.User;
import csc.users.UserType;
import csc.util.Constants;
import csc.util.Privileges;
import csc.util.TimeFormat;

import java.text.ParseException;
import java.util.Date;

/**
 * A fragment that is used to edit the info of a certain user.
 */
public class UserInfoFragment extends Fragment {
  /**
   * The email field.
   */
  private EditText idEmail = null;
  /**
   * The password field.
   */
  private EditText idPassword = null;
  /**
   * The confirm password field.
   */
  private EditText idConfirm = null;
  /**
   * The first names field.
   */
  private EditText idFirst = null;
  /**
   * The last name field.
   */
  private EditText idLast = null;
  /**
   * The credit card number field.
   */
  private EditText idCredit = null;
  /**
   * The expiry date field.
   */
  private EditText idExpiry = null;
  /**
   * The address field.
   */
  private EditText idAddress = null;

  /**
   * Spinner for user type; invisible to normal users.
   */
  private Spinner userSpinner = null;
  /**
   * The button for submitting password change.
   */
  private Button pwButton = null;
  /**
   * The button for submitting all other changes.
   */
  private Button editButton = null;

  /**
   * The button for cancelling.
   */
  private Button cancel = null;

  /**
   * The text for number of itineraries.
   */
  private TextView numItin = null;

  /**
   * User being edited.
   */
  private RegisteredUser user = null;

  /**
   * The method used when trying to edit the userinfo.
   * 
   * @param user
   *          The user
   */
  public static UserInfoFragment newInstance(String user) {
    UserInfoFragment edit = new UserInfoFragment();
    if (user != null) {
      Bundle args = new Bundle();
      args.putString(Constants.USER_KEY, user);
      edit.setArguments(args);
    }
    return edit;
  }

  @Override
  public void onCreate(Bundle saved) {
    super.onCreate(saved);
    // check if we actually have a user being edited; otherwise we are adding
    if (this.getArguments() != null) {
      String use = this.getArguments().getString(Constants.USER_KEY);
      if (use != null) {
        user = ((UserControl) getActivity().getApplicationContext()).getDatabase()
            .getUser(use);
      }
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_edit_user, container, false);

    idEmail = (EditText) rootView.findViewById(id.r_editText2);
    userSpinner = (Spinner) rootView.findViewById(id.spinner2);

    UserControl uc = ((UserControl) getActivity().getApplicationContext());
    User currUser = uc.getUser();
    // current user is the editor, while the user in the arguments is the one
    // being edited
    // spinner is invisible to non-admins
    if (currUser == null || !currUser.hasPrivilege(Privileges.EDIT_OTHER)
        || currUser.equals(user)) {
      idEmail.setClickable(false);
      idEmail.setEnabled(false);
      idEmail.setCursorVisible(false);
      idEmail.setFocusable(false);
      userSpinner.setEnabled(false);
      userSpinner.setClickable(false);
      userSpinner.setFocusable(false);
    }

    // populate the spinner with names
    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(),
        android.R.layout.simple_spinner_item, UserType.getUsableNames());
    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    userSpinner.setAdapter(spinnerArrayAdapter);

    idPassword = (EditText) rootView.findViewById(id.editText);

    idConfirm = (EditText) rootView.findViewById(id.editText2);

    idFirst = (EditText) rootView.findViewById(id.r_editText);

    idLast = (EditText) rootView.findViewById(id.r_editText5);

    idCredit = (EditText) rootView.findViewById(id.r_editText7);

    idExpiry = (EditText) rootView.findViewById(id.r_editText8);

    idAddress = (EditText) rootView.findViewById(id.r_editText6);

    pwButton = (Button) rootView.findViewById(id.button3);
    editButton = (Button) rootView.findViewById(id.r_button);
    cancel = (Button) rootView.findViewById(id.button2);
    numItin = (TextView) rootView.findViewById(id.r_checkedTextView9);

    if (user == null) {
      // to top
      TextView nameText = (TextView) rootView.findViewById(id.r_checkedTextView);
      RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) nameText
          .getLayoutParams();
      params.setMargins(0, 0, 0, 0);
      nameText.setLayoutParams(params);

      numItin.setVisibility(View.GONE);
      // remove cancel button and password
      cancel.setVisibility(View.GONE);
      pwButton.setVisibility(View.GONE);
      idPassword.setVisibility(View.GONE);
      idConfirm.setVisibility(View.GONE);
    }

    refresh();
    return rootView;
  }

  /**
   * Refreshes the view to the user's current info.
   */
  public void refresh() {
    if (numItin == null) { // haven't initialized yet!
      return;
    }
    cancel();

    // add travel list
    if (user != null) {
      numItin.setText(getString(R.string.booked_itin, user.getBookedItineraries().size()));
      ItineraryFragment tf = ItineraryFragment.newInstance(user.getBookedItineraries());
      getChildFragmentManager().beginTransaction().replace(R.id.layout, tf).commit();
    }
  }

  /**
   * Returns the text fields to their original state.
   */
  public void cancel() {
    if (user == null) {
      return;
    }
    idEmail.setText(user.getIdentifier());
    idFirst.setText(user.getFirstNames());
    idLast.setText(user.getLastName());
    idCredit.setText(user.getCreditCard());
    idExpiry.setText(user.getExpiryDate());
    idAddress.setText(user.getAddress());
    userSpinner.setSelection(user.getType().ordinal());
  }

  /**
   * Action for when the submit button is pressed.
   */
  public void submit() {
    UserType ut = UserType.getByIndex(userSpinner.getSelectedItemPosition());
    if (ut == null) {
      return;
    }
    // Reset errors.
    idEmail.setError(null);
    idFirst.setError(null);
    idLast.setError(null);
    idExpiry.setError(null);
    idAddress.setError(null);
    idCredit.setError(null);
    editButton.setText(getString(R.string.register_submit));

    // Store values at the time of the login attempt.
    final String inputEmail = idEmail.getText().toString();
    final String first = idFirst.getText().toString();
    final String last = idLast.getText().toString();
    final String expiry = idExpiry.getText().toString();
    final String address = idAddress.getText().toString();
    final String credit = idCredit.getText().toString();

    View focusView = null;

    UserControl uc = (UserControl) getActivity().getApplicationContext();
    User currUser = uc.getUser();
    if (currUser == null) { // impossible by normal means
      idEmail.setError(getString(string.error_invalid_email));
      focusView = idEmail;
    }
    if (TextUtils.isEmpty(expiry)) {
      idExpiry.setError(getString(string.error_field_required));
      focusView = idExpiry;
    }
    if (TextUtils.isEmpty(credit)) {
      idCredit.setError(getString(string.error_field_required));
      focusView = idCredit;
    }
    if (TextUtils.isEmpty(address)) {
      idAddress.setError(getString(string.error_field_required));
      focusView = idAddress;
    }
    if (TextUtils.isEmpty(last) || last.length() >= 30) {
      idLast.setError(getString(string.error_field_required));
      focusView = idLast;
    }
    if (TextUtils.isEmpty(first) || first.length() >= 50) {
      idFirst.setError(getString(string.error_field_required));
      focusView = idFirst;
    }
    // Check for a valid email address.
    if (TextUtils.isEmpty(inputEmail) || Constants.isEmailInvalid(inputEmail)) {
      idEmail.setError(getString(string.error_invalid_email));
      focusView = idEmail;
    }

    Date expiryDate = null;
    try {
      expiryDate = TimeFormat.DATE.parseString(expiry);
    } catch (ParseException ignore) {
      idExpiry.setError(getString(string.error_invalid));
      focusView = idExpiry;
    }
    if (focusView != null) {
      // There was an error; don't attempt login and focus the first
      // form field with an error.
      focusView.requestFocus();
    } else if (currUser != null) { // always true
      if (user == null) {
        if (!currUser.hasPrivilege(Privileges.EDIT_OTHER) || !ut.isUsable()
            || ut.getPrivilege() > currUser.getType().getPrivilege()
            || uc.getDatabase().getUser(inputEmail) != null) {
          idEmail.setError(getString(string.error_invalid));
          idEmail.requestFocus();
          return;
        }
        // LastName,FirstNames,Email,Address,CreditCardNumber,ExpiryDate
        try {
          user = ut.create(last, first, inputEmail, address, credit, expiry);
        } catch (ParseException ignore) { // impossible
          idExpiry.setError(getString(string.error_invalid));
          idExpiry.requestFocus();
          return;
        }
        // add cancel button and password
        cancel.setVisibility(View.VISIBLE);
        pwButton.setVisibility(View.VISIBLE);
        idPassword.setVisibility(View.VISIBLE);
        idConfirm.setVisibility(View.VISIBLE);
      } else {
        // if different email is provided, check if it is not taken
        if (!inputEmail.equals(user.getIdentifier())) {
          if (!currUser.hasPrivilege(Privileges.EDIT_OTHER)
              || uc.getDatabase().getUser(inputEmail) != null) { // admin
            idEmail.setError(getString(string.error_invalid_email));
            idEmail.requestFocus();
            return;
          }
          uc.getDatabase().changeEmail(user, inputEmail);
        }
        // if different user type is specified, check if it is possible
        if (ut != user.getType()) {
          if (!currUser.hasPrivilege(Privileges.EDIT_OTHER) || !ut.isUsable()
              || ut.getPrivilege() > currUser.getType().getPrivilege()) { // admin
            idEmail.setError(getString(string.error_invalid));
            idEmail.requestFocus();
            return;
          }
          user.setType(ut);
        }
        user.setAddress(address);
        user.setCreditCard(credit);
        user.setExpiryDate(expiryDate);
        user.setFirstNames(first);
        user.setLastName(last);
      }
      uc.getDatabase().addUser(user); // refresh serialized
      cancel(); // refresh date to actual

      uc.save();

      editButton.setText(getString(R.string.saved));
    }
  }

  /**
   * Action for when the submit button is pressed.
   */
  public void submitPass() {
    if (user == null) {
      return;
    }
    // Reset errors.
    idPassword.setError(null);
    idConfirm.setError(null);
    pwButton.setText(getString(R.string.register_submit));

    // Store values at the time of the login attempt.
    final String password = idPassword.getText().toString();
    final String confirm = idConfirm.getText().toString();

    View focusView = null;

    UserControl uc = (UserControl) getActivity().getApplicationContext();
    if (!password.equals(confirm)) {
      idConfirm.setError(getString(R.string.error_invalid_password));
      focusView = idConfirm;
    }
    if (Constants.isPasswordInvalid(password)) {
      idPassword.setError(getString(R.string.error_invalid_password));
      focusView = idPassword;
    }
    if (InputOperations.hashPassword(password).equals(user.getPassword())) {
      idPassword.setError(getString(R.string.error_invalid));
      focusView = idPassword;
    }
    if (focusView != null) {
      // There was an error; don't attempt login and focus the first
      // form field with an error.
      focusView.requestFocus();
    } else if (user != null) { // always true
      user.setPassword(InputOperations.hashPassword(password));
      uc.getDatabase().addUser(user); // refresh serialized
      uc.save();
      pwButton.setText(getString(R.string.saved));
    }
  }
}
