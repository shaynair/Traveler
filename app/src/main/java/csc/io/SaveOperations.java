package csc.io;

import android.content.Context;

import csc.database.MainDatabase;
import csc.project.UserControl;
import csc.travel.Itinerary;
import csc.travel.SingleTravel;
import csc.travel.TravelType;
import csc.users.GuestUser;
import csc.users.RegisteredUser;
import csc.users.UserType;
import csc.util.Constants;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * A class for internally loading and saving progress.
 */
public class SaveOperations {
  /**
   * The logger for this class.
   */
  private static final Logger log = Logger.getLogger(SaveOperations.class.getName());

  /**
   * The internal storage database.
   */
  private final DatabaseOperations storage;

  /**
   * Initializes the save database with a connection to the Android database.
   *
   * @param context
   *          the Android context
   */
  public SaveOperations(Context context) {
    this.storage = new DatabaseOperations(context);
  }

  /**
   * Serializes the database to a file and a database.
   *
   * @param uc
   *          the application to write
   */
  public void serializeDatabase(UserControl uc) {
    // truncate the file by default.
    try {
      byte[] bytes = serializeToBytes(uc);

      writeBytesToFile(bytes, uc, Constants.SAVE_FILE);
      storage.insertEntry(bytes);

    } catch (IOException | GeneralSecurityException e) {
      log.log(Level.SEVERE, "Error serializing.", e);
    }
  }

  /**
   * Writes a byte array to a save file.
   *
   * @param data
   *          the byte array to write
   * @param context
   *          the Android context
   * @param path
   *          the path of the file
   */
  private static void writeBytesToFile(byte[] data, Context context, String path) {
    try (FileOutputStream out = context.openFileOutput(path, Context.MODE_PRIVATE);
        BufferedOutputStream writer = new BufferedOutputStream(out)) {

      writer.write(data);
    } catch (IOException e) {
      log.log(Level.SEVERE, "Error writing bytes to file.", e);
    }
  }

  /**
   * Converts a MainDatabase into a byte array for serialization.
   *
   * @param uc
   *          the database to serialize
   * @return a byte array representing the database
   * @throws IOException
   *           if there was an error in closing
   * @throws GeneralSecurityException
   *           if there was an error in encryption
   */
  private static byte[] serializeToBytes(UserControl uc)
      throws IOException, GeneralSecurityException {

    MainDatabase data = uc.getDatabase();
    try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BufferedOutputStream writer = new BufferedOutputStream(bos)) {

      byte[] salt = generateSalt();
      Cipher enc = getEncryptionCipher(salt, null);
      // un-encrypted header (necessary information for decryption)
      writer.write(Constants.DATABASE_VERSION);
      writer.write(salt.length);
      writer.write(salt);
      writer.write(enc.getIV().length);
      writer.write(enc.getIV());

      try (ObjectOutputStream oos = new ObjectOutputStream(
          new CipherOutputStream(writer, enc))) {
        // if we need any settings, write them here
        if (uc.getUser() != null) {
          oos.writeByte(uc.getUser().getType().ordinal());
          oos.writeUTF(uc.getUser().getIdentifier());
        } else {
          oos.writeByte(-1);
          oos.writeUTF("");
        }

        // write the travel database
        oos.writeByte(TravelType.values().length);
        for (TravelType tt : TravelType.values()) {
          // we don't directly serialize the collection because
          // MainDatabase indexes the origins
          // so each SingleTravel must be added one by one on reading
          Collection<SingleTravel> travels = data.getAllTravels(tt);
          oos.writeInt(travels.size());
          for (SingleTravel st : travels) {
            oos.writeObject(st);
          }
        }

        // write the user database
        oos.writeInt(data.getAllUsers().size());
        for (RegisteredUser ru : data.getAllUsers()) {
          // we don't want to directly serialize the itineraries because we want
          // to strictly check them to make sure they're valid
          // plus, we can use the existing TravelDatabase to avoid conflicts
          // with equals() and hashCode() for itineraries
          oos.writeObject(ru);

          oos.writeInt(ru.getBookedItineraries().size());
          for (Itinerary it : ru.getBookedItineraries()) {
            oos.writeInt(it.size());
            for (SingleTravel st : it.getTravels()) {
              oos.writeByte(st.getType().ordinal());
              oos.writeUTF(st.getIdentifier());
            }
          }
        }
      }
      return bos.toByteArray();
    }
  }

  /**
   * Deserializes the database from a file. If the file cannot be read, a
   * database is read from.
   *
   * @param uc
   *          the application to deserialize
   */
  public void deserializeDatabase(UserControl uc) {
    try {
      byte[] bytes = readBytesFromFile(uc, Constants.SAVE_FILE);
      if (bytes == null) {
        bytes = storage.getLastEntry();
      }
      if (bytes != null) {
        deserializeFromBytes(uc, bytes);
      }
    } catch (IOException | GeneralSecurityException | ClassNotFoundException e) {
      log.log(Level.SEVERE, "Error deserializing.", e);
    }
  }

  /**
   * Reads a byte array from the save file.
   *
   * @param context
   *          the Android context
   * @param path
   *          the path of the file
   * @return a byte array of the file's contents; null if an error occurred
   */
  private static byte[] readBytesFromFile(Context context, String path) {
    // first check if the file exists
    try (FileInputStream in = context.openFileInput(path);
        BufferedInputStream reader = new BufferedInputStream(in)) {

      byte[] bytes = new byte[reader.available()];
      reader.read(bytes);
      return bytes;
    } catch (IOException e) {
      log.log(Level.SEVERE, "Error reading bytes from file.", e);
    }
    return null;
  }

  /**
   * Deserializes a database from a byte array.
   *
   * @param uc
   *          the database to input the information into
   * @param in
   *          the byte array to read from
   * @throws IOException
   *           if there was an error in reading
   * @throws GeneralSecurityException
   *           if there was an error in encryption
   * @throws ClassNotFoundException
   *           if there was an error in compatibility
   */
  private static void deserializeFromBytes(UserControl uc, byte[] in)
      throws IOException, GeneralSecurityException, ClassNotFoundException {
    MainDatabase data = uc.getDatabase();
    try (ByteArrayInputStream bis = new ByteArrayInputStream(in);
        BufferedInputStream reader = new BufferedInputStream(bis)) {

      if (reader.read() != Constants.DATABASE_VERSION) {
        // abort; likely changed version
        return;
      }
      // un-encrypted header (salt and iv necessary for decryption)
      byte[] salt = new byte[reader.read()];
      if (reader.read(salt) != salt.length) {
        return;
      }
      byte[] iv = new byte[reader.read()];
      if (reader.read(iv) != iv.length) {
        return;
      }
      Cipher enc = getEncryptionCipher(salt, iv);

      try (CipherInputStream cis = new CipherInputStream(reader, enc);
          ObjectInputStream ois = new ObjectInputStream(cis)) {

        // if we need any settings, read them here
        final byte type = ois.readByte();
        final String user = ois.readUTF();

        // read the travel database
        int count = ois.readByte();
        for (int i = 0; i < count; i++) {
          int size = ois.readInt();
          for (int j = 0; j < size; j++) {
            data.addTravel((SingleTravel) ois.readObject());
          }
        }

        // read the user database
        count = ois.readInt();
        for (int i = 0; i < count; i++) {
          RegisteredUser ru = (RegisteredUser) ois.readObject();
          data.addUser(ru);

          int size = ois.readInt();
          for (int j = 0; j < size; j++) {
            Itinerary it = readItinerary(ois, data);
            if (it != null) {
              ru.bookItinerary(it);
            }
          }
        }
        if (type == UserType.Guest.ordinal()) {
          uc.setUser(new GuestUser());
        } else if (!user.isEmpty()) {
          uc.setUser(data.getUser(user));
        }
      }
    }
  }

  /**
   * Reads an itinerary from the stream.and validates it.
   *
   * @param ois
   *          the stream to read from
   * @param data
   *          the main database to associate the itinerary with
   * @return the read itinerary. Null is returned if it was invalid.
   * @throws IOException
   *           if an error occurred during reading
   */
  private static Itinerary readItinerary(DataInput ois, MainDatabase data) throws IOException {
    int itSize = ois.readInt();
    // check the itinerary to make sure it is still valid
    boolean validItinerary = itSize > 0;
    Itinerary it = new Itinerary();
    for (int k = 0; k < itSize; k++) {
      try {
        SingleTravel st = data.getTravel(TravelType.values()[ois.readByte()], ois.readUTF());
        if (st == null) { // doesn't exist; do not add itinerary
          validItinerary = false;
        } else {
          it.add(st);
        }
      } catch (IllegalArgumentException ignore) {
        validItinerary = false;
        // we don't need to log this -- probably a travel expired
      }
    }
    if (validItinerary) {
      return it;
    }
    return null;
  }

  /**
   * Gets the encryption cipher for the file.
   *
   * @param salt
   *          the salt to use when creating the key
   * @param iv
   *          the initialization vector to start with; if null, the cipher is in
   *          encrypt mode and generates a random IV. otherwise, the cipher is
   *          in decrypt mode with the given IV.
   * @return the encryption cipher
   * @throws GeneralSecurityException
   *           when the padding or algorithm does not exist or is invalid
   * @throws IOException
   *           when the encoding is invalid
   */
  private static Cipher getEncryptionCipher(byte[] salt, byte[] iv)
      throws GeneralSecurityException, IOException {

    // key is a static password + random salt, iv is random
    byte[] password = InputOperations
        .cryptPassword(Constants.CIPHER_PASSWORD + new String(salt, Constants.FILE_ENCODING));
    Key secretKey = new SecretKeySpec(password, Constants.CIPHER);

    Cipher cipher = Cipher.getInstance(Constants.CIPHER_PADDING);
    if (iv == null) {
      cipher.init(Cipher.ENCRYPT_MODE, secretKey);
    } else {
      cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
    }
    return cipher;
  }

  /**
   * Generates a random salt for the encryption cipher.
   *
   * @return a byte array of the salt
   */
  private static byte[] generateSalt() {
    byte[] salt = new byte[Constants.CIPHER_SALT_LENGTH];
    SecureRandom rand = new SecureRandom();
    rand.nextBytes(salt);
    return salt;
  }
}
