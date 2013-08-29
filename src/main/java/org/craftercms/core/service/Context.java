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
package org.craftercms.core.service;

import org.craftercms.core.store.ContentStoreAdapter;

/**
 * Contains information of the content store used by a particular tenant.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public class Context {

    public static final boolean DEFAULT_CACHE_ON = true;
    public static final int DEFAULT_MAX_ALLOWED_ITEMS_IN_CACHE = 0;
    public static final boolean DEFAULT_IGNORE_HIDDEN_FILES = true;

    protected String id;
    protected ContentStoreAdapter storeAdapter;
    protected String storeServerUrl;
    protected String rootFolderPath;
    protected boolean cacheOn;
    protected int maxAllowedItemsInCache;
    protected boolean ignoreHiddenFiles;

    public Context(String id, ContentStoreAdapter storeAdapter, String storeServerUrl, String rootFolderPath, boolean cacheOn,
                   int maxAllowedItemsInCache, boolean ignoreHiddenFiles) {
        this.id = id;
        this.storeAdapter = storeAdapter;
        this.storeServerUrl = storeServerUrl;
        this.rootFolderPath = rootFolderPath;
        this.cacheOn = cacheOn;
        this.maxAllowedItemsInCache = maxAllowedItemsInCache;
        this.ignoreHiddenFiles = ignoreHiddenFiles;
    }

    public String getId() {
        return id;
    }

    public ContentStoreAdapter getStoreAdapter() {
        return storeAdapter;
    }

    public String getStoreServerUrl() {
        return storeServerUrl;
    }

    public String getRootFolderPath() {
        return rootFolderPath;
    }

    public boolean isCacheOn() {
        return cacheOn;
    }

    public int getMaxAllowedItemsInCache() {
        return maxAllowedItemsInCache;
    }

    public boolean ignoreHiddenFiles() {
        return ignoreHiddenFiles;
    }

    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        Context context = (Context) o;

        if ( !getId().equals(context.getId()) ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public String toString() {
        return "Context[" +
                "id='" + id + '\'' +
                ", storeAdapter='" + storeAdapter + '\'' +
                ", storeServerUrl='" + storeServerUrl + '\'' +
                ", rootFolderPath='" + rootFolderPath + '\'' +
                ", cacheOn=" + cacheOn +
                ", maxAllowedItemsInCache=" + maxAllowedItemsInCache +
                ", ignoreHiddenFiles=" + ignoreHiddenFiles +
                ']';
    }

}
