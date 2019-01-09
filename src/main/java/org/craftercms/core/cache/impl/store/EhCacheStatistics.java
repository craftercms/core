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

package org.craftercms.core.cache.impl.store;

import net.sf.ehcache.statistics.FlatStatistics;
import org.craftercms.core.cache.CacheStatistics;

public class EhCacheStatistics extends CacheStatistics {

    private long localHeapSize;
    private long localOffHeapSize;
    private long localDiskSize;
    private double cacheHitRatio;
    private long cacheHitCount;
    private long cacheMissCount;
    private long cacheMissExpiredCount;
    private long cacheMissNotFoundCount;

    public EhCacheStatistics(FlatStatistics stats) {
        size = stats.getSize();
        localHeapSize = stats.getLocalHeapSize();
        localOffHeapSize = stats.getLocalOffHeapSize();
        localDiskSize = stats.getLocalDiskSize();
        cacheHitRatio = stats.cacheHitRatio();
        cacheHitCount = stats.cacheHitCount();
        cacheMissExpiredCount = stats.cacheMissExpiredCount();
        cacheMissNotFoundCount = stats.cacheMissNotFoundCount();
        cacheMissCount = stats.cacheMissCount();
    }

    public long getLocalHeapSize() {
        return localHeapSize;
    }

    public long getLocalOffHeapSize() {
        return localOffHeapSize;
    }

    public long getLocalDiskSize() {
        return localDiskSize;
    }

    public double getCacheHitRatio() {
        return cacheHitRatio;
    }

    public long getCacheHitCount() {
        return cacheHitCount;
    }

    public long getCacheMissCount() {
        return cacheMissCount;
    }

    public long getCacheMissExpiredCount() {
        return cacheMissExpiredCount;
    }

    public long getCacheMissNotFoundCount() {
        return cacheMissNotFoundCount;
    }

}
