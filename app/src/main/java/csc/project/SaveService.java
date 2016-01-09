package csc.project;

import android.app.IntentService;
import android.content.Intent;

/**
 * Class for handling saving in the background.
 */
public class SaveService extends IntentService {

  /**
   * Constructor that passes on the name of this service.
   */
  public SaveService() {
    super(SaveService.class.getSimpleName());
  }

  @Override
  protected void onHandleIntent(Intent workIntent) {
    ((UserControl) getApplicationContext()).saveData();
  }
}