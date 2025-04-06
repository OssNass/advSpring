package io.ossnass.advSpring.annotations;

import io.ossnass.advSpring.DtoMapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides information about the controller including
 * <ol>
 *     <li>the type of the entity class</li>
 *     <li>the mapper to map from entity to dto</li>
 *     <li>
 *         THe following methods which allows ypu control the ability to enable add/batch add, edit/batch edit and
 *         delete/batch delete
 *         <ul>
 *             <li>{@link ControllerInfo#disableAdd()} to disable the adding of a new entity, false by default</li>
 *
 *             <li>{@link ControllerInfo#disableEdit()} to disable the editing of an existing entity, false by
 *             default</li>
 *             <li>{@link ControllerInfo#disableDelete()} to disable the  deleting of an existing entity, false by
 *             default</li>
 *         </ul>
 *     </li>
 * </ol>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ControllerInfo {

    /**
     * Specifies the DTO mapper class
     *
     * @return the actual mapper class
     */
    Class<? extends DtoMapper<?, ?>> mapper();

    /**
     * true to disable adding, false to enable
     *
     * @return the status of adding
     */
    boolean disableAdd() default false;

    /**
     * true to disable edit, false to enable
     *
     * @return the status of batch edit
     */
    boolean disableEdit() default false;

    /**
     * true to disable delete, false to enable
     *
     * @return the status of deleting
     */
    boolean disableDelete() default false;

//    /**
//     * true to disable soft delete, false to enable
//     *
//     * @return the status of batch deleting
//     */
//    boolean disableSoftDelete() default false;

}
