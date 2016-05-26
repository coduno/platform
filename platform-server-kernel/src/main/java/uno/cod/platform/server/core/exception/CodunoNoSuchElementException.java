package uno.cod.platform.server.core.exception;

public class CodunoNoSuchElementException extends CodunoException {
    public CodunoNoSuchElementException() {
    }

    public CodunoNoSuchElementException(String message) {
        super(message);
    }

    public CodunoNoSuchElementException(String message, Object[] args) {
        super(message, args);
    }

    public CodunoNoSuchElementException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodunoNoSuchElementException(String message, Throwable cause, Object[] args) {
        super(message, cause, args);
    }

    public CodunoNoSuchElementException(Throwable cause) {
        super(cause);
    }

    public CodunoNoSuchElementException(Throwable cause, Object[] args) {
        super(cause, args);
    }

    public CodunoNoSuchElementException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public CodunoNoSuchElementException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Object[] args) {
        super(message, cause, enableSuppression, writableStackTrace, args);
    }
}
