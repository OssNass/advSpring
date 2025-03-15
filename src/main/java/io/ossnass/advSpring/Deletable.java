package io.ossnass.advSpring;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * The base class for all entities in the system
 * <p>
 * It supports soft delete using  {@link #deleted} and
 * <p>
 * tels if an entity can be deleted based on{@link #isDeletable}
 */
@MappedSuperclass
@Getter
@Setter
@Accessors(chain = true)
public class Deletable implements Serializable {
    /**
     * Soft delete flag
     */
    @Column(name = "deleted")
    protected Boolean deleted;
    /**
     * Flag to tell if an entity can be deleted
     * <p>
     * this flag is not stored
     */
    @Transient
    protected Boolean isDeletable;
}
