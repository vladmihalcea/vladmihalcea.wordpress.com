package vladmihalcea.customerlock;

import java.util.concurrent.Callable;

/**
 * CallableException - Exception thrown during a Callable execution
 *
 * @author Vlad Mihalcea
 */
public class CallableException extends RuntimeException {

    private final Callable callable;

    public CallableException(Callable callable) {
        this.callable = callable;
    }

    public CallableException(String message, Callable callable) {
        super(message);
        this.callable = callable;
    }

    public CallableException(String message, Throwable cause, Callable callable) {
        super(message, cause);
        this.callable = callable;
    }

    public CallableException(Throwable cause, Callable callable) {
        super(cause);
        this.callable = callable;
    }

    public Callable getCallable() {
        return callable;
    }
}
