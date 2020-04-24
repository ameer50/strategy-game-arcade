package ooga.utility;

import ooga.view.DisplayError;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class CopyUtility {

  public CopyUtility() { }

  public Object getSerializedCopy(Object o) {
    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      ObjectOutputStream objOutputStream = new ObjectOutputStream(outputStream);
      objOutputStream.writeObject(o);
      ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
      ObjectInputStream objInputStream = new ObjectInputStream(inputStream);
      return objInputStream.readObject();
    } catch (ClassNotFoundException | IOException e) {
      new DisplayError("Unable to find class");
    }
    return null;
  }
}
