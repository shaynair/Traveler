package csc.project.login;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.WindowManager;

/**
 * A utility class that helps with showing and hiding system UI such as the
 * status bar and navigation/system bar. This class uses backward-compatibility
 * techniques described in <a href=
 * "http://developer.android.com/training/backward-compatible-ui/index.html">
 * Creating Backward-Compatible UIs</a> to ensure that devices running any
 * version of Android OS are supported.
 * <p/>
 * For more on system bars, see <a href=
 * "http://developer.android.com/design/get-started/ui-overview.html#system-bars"
 * > System Bars</a>.
 *
 * @see View#setSystemUiVisibility(int)
 * @see WindowManager.LayoutParams#FLAG_FULLSCREEN
 */
public class SystemUiHider {
  /**
   * When this flag is set, the
   * {@link WindowManager.LayoutParams#FLAG_LAYOUT_IN_SCREEN} flag will be set
   * on older devices, making the status bar "float" on top of the activity
   * layout. This is most useful when there are no controls at the top of the
   * activity layout.
   * <p/>
   * This flag isn't used on newer devices because the
   * <a href="http://developer.android.com/design/patterns/actionbar.html">
   * action bar</a>, the most important structural element of an Android app,
   * should be visible and not obscured by the system UI.
   */
  public static final int FLAG_LAYOUT_IN_SCREEN_OLDER_DEVICES = 0x1;

  /**
   * When this flag is set, {@link #show()} and {@link #hide()} will toggle the
   * visibility of the status bar. If there is a navigation bar, show and hide
   * will toggle low profile mode.
   */
  public static final int FLAG_FULLSCREEN = 0x2;

  /**
   * When this flag is set, {@link #show()} and {@link #hide()} will toggle the
   * visibility of the navigation bar, if it's present on the device and the
   * device allows hiding it. In cases where the navigation bar is present but
   * cannot be hidden, show and hide will toggle low profile mode.
   */
  public static final int FLAG_HIDE_NAVIGATION = FLAG_FULLSCREEN | 0x4;

  /**
   * The view on which {@link View#setSystemUiVisibility(int)} will be called.
   */
  private final View anchorView;

  /**
   * The current UI hider flags.
   *
   * @see #FLAG_FULLSCREEN
   * @see #FLAG_HIDE_NAVIGATION
   * @see #FLAG_LAYOUT_IN_SCREEN_OLDER_DEVICES
   */
  private final int uiFlags;

  /**
   * Flags for {@link View#setSystemUiVisibility(int)} to use when showing the
   * system UI.
   */
  private int showFlags;

  /**
   * Flags for {@link View#setSystemUiVisibility(int)} to use when hiding the
   * system UI.
   */
  private int hideFlags;

  /**
   * Flags to test against the first parameter in
   * {@link View.OnSystemUiVisibilityChangeListener#onSystemUiVisibilityChange(int)}
   * to determine the system UI visibility state.
   */
  private int testFlags;

  /**
   * Whether or not the system UI is currently visible. This is a cached value
   * from calls to {@link #hide()} and {@link #show()}.
   */
  private boolean isVisible = true;

  /**
   * The current visibility callback.
   */
  private final OnVisibilityChangeListener visibilityChangeListener;

  /**
   * Creates and returns an instance of SystemUiHider that is appropriate for
   * this device.
   *
   * @param anchorView
   *          The view on which {@link View#setSystemUiVisibility(int)} will be
   *          called.
   * @param flags
   *          Either 0 or any combination of {@link #FLAG_FULLSCREEN},
   *          {@link #FLAG_HIDE_NAVIGATION}, and
   *          {@link #FLAG_LAYOUT_IN_SCREEN_OLDER_DEVICES}.
   * @param controlsView
   *          The view for the visibility.
   * @param animTime
   *          The animation time for transitions.
   */

  protected SystemUiHider(View anchorView, int flags, View controlsView, int animTime) {
    this.anchorView = anchorView;
    uiFlags = flags;
    visibilityChangeListener = new OnVisibilityChangeListener(controlsView, animTime);

    showFlags = View.SYSTEM_UI_FLAG_VISIBLE;
    hideFlags = View.SYSTEM_UI_FLAG_LOW_PROFILE;
    testFlags = View.SYSTEM_UI_FLAG_LOW_PROFILE;

    if ((uiFlags & FLAG_FULLSCREEN) != 0) {
      // If the client requested fullscreen, add flags relevant to hiding
      // the status bar. Note that some of these constants are new as of
      // API 16 (Jelly Bean). It is safe to use them, as they are inlined
      // at compile-time and do nothing on pre-Jelly Bean devices.
      showFlags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
      hideFlags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN;
    }

    if ((uiFlags & FLAG_HIDE_NAVIGATION) != 0) {
      // If the client requested hiding navigation, add relevant flags.
      showFlags |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
      hideFlags |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
          | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
      testFlags |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }
  }

  /**
   * Sets up the system UI hider. Should be called from
   * {@link Activity#onCreate}.
   */
  public void setup() {
    anchorView.setOnSystemUiVisibilityChangeListener(visibilityListener);
  }

  /**
   * Returns whether or not the system UI is visible.
   */
  public boolean isVisible() {
    return isVisible;
  }

  /**
   * Hide the system UI.
   */
  public void hide() {
    anchorView.setSystemUiVisibility(hideFlags);
  }

  /**
   * Show the system UI.
   */
  public void show() {
    anchorView.setSystemUiVisibility(showFlags);
  }

  /**
   * Toggle the visibility of the system UI.
   */
  public void toggle() {
    if (isVisible()) {
      hide();
    } else {
      show();
    }
  }

  /**
   * The visibility listener for HoneyComb API and above.
   */
  private final OnSystemUiVisibilityChangeListener visibilityListener =
      new OnSystemUiVisibilityChangeListener() {
    @Override
    public void onSystemUiVisibilityChange(int vis) {
      // Test against testFlags to see if the system UI is visible.
      if ((vis & testFlags) == 0) {
        anchorView.setSystemUiVisibility(showFlags);
        isVisible = true;
      } else {
        isVisible = false;
      }
      visibilityChangeListener.onVisibilityChange(isVisible);
    }
  };

  /**
   * A callback interface used to listen for system UI visibility changes.
   */
  private static final class OnVisibilityChangeListener {
    // Cached values.
    private final int controlsHeight;
    private final int shortAnimTime;
    private final View controlsView;

    private OnVisibilityChangeListener(View controlsView, int animTime) {
      this.controlsView = controlsView;
      controlsHeight = controlsView.getHeight();
      shortAnimTime = animTime;
    }

    /**
     * Called when the system UI visibility has changed.
     *
     * @param visible
     *          True if the system UI is visible.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void onVisibilityChange(boolean visible) {
      // If the ViewPropertyAnimator API is available
      // (Honeycomb MR2 and later), use it to animate the
      // in-layout UI controls at the bottom of the
      // screen.
      controlsView.animate().translationY(visible ? 0 : controlsHeight)
          .setDuration(shortAnimTime);
    }
  }
}