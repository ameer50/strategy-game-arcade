package ooga.exceptions;

public class ResourceBundleException extends DeprecationException {

    private static final String message = "Unable to find resource bundle";

    public ResourceBundleException() {
        super();
        this.myString = message;
    }

    public ResourceBundleException(String message) {
        super(message);
    }
}
