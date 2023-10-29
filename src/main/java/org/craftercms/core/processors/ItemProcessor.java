/*
 * Copyright (C) 2007-2023 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.core.processors;

import org.craftercms.core.exception.ItemProcessingException;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.service.Item;

/**
 * Processes an {@link Item}, by applying any needed modification to it.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public interface ItemProcessor {

    /**
     * When explicitly passing the processor (not in a pipeline), this method indicates whether
     * the processor should be executed exclusively and ignore any other processors.
     *
     * @return true if the processor should be executed exclusively, false otherwise.
     */
    default boolean isExclusive() {
        return false;
    }

    /**
     * Processes an {@link Item}.
     *
     * @param context        the current context
     * @param cachingOptions caching options in case you need access to items
     * @param item           the item to process
     * @return the modified item or a new item.
     * @throws ItemProcessingException if an error occurred while processing the item
     */
    Item process(Context context, CachingOptions cachingOptions, Item item) throws ItemProcessingException;

}
