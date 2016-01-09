package csc.project.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import csc.io.InputOperations;
import csc.project.BaseActivity;
import csc.project.R.id;
import csc.project.R.layout;
import csc.project.R.string;
import csc.project.UserControl;
import csc.project.main.MainActivity;
import csc.users.RegisteredUser;
import csc.users.UserType;
import csc.util.Constants;

import java.text.ParseException;

/**
 * A class that is used when trying to create an user.
 */
public class RegisterActivity extends BaseActivity {
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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(layout.activity_register);

    idEmail = (EditText) findViewById(id.r_editText2);

    idPassword = (EditText) findViewById(id.r_editText3);

    idConfirm = (EditText) findViewById(id.r_editText4);

    idFirst = (EditText) findViewById(id.r_editText);

    idLast = (EditText) findViewById(id.r_editText5);

    idCredit = (EditText) findViewById(id.r_editText7);

    idExpiry = (EditText) findViewById(id.r_editText8);

    idAddress = (EditText) findViewById(id.r_editText6);

    // get the Intent that launched me
    Intent intent = getIntent();
    // get the String that was put into the Intent with key dataKey
    idEmail.setText(intent.getStringExtra(getString(string.r_emailKey)));
    idPassword.setText(intent.getStringExtra(getString(string.r_passKey)));
  }

  /**
   * The action to take when submit is clicked.
   * 
   * @param view
   *          The current view.
   */
  public void submit(View view) {
    // Reset errors.
    idEmail.setError(null);
    idPassword.setError(null);
    idConfirm.setError(null);
    idFirst.setError(null);
    idLast.setError(null);
    idExpiry.setError(null);
    idAddress.setError(null);
    idCredit.setError(null);

    // Store values at the time of the login attempt.
    final String email = idEmail.getText().toString();
    final String password = idPassword.getText().toString();
    final String confirm = idConfirm.getText().toString();
    final String first = idFirst.getText().toString();
    final String last = idLast.getText().toString();
    final String expiry = idExpiry.getText().toString();
    final String address = idAddress.getText().toString();
    final String credit = idCredit.getText().toString();

    View focusView = null;

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
    if (!password.equals(confirm)) {
      idConfirm.setError(getString(string.error_invalid_password));
      focusView = idConfirm;
    }
    if (Constants.isPasswordInvalid(password)) {
      idPassword.setError(getString(string.error_invalid_password));
      focusView = idPassword;
    }
    // Check for a valid email address.
    if (TextUtils.isEmpty(email) || Constants.isEmailInvalid(email)) {
      idEmail.setError(getString(string.error_invalid_email));
      focusView = idEmail;
    }
    UserControl uc = (UserControl) getApplicationContext();
    if (uc.getDatabase().getUser(email) != null) {
      idEmail.setError(getString(string.error_email_exists));
      focusView = idEmail;
    }

    if (focusView != null) {
      // There was an error; don't attempt login and focus the first
      // form field with an error.
      focusView.requestFocus();
    } else {
      UserType ut = UserType.Client;
      if (uc.getNumUsers() == 0) { // First user is admin
        ut = UserType.Administrator;
      }

      RegisteredUser ru;
      try {
        ru = ut.create(last, first, email, address, credit, expiry);
      } catch (ParseException ignore) {
        idExpiry.setError(getString(string.error_invalid));
        idExpiry.requestFocus();
        return;
      }
      ru.setPassword(InputOperations.hashPassword(password));
      uc.getDatabase().addUser(ru);
      uc.save();
      // Registered!

      finish();

      uc.setUser(ru);
      startActivity(new Intent(this, MainActivity.class));
    }
  }

}
