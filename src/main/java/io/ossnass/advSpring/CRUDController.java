package io.ossnass.advSpring;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * This class is used to create a controller for a CRUD service
 * <p>
 * Allows to create, edit and delete entities from the system
 * <p>
 * Supports the following functions:
 * <ul>
 *     <li>{@link CRUDController#post(Dto)}: creates a new entity</li>
 *     <li>{@link CRUDController#put(Dto)}: edits an existing entity</li>
 *     <li>{@link CRUDController#delete(String)}: deletes an existing entity</li>
 * </ul>
 *
 * @param <Entity> the entity type
 * @param <ID>     the id type
 * @param <Dto>    the DTO type
 */
public class CRUDController<Entity extends Deletable, ID, Dto> extends ReadOnlyController<Entity, ID, Dto> {

    public CRUDController(CRUDService<Entity, ID> service) {
        super(service);
    }


    /**
     * Creates a new entity
     * <p>
     * If the entity already exists, the {@link EntityExistsException} will be thrown
     *
     * @param dto the DTO to create the entity from
     * @return the created entity, error 409 (conflict) if the entity already exists, error 500 if something goes wrong
     */
    @PostMapping
    public Dto post(@RequestBody Dto dto) {
        if (this.controllerInfo.disableAdd())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        try {

            return mapper.fromEntity(((CRUDService<Entity, ID>) service).save(mapper.fromDto(dto)));
        } catch (EntityExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Edits an existing entity
     * <p>
     * If the entity doesn't exist, the {@link EntityNotFoundException} will be thrown
     *
     * @param dto the DTO to edit the entity from
     * @return the edited entity, error 404 (not found) if the entity doesn't exist, error 500 if something goes wrong
     */
    @PutMapping
    public Dto put(@RequestBody Dto dto) {
        if (this.controllerInfo.disableEdit())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        try {
            return mapper.fromEntity(((CRUDService<Entity, ID>) service).edit(mapper.fromDto(dto)));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes an existing entity
     * <p>
     * If the entity doesn't exist, the {@link EntityNotFoundException} will be thrown
     *
     * @param id the id of the entity to delete
     * @throws ResponseStatusException with code 404 (not found) if the entity doesn't exist, code 500 if something goes wrong
     */
    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") String id) {
        Assert.notNull(id, "cannot delete a null id");
        if (this.controllerInfo.disableDelete())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        try {
            ((CRUDService<Entity, ID>) service).delete(service.getById(service.convertStringToIds(id.split(",")).get(0)));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*/**
     * Soft deletes an existing entity
     * <p>
     * If the entity doesn't exist, the {@link EntityNotFoundException} will be thrown
     *
     * @param id the id of the entity to softly delete
     * @throws ResponseStatusException with code 404 (not found) if the entity doesn't exist, code 500 if something goes wrong
     */
//    @DeleteMapping("/soft/{id}")
//    public void softDelete(@PathParam("id") String id) {
//        if (this.controllerInfo.disableSoftDelete())
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
//        try {
//
//            ((CRUDService) service).softDelete(service.getById(service.convertStringToIds(id.split(",")).get(0)));
//        } catch (EntityNotFoundException e) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
//        } catch (Exception e) {
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

}
