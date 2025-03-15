package io.ossnass.advSpring;

/**
 * This interface is used to convert between entities and DTOs
 *
 * @param <Entity> the entity type
 * @param <Dto>    the DTO type
 */
public interface DtoMapper<Entity extends Deletable, Dto> {
    /**
     * Converts an entity to a DTO
     *
     * @param entity the entity to convert
     * @return the DTO
     */
    Dto fromEntity(Entity entity);

    /**
     * Converts a DTO to an entity
     *
     * @param dto the DTO to convert
     * @return the entity
     */
    Entity fromDto(Dto dto);
}
