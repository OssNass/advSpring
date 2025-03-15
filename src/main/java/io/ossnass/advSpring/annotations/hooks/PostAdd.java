package io.ossnass.advSpring.annotations.hooks;

import io.ossnass.advSpring.CRUDService;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When a function is annotated with this annotation inside {@link CRUDService} class, the function will be called after calling the add function in said service
 * <p>
 * Supports multiple functions with the same annotation having different order value to set with function should be executed first
 * <p>
 * The function of postAdd must accept 2 parameters, first one for input of the same class the entity and the second for input of type Object, the return value must be the entity after being modified by the data inside the second parameter
 * <code>
 * void postAdd(Entity e,Object o){
 * //do something
 * }
 * </code>
 * <p>
 * <p>
 * If you need to throw an exception, please use a class extending  {@link org.springframework.web.client.HttpStatusCodeException} or {@link org.springframework.web.server.ResponseStatusException} to pass the error to the client
 * The reason behind this structure is simple, sometimes we want to process data inside the entity post add/edit and
 * as a result it will be passed from functions annotated {@link PreAdd} with the same order as the current postAdd hook
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostAdd {
    /**
     * The order of the  executed function
     * If 2 functions have the same order, an exception will be thrown
     *
     * @return the order of execution of the function
     */
    int value() default 1;
}
