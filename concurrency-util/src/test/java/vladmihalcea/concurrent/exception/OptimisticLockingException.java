package vladmihalcea.concurrent.exception;

/**
 * OptimisticLockingException - OptimisticLockingException
 *
 * @author Vlad Mihalcea
 */
public class OptimisticLockingException extends RuntimeException {

    public OptimisticLockingException() {
    }

    public OptimisticLockingException(String message) {
        super(message);
    }

    public OptimisticLockingException(String message, Throwable cause) {
        super(message, cause);
    }

    public OptimisticLockingException(Throwable cause) {
        super(cause);
    }
}
