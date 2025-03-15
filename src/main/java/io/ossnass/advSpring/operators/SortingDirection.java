package io.ossnass.advSpring.operators;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The sorting direction enum used in {@link io.ossnass.advSpring.annotations.SortInfo} to determine the direction of the sorting
 */
@AllArgsConstructor
@Getter
public enum SortingDirection {
    /**
     * Ascending
     */
    Ascending("asc"),
    /**
     * Descending
     */
    Descending("desc");

    private final String value;

}
