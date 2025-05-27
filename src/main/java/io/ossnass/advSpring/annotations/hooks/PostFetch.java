package io.ossnass.advSpring.annotations.hooks;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to handle the event after executing
 * {@link io.ossnass.advSpring.ReadOnlyService#all(String[], String[], String[], Long, Long, String)}
 * and {@link io.ossnass.advSpring.ReadOnlyService#getOnes(String)} but before calling the map function
 *
 * <p>
 * Useful to add manual postprocessing for fetched elements
 *
 * The function must have the following signature:
 * <code>
 *     public List<Entity> postFetch(List<Entity> items,boolean isAll)
 * </code>
 * where:
 * <ul>
 *     <li>items are the items fetched from the database</li>
 *     <li>isAll true when called from {@link io.ossnass.advSpring.ReadOnlyService#all(String[], String[], String[], Long, Long, String)} and false when calling from {@link io.ossnass.advSpring.ReadOnlyService#getOnes(String)}} </li>
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PostFetch {

    /**
     * The order of the  executed function
     * If 2 functions have the same order, an exception will be thrown
     *
     * @return the order of execution of the function
     */
    int value() default 1;
}
