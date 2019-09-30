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

import org.craftercms.core.store.ContentStoreAdapter;
import org.craftercms.core.util.CacheUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default {@link Context} implementation.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public class ContextImpl implements Context {

    protected static final String CACHE_SCOPE_FORMAT = "%s-v%s";

    protected String id;
    protected ContentStoreAdapter storeAdapter;
    protected String rootFolderPath;
    protected boolean mergingOn;
    protected boolean cacheOn;
    protected volatile long cacheVersion;
    protected int maxAllowedItemsInCache;
    protected boolean ignoreHiddenFiles;

    public ContextImpl(String id, ContentStoreAdapter storeAdapter, String rootFolderPath, boolean mergingOn,
                       boolean cacheOn, int maxAllowedItemsInCache, boolean ignoreHiddenFiles) {
        this.id = id;
        this.storeAdapter = storeAdapter;
        this.rootFolderPath = rootFolderPath;
        this.mergingOn = mergingOn;
        this.cacheOn = cacheOn;
        this.cacheVersion = System.nanoTime();
        this.maxAllowedItemsInCache = maxAllowedItemsInCache;
        this.ignoreHiddenFiles = ignoreHiddenFiles;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getCacheVersion() {
        return cacheVersion;
    }

    @Override
    public void setCacheVersion(long cacheVersion) {
        this.cacheVersion = cacheVersion;
    }

    @Override
    public String getCacheScope() {
        return String.format(CACHE_SCOPE_FORMAT, id, cacheVersion);
    }

    @Override
    public ContentStoreAdapter getStoreAdapter() {
        return storeAdapter;
    }

    @Override
    public boolean isMergingOn() {
        return mergingOn;
    }

    @Override
    public boolean isCacheOn() {
        return cacheOn;
    }

    @Override
    public int getMaxAllowedItemsInCache() {
        return maxAllowedItemsInCache;
    }

    @Override
    public boolean ignoreHiddenFiles() {
        return ignoreHiddenFiles;
    }

    @Override
    public Context clone() {
        try {
            return (Context) super.clone();
        } catch (CloneNotSupportedException e) {
            // Shouldn't happen
            throw new RuntimeException(e);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Context context = (Context)o;

        if (!getId().equals(context.getId())) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
               "id='" + id + '\'' +
               ", rootFolderPath='" + rootFolderPath + '\'' +
               '}';
    }

}
