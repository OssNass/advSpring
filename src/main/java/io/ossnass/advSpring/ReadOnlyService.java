package io.ossnass.advSpring;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import io.ossnass.advSpring.annotations.ServiceInfo;
import io.ossnass.advSpring.annotations.hooks.FilterContainer;
import io.ossnass.advSpring.annotations.hooks.PostFetch;
import io.ossnass.advSpring.annotations.hooks.PreFetch;
import io.ossnass.advSpring.operators.Filter;
import io.ossnass.advSpring.operators.Sort;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.jinq.jpa.JPAJinqStream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * This class is used to create a server to read entities from the system
 * <p>
 * Supports the following functions:
 * <ul>
 *     <li>{@link ReadOnlyService#all(String[], String[], String[], Long, Long, String)}: returns a list of entities</li>
 *     <li>{@link ReadOnlyService#getOnes(String)}: returns a list of entities based on the passed ids</li>
 *     <li>{@link ReadOnlyService#count(String[], String[], String[])}: returns the number of entities based on the passed filters</li>
 * </ul>
 * <p>
 * Supports the following hooks:
 * <ul>
 *     <li>{@link PreFetch}: used to add required filters before fetching the stream using {@link ReadOnlyService#all(String[], String[], String[], Long, Long, String)} and {@link ReadOnlyService#getOnes(String)}</li>
 *     <li>{@link PostFetch}: used to add post-fetch hooks post {@link ReadOnlyService#all(String[], String[], String[], Long, Long, String)} and {@link ReadOnlyService#getOnes(String)}</li>
 * </ul>
 *
 * @param <Entity> the class of the entity
 * @param <Id>     the id of the entity
 */
public abstract class ReadOnlyService<Entity extends Deletable, Id> extends Loggable {
    protected final EntityManager em;
    protected final SearchSession searchSession;
    protected final JinqStreamService streamService;
    protected final JpaRepository<Entity, Id> repository;
    protected final Class<Entity> entityClass;
    protected final Map<Class<? extends Annotation>, Map<Integer, Method>> hooks;
    /**
     * Stores all the filters applicable to this controller
     */
    protected Map<String, Filter<Entity>> filters;
    /**
     * Stores all the sorts applicable to this controller
     */
    protected Map<String, Sort<Entity>> sorts;


    public ReadOnlyService(FilterAndSortInfoService filterService, EntityManager em, SearchSession searchSession,
                           JinqStreamService streamService, JpaRepository<Entity, Id> repository) {
        this.em = em;
        this.searchSession = searchSession;
        this.streamService = streamService;
        this.repository = repository;
        this.hooks = new HashMap<>();
        var serviceInfo = this.getClass()
                              .getAnnotation(ServiceInfo.class);
        Assert.notNull(serviceInfo,
                "A service must be annotated with ServiceInfo");

        Assert.notNull(serviceInfo.id(),
                "A service must have id");
        Assert.notNull(serviceInfo.entityClass(), "A service must have an entity class info");
        this.entityClass = (Class<Entity>) serviceInfo.entityClass();
        executePreFetchHooks(filterService, serviceInfo);

        hooks.put(PreFetch.class, new HashMap<>());
        hooks.put(PostFetch.class, new HashMap<>());
        this.loadHooks(new HashSet<>(Arrays.asList(PreFetch.class, PostFetch.class)));
    }

    private void executePreFetchHooks(FilterAndSortInfoService filterService, ServiceInfo serviceInfo) {
        var tmpFilters = filterService.getFilters(serviceInfo.id());
        //Will now move filters with isMandatory true to the mandatory filter map, this will insure we will not add the
        // same filter twice
        filters = new HashMap<>();

        tmpFilters.forEach(filters::put);
        sorts = new HashMap<>();
        filterService.getSorts(serviceInfo.id())
                     .forEach((key, value) -> sorts.put(key,
                             value));
    }


    /**
     * The purpose of this function is to validate the filters passed to controller to exists with the supported
     * operations.
     * <p>
     * It will search inside {@link CRUDService#filters} for the filters.
     * <p>
     * Also, it will validate the lengths of the 3 input arrays and makes sure they are of the same size
     *
     * @param filters          the filter names
     * @param filterOperations the filter operations
     * @param filterValues     the filter values
     * @return true if the filters are valid, false otherwise
     */
    protected boolean validateFilters(String[] filters,
                                      String[] filterOperations,
                                      String[] filterValues) {
        //validating filter sizes
        if (filters.length != filterOperations.length || filters.length != filterValues.length)
            return false;
        //arrays of filter indexes not found in the first array
        var indexes = new ArrayList<String>();
        //we look for  mandatory filters
        var tmpMap = new HashMap<String, Filter<Entity>>();
        this.filters.entrySet()
                    .stream()
                    .filter(item -> item.getValue()
                                        .isMandatory())
                    .forEach(item -> tmpMap.put(item.getKey(),
                            item.getValue()));

        for (int i = 0; i < filters.length; i++) {
            //Now we check if the filter is in mandatory filters
            //if it is in mandatory filters and not exists in the filters we pass, return false
            //else we check for the filters in the standard filter list
            //this way filters with dynamic mandatory value can have the dynamic neediness
            if (tmpMap.containsKey((filters[i] + filterOperations[i]).toLowerCase()))
                //the filter is not found
                continue;
            if (this.filters.containsKey((filters[i] + filterOperations[i]).toLowerCase()))
                continue;
            indexes.add((filters[i] + filterOperations[i]).toLowerCase());
        }
        //so basically we are populating the indexes list with all the not found filters, and the result is valid filter
        // if the final size of it is 0
        return indexes.isEmpty();
    }


    /**
     * Adds filters to the stream
     *
     * @param stream           the stream to add to
     * @param filters          the filter names
     * @param filterOperations the filter operations
     * @param filterValues     the filter values
     * @return the stream with filters applied to
     */
    protected JPAJinqStream<Entity> applyFilters(JPAJinqStream<Entity> stream,
                                                 String[] filters,
                                                 String[] filterOperations,
                                                 String[] filterValues) {
        for (int i = 0; i < filters.length; i++) {
            var filter = (filters[i] + filterOperations[i]).toLowerCase();
            if (this.filters.containsKey(filter))
                stream = this.filters.get(filter)
                                     .addFilter(stream,
                                             filterValues[i]);
        }
        return stream;
    }

    /**
     * This functions combine all the filters and sorting method into a single stream.
     * <p>
     * it's shared between {@link CRUDService#count(String[], String[], String[])} and
     * {@link CRUDService#all(String[], String[], String[], Long, Long, String)} that is why it is extracted
     * into a self-contained function
     *
     * @param stream           the stream we are working on
     * @param filters          the filter names
     * @param filterOperations the filter operations
     * @param filterValues     the filter values
     * @param sorting          the sorting method keys
     * @return the stream with all the sorting and filters add to it
     * @throws ResponseStatusException with code 400 with the following messages:
     *                                 <ul>
     *                                     <li>"Bad filter": in case of error in the filters</li>
     *                                     <li>"Bad sort": in case of error in the sorting methods</li>
     *                                     <li>"Bad pagination": when either start or count is null and the other is
     *                                     with value </li>
     *                                 </ul>
     */
    protected JPAJinqStream<Entity> combineStream(JPAJinqStream<Entity> stream,
                                                  String[] filters,
                                                  String[] filterOperations,
                                                  String[] filterValues,
                                                  String sorting,
                                                  Long start,
                                                  Long count) {
        // handles prefetch hook
        if (!hooks.get(PreFetch.class).isEmpty()) {
            var container = executePreFetchHooks(filters, filterOperations, filterValues);
            filters = container.getFilters().toArray(new String[0]);
            filterOperations = container.getFilterOperations().toArray(new String[0]);
            filterValues = container.getFilterValues().toArray(new String[0]);
        }
        //if the filters are valid we continue
        //the filters array must be larger than 0 and all the filters must pass
        stream = processFilters(stream, filters, filterOperations, filterValues);
        //we are now doing the same thing for sort
        stream = proceessSorters(stream, sorting);
        //handling start and count
        if ((start == null && count != null) || (start != null && count == null))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Bad pagination");
        if (start != null)
            stream = stream.skip(start)
                           .limit(count);
        if (logger.isDebugEnabled())
            logger.debug(stream.getDebugQueryString());
        return stream.where(item -> !item.getDeleted());
    }

    /**
     * This function is used to execute the post-fetch hooks
     *
     * @param result the result of the stream
     * @param isAll  true if the stream is all, false if it is a paginated stream
     * @return the result of the stream
     * @throws IllegalAccessException    when the method is not accessible
     * @throws InvocationTargetException when the method calling throws an exception
     */
    protected final List<Entity> executePostFetchHooks(List<Entity> result, boolean isAll) {
        if (!hooks.get(PostFetch.class).isEmpty()) {
            var sortedMethodsKeys = hooks.get(PostFetch.class).keySet().stream().sorted().toList();
            for (var key : sortedMethodsKeys) {
                var method = hooks.get(PostFetch.class).get(key);
                try {
                    result = (List<Entity>) method.invoke(this, result, isAll);
                } catch (IllegalAccessException | InvocationTargetException ex) {
                    logger.error("Error during processing hooks in {} cannot call the function, message {}",
                            this.getClass().getSimpleName(), ex.getMessage(), ex);
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
                } catch (ResponseStatusException | HttpStatusCodeException ex) {
                    var code = 0;
                    if (ex instanceof ResponseStatusException)
                        code = ((ResponseStatusException) ex).getStatusCode().value();
                    else code = ((HttpStatusCodeException) ex).getStatusCode().value();
                    logger.error("Error during processing hooks in {} cannot call the function, code {}",
                            this.getClass().getSimpleName(), code);
                    throw ex;
                }
            }
        }
        return result;
    }


    /**
     * This function is used to execute the pre-fetch hooks
     *
     * @param filters          the filter names
     * @param filterOperations the filter operations
     * @param filterValues     the filter values
     * @return the container with the filters applied
     * @throws ResponseStatusException   with code 400 with the following messages:
     *                                   <ul>
     *                                       <li>"Bad filter": in case of error in the filters</li>
     *                                   </ul>
     * @throws IllegalAccessException    when the method is not accessible
     * @throws InvocationTargetException when the method calling throws an exception
     */
    protected final FilterContainer executePreFetchHooks(String[] filters, String[] filterOperations,
                                                         String[] filterValues) {
        var container = new FilterContainer();
        if (filters != null) {
            container.populateFilters(filters, filterOperations, filterValues);
            var sortedMethodsKeys = hooks.get(PreFetch.class).keySet().stream().sorted().toList();
            for (var key : sortedMethodsKeys) {
                var method = hooks.get(PreFetch.class).get(key);
                try {
                    container = (FilterContainer) method.invoke(this, container);
                } catch (IllegalAccessException | InvocationTargetException ex) {
                    logger.error("Error during processing hooks in {} cannot call the function, message {}",
                            this.getClass().getSimpleName(), ex.getMessage(), ex);
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
                } catch (ResponseStatusException | HttpStatusCodeException ex) {
                    var code = 0;
                    if (ex instanceof ResponseStatusException)
                        code = ((ResponseStatusException) ex).getStatusCode().value();
                    else code = ((HttpStatusCodeException) ex).getStatusCode().value();
                    logger.error("Error during processing hooks in {} cannot call the function, code {}",
                            this.getClass().getSimpleName(), code);
                    throw ex;
                }
            }
        }
        return container;
    }

    /**
     * This function is used to sort the stream
     *
     * @param stream  the stream to sort
     * @param sorting the sorting method keys
     * @return the stream with all the sorting and filters add to it
     * @throws ResponseStatusException with code 400 with the following messages:
     *                                 <ul>
     *                                     <li>"Bad sort": in case of error in the sorting methods</li>
     *                                 </ul>
     */
    private JPAJinqStream<Entity> proceessSorters(JPAJinqStream<Entity> stream, String sorting) {
        if (sorting != null && !sorting.trim()
                                       .isEmpty()) {
            sorting = sorting.toLowerCase();
            if (this.sorts.containsKey(sorting))
                stream = sorts.get(sorting)
                              .sort(stream);
            else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Bad sort");
        }
        return stream;
    }

    /**
     * This function is used to filter the stream
     *
     * @param stream           the stream we are working on
     * @param filters          the filter names
     * @param filterOperations the filter operations
     * @param filterValues     the filter values
     * @return the stream with filters applied to
     * @throws ResponseStatusException with code 400 with the following messages:
     *                                 <ul>
     *                                     <li>"Bad filter": in case of error in the filters</li>
     *                                 </ul>
     */

    private JPAJinqStream<Entity> processFilters(JPAJinqStream<Entity> stream, String[] filters,
                                                 String[] filterOperations, String[] filterValues) {
        if (filters != null && filters.length > 0)
            if (validateFilters(filters,
                    filterOperations,
                    filterValues))
                stream = applyFilters(stream,
                        filters,
                        filterOperations,
                        filterValues);
            else
                //in case of a bad filter but not empty filter throw an error
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Bad filter");
        return stream;
    }

    /**
     * Returns the count of elements in the table based on the passed filters,
     * <p>
     * and thus will throw the same errors
     *
     * @param filters          the filter names
     * @param filterOperations the filter operations
     * @param filterValues     the filter values
     * @return the number of elements in the table
     */
    public Long count(String[] filters,
                      String[] filterOperations,
                      String[] filterValues) {
        var stream = streamService.createCustomStream(entityClass);
        stream = combineStream(stream,
                filters,
                filterOperations,
                filterValues,
                null,
                null,
                null);
        Long res = stream.count();
        stream.close();
        return res;
    }

    /**
     * Retrieve al the entities from the database that matches with the requested filters, sorted by the request
     * sorting method and paged using the requested pagination
     * <p>
     * and thus will throw the same errors
     *
     * @param filters          the filter names
     * @param filterOperations the filter operations
     * @param filterValues     the filter values
     * @param start            the start value of the pagination (when to start fetching)
     * @param count            the count value of the pagination (the number of elements to fetch)
     * @param sort             the name of the sorting method
     * @return a list of rows (DTO objects) from the table
     */
    public List<Entity> all(String[] filters,
                            String[] filterOperations,
                            String[] filterValues,
                            Long start,
                            Long count,
                            String sort
    ) {
        var stream = streamService.createCustomStream(entityClass);
        stream = combineStream(stream,
                filters,
                filterOperations,
                filterValues,
                sort,
                start,
                count);

        //mapping the entities to DTOs
        var result = stream.toList();
        if (!hooks.get(PostFetch.class).isEmpty())
            result = executePostFetchHooks(result, true);
        if (logger.isDebugEnabled())
            logger.debug(stream.getDebugQueryString());
        stream.close();
        return result;
    }

    /**
     * Returns a set of entities based on the passed identities.
     * <p>
     * Useful to an entity or entities based on id\ids
     *
     * @param idString the ids inform of string (more than one identity and all parts of each identity)
     * @return a list of matching entities as DTOs
     */
    public List<Entity> getOnes(String idString) {
        List<Id> ids = convertStringToIds(idString.split(","));
        var result = repository.findAllById(ids).stream().filter(item -> !item.getDeleted()).toList();
        if (!hooks.get(PostFetch.class).isEmpty())
            result = executePostFetchHooks(result, false);
        return result;
    }

    public List<Id> convertStringToIds(String[] idString) {
        List<Id> ids = new ArrayList<>();
        for (int i = 0; i < idString.length; i += idFieldCount()) {
            var id = partsToIdClass(Arrays.copyOfRange(idString,
                    i,
                    i + idFieldCount()));
            ids.add(id);
        }
        return ids;
    }

    /**
     * loads hooks methods into the matching maps with the correct order
     *
     * @throws DuplicateHookOrderException if a method with the same order exists in the same hook list
     */
    protected final void loadHooks(Set<Class<? extends Annotation>> hookClasses) {
        //TODO check methods 1 vs method 2
//        var hookClasses = hooks.keySet();
        for (var hookClass : hookClasses) {
            var methods = searchForAnnotatedFunction(hookClass);
            if (methods != null && !methods.isEmpty())
                for (var method : methods) {
                    //Method 1
                    var hookAnnotation = method.getAnnotation(hookClass);
                    try {
                        var valueMethod =hookAnnotation.annotationType().getDeclaredMethod("value");
                        var order = (Integer) valueMethod.invoke(hookAnnotation);
                        if (hooks.get(hookClass).containsKey(order)) {
                            logger.error("Error during loading hooks in {} order {} already exists list {}",
                                    this.getClass().getSimpleName(), order, hookClass.getSimpleName());
                            throw new DuplicateHookOrderException(order);
                        }
                        hooks.get(hookClass).put(order, method);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        logger.error("Cannot invoke value function in annotation {}, message{}",
                                hookAnnotation.getClass().getSimpleName(), e.getMessage(), e);
                    }
                    //Method 2
//                    if (hookAnnotation instanceof PreAdd) {
//                        hooks.get(hookClass).put(((PreAdd) hookAnnotation).value(), method);
//                        continue;
//                    }
//                    if (hookAnnotation instanceof PostAdd) {
//                        hooks.get(hookClass).put(((PostAdd) hookAnnotation).value(), method);
//                        continue;
//                    }
//                    if (hookAnnotation instanceof PreEdit) {
//                        hooks.get(hookClass).put(((PreEdit) hookAnnotation).value(), method);
//                        continue;
//                    }
//                    if (hookAnnotation instanceof PostEdit) {
//                        hooks.get(hookClass).put(((PostEdit) hookAnnotation).value(), method);
//                        continue;
//                    }
//                    if (hookAnnotation instanceof PreDelete) {
//                        hooks.get(hookClass).put(((PreDelete) hookAnnotation).value(), method);
//                        continue;
//                    }
//                    if (hookAnnotation instanceof PostDelete) {
//                        hooks.get(hookClass).put(((PostDelete) hookAnnotation).value(), method);
//                        continue;
//                    }
                }
        }
    }

    /**
     * This function is used to convert from an array of strings to a single
     * IdClass.
     * <p>
     * Used by {@link ReadOnlyService#getOnes(String)}
     *
     * @param idParts the string components of the id
     * @return the corresponding IdClass
     */
    protected abstract Id partsToIdClass(String[] idParts);

    /**
     * This function is called by {@link ReadOnlyService#getOnes(String)} to
     * assist in correctly parsing the string fields
     * in the list into viable IdClass objects by simply walking over the number of
     * fields each time in the String array;
     *
     * @return the amount of fields in the id class
     */
    protected abstract int idFieldCount();


    /**
     * Looks for annotated methods with a certain annotation in the class itself
     *
     * @param annotationClass the annotation to search for
     * @return a list of methods annotated with said annotation
     */
    private List<Method> searchForAnnotatedFunction(Class<? extends Annotation> annotationClass) {
        var methods = getClass().getDeclaredMethods();
        return Arrays.stream(methods).filter(method -> method.isAnnotationPresent(annotationClass)).toList();
    }

    /**
     * This function is used to get an entity by its id
     *
     * @param id the id of the entity
     * @return the entity
     * @throws EntityNotFoundException if the entity doesn't exist
     */
    public Entity getById(Id id) {
        return repository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

}
