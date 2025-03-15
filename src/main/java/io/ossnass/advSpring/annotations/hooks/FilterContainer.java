package io.ossnass.advSpring.annotations.hooks;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Wrapper used to pass and return filters in {@link PreFetch}
 */
@Getter
public class FilterContainer {
    private final ArrayList<String> filters;
    private final ArrayList<String> filterOperations;
    private final ArrayList<String> filterValues;

    public FilterContainer() {
        filters = new ArrayList<>();
        filterOperations = new ArrayList<>();
        filterValues = new ArrayList<>();
    }

    public void populateFilters(String[] filters, String[] filterOperations, String[] filterValues) {
        if (filters == null || filters.length == 0) return;
        this.filters.addAll(Arrays.asList(filters));
        this.filterOperations.addAll(Arrays.asList(filterOperations));
        this.filterValues.addAll(Arrays.asList(filterValues));

    }

}
