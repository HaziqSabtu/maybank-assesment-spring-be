package com.assesment.maybank.spring_be.util;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageableUtils {

    /**
     * Enforces a stable secondary sort by appending a unique column (e.g. "id"). If
     * no sort is provided, uses a default sort + id.
     *
     * @param pageable    Incoming pageable from controller
     * @param defaultSort Default sort field to use when none is provided
     * @param idField     Unique column to use as tie-breaker (usually "id")
     * @return Modified Pageable with enforced secondary sort
     */
    public static Pageable enforceStableSort(Pageable pageable, String defaultSort, String idField) {
        Sort incomingSort = pageable.getSort();

        Sort safeSort = incomingSort.isSorted() ? incomingSort : Sort.by(Sort.Order.desc(defaultSort));

        if (!containsProperty(safeSort, idField)) {
            safeSort = safeSort.and(Sort.by(idField));
        }

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), safeSort);
    }

    private static boolean containsProperty(Sort sort, String property) {
        return sort.stream().anyMatch(order -> order.getProperty().equalsIgnoreCase(property));
    }
}
