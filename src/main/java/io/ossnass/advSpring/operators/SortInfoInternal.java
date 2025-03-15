package io.ossnass.advSpring.operators;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Should not be used by the end user, represents information about a sort
 */
@Getter
@Setter
@Accessors(chain = true)
public class SortInfoInternal {
    private Class<? extends Sort> sortClass;
    private String controllerId;
    private String id;
}
