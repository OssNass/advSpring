package io.ossnass.advSpring.annotations;

import io.ossnass.advSpring.operators.Filter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *      Provides information about the services:
 *      <ol>
 *      <li>id: the services id, used to link the service to its {@link Filter} using {@link FilterInfo} and sorting
 *      methods using
 *      {@link FilterInfo} and {@link  SortInfo}</li>
 *      <li>entityClass: the entity class used by the services</li>
 *      </ol>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ServiceInfo {
    /**
     * The id of the service
     * <p>
     * used by {@link FilterInfo} and {@link SortInfo} to link the filter and sorter to the service
     *
     * @return the id of the service
     */
    String id();

    /**
     * The entity class of the service
     *
     * @return the entity class of the service
     */
    Class<?> entityClass();
}
