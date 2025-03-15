package io.ossnass.advSpring.operators;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The operation enum used in {@link io.ossnass.advSpring.annotations.FilterInfo} to determine the operation of the filter
 */
@Getter
@AllArgsConstructor
public enum Operation {

    /**
     * Equals
     */
    EQUALS("equals"),
    /**
     * Not Equals
     */
    NOT_EQUALS("notequals"),
    /**
     * Less Than
     */
    SMALLER_THAN("lt"),
    /**
     * Less Than or Equals
     */
    SMALLER_THAN_OR_EQUAL("lte"),
    /**
     * Greater Than
     */
    GREATER_THAN("gt"),
    /**
     * Greater Than or Equals
     */
    GREATER_THAN_OR_EQUAL("gte"),
    /**
     * date doesn't match
     */
    DATE_IS_NOT_EQUALS("dateisnot"),
    /**
     * date matches
     */
    DATE_IS_EQUALS("dateis"),
    /**
     * date is before
     */
    DATE_IS_BEFORE("datebefore"),
    /**
     * date is after
     */
    DATE_IS_AFTER("dateafter");

    private final String value;

}
