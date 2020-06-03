/*
 * Copyright (C) 2007-2020 Crafter Software Corporation. All Rights Reserved.
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
package org.craftercms.core.cache.impl;

import java.util.List;

import org.craftercms.core.cache.Cache;
import org.craftercms.core.cache.CacheItem;
import org.craftercms.core.cache.CacheLoader;

/**
 * Refreshes a list of {@link org.craftercms.core.cache.CacheItem}s in a {@link org.craftercms.core.cache.Cache}. New
 * values for the items will usually be obtained through
 * {@link CacheLoader}s.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public interface CacheRefresher {

    /**
     * Refreshes the specified list of {@link org.craftercms.core.cache.CacheItem}s.
     *
     * @param itemsToRefresh
     * @param cache          the cache where the new item values should be put
     */
    void refreshItems(List<CacheItem> itemsToRefresh, Cache cache);

}
