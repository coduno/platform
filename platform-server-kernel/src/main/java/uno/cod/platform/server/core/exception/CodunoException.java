package uno.cod.platform.server.core.exception;

/*
*   This is the base class we use for all exceptions that we throw.
*   It wraps the behaviour of java.lang.Exception and a list of arguments
*   that are passed to the messageSource to build appropriate exception messages
* */
public abstract class CodunoException extends RuntimeException {
    private Object[] args;

    public CodunoException() {
    }

    public CodunoException(String message) {
        super(message);
    }

    public CodunoException(String message, Object[] args) {
        super(message);
        this.args = args;
    }

    public CodunoException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodunoException(String message, Throwable cause, Object[] args) {
        super(message, cause);
        this.args = args;
    }

    public CodunoException(Throwable cause) {
        super(cause);
    }

    public CodunoException(Throwable cause, Object[] args) {
        super(cause);
        this.args = args;
    }

    public CodunoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public CodunoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Object[] args) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.args = args;
    }

    public Object[] getArgs() {
        return args;
    }
}
