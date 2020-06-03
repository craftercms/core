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
package org.craftercms.core.cache.impl.store;

import java.util.Collection;
import java.util.Collections;

import org.craftercms.core.cache.CacheItem;
import org.craftercms.core.cache.CacheStatistics;
import org.craftercms.core.cache.impl.CacheStoreAdapter;

/**
 * Implementation of a {@link org.craftercms.core.cache.impl.CacheStoreAdapter} that uses no data structure and whose
 * operations do nothing. Useful
 * in scenarios where you need a cache reference but don't need to cache anything or it's not appropriate, and when
 * testing or troubleshooting.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public class NoopCacheStoreAdapter implements CacheStoreAdapter {

    @Override
    public boolean hasScope(String scope) throws Exception {
        return false;
    }

    public Collection<String> getScopes() {
        return Collections.emptyList();
    }

    public void addScope(String scope, int maxItemsInMemory) {
    }

    public void removeScope(String scope) {
    }

    public int getSize(String scope) {
        return 0;
    }

    public Collection<Object> getKeys(String scope) {
        return Collections.emptyList();
    }

    public boolean hasKey(String scope, Object key) {
        return false;
    }

    public CacheItem get(String scope, Object key) {
        return null;
    }

    public void put(CacheItem item) {
    }

    public boolean remove(String scope, Object key) {
        return false;
    }

    public void clearAll() {
    }

    public void clearScope(String scope) {
    }

    public CacheStatistics getStatistics(String scope) {
        return CacheStatistics.EMPTY;
    }
}
