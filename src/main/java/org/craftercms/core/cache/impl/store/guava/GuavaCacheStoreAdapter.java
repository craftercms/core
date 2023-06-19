/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
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
package org.craftercms.core.cache.impl.store.guava;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.craftercms.core.cache.CacheItem;
import org.craftercms.core.cache.CacheStatistics;
import org.craftercms.core.cache.impl.CacheStoreAdapter;
import org.springframework.beans.factory.DisposableBean;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of a {@link CacheStoreAdapter} using Guava {@link Cache}s.
 *
 * @author joseross
 */
public class GuavaCacheStoreAdapter implements CacheStoreAdapter, DisposableBean {

    protected final Map<String, Cache<Object, Object>> caches = new ConcurrentHashMap<>();

    /**
     * Destroy method, called by the Spring container. Calls {@link Cache#cleanUp()} for all instances.
     */
    @Override
    public void destroy() {
        caches.forEach((scope, cache) -> {
            cache.invalidateAll();
            cache.cleanUp();
        });
    }

    @Override
    public boolean hasScope(String scope) {
        return caches.containsKey(scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getScopes() {
        return caches.keySet();
    }

    /**
     * Adds a new scope. The scope is an instance of Guava's {@link Cache}.
     *
     * @param scope            the name of the scope
     * @param maxItemsInMemory the maximum number of items in memory, before they are evicted
     */
    @Override
    public void addScope(String scope, int maxItemsInMemory) {
        caches.put(scope, CacheBuilder.newBuilder().recordStats().maximumSize(maxItemsInMemory).build());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeScope(String scope) {
        caches.remove(scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSize(String scope) {
        return (int) caches.get(scope).size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Object> getKeys(String scope) {
        return caches.get(scope).asMap().keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasKey(String scope, Object key) {
        return caches.get(scope).asMap().containsKey(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CacheItem get(String scope, Object key) {
        var element = caches.get(scope).getIfPresent(key);
        if (element != null) {
            return (CacheItem) element;
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(CacheItem item) {
        caches.get(item.getScope()).put(item.getKey(), item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(String scope, Object key) {
        caches.get(scope).invalidate(key);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearAll() {
        caches.values().forEach(Cache::invalidateAll);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearScope(String scope) {
        caches.get(scope).invalidateAll();
    }

    /**
     * {@inheritDoc}
     */
    public CacheStatistics getStatistics(String scope) {
        return new GuavaCacheStatistics(caches.get(scope));
    }

}
