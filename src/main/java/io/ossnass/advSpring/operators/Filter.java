package io.ossnass.advSpring.operators;

import io.ossnass.advSpring.CRUDService;
import io.ossnass.advSpring.JinqStreamService;
import io.ossnass.advSpring.Loggable;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.jinq.jpa.JPAJinqStream;

/**
 * The filter object that is applied on {@link JPAJinqStream} when filtering in
 * {@link CRUDService}
 * <p>
 * To work, must be annotated with {@link io.ossnass.advSpring.annotations.FilterInfo} annotation
 * <p>
 * If you want to mark the filter as mandatory then you need to override {@link Filter#isMandatory()}  to make it
 * return true
 * <p>
 * You have the ability to access hibernate search session by the use of {@link Filter#searchSession}
 *
 * @param <Entity> the entity applied to
 */
public abstract class Filter<Entity> extends Loggable {
    /**
     * The search session, in case you need to access hibernate search
     */
    protected SearchSession searchSession;
    protected JinqStreamService streamService;

    public Filter(SearchSession searchSession, JinqStreamService streamService) {
        this.searchSession = searchSession;
        this.streamService = streamService;
    }

    /**
     * The actual filtering function
     *
     * @param stream the stream to apply the filter on
     * @param value  the filter value in string
     * @return the stream after applying the filter
     */
    public abstract JPAJinqStream<Entity> addFilter(JPAJinqStream<Entity> stream,
                                                    String value);

    /**
     * Indicates weather this filter is mandatory or not, if true then the
     * {@link CRUDService} expects to find the filter in the request parameters,
     * and will throw an error if not found.
     * <p>
     * By default, will return false
     *
     * @return true if mandatory, false otherwise
     */
    public boolean isMandatory() {
        return false;
    }

}
