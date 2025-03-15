package io.ossnass.advSpring;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import io.ossnass.advSpring.annotations.hooks.*;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * A Service class to provide a service for multiple controllers in the system
 * <p>
 * Performs the following functions:
 * <ul>
 *     <li>
 *         CRUD operations:
 *         <ol>
 *             <li>save a new using {@link CRUDService#save(Deletable)}</li>
 *             <li>edits an existing using {@link CRUDService#edit(Deletable)}</li>
 *             <li>soft delete an existing using {@link CRUDService#softDelete(Deletable)}</li>
 *             <li>hard delete an existing using {@link CRUDService#delete(Deletable)}</li>
 *         </ol>
 *     </li>
 *     <li>Provides hooks to be executed before and after the operations based on annotations:
 *      <ol>
 *          <li>Add: preAdd {@link PreAdd} & postAdd {@link PostAdd}</li>
 *          <li>Edit: preEdit {@link PreEdit}& postEdit {@link PostEdit}</li>
 *          <li>Delete: preDelete {@link PreDelete}& postDelete {@link PostDelete}, called before hard delete {@link CRUDService#delete(Deletable)}</li>
 *          <li>Fetch: preFetch {}& postFetch {}</li>
 *     </ol>
 *     </li>
 * </ul>
 *
 * @param <Entity> the class of the entity
 * @param <ID>     the id of the entity
 */
public abstract class CRUDService<Entity extends Deletable, ID> extends ReadOnlyService<Entity, ID> {
    public CRUDService(FilterAndSortInfoService filterService, JpaRepository<Entity, ID> repository, EntityManager em,
                       SearchSession searchSession, JinqStreamService streamService) {
        super(filterService, em, searchSession, streamService, repository);
        this.processHooksInitialization();

    }

    /**
     * Performs hook initialization
     */
    private void processHooksInitialization() {
        hooks.put(PreAdd.class, new HashMap<>());
        hooks.put(PostAdd.class, new HashMap<>());
        hooks.put(PreEdit.class, new HashMap<>());
        hooks.put(PostEdit.class, new HashMap<>());
        hooks.put(PreDelete.class, new HashMap<>());
        hooks.put(PostDelete.class, new HashMap<>());
        this.loadHooks(new HashSet<>(
                Arrays.asList(PreAdd.class, PostAdd.class, PreEdit.class, PostEdit.class, PreDelete.class,
                        PostDelete.class)));
    }

    /**
     * Saves a new entity into the database
     * <p>
     * Will call functions annotated with {@link PreAdd} annotation before adding and {@link PostAdd} after adding
     *
     * @param e the entity to save
     * @return the saved entity
     * @throws EntityExistsException if the entity already exists
     */
    public Entity save(Entity e) {
        Assert.notNull(e, "entity to add cannot be null");
        var id = extractId(e);
        if (repository.existsById(id))
            throw new EntityExistsException();
        var preAddHooks = hooks.get(PreAdd.class);
        var postAddHooks = hooks.get(PostAdd.class);
        var objectsToPass = new HashMap<Integer, Object>();
        return executeCUHooks(e, e, preAddHooks, objectsToPass, postAddHooks);
    }

    /**
     * Edits an existing entity into the database
     * <p>
     * The way it works is the following:
     * <ol>
     *     <li>First it looks for the entity in the database</li>
     *     <li>
     *         If entity is found the values are updated then all the {@link PreEdit} hooks are called, we merge the new entity and then call {@link PostEdit} hooks and return the new value
     *     </li>
     *     <li>
     *         If entity is not found, an {@link EntityNotFoundException} is thrown
     *     </li>
     * </ol>
     *
     * @param e the entity to edit
     * @return the updated entity
     * @throws EntityNotFoundException if the entity doesn't exist in the database
     */
    public Entity edit(Entity e) {
        Assert.notNull(e, "entity to edit cannot be null");
        var entity = repository.findById(extractId(e));
        var updatedEntity = entity.map(item -> processUpdate(item, e)).orElseThrow(EntityNotFoundException::new);
        var preEditHooks = hooks.get(PreEdit.class);
        var objectsToPass = new HashMap<Integer, Object>();
        var postEditHooks = hooks.get(PostEdit.class);
        return executeCUHooks(e, updatedEntity, preEditHooks, objectsToPass, postEditHooks);
    }

    private Entity executeCUHooks(Entity e, Entity updatedEntity, Map<Integer, Method> preHooks,
                                  HashMap<Integer, Object> objectsToPass, Map<Integer, Method> postHooks) {
        if (preHooks != null && !preHooks.isEmpty()) {
            processHooks(e, preHooks, objectsToPass);
        }
        updatedEntity = repository.saveAndFlush(updatedEntity);
        if (postHooks != null && !postHooks.isEmpty()) {
            processHooks(updatedEntity, postHooks, objectsToPass);
        }
        return updatedEntity;
    }

    /**
     * Performs a soft delete by setting {@link Entity#isDeletable} to true
     *
     * @param e the entity to softly delete
     */
    public void softDelete(Entity e) {
        Assert.notNull(e, "entity to soft delete cannot be null");
        edit((Entity) e.setIsDeletable(true));
    }

    /**
     * Deletes an entity from the database
     * <p>
     * The way it works is the following:
     * <ol>
     *     <li>First it looks for the entity in the database</li>
     *     <li>
     *         If entity is found, all the {@link PreDelete} hooks are called, we delete from the database and then execute all {@link PostDelete}
     *     </li>
     *     <li>if no entity found in the database an {@link EntityNotFoundException} is thrown</li>
     * </ol>
     *
     * @param e the entity to delete
     * @throws EntityNotFoundException if the entity is not in the database
     */
    public void delete(Entity e) {
        Assert.notNull(e, "entity to delete cannot be null");
        var entity = repository.findById(extractId(e));
        var entityToDelete = entity.map(item -> processUpdate(item, e))
                                   .orElseThrow(EntityNotFoundException::new);
        var preDeleteHooks = hooks.get(PreDelete.class);
        var postDeleteHooks = hooks.get(PostDelete.class);
        if (preDeleteHooks != null && !preDeleteHooks.isEmpty()) {
            processHooks(entityToDelete, preDeleteHooks, null);
        }
        repository.delete(entityToDelete);
        if (postDeleteHooks != null && !postDeleteHooks.isEmpty()) {
            processHooks(entityToDelete, postDeleteHooks, null);
        }
    }

    /**
     * Extracts the id of an entity
     *
     * @param e the entity to extract id from
     * @return the id of the entity
     */
    protected ID extractId(Entity e) {
        Assert.notNull(e, "entity to extract its id cannot be null");
        return (ID) em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(e);
    }

    /**
     * This function works by copying values from the requestEntity to dbObject inorder to save in the database
     * <p>
     * This what will eventually be saved when updating an entity
     *
     * @param dbObject      the object stored in the database
     * @param requestEntity the modified object from the user
     * @return the updated values
     */
    protected abstract Entity processUpdate(Entity dbObject, Entity requestEntity);

    /**
     * Processes the hooks on an entity
     *
     * @param e             the entity to process
     * @param hooks         the order hooks to execute
     * @param objectsToPass the extra object from {@link PreEdit} and {@link PreAdd} hooks
     */
    private void processHooks(Entity e, Map<Integer, Method> hooks, Map<Integer, Object> objectsToPass) {
        Assert.notNull(e, "entity to process hooks for cannot be null");
        Assert.notNull(hooks, "hooks cannot be empty");
        var keys = hooks.keySet().stream().sorted().toList();
        for (var key : keys) {
            var method = hooks.get(key);
            try {
                var paramList = new ArrayList<>();
                paramList.add(e);
                if (method.getParameterCount() == 2 && objectsToPass != null)
                    paramList.add(objectsToPass.get(key));
                if (method.getReturnType().equals(Void.TYPE))
                    method.invoke(this,paramList.toArray());
                else {
                    var o = method.invoke(this, paramList.toArray());
                    if (objectsToPass != null)
                        objectsToPass.put(key, o);
                }
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
            } catch (Exception ex) {
                logger.error("Unknown error occurred, message {}", ex.getMessage(), ex);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }


}
