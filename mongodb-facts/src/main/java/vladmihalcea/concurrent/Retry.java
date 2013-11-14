package vladmihalcea.concurrent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Retry - mark a given method for retrying.
 *
 * @author Vlad Mihalcea
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Retry {

    Class<? extends Exception>[] on();

    int times() default 1;
}
