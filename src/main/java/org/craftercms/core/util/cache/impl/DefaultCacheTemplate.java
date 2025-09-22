package org.craftercms.core.util.cache.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craftercms.commons.concurrency.locks.LockByKey;
import org.craftercms.commons.lang.Callback;
import org.craftercms.core.cache.CacheItem;
import org.craftercms.core.cache.CacheLoader;
import org.craftercms.core.service.CacheService;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.util.CacheUtils;
import org.craftercms.core.util.cache.CacheTemplate;

/**
 * Default implementation of {@link CacheTemplate} that ensures only one thread loads
 * a given cache item at a time, using per-key locks.
 * <p>
 * This class provides thread-safe cache access and loading, preventing cache stampede
 * by synchronizing cache loads per key.
 * </p>
 *
 * <p>
 * Usage:
 * <ul>
 *   <li>Use {@link #getObject(Context, Callback, Object...)} to retrieve or load a cache item.</li>
 *   <li>Locks are managed internally using {@link LockByKey} for efficiency.</li>
 * </ul>
 * </p>
 *
 * @author Alfonso Vásquez
 */
public class DefaultCacheTemplate implements CacheTemplate {

    private static final Log logger = LogFactory.getLog(DefaultCacheTemplate.class);

    /** The cache service used for cache operations. */
    protected CacheService cacheService;

    /**
     * Helper class that allows locking by a string key
     */
    protected LockByKey<String> lockByKey;

    /**
     * Creates a DefaultCacheTemplate with a configurable number of stripes.
     *
     * @param cacheService the cache service to use for cache operations
     */
    public DefaultCacheTemplate(CacheService cacheService) {
        this(cacheService, new LockByKey<>());
    }

    /**
     * Creates a DefaultCacheTemplate with a configurable number of stripes.
     *
     * @param cacheService the cache service to use for cache operations
     * @param lockByKey    the LockByKey instance to use for per-key locking
     */
    public DefaultCacheTemplate(CacheService cacheService, LockByKey<String> lockByKey) {
        this.cacheService = cacheService;
        this.lockByKey = lockByKey;
    }

    /**
     * Returns the underlying {@link CacheService}.
     *
     * @return the cache service
     */
    @Override
    public CacheService getCacheService() {
        return cacheService;
    }

    /**
     * Generates a cache key from the given key elements.
     *
     * @param keyElements elements to generate the key from
     * @return the generated cache key
     */
    @Override
    public Object getKey(Object... keyElements) {
        return CacheUtils.generateKey(keyElements);
    }

    /**
     * Checks if the cache contains an object for the given context and key elements.
     *
     * @param context     the cache context
     * @param keyElements elements to generate the key from
     * @return true if the object exists in the cache, false otherwise
     */
    @Override
    public boolean hasObject(Context context, Object... keyElements) {
        return cacheService.hasKey(context, getKey(keyElements));
    }

    /**
     * Retrieves an object from the cache, or loads and caches it using the given callback if not present.
     * Uses default caching options.
     *
     * @param context  the cache context
     * @param callback the callback to load the object if not present
     * @param keyElements elements to generate the key from
     * @param <T>      the type of the object
     * @return the cached or loaded object
     */
    @Override
    public <T> T getObject(Context context, Callback<T> callback, Object... keyElements) {
        return getObject(context, null, callback, keyElements);
    }

    /**
     * Retrieves an object from the cache, or loads and caches it using the given callback and caching options if not present.
     * Ensures only one thread loads the object per key at a time.
     *
     * @param context        the cache context
     * @param cachingOptions the caching options to use
     * @param callback       the callback to load the object if not present
     * @param keyElements    elements to generate the key from
     * @param <T>            the type of the object
     * @return the cached or loaded object
     */
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

    /**
     * Attempts to retrieve an object from the cache.
     *
     * @param context  the cache context
     * @param callback the callback (for logging purposes)
     * @param key      the cache key
     * @param <T>      the type of the object
     * @return the cached object, or null if not found or on error
     */
    @SuppressWarnings("unchecked")
    protected <T> T doGet(Context context, Callback<T> callback, Object key) {
        try {
            return (T) cacheService.get(context, key);
        } catch (Exception e) {
            logGetFailure(context, callback, key, e);
            return null;
        }
    }

    /**
     * Loads an object using the callback and puts it in the cache, ensuring only one thread loads per key.
     *
     * @param context        the cache context
     * @param cachingOptions the caching options to use
     * @param callback       the callback to load the object
     * @param key            the cache key
     * @param <T>            the type of the object
     * @return the loaded object, or null if loading failed
     */
    protected <T> T loadAndPutInCache(Context context, CachingOptions cachingOptions, Callback<T> callback, Object key) {
        String lockKey = context.getCacheScope() + ":" + key;
        lockByKey.lock(lockKey);
        try {
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
            lockByKey.unlock(lockKey);
        }
    }

    /**
     * Puts an object in the cache using the given caching options and loader.
     *
     * @param context        the cache context
     * @param cachingOptions the caching options to use
     * @param callback       the callback (for loader creation)
     * @param key            the cache key
     * @param obj            the object to cache
     * @param <T>            the type of the object
     * @return the cached object
     */
    protected <T> T doPut(Context context, CachingOptions cachingOptions, Callback<T> callback, Object key, T obj) {
        try {
            CacheLoader loader = getCacheLoader(callback, cachingOptions.getRefreshFrequency());
            cacheService.put(context, key, obj, cachingOptions, loader);
        } catch (Exception e) {
            logPutFailure(context, callback, key, obj, e);
        }
        return obj;
    }

    /**
     * Returns a {@link CacheLoader} if the refresh frequency is set, otherwise null.
     *
     * @param callback         the callback to use for loading
     * @param refreshFrequency the refresh frequency in milliseconds
     * @param <T>              the type of the object
     * @return a cache loader or null if never refresh
     */
    protected <T> CacheLoader getCacheLoader(final Callback<T> callback, long refreshFrequency) {
        return (refreshFrequency != CacheItem.NEVER_REFRESH) ? parameters -> callback.execute() : null;
    }

    /**
     * Logs a cache get failure.
     *
     * @param context  the cache context
     * @param callback the callback used
     * @param key      the cache key
     * @param e        the exception thrown
     */
    protected void logGetFailure(Context context, Callback<?> callback, Object key, Exception e) {
        logger.error("Unable to retrieve cached object: key='" + key + "', context=" + context +
                     ", callback=" + callback, e);
    }

    /**
     * Logs a cache put failure.
     *
     * @param context  the cache context
     * @param callback the callback used
     * @param key      the cache key
     * @param obj      the object being cached
     * @param e        the exception thrown
     */
    protected void logPutFailure(Context context, Callback<?> callback, Object key, Object obj, Exception e) {
        logger.error("Unable to put cache object: key='" + key + "', context=" + context +
                     ", obj=" + obj + ", callback=" + callback, e);
    }

}