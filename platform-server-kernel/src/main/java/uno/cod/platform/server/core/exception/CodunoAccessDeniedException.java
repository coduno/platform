package uno.cod.platform.server.core.exception;

public class CodunoAccessDeniedException extends CodunoException {
    public CodunoAccessDeniedException() {
    }

    public CodunoAccessDeniedException(String message) {
        super(message);
    }

    public CodunoAccessDeniedException(String message, Object[] args) {
        super(message, args);
    }

    public CodunoAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodunoAccessDeniedException(String message, Throwable cause, Object[] args) {
        super(message, cause, args);
    }

    public CodunoAccessDeniedException(Throwable cause) {
        super(cause);
    }

    public CodunoAccessDeniedException(Throwable cause, Object[] args) {
        super(cause, args);
    }

    public CodunoAccessDeniedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public CodunoAccessDeniedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Object[] args) {
        super(message, cause, enableSuppression, writableStackTrace, args);
    }
}
