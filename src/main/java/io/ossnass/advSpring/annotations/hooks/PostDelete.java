package io.ossnass.advSpring.annotations.hooks;

import io.ossnass.advSpring.CRUDService;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When a function is annotated with this annotation inside {@link CRUDService} class, the function will be called after calling the delete function in said service
 * <p>
 * Supports multiple functions with the same annotation having different order value to set with function should be executed first
 * <p>
 * The function of postDelete must accept 1 parameter for input of the same class the entity returns void
 * <code>
 * void postDelete(Entity e){
 * // do something
 * }
 * </code>
 * <p>
 * <p>
 * If you need to throw an exception, please use a class extending  {@link org.springframework.web.client.HttpStatusCodeException} or {@link org.springframework.web.server.ResponseStatusException} to pass the error to the client
 * The reason behind this structure is simple, sometimes we want to process data inside the entity after we delete it
 * <p>
 * This hook is independent of {@link PreDelete} and the order here is not linked to the order in that annotation
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostDelete {
    /**
     * The order of the  executed function
     * If 2 functions have the same order, an exception will be thrown
     *
     * @return the order of execution of the function
     */
    int value() default 1;
}
