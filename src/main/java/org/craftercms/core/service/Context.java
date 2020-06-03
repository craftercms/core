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

package org.craftercms.core.service;

import org.craftercms.core.store.ContentStoreAdapter;

/**
 * Contains information of the content store used by a particular tenant.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public interface Context extends Cloneable {

    boolean DEFAULT_MERGING_ON = true;
    boolean DEFAULT_CACHE_ON = true;
    int DEFAULT_MAX_ALLOWED_ITEMS_IN_CACHE = 0;
    boolean DEFAULT_IGNORE_HIDDEN_FILES = true;

    String getId();

    long getCacheVersion();

    void setCacheVersion(long cacheVersion);

    String getCacheScope();

    ContentStoreAdapter getStoreAdapter();

    boolean isMergingOn();

    boolean isCacheOn();

    int getMaxAllowedItemsInCache();

    boolean ignoreHiddenFiles();

    Context clone();

}
