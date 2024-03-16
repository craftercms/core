/*
 * Copyright (C) 2007-2024 Crafter Software Corporation. All Rights Reserved.
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
package org.craftercms.core.store.impl.filesystem;

import org.craftercms.core.service.ContextImpl;

import java.util.Map;

/**
 * Extension of context that adds properties used by the {@link FileSystemContentStoreAdapter}.
 *
 * @author Alfonso Vásquez
 */
public class FileSystemContext extends ContextImpl {

    private final FileSystemFile rootFolder;

    public FileSystemContext(String id, FileSystemContentStoreAdapter storeAdapter, String rootFolderPath,
                             FileSystemFile rootFolder, boolean mergingOn, boolean cacheOn, int maxAllowedItemsInCache,
                             boolean ignoreHiddenFiles, Map<String, String> configurationVariables) {
        super(id, storeAdapter, rootFolderPath, mergingOn, cacheOn, maxAllowedItemsInCache, ignoreHiddenFiles, configurationVariables);

        this.rootFolder = rootFolder;
    }

    public FileSystemFile getRootFolder() {
        return rootFolder;
    }


}
