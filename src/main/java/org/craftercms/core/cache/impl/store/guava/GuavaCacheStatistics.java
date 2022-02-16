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
import org.craftercms.core.cache.CacheStatistics;

/**
 * @author joseross
 * @since
 */
public class GuavaCacheStatistics extends CacheStatistics {

    protected final long hitCount;
    protected final long missCount;
    protected final long loadSuccessCount;
    protected final long loadExceptionCount;
    protected final long totalLoadTime;
    protected final long evictionCount;

    public <K, V> GuavaCacheStatistics(Cache<K, V> cache) {
        super(cache.size());
        var stats = cache.stats();
        hitCount = stats.hitCount();
        missCount = stats.missCount();
        loadSuccessCount = stats.loadSuccessCount();
        loadExceptionCount = stats.loadExceptionCount();
        totalLoadTime = stats.totalLoadTime();
        evictionCount = stats.evictionCount();
    }

    public long getHitCount() {
        return hitCount;
    }

    public long getMissCount() {
        return missCount;
    }

    public long getLoadSuccessCount() {
        return loadSuccessCount;
    }

    public long getLoadExceptionCount() {
        return loadExceptionCount;
    }

    public long getTotalLoadTime() {
        return totalLoadTime;
    }

    public long getEvictionCount() {
        return evictionCount;
    }

}
