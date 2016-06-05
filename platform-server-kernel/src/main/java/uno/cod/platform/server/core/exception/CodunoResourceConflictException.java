package uno.cod.platform.server.core.exception;

/**
 * used for data conflicts, like try to create a user that already exists
 */
public class CodunoResourceConflictException extends CodunoException {
    public CodunoResourceConflictException() {
    }

    public CodunoResourceConflictException(String message) {
        super(message);
    }

    public CodunoResourceConflictException(String message, Object[] args) {
        super(message, args);
    }

    public CodunoResourceConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodunoResourceConflictException(String message, Throwable cause, Object[] args) {
        super(message, cause, args);
    }

    public CodunoResourceConflictException(Throwable cause) {
        super(cause);
    }

    public CodunoResourceConflictException(Throwable cause, Object[] args) {
        super(cause, args);
    }

    public CodunoResourceConflictException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public CodunoResourceConflictException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Object[] args) {
        super(message, cause, enableSuppression, writableStackTrace, args);
    }
}
