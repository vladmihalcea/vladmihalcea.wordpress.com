package vladmihalcea.concurrent;

import java.lang.annotation.*;

/**
 * Retry - mark a given method for retrying.
 *
 * @author Vlad Mihalcea
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface Retry {

    Class<? extends Exception>[] on();

    int times() default 1;

    boolean failInTransaction() default true;
}
