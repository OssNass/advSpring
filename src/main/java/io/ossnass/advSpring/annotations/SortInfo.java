package io.ossnass.advSpring.annotations;


import io.ossnass.advSpring.operators.SortingDirection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation must be placed on the class that implements the interface (@link Filter}.
 * <p>
 * It contains 3 mandatory data inputs:
 * <ol>
 *     <li>the {@link SortInfo#fieldName()} to indicate the field on which the sort is applied (string only no code)
 *     </li>
 *     <li>the {@link SortInfo#sortingDirection()} ()}, combined with the filed name it creates the sort Id</li>
 *     <li>the {@link SortInfo#controllerId()} linking this sort to a controller by using {@link ServiceInfo#id()}
 *     field</li>
 * </ol>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SortInfo {
    /**
     * the field name on which the filter is applied, part of the filter Id
     *
     * @return the field name on which the filter is applied
     */
    String fieldName();

    /**
     * The direction of the sorting performed on the field, part of the field Id
     *
     * @return The direction of the sorting performed on the field
     */
    SortingDirection sortingDirection();

    /**
     * The controller to which this filter is linked
     *
     * @return The controller to which this filter is linked
     */
    String controllerId();
}
