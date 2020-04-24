package ooga.exceptions;

public class DeprecationException extends RuntimeException {
    protected static final ErrorSeverity myErrorSeverity = ErrorSeverity.CRITICAL;
    protected String myString;
    private static final int SLEEP_TIME = 10000;

    public DeprecationException() {
        super();
    }

    public DeprecationException(String message) {
        super(message);
        this.myString = message;
    }

    public ErrorSeverity getErrorSeverity() {
        return myErrorSeverity;
    }

    public String getMessage() {
        return this.myString;
    }

    public int getSleep() { return SLEEP_TIME; }

}
