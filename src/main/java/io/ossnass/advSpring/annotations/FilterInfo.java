package io.ossnass.advSpring.annotations;

import io.ossnass.advSpring.operators.Operation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation must be placed on the class that implements the interface (@link Filter}.
 * <p>
 * It contains 3 mandatory data inputs:
 * <ol>
 *     <li>the {@link FilterInfo#fieldName()} to indicate the field on which the filter is applied (string only no
 *     code)</li>
 *     <li>the {@link FilterInfo#operation()}, combined with the filed name it creates the filter id</li>
 *     <li>the {@link FilterInfo#serviceId()} linking this filter to a controller by using
 *     {@link ServiceInfo#id()} field</li>
 * </ol>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FilterInfo {
    /**
     * the field name on which the filter is applied, part of the filter Id
     *
     * @return the field name on which the filter is applied
     */
    String fieldName();

    /**
     * The type of the operation performed on the field, part of the field Id
     *
     * @return The type of the operation performed on the field
     */
    Operation operation();

    /**
     * The service to which this filter is linked
     *
     * @return The serviceId to which this filter is linked
     */
    String serviceId();
}
