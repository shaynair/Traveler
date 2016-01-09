package csc.project.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import csc.project.R;
import csc.project.UserControl;
import csc.project.view.UserViewFragment;
import csc.users.RegisteredUser;

import java.util.ArrayList;

/**
 * Fragment to search users.
 */
public class SearchUserFragment extends Fragment {
  /**
   * Text field for email.
   */
  private EditText idEmail = null;
  /**
   * Text field for name.
   */
  private EditText idName = null;

  /**
   * Returns a new instance of this fragment.
   */
  public static SearchUserFragment newInstance() {
    return new SearchUserFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_search_user, container, false);

    idEmail = (EditText) rootView.findViewById(R.id.email);
    idName = (EditText) rootView.findViewById(R.id.name);

    UserViewFragment frag = UserViewFragment.newInstance(new ArrayList<RegisteredUser>());
    getChildFragmentManager().beginTransaction().add(R.id.layout, frag).commit();

    return rootView;
  }

  /**
   * Action to take when search by email button is pressed.
   */
  public void searchEmail() {
    search("", idEmail.getText().toString());
  }

  /**
   * Action to take when search by name button is pressed.
   */
  public void searchName() {
    search(idName.getText().toString(), "");
  }

  /**
   * Action to take when search by both button is pressed.
   */
  public void searchBoth() {
    search(idName.getText().toString(), idEmail.getText().toString());
  }

  /**
   * Action to take when search is pressed.
   *
   * @param name
   *          the current name to search for
   * @param email
   *          the email to search for
   */
  private void search(String name, String email) {
    idName.setError(null);
    idEmail.setError(null);

    UserControl uc = (UserControl) getActivity().getApplicationContext();
    ArrayList<RegisteredUser> list = uc.getDatabase().searchUsers(name, email);
    if (list.isEmpty()) {
      idName.setError(getString(R.string.error_none));
      idEmail.setError(getString(R.string.error_none));
      idName.requestFocus();
    } else {
      UserViewFragment frag = UserViewFragment.newInstance(list);
      FragmentTransaction ft = getChildFragmentManager().beginTransaction();
      ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
      ft.replace(R.id.layout, frag).commit();
    }
  }
}
