package csc.project;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import csc.database.MainDatabase;
import csc.interfaces.LineParse;
import csc.io.InputOperations;
import csc.io.SaveOperations;
import csc.project.R.string;
import csc.users.RegisteredUser;
import csc.users.User;
import csc.util.Constants;

import java.io.File;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * Driver class for interacting with the user.
 */
public class UserControl extends Application {
  /**
   * The logger for this class.
   */
  private static final Logger log = Logger.getLogger(UserControl.class.getName());

  /**
   * The current user of ths system.
   */
  private User user = null;
  /**
   * The database of the application.
   */
  private MainDatabase database = null;
  /**
   * The utility to save the database.
   */
  private SaveOperations storage = null;
  /**
   * The preferences of this app.
   */
  private SharedPreferences pref = null;

  @Override
  public void onCreate() {
    super.onCreate();
    initialize();
  }

  /**
   * Initializes the database. Does nothing if already initialized.
   */
  private void initialize() {
    if (storage != null) {
      return; // already initialized
    }
    database = new MainDatabase();
    storage = new SaveOperations(this);
    storage.deserializeDatabase(this);
    pref = getSharedPreferences(getString(string.pref_key), Context.MODE_PRIVATE);
  }

  /**
   * Starts a background service for saving data.
   */
  public void save() {
    if (storage == null) {
      return; // not initialized
    }
    startService(new Intent(this, SaveService.class));
  }

  /**
   * Saves data directly to the file. Does nothing if this is not initialized.
   */
  public void saveData() {
    if (storage == null) {
      return; // not initialized
    }
    storage.serializeDatabase(this);
  }

  /**
   * Parses data from a given file, parsing objects line by line. The data
   * returned is of class T, and type is of class E, where E can parse lines to
   * create T.
   *
   * @param path
   *          the path to the file
   * @param type
   *          the type of the objects
   * @return a set containing all users parsed from the file
   */
  public <T, E extends LineParse<T>> Collection<T> parseData(String path, E type) {
    return InputOperations.parseData(
        new File(getDir(Constants.SAVE_DIR, Context.MODE_PRIVATE), path).getPath(), type);
  }

  /**
   * Gets the shared preferences of this application.
   *
   * @return the shared preferences.
   */
  public SharedPreferences getPreferences() {
    return pref;
  }

  /**
   * Gets the main database of this instance.
   *
   * @return the main database
   */
  public MainDatabase getDatabase() {
    return database;
  }

  /**
   * Gets the user of this instance.
   *
   * @return the user
   */
  public User getUser() {
    return user;
  }

  /**
   * Sets the user of this instance.
   *
   * @param user
   *          the user to set
   */
  public void setUser(User user) {
    this.user = user;
  }

  /**
   * Gets the amount of users in the database.
   *
   * @return the amount of users
   */
  public int getNumUsers() {
    return database.getAllUsers().size();
  }

  /**
   * Validates the credentials entered and gets the user.
   *
   * @param email
   *          the email entered
   * @param password
   *          the password entered
   * @return the user if the login was successful, null otherwise
   */
  public RegisteredUser validateCredentials(String email, String password) {
    return validateSecureCredentials(email, InputOperations.hashPassword(password));
  }

  /**
   * Validates the credentials entered and gets the user.
   *
   * @param email
   *          the email entered
   * @param password
   *          the hashed password entered
   * @return the user if the login was successful, null otherwise
   */
  public RegisteredUser validateSecureCredentials(String email, String password) {
    RegisteredUser ru = database.getUser(email);
    // no password or hashed is good
    if (ru != null) {
      if (ru.getPassword().isEmpty()) {
        ru.setPassword(password);
      }
      if (password.equals(ru.getPassword())) {
        return ru;
      }
    }
    return null;
  }
}
