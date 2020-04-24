package ooga.exceptions;

/**
 * Exception is thrown when invalid keywords are used in a reflection.
 */
public class ReflectionException extends LogicalException {

    private static final String DEFAULT_MESSAGE = "Reflection not able to be performed";

    public ReflectionException() {
        super();
        this.myErrorSeverity = ErrorSeverity.MEDIUM;
        this.myString = DEFAULT_MESSAGE;
    }

    public ReflectionException(String message) {
        super(message);
        this.myString = message;
        this.myErrorSeverity = ErrorSeverity.MEDIUM;
    }
}
