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
package org.craftercms.core.processors.impl;

import org.craftercms.core.exception.ItemProcessingException;
import org.craftercms.core.processors.ItemProcessor;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.service.Item;

/**
 * No-op {@link ItemProcessor} exclusive implementation.
 * This processor does nothing but return the item, a NOOP.
 * It is also exclusive, so if passed into getItem, it will disable
 * the processor chain and simply return the item as-is.
 *
 * @since 4.1.2
 */
public class SkipAllItemProcessor implements ItemProcessor {
    @Override
    public boolean isExclusive() {
        return true;
    }

    @Override
    public Item process(Context context, CachingOptions cachingOptions, Item item) throws ItemProcessingException {
        return item;
    }
}
