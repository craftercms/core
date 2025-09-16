package org.craftercms.core.util.cache.impl;

import com.google.common.util.concurrent.Striped;
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

import java.util.concurrent.locks.Lock;

/**
 * Default implementation of CacheTemplate that ensures only one thread loads
 * a given cache item at a time, using per-key striped locks.
 *
 * @author Alfonso Vásquez
 */
public class DefaultCacheTemplate implements CacheTemplate {

    private static final Log logger = LogFactory.getLog(DefaultCacheTemplate.class);

    private final CacheService cacheService;
    private final Striped<Lock> stripedLocks;

    /**
     * Creates a DefaultCacheTemplate with a configurable number of stripes.
     *
     * @param cacheService the cache service
     * @param stripeCount  number of stripes (recommend a power of two ≥ maxThreads of app server)
     */
    public DefaultCacheTemplate(CacheService cacheService, int stripeCount) {
        this.cacheService = cacheService;
        this.stripedLocks = Striped.lazyWeakLock(stripeCount);
    }

    @Override
    public CacheService getCacheService() {
        return cacheService;
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
            obj = loadAndPutInCache(context, cachingOptions, callback, key);
        }

        return obj;
    }

    @SuppressWarnings("unchecked")
    protected <T> T doGet(Context context, Callback<T> callback, Object key) {
        try {
            return (T) cacheService.get(context, key);
        } catch (Exception e) {
            logGetFailure(context, callback, key, e);
            return null;
        }
    }

    protected <T> T loadAndPutInCache(Context context, CachingOptions cachingOptions, Callback<T> callback, Object key) {
        Lock lock = stripedLocks.get(context.getCacheScope() + ":" + key);
        lock.lock();
        try {
            // Check if another thread already has put the item in cache
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
        } finally {
            lock.unlock();
        }
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
        return (refreshFrequency != CacheItem.NEVER_REFRESH) ? parameters -> callback.execute() : null;
    }

    protected void logGetFailure(Context context, Callback<?> callback, Object key, Exception e) {
        logger.error("Unable to retrieve cached object: key='" + key + "', context=" + context +
                     ", callback=" + callback, e);
    }

    protected void logPutFailure(Context context, Callback<?> callback, Object key, Object obj, Exception e) {
        logger.error("Unable to put cache object: key='" + key + "', context=" + context +
                     ", obj=" + obj + ", callback=" + callback, e);
    }

}
