package csc.project;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Base class for all activities.
 */
public abstract class BaseActivity extends AppCompatActivity {

  /**
   * The method that is called upon the creation of this activity.
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
  }

  /**
   * The method that is called then the activity stops.
   */
  @Override
  protected void onStop() {
    super.onStop(); // Always call the superclass method first
    ((UserControl) getApplicationContext()).save();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    if (id == R.id.action_exit) {
      killProcess();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  /**
   * Kills the app.
   */
  protected void killProcess() {
    android.os.Process.killProcess(android.os.Process.myPid());
  }
}
