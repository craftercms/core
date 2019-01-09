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
package org.craftercms.core.service;

import java.util.List;

/**
 * Interface for item filters. Used by {@link ContentStoreService}s to filter the items returned to the user of
 * the API.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public interface ItemFilter {

    /**
     * Returns true if the filter should be executed before any item is processed (basically, before any
     * {@link org.craftercms.core.processors.ItemProcessor} is called).
     */
    boolean runBeforeProcessing();

    /**
     * Returns true if the filter should be executed after all items are processed (basically, after all
     * {@link org.craftercms.core.processors.ItemProcessor} are called).
     */
    boolean runAfterProcessing();

    /**
     * Return true if the given item is accepted by the filter.
     *
     * @param item                      the item to accept or reject
     * @param acceptedItems             the list of the currently accepted items
     * @param rejectedItems             the list of the currently rejected items
     * @param runningBeforeProcessing   if the filter is running before processing (true) or after processing (false)
     */
    boolean accepts(Item item, List<Item> acceptedItems, List<Item> rejectedItems, boolean runningBeforeProcessing);

}
