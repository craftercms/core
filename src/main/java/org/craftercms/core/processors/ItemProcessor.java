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
