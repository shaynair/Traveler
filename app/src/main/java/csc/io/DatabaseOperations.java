package csc.io;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import csc.util.Constants;

/**
 * A helper class for interacting with the SQLite Database and getting stored
 * data.
 */
public class DatabaseOperations extends SQLiteOpenHelper {

  /**
   * The constructor for this database helper.
   *
   * @param context
   *          The Android Context to use for this database.
   */
  public DatabaseOperations(Context context) {
    super(context, Constants.SAVE_FILE, null, Constants.DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE " + Constants.DATABASE_TABLE + ' '
        + "(id INTEGER PRIMARY KEY AUTOINCREMENT, " + Constants.DATABASE_COLUMN + " BLOB)");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // drop and re-make
    db.execSQL("DROP TABLE IF EXISTS " + Constants.DATABASE_TABLE);
    onCreate(db);
  }

  /**
   * Inserts a byte array entry into the database.
   *
   * @param bytes
   *          the byte array to add
   */
  public void insertEntry(byte[] bytes) {
    try (SQLiteDatabase db = this.getWritableDatabase()) {
      ContentValues contentValues = new ContentValues();
      contentValues.put(Constants.DATABASE_COLUMN, bytes);
      long id = db.insert(Constants.DATABASE_TABLE, null, contentValues);

      // if it is successful, delete old entries to save space
      db.delete(Constants.DATABASE_TABLE, "id < ?", new String[] { String.valueOf(id) });
    }
  }

  /**
   * Gets a last byte array saved in this database.
   *
   * @return the last entry in this database; null if none was found
   */
  public byte[] getLastEntry() {
    try (SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + Constants.DATABASE_TABLE, null)) {
      // get last entry
      if (res.moveToLast()) {
        return res.getBlob(res.getColumnIndex(Constants.DATABASE_COLUMN));
      }
    }
    return null;
  }
}