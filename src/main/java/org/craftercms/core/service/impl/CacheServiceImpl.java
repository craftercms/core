/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.craftercms.core.cache.Cache;
import org.craftercms.core.cache.CacheItem;
import org.craftercms.core.cache.CacheLoader;
import org.craftercms.core.exception.InternalCacheEngineException;
import org.craftercms.core.exception.InvalidContextException;
import org.craftercms.core.exception.InvalidScopeException;
import org.craftercms.core.service.CacheService;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of {@link CacheService}. Adapts a {@link Cache}.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public class CacheServiceImpl implements CacheService {

    protected Cache cache;

    /**
     * Sets the cache engine.
     *
     * @param cache
     */
    @Required
    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public Collection<String> getScopes() throws InternalCacheEngineException {
        return cache.getScopes();
    }

    public void clearAll() throws InternalCacheEngineException {
        cache.clearAll();
    }

    @Override
    public void addScope(Context context) throws InternalCacheEngineException {
        if (context.isCacheOn()) {
            cache.addScope(context.getId(), context.getMaxAllowedItemsInCache());
        }
    }

    @Override
    public void removeScope(Context context) throws InvalidContextException, InternalCacheEngineException {
        if (context.isCacheOn()) {
            try {
                cache.removeScope(context.getId());
            } catch (InvalidScopeException e) {
                throw new InvalidContextException("No scope associated to context " + context);
            }
        }
    }

    @Override
    public boolean hasScope(Context context) throws InvalidContextException, InternalCacheEngineException {
        if (context.isCacheOn()) {
            try {
                return cache.hasScope(context.getId());
            } catch (InvalidScopeException e) {
                throw new InvalidContextException("No scope associated to context " + context);
            }
        } else {
            return false;
        }
    }

    @Override
    public int getSize(Context context) throws InvalidContextException, InternalCacheEngineException {
        if (context.isCacheOn()) {
            try {
                return cache.getSize(context.getId());
            } catch (InvalidScopeException e) {
                throw new InvalidContextException("No scope associated to context " + context);
            }
        } else {
            return 0;
        }
    }

    @Override
    public Collection<?> getKeys(Context context) throws InvalidContextException, InternalCacheEngineException {
        if (context.isCacheOn()) {
            try {
                return cache.getKeys(context.getId());
            } catch (InvalidScopeException e) {
                throw new InvalidContextException("No scope associated to context " + context);
            }
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean hasKey(Context context, Object key) throws InvalidContextException, InternalCacheEngineException {
        if (context.isCacheOn()) {
            try {
                return cache.hasKey(context.getId(), key);
            } catch (InvalidScopeException e) {
                throw new InvalidContextException("No scope associated to context " + context);
            }
        } else {
            return false;
        }
    }

    @Override
    public CacheItem getItem(Context context, Object key) throws InvalidContextException, InternalCacheEngineException {
        if (context.isCacheOn()) {
            try {
                return cache.get(context.getId(), key);
            } catch (InvalidScopeException e) {
                throw new InvalidContextException("No scope associated to context " + context);
            }
        } else {
            return null;
        }
    }

    @Override
    public Object get(Context context, Object key) throws InvalidContextException, InternalCacheEngineException {
        if (context.isCacheOn()) {
            try {
                CacheItem item = cache.get(context.getId(), key);
                if (item != null) {
                    return item.getValue();
                } else {
                    return null;
                }
            } catch (InvalidScopeException e) {
                throw new InvalidContextException("No scope associated to context " + context);
            }
        } else {
            return null;
        }
    }

    @Override
    public void put(Context context, Object key, Object value) throws InvalidContextException,
        InternalCacheEngineException {
        if (context.isCacheOn()) {
            try {
                cache.put(context.getId(), key, value);
            } catch (InvalidScopeException e) {
                throw new InvalidContextException("No scope associated to context " + context);
            }
        }
    }

    @Override
    public void put(Context context, Object key, Object value, CachingOptions cachingOptions, CacheLoader loader,
                    Object... loaderParams) throws InvalidContextException, InternalCacheEngineException {
        if (context.isCacheOn() && cachingOptions.doCaching()) {
            try {
                cache.put(context.getId(), key, value, cachingOptions.getExpireAfter(),
                    cachingOptions.getRefreshFrequency(), loader, loaderParams);
            } catch (InvalidScopeException e) {
                throw new InvalidContextException("No scope associated to context " + context);
            }
        }
    }

    @Override
    public boolean remove(Context context, Object key) throws InvalidContextException, InternalCacheEngineException {
        if (context.isCacheOn()) {
            try {
                return cache.remove(context.getId(), key);
            } catch (InvalidScopeException e) {
                throw new InvalidContextException("No scope associated to context " + context);
            }
        } else {
            return false;
        }
    }

    @Override
    public void clearScope(Context context) throws InvalidContextException, InternalCacheEngineException {
        if (context.isCacheOn()) {
            try {
                cache.clearScope(context.getId());
            } catch (InvalidScopeException e) {
                throw new InvalidContextException("No scope associated to context " + context);
            }
        }
    }

}
