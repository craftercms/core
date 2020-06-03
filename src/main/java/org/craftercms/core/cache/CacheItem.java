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
package org.craftercms.core.cache;

import java.util.List;

/**
 * Provides information about an item residing in the cache.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public interface CacheItem {

    /**
     * Indicates that an item should never expire.
     */
    long NEVER_EXPIRE = 0;
    /**
     * Indicates that an item should never refresh.
     */
    long NEVER_REFRESH = 0;

    /**
     * Returns the item's scope.
     */
    String getScope();

    /**
     * Returns the item's key, used to identify the item within the cache.
     */
    Object getKey();

    /**
     * Returns the item's value.
     */
    Object getValue();

    /**
     * Returns the number of ticks that had passed at the moment the item was created.
     */
    long getTicksAtCreation();

    /**
     * Returns the number of ticks that are required for the item to expire.
     */
    long getTicksToExpire();

    /**
     * Returns the number of ticks the are required for the item to be refreshed.
     */
    long getTicksToRefresh();

    /**
     * Returns the {@link CacheLoader} used to refresh this item.
     */
    CacheLoader getLoader();

    /**
     * Returns the additional parameters required by the {@link CacheLoader#load(Object...)} method.
     */
    Object[] getLoaderParams();

    /**
     * Returns true if the item has expired according to the number of ticks specified.
     *
     * @param currentTicks the current number of ticks
     * @return true if the item has expired according to the number of ticks specified, false otherwise
     */
    boolean isExpired(long currentTicks);

    /**
     * Returns true if the item needs to be refreshed according to the number of ticks specified.
     *
     * @param currentTicks the current number of ticks
     * @return true if the item needs to be refreshed according to the number of ticks specified, false otherwise
     */
    boolean needsRefresh(long currentTicks);

}
