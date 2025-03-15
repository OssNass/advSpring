package io.ossnass.advSpring;

import io.ossnass.advSpring.annotations.ControllerInfo;
import org.mapstruct.factory.Mappers;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * This class is used to create a controller for a read only service
 * <p>
 * Allows to read entities from the system
 * <p>
 * Supports the following functions:
 * <ul>
 *     <li>{@link ReadOnlyController#all(String[], String[], String[], Long, Long, String)}: returns a list of entities</li>
 *     <li>{@link ReadOnlyController#getOnes(String)}: returns a list of entities based on the passed ids</li>
 *     <li>{@link ReadOnlyController#count(String[], String[], String[])}: returns the number of entities based on the passed filters</li>
 * </ul>
 *
 * @param <Entity>
 * @param <ID>
 * @param <Dto>
 */
public class ReadOnlyController<Entity extends Deletable, ID, Dto> extends Loggable {
    protected final DtoMapper<Entity, Dto> mapper;
    protected final ReadOnlyService<Entity, ID> service;
    protected final ControllerInfo controllerInfo;

    public ReadOnlyController(ReadOnlyService<Entity, ID> service) {
        this.service = service;
        this.controllerInfo = getClass().getAnnotation(ControllerInfo.class);
        Assert.notNull(controllerInfo, "A controller must be annotated with ControllerInfo");
        Assert.notNull(controllerInfo.mapper(), "A controller must have a mapper");
        this.mapper = (DtoMapper<Entity, Dto>) Mappers.getMapper(controllerInfo.mapper());
    }

    /**
     * Returns a list of entities based on the passed filters
     *
     * @param filter          the filter names
     * @param filterOperation the filter operations
     * @param filterValue     the filter values
     * @param start            the start value of the pagination (when to start fetching)
     * @param count            the count value of the pagination (the number of elements to fetch)
     * @param sort             the name of the sorting method
     * @return a list of entities
     */
    @GetMapping
    public List<Dto> all(@RequestParam(value = "filter",required = false) String[] filter,
                         @RequestParam(value = "filterOperation",required = false)String[] filterOperation,
                         @RequestParam(value = "filterValue",required = false)String[] filterValue,
                         @RequestParam(value = "start",required = false)Long start,
                         @RequestParam(value = "count",required = false)Long count,
                         @RequestParam(value = "sort",required = false)String sort
    ) {
        return service.all(filter, filterOperation, filterValue, start, count, sort).stream().map(mapper::fromEntity)
                      .toList();
    }

    /**
     * Returns a list of entities based on the passed ids
     *
     * @param idString the ids inform of string (more than one identity and all parts of each identity)
     * @return a list of entities
     */
    @GetMapping("/ones/{idString}")
    public List<Dto> getOnes(@PathVariable("idString") String idString) {
        return service.getOnes(idString).stream().map(mapper::fromEntity).toList();
    }

    /**
     * Returns the number of entities based on the passed filters
     *
     * @param filter          the filter names
     * @param filterOperation the filter operations
     * @param filterValue     the filter values
     * @return the number of entities
     */
    @GetMapping("/count")
    public Long count(@RequestParam(value = "filter",required = false)String[] filter,
                      @RequestParam(value = "filterOperation",required = false)String[] filterOperation,
                      @RequestParam(value = "filterValue",required = false)String[] filterValue) {
        return service.count(filter, filterOperation, filterValue);
    }
}
