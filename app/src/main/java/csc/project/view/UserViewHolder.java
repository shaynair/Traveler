package csc.project.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import csc.project.R;
import csc.users.RegisteredUser;

/**
 * Holds a view of an item.
 */
public class UserViewHolder extends RecyclerView.ViewHolder {
  /**
   * The user associated with this item.
   */
  private RegisteredUser item = null;
  /**
   * The whole view of this item.
   */
  private final View view;
  /**
   * The name field to show.
   */
  private final TextView idName;
  /**
   * The credit field to show.
   */
  private final TextView idCredit;
  /**
   * The expiry field to show.
   */
  private final TextView idExpiry;
  /**
   * The address field to show.
   */
  private final TextView idAddress;
  /**
   * The booked field to show.
   */
  private final TextView idBooked;
  /**
   * The email field to show.
   */
  private final TextView idEmail;
  /**
   * The type field to show.
   */
  private final TextView idType;

  /**
   * Creates a new ViewHolder with the view.
   *
   * @param view
   *          the view to hold
   */
  public UserViewHolder(View view) {
    super(view);
    this.view = view;
    idName = (TextView) view.findViewById(R.id.name);
    idExpiry = (TextView) view.findViewById(R.id.expiry);
    idCredit = (TextView) view.findViewById(R.id.credit_card);
    idBooked = (TextView) view.findViewById(R.id.booked);
    idAddress = (TextView) view.findViewById(R.id.address);
    idEmail = (TextView) view.findViewById(R.id.email);
    idType = (TextView) view.findViewById(R.id.type);
  }

  /**
   * Gets the item of this view.
   *
   * @return the item
   */
  public RegisteredUser getItem() {
    return item;
  }

  /**
   * Sets the item of this view.
   *
   * @param item
   *          the item to set
   */
  public void setItem(RegisteredUser item) {
    this.item = item;

    idName.setText(item.getName());
    idExpiry.setText("Expires " + item.getExpiryDate());
    idCredit.setText("Card: " + item.getCreditCard());
    idAddress.setText("Address: " + item.getAddress());
    idEmail.setText("Email: " + item.getIdentifier());
    idBooked.setText(item.getBookedItineraries().size() + " booked itineraries");
    idType.setText(item.getType().name());
  }

  /**
   * Gets the view.
   *
   * @return the view
   */
  public View getView() {
    return view;
  }
}