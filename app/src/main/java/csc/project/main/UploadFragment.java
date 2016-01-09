package csc.project.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import csc.project.R;
import csc.project.UserControl;
import csc.travel.SingleTravel;
import csc.travel.TravelType;
import csc.users.RegisteredUser;
import csc.users.UserType;

import java.util.Collection;

/**
 * Fragment to upload user or travel info.
 */
public class UploadFragment extends Fragment {

  /**
   * A key for the arguments of this class.
   */
  private static final String KEY = "isTravel";

  /**
   * The chooser spinner for the travel type.
   */
  private Spinner travelSpinner = null;
  /**
   * The chooser spinner for the user type.
   */
  private Spinner userSpinner = null;
  /**
   * Path chooser for user files.
   */
  private EditText userPath = null;
  /**
   * Path chooser for travel files.
   */
  private EditText travelPath = null;

  /**
   * For outputting user data.
   */
  private TextView userOutput = null;
  /**
   * For outputting travel data.
   */
  private TextView travelOutput = null;

  /**
   * Returns a new instance of this fragment.
   */
  public static UploadFragment newInstance() {
    return new UploadFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_upload, container, false);

    // populate the spinners with the possible names

    travelSpinner = (Spinner) rootView.findViewById(R.id.spinner);
    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(),
        android.R.layout.simple_spinner_item, TravelType.getUsableNames());
    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    travelSpinner.setAdapter(spinnerArrayAdapter);

    userSpinner = (Spinner) rootView.findViewById(R.id.spinner2);
    spinnerArrayAdapter = new ArrayAdapter<>(getActivity(),
        android.R.layout.simple_spinner_item, UserType.getUsableNames());
    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    userSpinner.setAdapter(spinnerArrayAdapter);

    travelPath = (EditText) rootView.findViewById(R.id.r_editText);
    userPath = (EditText) rootView.findViewById(R.id.r_editText5);

    travelOutput = (TextView) rootView.findViewById(R.id.textView);
    userOutput = (TextView) rootView.findViewById(R.id.textView3);

    return rootView;
  }

  /**
   * Action for when the upload user file is pressed.
   */
  public void uploadUser() {
    uploadUser(userPath.getText().toString());
  }

  /**
   * Uploads users from the given file path.
   * 
   * @param path
   *          the path of the file.
   */
  public void uploadUser(String path) {
    UserType ut = UserType.getByIndex(userSpinner.getSelectedItemPosition());
    if (ut == null || !ut.isUsable() || path == null) {
      userOutput.setText(getString(R.string.invalid_param));
      return;
    }
    UserControl uc = (UserControl) getActivity().getApplicationContext();
    Collection<RegisteredUser> parsedData = uc.parseData(path, ut);

    if (parsedData.isEmpty()) {
      userOutput.setText(getString(R.string.empty_file));
      return;
    }
    // make a string based on each result

    StringBuilder sb = new StringBuilder();
    for (RegisteredUser ru : parsedData) {
      sb.append(ru.getIdentifier());
      if (uc.getDatabase().getUser(ru.getIdentifier()) != null) {
        sb.append(getString(R.string.updated));
      } else {
        sb.append(getString(R.string.added));
      }
      uc.getDatabase().addUser(ru);
      sb.append('\n');
    }

    userOutput.setText(sb.toString());
  }

  /**
   * Action for when the upload travel file is pressed.
   */
  public void uploadTravel() {
    uploadTravel(travelPath.getText().toString());
  }

  /**
   * Action for when the upload travel file is pressed.
   * 
   * @param path
   *          the path to get the file from
   */
  public void uploadTravel(String path) {
    TravelType tt = TravelType.getByIndex(travelSpinner.getSelectedItemPosition());
    if (tt == null || !tt.isUsable() || path == null) {
      travelOutput.setText(getString(R.string.invalid_param));
      return;
    }
    UserControl uc = (UserControl) getActivity().getApplicationContext();
    Collection<SingleTravel> parsedData = uc.parseData(path, tt);

    if (parsedData.isEmpty()) {
      travelOutput.setText(getString(R.string.empty_file));
      return;
    }
    // make a string based on each result
    StringBuilder sb = new StringBuilder();
    for (SingleTravel st : parsedData) {
      sb.append(st.getIdentifier());
      if (st.isInvalid()) {
        sb.append(getString(R.string.invalid_skip));
      } else {
        if (uc.getDatabase().getTravel(tt, st.getIdentifier()) != null) {
          sb.append(getString(R.string.updated));
        } else {
          sb.append(getString(R.string.added));
        }
        uc.getDatabase().addTravel(st);
      }
      sb.append('\n');
    }

    travelOutput.setText(sb.toString());

  }
}
