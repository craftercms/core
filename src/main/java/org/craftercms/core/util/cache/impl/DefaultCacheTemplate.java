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
package org.craftercms.core.util.cache.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craftercms.commons.lang.Callback;
import org.craftercms.core.cache.CacheItem;
import org.craftercms.core.cache.CacheLoader;
import org.craftercms.core.service.CacheService;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.util.CacheUtils;
import org.craftercms.core.util.cache.CacheTemplate;
import org.springframework.beans.factory.annotation.Required;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class DefaultCacheTemplate implements CacheTemplate {

    private static final Log logger = LogFactory.getLog(DefaultCacheTemplate.class);

    private CacheService cacheService;

    @Override
    public CacheService getCacheService() {
        return cacheService;
    }

    @Required
    public void setCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Override
    public Object getKey(Object... keyElements) {
        return CacheUtils.generateKey(keyElements);
    }

    @Override
    public boolean hasObject(Context context, Object... keyElements) {
        return cacheService.hasKey(context, getKey(keyElements));
    }

    @Override
    public <T> T getObject(Context context, Callback<T> callback, Object... keyElements) {
        return getObject(context, null, callback, keyElements);
    }

    @Override
    public <T> T getObject(Context context, CachingOptions cachingOptions, Callback<T> callback,
                           Object... keyElements) {
        Object key = getKey(keyElements);

        T obj = doGet(context, callback, key);
        if (obj == null) {
            obj = callback.execute();
            if (obj != null) {
                if (cachingOptions == null) {
                    cachingOptions = CachingOptions.DEFAULT_CACHING_OPTIONS;
                }

                obj = doPut(context, cachingOptions, callback, key, obj);
            }
        }

        return obj;
    }

    @SuppressWarnings("unchecked")
    protected <T> T doGet(Context context, Callback<T> callback, Object key) {
        T obj = null;
        try {
            obj = (T)cacheService.get(context, key);
        } catch (Exception e) {
            logGetFailure(context, callback, key, e);
        }

        return obj;
    }

    protected <T> T doPut(Context context, CachingOptions cachingOptions, Callback<T> callback, Object key, T obj) {
        try {
            CacheLoader loader = getCacheLoader(callback, cachingOptions.getRefreshFrequency());
            cacheService.put(context, key, obj, cachingOptions, loader);
        } catch (Exception e) {
            logPutFailure(context, callback, key, obj, e);
        }

        return obj;
    }

    protected <T> CacheLoader getCacheLoader(final Callback<T> callback, long refreshFrequency) {
        if (refreshFrequency != CacheItem.NEVER_REFRESH) {
            return parameters -> callback.execute();
        } else {
            return null;
        }
    }

    protected void logGetFailure(Context context, Callback<?> callback, Object key, Exception e) {
        logger.error("Unable to retrieve cached object: key='" + key + "', context=" + context + ", " +
                     "callback=" + callback, e);
    }

    protected void logPutFailure(Context context, Callback<?> callback, Object key, Object obj, Exception e) {
        logger.error("Unable to put cache object: key='" + key + "', context=" + context + ", obj=" + obj +
                     ", callback=" + callback, e);
    }

}
