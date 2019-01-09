/*
 * Copyright (C) 2007-2019 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.core.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.craftercms.core.service.Item;
import org.craftercms.core.service.ItemFilter;

/**
 * Composite {@link ItemFilter} implementation. Calls {@link ItemFilter}s before processing and after processing,
 * depending on what their {@link ItemFilter#runBeforeProcessing()} and {@link ItemFilter#runAfterProcessing()} methods
 * return.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public class CompositeItemFilter implements ItemFilter {

    /**
     * List of {@link ItemFilter}s that are executed sequentially.
     */
    private List<ItemFilter> filters;

    /**
     * Creates a composite filter with a <code>null</code> list of {@link ItemFilter}s.
     */
    public CompositeItemFilter() {
    }

    /**
     * Creates a composite filter with the specified list of {@link ItemFilter}s.
     */
    public CompositeItemFilter(final List<ItemFilter> filters) {
        this.filters = filters;
    }

    /**
     * Creates a composite filter with the specified array of {@link ItemFilter}s.
     */
    public CompositeItemFilter(final ItemFilter... filters) {
        this.filters = Arrays.asList(filters);
    }

    /**
     * Sets the list of filters.
     */
    public void setFilters(List<ItemFilter> filters) {
        this.filters = filters;
    }

    /**
     * Adds the specified {@link ItemFilter} to the filter list, creating the list if necessary.
     */
    public void addFilter(final ItemFilter filter) {
        if (filters == null) {
            filters = new ArrayList<ItemFilter>();
        }

        filters.add(filter);
    }

    /**
     * Removes the specified {@link ItemFilter} from the filter list, only if the list is not <code>null</code>.
     */
    public boolean removeFilter(final ItemFilter filter) {
        if (filters != null) {
            return filters.remove(filter);
        } else {
            return false;
        }
    }

    /**
     * Always returns true so that filters that need to run before processing are called.
     */
    @Override
    public boolean runBeforeProcessing() {
        return true;
    }

    /**
     * Always returns true so that filters that need to run after processing are called.
     */
    @Override
    public boolean runAfterProcessing() {
        return true;
    }

    /**
     * If {@code runningBeforeProcessing} is true, calls all filters that need to be run before processing. If
     * it is false, calls all filters that need to be run after processing. Filters in the chain are called until
     * one of them rejects the item.
     */
    @Override
    public boolean accepts(Item item, List<Item> acceptedItems, List<Item> rejectedItems,
                           boolean runningBeforeProcessing) {
        boolean accepted = true;

        if (CollectionUtils.isNotEmpty(filters)) {
            for (Iterator<ItemFilter> filterIter = filters.iterator(); accepted && filterIter.hasNext();) {
                ItemFilter filter = filterIter.next();
                if (runningBeforeProcessing && filter.runBeforeProcessing()) {
                    accepted = filter.accepts(item, acceptedItems, rejectedItems, runningBeforeProcessing);
                } else if (!runningBeforeProcessing && filter.runAfterProcessing()) {
                    accepted = filter.accepts(item, acceptedItems, rejectedItems, runningBeforeProcessing);
                }
            }
        }

        return accepted;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CompositeItemFilter that = (CompositeItemFilter)o;

        if (filters != null? !filters.equals(that.filters): that.filters != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return filters != null? filters.hashCode(): 0;
    }

    @Override
    public String toString() {
        return "CompositeItemFilter[filters=" + filters + ']';
    }

}
