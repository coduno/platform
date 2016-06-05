package uno.cod.platform.server.core.exception;

public class CodunoIllegalArgumentException extends CodunoException {
    public CodunoIllegalArgumentException() {
    }

    public CodunoIllegalArgumentException(String message) {
        super(message);
    }

    public CodunoIllegalArgumentException(String message, Object[] args) {
        super(message, args);
    }

    public CodunoIllegalArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodunoIllegalArgumentException(String message, Throwable cause, Object[] args) {
        super(message, cause, args);
    }

    public CodunoIllegalArgumentException(Throwable cause) {
        super(cause);
    }

    public CodunoIllegalArgumentException(Throwable cause, Object[] args) {
        super(cause, args);
    }

    public CodunoIllegalArgumentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public CodunoIllegalArgumentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Object[] args) {
        super(message, cause, enableSuppression, writableStackTrace, args);
    }
}
