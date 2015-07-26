package org.craftercms.core.service;

import org.craftercms.core.store.ContentStoreAdapter;

/**
 * Contains information of the content store used by a particular tenant.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public interface Context {

    boolean DEFAULT_CACHE_ON = true;
    int DEFAULT_MAX_ALLOWED_ITEMS_IN_CACHE = 0;
    boolean DEFAULT_IGNORE_HIDDEN_FILES = true;

    String getId();

    ContentStoreAdapter getStoreAdapter();

    String getStoreServerUrl();

    String getRootFolderPath();

    boolean isCacheOn();

    int getMaxAllowedItemsInCache();

    boolean ignoreHiddenFiles();

}
