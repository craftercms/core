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
package org.craftercms.core.service;

import org.craftercms.core.cache.CacheItem;

/**
 * Set of options that should be used when caching in a service call. {@code expireAfter} and {@code refreshFrequency}
 * are expressed in ticks.
 *
 * @author Alfonso Vásquez
 */
public class CachingOptions {

    public static final CachingOptions DEFAULT_CACHING_OPTIONS = new CachingOptions();
    public static final CachingOptions CACHE_OFF_CACHING_OPTIONS = new CachingOptions(false, 0, 0);

    private boolean doCaching;
    private long expireAfter;
    private long refreshFrequency;

    public CachingOptions() {
        this.doCaching = true;
        this.expireAfter = CacheItem.NEVER_EXPIRE;
        this.refreshFrequency = CacheItem.NEVER_REFRESH;
    }

    public CachingOptions(boolean doCaching, long expireAfter, long refreshFrequency) {
        this.doCaching = doCaching;
        this.expireAfter = expireAfter;
        this.refreshFrequency = refreshFrequency;
    }

    public boolean doCaching() {
        return doCaching;
    }

    public void setDoCaching(boolean doCaching) {
        this.doCaching = doCaching;
    }

    public long getExpireAfter() {
        return expireAfter;
    }

    public void setExpireAfter(long expireAfter) {
        this.expireAfter = expireAfter;
    }

    public long getRefreshFrequency() {
        return refreshFrequency;
    }

    public void setRefreshFrequency(long refreshFrequency) {
        this.refreshFrequency = refreshFrequency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CachingOptions options = (CachingOptions)o;

        if (doCaching != options.doCaching) {
            return false;
        }
        if (expireAfter != options.expireAfter) {
            return false;
        }
        if (refreshFrequency != options.refreshFrequency) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (doCaching? 1: 0);
        result = 31 * result + (int)(expireAfter ^ (expireAfter >>> 32));
        result = 31 * result + (int)(refreshFrequency ^ (refreshFrequency >>> 32));
        return result;
    }

}
