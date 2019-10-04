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
package org.craftercms.core.util.cache;

import org.craftercms.commons.lang.Callback;
import org.craftercms.core.service.CacheService;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;

/**
 * Template patten that allows easy usage of the cache service.
 *
 * @author Alfonso Vásquez
 */
public interface CacheTemplate {

    /**
     * Returns the underlying cache service used.
     */
    CacheService getCacheService();

    /**
     * Creates a key from the specified elements
     *
     * @param keyElements the key elements
     *
     * @return the key
     */
    Object getKey(Object... keyElements);

    /**
     * Returns true if the cache contains an object for the specified key elements
     *
     * @param context       the context (needed by the cache service)
     * @param keyElements   the key elements, used to create the final cache key
     *
     * @return true if the cache contains an object for the specified key elements
     */
    boolean hasObject(Context context, Object... keyElements);

    /**
     * Executes the template, using the specified callback to load the object to cache, if it's not already in the
     * cache. Works like {@link #getObject(org.craftercms.core.service.Context,
     * org.craftercms.core.service.CachingOptions, org.craftercms.commons.lang.Callback, Object...)}, but with
     * default caching options.
     *
     * @param context       the context (needed by the cache service)
     * @param callback      the callback to use in case the object is not in the cache
     * @param keyElements   the key elements, used to create the final cache key
     *
     * @return the cached object
     */
    <T> T getObject(Context context, Callback<T> callback, Object... keyElements);

    /**
     * Executes the template, using the specified callback to load the object to cache, if it's not already in the
     * cache.
     *
     * @param context           the context (needed by the cache service)
     * @param cachingOptions    the options used for caching.
     * @param callback          the callback to use in case the object is not in the cache
     * @param keyElements       the key elements, used to create the final cache key
     *
     * @return the cached object
     */
    <T> T getObject(Context context, CachingOptions cachingOptions, Callback<T> callback, Object... keyElements);

}
