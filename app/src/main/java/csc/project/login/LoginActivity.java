package csc.project.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import csc.project.BaseActivity;
import csc.project.R.id;
import csc.project.R.layout;
import csc.project.R.string;
import csc.project.UserControl;
import csc.project.main.MainActivity;
import csc.users.GuestUser;
import csc.users.RegisteredUser;
import csc.util.Constants;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {

  /**
   * The field for the email.
   */
  private EditText idEmailView = null;
  /**
   * The field for the password.
   */
  private EditText idPasswordView = null;
  /**
   * The checkbox for remembering usernames.
   */
  private CheckBox idRemember = null;
  /**
   * The checkbox for remembering auto login.
   */
  private CheckBox idAutoLog = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(layout.activity_login);

    UserControl uc = ((UserControl) getApplicationContext());
    uc.setUser(null);

    // Set up the login form.
    idEmailView = (EditText) findViewById(id.email);

    idPasswordView = (EditText) findViewById(id.password);

    // get the String that was put into the Intent with key dataKey
    idEmailView.setText(getIntent().getStringExtra(getString(string.r_emailKey)));

    idRemember = (CheckBox) findViewById(id.remember_username);
    idAutoLog = (CheckBox) findViewById(id.auto_login);

    // handle preferences
    SharedPreferences pref = uc.getPreferences();
    idRemember.setChecked(pref.getBoolean(getString(string.pref_remember), false));
    idAutoLog.setChecked(pref.getBoolean(getString(string.pref_autolog), false));

    if (idRemember.isChecked()) {
      idEmailView.setText(
          pref.getString(getString(string.pref_r_email), idEmailView.getText().toString()));
    }
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);

    if (idAutoLog.isChecked()) {
      attemptAutoLogin();
    }
  }

  /**
   * Exits the app.
   *
   * @param view
   *          the current view.
   */
  public void exit(View view) {
    killProcess();
  }

  /**
   * Attempts to sign in or register the account specified by the login form. If
   * there are form errors (invalid email, missing fields, etc.), the errors are
   * presented and no actual login attempt is made.
   */
  public void attemptLogin(View view) {
    // Reset errors.
    idEmailView.setError(null);
    idPasswordView.setError(null);

    // Store values at the time of the login attempt.
    String email = idEmailView.getText().toString();
    String password = idPasswordView.getText().toString();

    View focusView = null;

    // Check for a valid password, if the user entered one.
    if (Constants.isPasswordInvalid(password)) {
      idPasswordView.setError(getString(string.error_invalid_password));
      focusView = idPasswordView;
    }

    // Check for a valid email address.
    if (Constants.isEmailInvalid(email)) {
      idEmailView.setError(getString(string.error_invalid_email));
      focusView = idEmailView;
    }

    if (focusView != null) {
      // There was an error; don't attempt login and focus the first
      // form field with an error.
      focusView.requestFocus();
    } else {
      UserControl uc = (UserControl) getApplicationContext();

      if (uc.getDatabase().getUser(email) != null) {
        boolean noPassword = uc.getDatabase().getUser(email).getPassword().isEmpty();

        RegisteredUser user = uc.validateCredentials(email, password);
        if (user != null) { // login successful
          login(user, noPassword);
        } else {
          idPasswordView.setError(getString(string.error_incorrect_password));
          idPasswordView.requestFocus();
        }
      } else {
        idEmailView.setError(getString(string.error_incorrect_email));
        idEmailView.requestFocus();
      }
    }
  }

  /**
   * Attempts to sign in or register the account specified by the preferences.
   * If there are form errors (invalid email, missing fields, etc.), the errors
   * are silenced and no actual login attempt is made.
   */
  public void attemptAutoLogin() {
    UserControl uc = (UserControl) getApplicationContext();

    // get hashed password and email
    SharedPreferences pref = uc.getPreferences();
    String email = pref.getString(getString(string.pref_r_email),
        idEmailView.getText().toString());
    String password = pref.getString(getString(string.pref_r_pass),
        idPasswordView.getText().toString());

    RegisteredUser user = uc.validateSecureCredentials(email, password);
    if (user != null) { // login successful
      login(user, false);
    } else {
      // reset prefs if unsuccessful
      Editor edit = pref.edit();
      edit.putBoolean(getString(string.pref_autolog), false);
      idAutoLog.setChecked(false);
      edit.apply();
    }
  }

  /**
   * Logs in with the user provided.
   * 
   * @param user
   *          the user to log in with
   * @param noPassword
   *          if the user has had its password newly set
   */
  private void login(RegisteredUser user, boolean noPassword) {
    UserControl uc = (UserControl) getApplicationContext();

    // save user preferences
    Editor edit = uc.getPreferences().edit();
    edit.putBoolean(getString(string.pref_autolog), idAutoLog.isChecked());
    edit.putBoolean(getString(string.pref_remember), idRemember.isChecked());
    if (idRemember.isChecked() || idAutoLog.isChecked()) {
      edit.putString(getString(string.pref_r_email), user.getIdentifier());
    }
    if (idAutoLog.isChecked()) {
      edit.putString(getString(string.pref_r_pass), user.getPassword()); // secure
    }
    edit.apply();

    finish();

    uc.setUser(user);

    // tell the activity that the password was newly set (or not)
    Intent intent = new Intent(this, MainActivity.class);
    intent.putExtra(getString(string.r_passKey), noPassword);
    startActivity(intent);
  }

  /**
   * The transition method to the register activity.
   * 
   * @param view
   *          The current view.
   */
  public void attemptRegister(View view) {
    Intent inte = new Intent(this, RegisterActivity.class);
    // pass the user and password to the register class
    inte.putExtra(getString(string.r_emailKey), idEmailView.getText().toString());
    inte.putExtra(getString(string.r_passKey), idPasswordView.getText().toString());
    startActivity(inte);
  }

  /**
   * The transition method to the guest login activity.
   * 
   * @param view
   *          The current view.
   */
  public void attemptGuest(View view) {
    finish();

    ((UserControl) getApplicationContext()).setUser(new GuestUser());
    startActivity(new Intent(this, MainActivity.class));
  }

  /**
   * The transition method to the forgot password activity.
   * 
   * @param view
   *          The current view.
   */
  public void goToForgotPasswordActivity(View view) {
    Intent inte = new Intent(this, ForgotActivity.class);
    inte.putExtra(getString(string.r_emailKey), idEmailView.getText().toString());
    startActivity(inte);
  }
}
