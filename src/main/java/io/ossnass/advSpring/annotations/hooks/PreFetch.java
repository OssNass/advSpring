package io.ossnass.advSpring.annotations.hooks;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to handle the event before executing
 * {@link io.ossnass.advSpring.ReadOnlyService#all(String[], String[], String[], Long, Long, String)}
 * and {@link io.ossnass.advSpring.ReadOnlyService#count(String[], String[], String[])}
 *
 * <p>
 * Useful to add required filters. sometimes you need to insert filter based on user roles
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PreFetch {
    /**
     * The order of the  executed function
     * If 2 functions have the same order, an exception will be thrown
     *
     * @return the order of execution of the function
     */
    int value() default 0;
}
