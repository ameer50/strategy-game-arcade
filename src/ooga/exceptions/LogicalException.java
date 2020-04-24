package ooga.exceptions;

public abstract class LogicalException extends RuntimeException {

    protected ErrorSeverity myErrorSeverity;
    protected String myString;

    public LogicalException() {
        super();
    }

    public LogicalException(String message) {
        super(message);
        this.myString = message;
    }

    public ErrorSeverity getErrorSeverity() {
        return this.myErrorSeverity;
    }

    public String getMessage() {
        return this.myString;
    }

}
