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
package org.craftercms.core.store.impl.filesystem;

import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.craftercms.core.exception.AuthenticationException;
import org.craftercms.core.exception.InvalidContextException;
import org.craftercms.core.exception.PathNotFoundException;
import org.craftercms.core.exception.StoreException;
import org.craftercms.core.service.Context;
import org.craftercms.core.store.impl.AbstractFileBasedContentStoreAdapter;
import org.craftercms.core.store.impl.File;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * Implementation of {@link org.craftercms.core.store.ContentStoreAdapter} that enables access to a store in the
 * filesystem.
 *
 * @author Alfonso Vásquez
 */
public class FileSystemContentStoreAdapter extends AbstractFileBasedContentStoreAdapter implements ResourceLoaderAware {

    public static final String STORE_TYPE = "filesystem";

    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public Context createContext(String id, String storeServerUrl, String username, String password,
                                 String rootFolderPath, boolean cacheOn,
                                 int maxAllowedItemsInCache, boolean ignoreHiddenFiles) throws StoreException,
            AuthenticationException {
        try {
            Resource rootFolderResource = resourceLoader.getResource(rootFolderPath);
            FileSystemFile rootFolder = new FileSystemFile(rootFolderResource.getFile());

            return new FileSystemContext(id, this, null, rootFolderPath, rootFolder, cacheOn, maxAllowedItemsInCache,
                    ignoreHiddenFiles);
        } catch (IOException e) {
            throw new StoreException("Unable to get a File object from the specified rootFolderPath", e);
        }
    }

    @Override
    public void destroyContext(Context context) throws InvalidContextException, StoreException,
            AuthenticationException {
    }

    /**
     * Returns the file for the specified relative path.
     */
    @Override
    protected File getFile(Context context, String path) throws InvalidContextException, PathNotFoundException,
            StoreException {
        FileSystemFile rootFolder = ((FileSystemContext) context).getRootFolder();

        if ( StringUtils.isNotEmpty(path) ) {
            FileSystemFile file = new FileSystemFile(rootFolder, path);
            if ( file.getFile().exists() ) {
                return file;
            } else {
                throw new PathNotFoundException("File " + file + " can't be found");
            }
        } else {
            return rootFolder;
        }
    }

    @Override
    protected List<File> getChildren(Context context, File dir) throws InvalidContextException,
            PathNotFoundException, StoreException {
        java.io.File[] listing;
        if ( context.ignoreHiddenFiles() ) {
            listing = ((FileSystemFile) dir).getFile().listFiles(IgnoreHiddenFileFilter.INSTANCE);
        } else {
            listing = ((FileSystemFile) dir).getFile().listFiles();
        }

        if ( listing != null ) {
            List<File> children = new ArrayList<File>(listing.length);
            for (java.io.File file : listing) {
                children.add(new FileSystemFile(file));
            }

            return children;
        } else {
            return null;
        }
    }

    private static class IgnoreHiddenFileFilter implements FileFilter {

        public static final IgnoreHiddenFileFilter INSTANCE = new IgnoreHiddenFileFilter();

        private IgnoreHiddenFileFilter() {
        }

        @Override
        public boolean accept(java.io.File pathname) {
            return !pathname.isHidden();
        }

    }

}
