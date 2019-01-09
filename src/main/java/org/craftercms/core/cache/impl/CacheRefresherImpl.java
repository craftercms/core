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
package org.craftercms.core.cache.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craftercms.core.cache.Cache;
import org.craftercms.core.cache.CacheItem;
import org.craftercms.core.cache.CacheLoader;
import org.craftercms.core.exception.InternalCacheEngineException;

/**
 * Default implementation of {@link CacheRefresher}.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public class CacheRefresherImpl implements CacheRefresher {

    private static final Log logger = LogFactory.getLog(CacheRefresherImpl.class);
    /**
     * Refreshes the specified list of {@link org.craftercms.core.cache.CacheItem}s.
     */
    public void refreshItems(List<CacheItem> itemsToRefresh, Cache cache) {
        for (CacheItem item : itemsToRefresh) {
            try {
                refreshItem(item, cache);
            } catch (Exception ex) {
                logger.error("Refresh for " + getScopeAndKeyString(item) + " failed", ex);
            }
        }
    }

    /**
     * Refreshes only one item. To refresh the item, its {@link CacheLoader} is called to get a new value of the item.
     * If the value returned by the loader is null, the item is removed from the cache instead.
     *
     * @throws Exception
     */
    protected void refreshItem(CacheItem item, Cache cache) throws Exception {
        CacheLoader loader = item.getLoader();
        Object[] loaderParams = item.getLoaderParams();

        if (loader == null) {
            throw new InternalCacheEngineException("No cache loader for " + getScopeAndKeyString(item));
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Refreshing " + getScopeAndKeyString(item));
        }

        Object newValue = loader.load(loaderParams);
        if (newValue != null) {
            cache.put(item.getScope(), item.getKey(), newValue, item.getTicksToExpire(), item.getTicksToRefresh(),
                      item.getLoader(), item.getLoaderParams());
        } else {
            // If newValue returned is null, remove the item from the cache
            cache.remove(item.getScope(), item.getKey());
        }
    }

    protected String getScopeAndKeyString(CacheItem item) {
        return "[scope='" + item.getScope() + "', key=" + item.getKey() + "]";
    }

}
