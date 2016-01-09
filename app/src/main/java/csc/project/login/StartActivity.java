package csc.project.login;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;

import csc.project.R.id;
import csc.project.R.layout;

/**
 * A full-screen activity that shows and hides the system UI (i.e. status bar
 * and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class StartActivity extends Activity {
  /**
   * The flags to pass to SystemUiHider.
   */
  private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

  /**
   * The instance of the {@link SystemUiHider} for this activity.
   */
  private SystemUiHider uiHider = null;

  /**
   * A handler for a hide thread.
   */
  private final Handler hideHandler = new Handler();
  /**
   * A runnable for hiding the UI.
   */
  private final Runnable hideRunnable = new Runnable() {
    @Override
    public void run() {
      uiHider.hide();
    }
  };
  /**
   * A handler for a show thread.
   */
  private final Handler showHandler = new Handler();
  /**
   * A runnable for showing the UI.
   */
  private final Runnable showRunnable = new Runnable() {
    @Override
    public void run() {
      show();
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(layout.activity_start);
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    final View controlsView = findViewById(id.fullscreen_content_controls);
    final View contentView = findViewById(id.fullscreen_content);

    // Set up an instance of SystemUiHider to control the system UI for
    // this activity.
    final int animTime = getResources().getInteger(integer.config_shortAnimTime);
    uiHider = new SystemUiHider(contentView, HIDER_FLAGS, controlsView, animTime);
    uiHider.setup();

    // Set up the user interaction to manually show or hide the system UI.
    contentView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        delayedShow(10);
      }
    });
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);

    // Trigger the initial hide() shortly after the activity has been
    // created, to briefly hint to the user that UI controls
    // are available.
    delayedHide(10);
    delayedShow(2000);
  }

  @Override
  protected void onPostResume() {
    super.onPostResume();
    delayedShow(1000);
  }

  /**
   * Hides the UI.
   */
  private void hide() {
    uiHider.hide();
  }

  /**
   * Shows the UI.
   */
  private void show() {
    uiHider.show();
    finish();
    Intent inte = new Intent(this, LoginActivity.class);
    inte.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(inte);
  }

  /**
   * Schedules a call to hide() in [delay] milliseconds, canceling any
   * previously scheduled calls.
   */
  private void delayedHide(int delayMillis) {
    hideHandler.removeCallbacks(hideRunnable);
    hideHandler.postDelayed(hideRunnable, delayMillis);
  }

  /**
   * Schedules a call to show() in [delay] milliseconds, canceling any
   * previously scheduled calls.
   */
  private void delayedShow(int delayMillis) {
    showHandler.removeCallbacks(showRunnable);
    showHandler.postDelayed(showRunnable, delayMillis);
  }
}
