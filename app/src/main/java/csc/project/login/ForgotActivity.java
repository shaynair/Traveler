package csc.project.login;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import csc.io.InputOperations;
import csc.project.BaseActivity;
import csc.project.R.id;
import csc.project.R.layout;
import csc.project.R.string;
import csc.project.UserControl;
import csc.users.RegisteredUser;
import csc.util.Constants;

/**
 * A class that is used when for password generating when user needs it.
 */
public class ForgotActivity extends BaseActivity {

  /**
   * The field for entering the email.
   */
  private EditText idEmailView = null;
  /**
   * The text to show the password.
   */
  private TextView idPasswordMessage = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(layout.activity_forgot_password);
    idEmailView = (EditText) findViewById(id.username);
    idPasswordMessage = (TextView) findViewById(id.passwordMessage);

    // get the String that was put into the Intent with key dataKey
    idEmailView.setText(getIntent().getStringExtra(getString(string.r_emailKey)));
  }

  /**
   * A method used to send email to customer's email address.
   *
   * @param view
   *          The current view.
   */
  public void sendEmail(View view) {
    idEmailView.setError(null);
    String email = idEmailView.getText().toString();
    UserControl uc = (UserControl) getApplicationContext();

    RegisteredUser user = uc.getDatabase().getUser(email);
    if (user != null) {
      // generate a new password and set the hashed password and display
      // non-hashed
      String newPassword = InputOperations.generatePassword(Constants.MAX_PASSWORD);
      user.setPassword(InputOperations.hashPassword(newPassword));
      idPasswordMessage.setText(getString(string.password_send_success, newPassword));
      idPasswordMessage.setVisibility(View.VISIBLE);
    } else {
      idEmailView.setError(getString(string.password_send_fail));
      idEmailView.requestFocus();
      idPasswordMessage.setVisibility(View.GONE);
    }
  }
}
