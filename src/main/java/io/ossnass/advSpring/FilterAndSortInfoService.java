package io.ossnass.advSpring;

import io.github.classgraph.ClassGraph;
import io.ossnass.advSpring.annotations.FilterInfo;
import io.ossnass.advSpring.annotations.ServiceInfo;
import io.ossnass.advSpring.annotations.SortInfo;
import io.ossnass.advSpring.operators.Filter;
import io.ossnass.advSpring.operators.FilterInfoInternal;
import io.ossnass.advSpring.operators.Sort;
import io.ossnass.advSpring.operators.SortInfoInternal;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * This service is used by {@link ReadOnlyService} and @{@link CRUDService} when initializing the controllers.
 * It essentially scans the class path for filter classes
 * annotated with {@link FilterInfo} and sort classes annotated with {@link SortInfo}
 * and links it with appropriate controllers
 */
@Service
public class FilterAndSortInfoService {
    private final SearchSession searchSession;
    private final Map<String, Set<FilterInfoInternal>> filters;
    private final Map<String, Set<SortInfoInternal>> sorts;
    private final Logger logger = LoggerFactory.getLogger(FilterAndSortInfoService.class);
    private final JinqStreamService streamService;
    private List<FilterInfoInternal> _filters;
    private List<SortInfoInternal> _sorts;

    public FilterAndSortInfoService(SearchSession searchSession, JinqStreamService streamService) {
        this.searchSession = searchSession;
        this.streamService = streamService;

        filters = new HashMap<>();
        sorts = new HashMap<>();
        //in order to optimize performance we scan for all filters then all sorts then all controllers and match every thing
        logger.info("Filters and Sorts initialization process started");
        this.scanAllPackagesForFilters();
        this.scanAllPackagesForSort();
        this.matchAllData();
        this._filters = null;
        this._sorts = null;
        logger.info("Filters and Sorts initialization process completed");
    }

    private void matchAllData() {
        logger.info("Starting the matching process between sorts,filters and controllers");
        try (var scan = new ClassGraph().enableClassInfo()
                .enableAnnotationInfo()
                .scan()) {
            for (var classInfo : scan.getClassesWithAnnotation(ServiceInfo.class)) {
                logger.debug("Found controller: " + classInfo.loadClass()
                        .getCanonicalName());
                var annotation = (ServiceInfo) classInfo.getAnnotationInfo(ServiceInfo.class.getCanonicalName())
                        .loadClassAndInstantiate();
                Set<SortInfoInternal> controllerSortList = new HashSet<SortInfoInternal>();
                if (this.sorts.containsKey(annotation.id()))
                    controllerSortList = this.sorts.get(annotation.id());
                else this.sorts.put(annotation.id(), controllerSortList);
                var cSortList = controllerSortList;
                this._sorts.stream()
                        .filter(item -> item.getControllerId()
                                .equals(annotation.id())
                                && cSortList.stream()
                                .noneMatch(value -> value.getId()
                                        .equals(item.getId())))
                        .forEach(cSortList::add);
                Set<FilterInfoInternal> controllerFilterList = new HashSet<FilterInfoInternal>();
                if (this.filters.containsKey(annotation.id()))
                    controllerFilterList = this.filters.get(annotation.id());
                else this.filters.put(annotation.id(), controllerFilterList);
                var cFilterList = controllerFilterList;
                this._filters.stream()
                        .filter(item -> item.getServiceId()
                                .equals(annotation.id())
                                && cFilterList.stream()
                                .noneMatch(value -> value.getId()
                                        .equals(item.getId())))
                        .forEach(cFilterList::add);
            }
        }
        logger.info("Matching process completed");
    }

    private void scanAllPackagesForSort() {
        logger.info("Scanning for sorts");
        //we  scan for the all the sorts
        this._sorts = new ArrayList<SortInfoInternal>();
        try (var scan = new ClassGraph().enableClassInfo()
                .enableAnnotationInfo()
                .scan()) {
            for (var classInfo : scan.getClassesWithAnnotation(SortInfo.class)) {
                var data = (SortInfo) classInfo.getAnnotationInfo(SortInfo.class.getCanonicalName())
                        .loadClassAndInstantiate();
                logger.debug("Found sort: " + classInfo.loadClass()
                        .getCanonicalName());
                this._sorts.add(new SortInfoInternal()
                        .setId((data.fieldName() + data.sortingDirection()
                                .getValue()).toLowerCase())
                        .setControllerId(data.serviceId())
                        .setSortClass((Class<? extends Sort>) classInfo.loadClass()));

            }
        }
        logger.info("Sort scanning completed");

    }

    private void scanAllPackagesForFilters() {
        //we  scan for the all the filters
        logger.info("Scanning for filters");
        this._filters = new ArrayList<FilterInfoInternal>();
        try (var scan = new ClassGraph().enableClassInfo()
                .enableAnnotationInfo()
                .scan()) {
            for (var classInfo : scan.getClassesWithAnnotation(FilterInfo.class)) {
                var data = (FilterInfo) classInfo.getAnnotationInfo(FilterInfo.class.getCanonicalName())
                        .loadClassAndInstantiate();
                logger.debug("Found filter: " + classInfo.loadClass()
                        .getCanonicalName());
                this._filters.add(new FilterInfoInternal()
                        .setId((data.fieldName() + data.operation()
                                .getValue()).toLowerCase())
                        .setServiceId(data.serviceId())
                        .setFilterClass((Class<? extends Filter>) classInfo.loadClass()));
            }
        }
        logger.info("Filter scanning completed");
    }

    /**
     * Returns all the sorts of a certain controller
     *
     * @param controllerId of the controller for which you need it sorts
     * @return map of sorts with key being the name+direction and the class itself as the value
     */
    public Map<String, Sort> getSorts(String controllerId) {
        var result = new HashMap<String, Sort>();
        var sorts = this.sorts.get(controllerId);
        if (sorts != null)
            sorts.forEach(item -> {
                try {
                    result.put(item.getId(), item.getSortClass()
                            .getDeclaredConstructor()
                            .newInstance());
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    logger.error(
                            "Cannot create a sort of id " + item.getId() + " for controller " + item.getControllerId() + " error " + e.getMessage());
                }
            });
        return result;
    }

    /**
     * Returns all the fitlers of a certain controller
     *
     * @param controllerId of the controller for which you need it filters
     * @return map of filters with key being the name+operation and the class itself as the value
     */
    public Map<String, Filter> getFilters(String controllerId) {
        var result = new HashMap<String, Filter>();
        var filters = this.filters.get(controllerId);
        if (filters != null)
            filters.forEach(item -> {
                try {
                    result.put(item.getId(), item.getFilterClass()
                            .getDeclaredConstructor(SearchSession.class, JinqStreamService.class)
                            .newInstance(searchSession, streamService));
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    logger.error(
                            "Cannot create a filter of id " + item.getId() + " for controller " + item.getServiceId() + " error " + e.getMessage());
                }
            });
        return result;
    }
}
