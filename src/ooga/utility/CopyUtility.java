package ooga.utility;

import ooga.view.SetUpError;

import java.io.*;

public class CopyUtility {

    public CopyUtility() {
    }

    public Object getSerializedCopy(Object o) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream objOutputStream = new ObjectOutputStream(outputStream);
            objOutputStream.writeObject(o);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            ObjectInputStream objInputStream = new ObjectInputStream(inputStream);
            return objInputStream.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new SetUpError("Unable to find class");
        }
    }
}
