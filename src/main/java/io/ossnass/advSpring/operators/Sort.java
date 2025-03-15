package io.ossnass.advSpring.operators;

import io.ossnass.advSpring.CRUDService;
import io.ossnass.advSpring.Loggable;
import org.jinq.jpa.JPAJinqStream;

/**
 * Apply a sorting to a {@link JPAJinqStream} in {@link CRUDService}
 * <p>
 * For it to work must be annotated with {@link io.ossnass.advSpring.annotations.SortInfo} annotation
 *
 * @param <Entity> the entity(table) we are sorting
 */
public abstract class Sort<Entity> extends Loggable {
    /**
     * The actual sorting function
     *
     * @param stream the stream to apply the sorting on
     * @return the stream after applying the sorting
     */
    public abstract JPAJinqStream<Entity> sort(JPAJinqStream<Entity> stream);
}
