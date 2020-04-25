package ooga.utility;

import ooga.view.SetUpError;

import java.io.*;

public class CopyUtility {

    public static final String CANNOT_FIND_CLASS_ERROR = "Unable to find class";

    public Object getSerializedCopy(Object o) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream objOutputStream = new ObjectOutputStream(outputStream);
            objOutputStream.writeObject(o);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            ObjectInputStream objInputStream = new ObjectInputStream(inputStream);
            return objInputStream.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new SetUpError(CANNOT_FIND_CLASS_ERROR);
        }
    }
}
