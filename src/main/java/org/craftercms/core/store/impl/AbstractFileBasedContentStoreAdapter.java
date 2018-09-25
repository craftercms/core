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
package org.craftercms.core.store.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craftercms.core.exception.InvalidContextException;
import org.craftercms.core.exception.InvalidScopeException;
import org.craftercms.core.exception.PathNotFoundException;
import org.craftercms.core.exception.StoreException;
import org.craftercms.core.exception.XmlFileParseException;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Content;
import org.craftercms.core.service.Context;
import org.craftercms.core.service.Item;
import org.craftercms.core.util.cache.impl.CachingAwareList;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Required;

/**
 * File-based content store adapter. Takes away common stuff from actual implementations,
 * like handling metadata files and
 * loading descriptor DOMs.
 *
 * @author Alfonso Vásquez
 */
public abstract class AbstractFileBasedContentStoreAdapter extends AbstractCachedContentStoreAdapter {

    private static final Log logger = LogFactory.getLog(AbstractFileBasedContentStoreAdapter.class);

    private String descriptorFileExtension;
    private String metadataFileExtension;

    @Required
    public void setDescriptorFileExtension(String descriptorFileExtension) {
        this.descriptorFileExtension = descriptorFileExtension;
    }

    @Required
    public void setMetadataFileExtension(String metadataFileExtension) {
        this.metadataFileExtension = metadataFileExtension;
    }

    @Override
    public boolean exists(Context context, String path) throws InvalidScopeException, StoreException {
        return findFile(context, path) != null;
    }

    @Override
    protected Content doFindContent(Context context, CachingOptions cachingOptions, String path)
        throws InvalidContextException, StoreException {
        path = normalizePath(path);

        File file = findFile(context, path);

        if (file != null && !file.isFile()) {
            throw new StoreException("Unable to find content: " + file + " is not a file");
        }

        return file;
    }

    @Override
    protected Item doFindItem(Context context, CachingOptions cachingOptions, String path, boolean withDescriptor)
        throws InvalidContextException, PathNotFoundException,
        XmlFileParseException, StoreException {
        path = normalizePath(path);

        File file = findFile(context, path);

        if (file == null) {
            return null;
        }

        Item item = new Item();
        item.setName(file.getName());
        item.setUrl(path);
        item.setFolder(file.isDirectory());

        if (withDescriptor) {
            File descriptorFile;

            // If it's a file and it's a descriptor, set the descriptor url to the item's path and load the file as
            // a DOM.
            if (file.isFile() && item.getName().endsWith(descriptorFileExtension)) {
                item.setDescriptorUrl(path);

                descriptorFile = file;
                // If it's not a file (a dir) or is not a descriptor (a static asset, like an image), locate the file's
                // descriptor by appending a metadata file extension to the file name. If the file exists, load it as
                // a DOM.
            } else {
                String descriptorPath = FilenameUtils.removeExtension(path) + metadataFileExtension;

                item.setDescriptorUrl(descriptorPath);

                descriptorFile = findFile(context, descriptorPath);
                if (descriptorFile != null && !descriptorFile.isFile()) {
                    throw new StoreException("Descriptor file at " + descriptorFile + " is not really a file");
                }
            }

            if (descriptorFile != null) {
                try {
                    InputStream fileInputStream = new BufferedInputStream(descriptorFile.getInputStream());
                    try {
                        item.setDescriptorDom(createXmlReader().read(fileInputStream));
                    } finally {
                        try {
                            fileInputStream.close();
                        } catch (IOException e) {
                            logger.warn("Unable to close input stream for descriptor file at " + descriptorFile, e);
                        }
                    }
                } catch (IOException e) {
                    throw new StoreException("Unable to open input stream for descriptor file at " + descriptorFile, e);
                } catch (DocumentException e) {
                    throw new XmlFileParseException("Error while parsing xml document at " + descriptorFile, e);
                }
            }
        }

        return item;
    }

    @Override
    protected List<Item> doFindItems(Context context, CachingOptions cachingOptions, String path,
                                     boolean withDescriptor) throws InvalidContextException, PathNotFoundException,
        XmlFileParseException, StoreException {
        path = normalizePath(path);

        File dir = findFile(context, path);

        if (dir == null) {
            return null;
        }

        if (!dir.isDirectory()) {
            throw new StoreException("The path " + dir + " doesn't correspond to a dir");
        }

        List<File> children = getChildren(context, dir);
        CachingAwareList<Item> items = new CachingAwareList<>(children.size());

        if (CollectionUtils.isNotEmpty(children)) {
            for (File child : children) {
                // Ignore any item metadata file. Metadata file DOMs are included in their respective
                // items.
                if (!child.isFile() || !child.getName().endsWith(metadataFileExtension)) {
                    String fileRelPath = path + (!path.equals("/")? "/": "") + child.getName();
                    Item item = findItem(context, cachingOptions, fileRelPath, withDescriptor);

                    if (item != null) {
                        items.add(item);
                    }
                }
            }
        }

        return items;
    }

    /**
     * Normalize the path: this means, append a leading '/' at the beginning and remove any trailing '/' (unless
     * the path is root). This is done for consistency in handling paths.
     *
     * @param path
     * @return the normalized path (with a leading '/' and without a trailing '/')
     */
    protected String normalizePath(String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (!path.equals("/")) {
            path = StringUtils.stripEnd(path, "/");
        }

        return path;
    }

    /**
     * Creates and configures an XML SAX reader.
     */
    protected SAXReader createXmlReader() {
        SAXReader xmlReader = new SAXReader();
        xmlReader.setMergeAdjacentText(true);
        xmlReader.setStripWhitespaceText(true);
        xmlReader.setIgnoreComments(true);

        return xmlReader;
    }

    /**
     * Returns the {@link File} at the given path, returning null if not found.
     */
    protected abstract File findFile(Context context, String path) throws InvalidContextException, StoreException;

    /**
     * Returns the list of children of the given directory.
     */
    protected abstract List<File> getChildren(Context context, File dir) throws InvalidContextException, StoreException;

}
