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

import java.util.Collection;
import java.util.List;

import org.craftercms.core.cache.CacheItem;
import org.craftercms.core.cache.CacheLoader;
import org.craftercms.core.cache.CacheStatistics;
import org.craftercms.core.exception.InternalCacheEngineException;
import org.craftercms.core.exception.InvalidContextException;
import org.craftercms.core.exception.InvalidScopeException;

/**
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public interface CacheService {

    /**
     * Returns the list of scopes this cache manages.
     */
    Collection<String> getScopes() throws InternalCacheEngineException;

    /**
     * Clears the contents of the entire cache.
     */
    void clearAll() throws InternalCacheEngineException;

    /**
     * Adds a new scope to the cache for the given context.
     */
    void addScope(Context context) throws InternalCacheEngineException;

    /**
     * Removes the scope associated to the given context
     */
    void removeScope(Context context) throws InvalidContextException, InternalCacheEngineException;

    /**
     * Returns true if the scope of the given context exists.
     */
    boolean hasScope(Context context) throws InvalidContextException, InternalCacheEngineException;

    /**
     * Returns the quantity of items present in scope of the given context.
     */
    int getSize(Context context) throws InvalidContextException, InternalCacheEngineException;

    /**
     * Returns a list of the keys of the items present in the scope of the given context.
     */
    Collection<?> getKeys(Context context) throws InvalidContextException, InternalCacheEngineException;

    /**
     * Returns true if there's and item with the specified key in the scope of the given context.
     */
    boolean hasKey(Context context, Object key) throws InvalidContextException, InternalCacheEngineException;

    /**
     * Retrieves an item from the scope of the given context, or null if not found.
     */
    CacheItem getItem(Context context, Object key) throws InvalidContextException, InternalCacheEngineException;

    /**
     * Retrieves an item's value from the scope of the given context, or null if not found.
     */
    Object get(Context context, Object key) throws InvalidContextException, InternalCacheEngineException;

    /**
     * Puts an item in the scope of the given context.
     */
    void put(Context context, Object key, Object value) throws InvalidContextException, InternalCacheEngineException;

    /**
     * Puts and item in the scope of the given context.
     */
    void put(Context context, Object key, Object value, CachingOptions cachingOptions, CacheLoader loader,
             Object... loaderParams) throws InvalidContextException, InternalCacheEngineException;

    /**
     * Removes an item from the scope of the given context.
     */
    boolean remove(Context context, Object key) throws InvalidContextException, InternalCacheEngineException;

    /**
     * Clears the contents of the scope of the given context.
     */
    void clearScope(Context context) throws InvalidContextException, InternalCacheEngineException;

    /**
     * Returns the statistics for the scope of the given context.
     */
    CacheStatistics getStatistics(Context context);

}
