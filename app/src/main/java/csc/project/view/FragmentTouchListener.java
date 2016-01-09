package csc.project.view;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

/**
 * Allows recycler view scrolling within other scrollable views.
 */
public class FragmentTouchListener implements RecyclerView.OnItemTouchListener {
  /**
   * Whether the user has touched the view or not.
   */
  private boolean touched = false;
  /**
   * The initial location of where they touched the view.
   */
  private double initialX = 0.0;
  /**
   * The initial location of where they touched the view.
   */
  private double initialY = 0.0;

  @Override
  public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent event) {

    // user is trying to scroll within the RecyclerView
    // so disable the parent's scrolling IF it is vertical
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        touched = true;
        initialX = event.getX();
        initialY = event.getY();
        break;
      case MotionEvent.ACTION_POINTER_UP:
        touched = true;
        break;
      case MotionEvent.ACTION_MOVE:
      case MotionEvent.ACTION_UP:
        if (touched) {
          double changeX = Math.abs(event.getX() - initialX);
          double changeY = Math.abs(event.getY() - initialY);

          // vertical scrolling
          if (changeY > changeX) {
            rv.getParent().requestDisallowInterceptTouchEvent(true);
          }
        }
        break;
      default:
        break;
    }
    return false;
  }

  @Override
  public void onTouchEvent(RecyclerView rv, MotionEvent event) {

  }

  @Override
  public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

  }
}
