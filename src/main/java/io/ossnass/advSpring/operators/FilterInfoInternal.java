package io.ossnass.advSpring.operators;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Should not be used by the end user, represents information about a filter
 */
@Getter
@Setter
@Accessors(chain = true)
public class FilterInfoInternal {
    private Class<? extends Filter> filterClass;
    private String serviceId;
    private String id;
}
