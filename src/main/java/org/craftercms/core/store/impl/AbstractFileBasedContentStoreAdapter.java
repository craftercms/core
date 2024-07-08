/*
 * Copyright (C) 2007-2023 Crafter Software Corporation. All Rights Reserved.
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
package org.craftercms.core.store.impl;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.craftercms.commons.validation.ValidationResult;
import org.craftercms.commons.validation.ValidationUtils;
import org.craftercms.core.exception.*;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Content;
import org.craftercms.core.service.Context;
import org.craftercms.core.service.Item;
import org.craftercms.core.util.ContentStoreUtils;
import org.craftercms.core.util.cache.impl.CachingAwareList;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Validator;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import static java.lang.String.format;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

/**
 * File-based content store adapter. Takes away common stuff from actual implementations, like handling metadata files
 * and loading descriptor DOMs.
 *
 * @author Alfonso Vásquez
 */
public abstract class AbstractFileBasedContentStoreAdapter extends AbstractCachedContentStoreAdapter {

    public static final String DEFAULT_CHARSET = "UTF-8";

    protected Validator pathValidator;
    protected String charset;
    protected String descriptorFileExtension;
    protected String metadataFileExtension;
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFileBasedContentStoreAdapter.class);

    public AbstractFileBasedContentStoreAdapter() {
        charset = DEFAULT_CHARSET;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    @Required
    public void setPathValidator(final Validator pathValidator) {
        this.pathValidator = pathValidator;
    }

    @Required
    public void setDescriptorFileExtension(String descriptorFileExtension) {
        this.descriptorFileExtension = descriptorFileExtension;
    }

    @Required
    public void setMetadataFileExtension(String metadataFileExtension) {
        this.metadataFileExtension = metadataFileExtension;
    }

    @Override
    public boolean doExists(Context context, CachingOptions cachingOptions, String path)
        throws InvalidScopeException, StoreException {
        return findFile(context, cachingOptions, path) != null;
    }

    @Override
    protected Content doFindContent(Context context, CachingOptions cachingOptions, String path)
        throws InvalidContextException, StoreException {
        validatePath(path);

        path = ContentStoreUtils.normalizePath(path);

        File file = findFile(context, cachingOptions, path);

        if (file == null) {
            return null;
        }
        if (!file.isFile()) {
            throw new StoreException("Unable to find content: " + file + " is not a file");
        }
        return getContent(context, cachingOptions, file);
    }

    @Override
    protected Item doFindItem(Context context, CachingOptions cachingOptions, String path, boolean withDescriptor)
        throws InvalidContextException, PathNotFoundException, XmlFileParseException, StoreException {
        validatePath(path);

        path = ContentStoreUtils.normalizePath(path);

        File file = findFile(context, cachingOptions, path);

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

                descriptorFile = findFile(context, cachingOptions, descriptorPath);
                if (descriptorFile != null && !descriptorFile.isFile()) {
                    throw new StoreException("Descriptor file at " + descriptorFile + " is not really a file");
                }
            }

            if (descriptorFile != null) {
                try {
                    InputStream fileInputStream = getContent(context, cachingOptions, descriptorFile).getInputStream();
                    Reader fileReader = new InputStreamReader(fileInputStream, charset);

                    try {
                        item.setDescriptorDom(createXmlReader().read(fileReader));
                    } finally {
                        IOUtils.closeQuietly(fileReader);
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
    protected List<Item> doFindItems(Context context, CachingOptions cachingOptions, String path)
            throws InvalidContextException, PathNotFoundException,
        XmlFileParseException, StoreException {
        validatePath(path);

        path = ContentStoreUtils.normalizePath(path);

        File dir = findFile(context, cachingOptions, path);

        if (dir == null) {
            return null;
        }

        if (!dir.isDirectory()) {
            throw new StoreException(format("The path '%s' doesn't correspond to a dir", path));
        }

        List<File> children = getChildren(context, cachingOptions, dir);
        CachingAwareList<Item> items = new CachingAwareList<>(children.size());

        if (isNotEmpty(children)) {
            for (File child : children) {
                // Ignore any item metadata file. Metadata file DOMs are included in their respective
                // items.
                if (!child.isFile() || !child.getName().endsWith(metadataFileExtension)) {
                    String fileRelPath = path + (!path.equals("/")? "/": "") + child.getName();
                    Item item = findItem(context, cachingOptions, fileRelPath, false);

                    if (item != null) {
                        items.add(item);
                    }
                }
            }
        }

        return items;
    }

    /**
     * Creates and configures an XML SAX reader.
     */
    protected SAXReader createXmlReader() {
        SAXReader xmlReader = new SAXReader();
        xmlReader.setMergeAdjacentText(true);
        xmlReader.setStripWhitespaceText(true);
        xmlReader.setIgnoreComments(true);

        try {
            xmlReader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            xmlReader.setFeature("http://xml.org/sax/features/external-general-entities", false);
            xmlReader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        }catch (SAXException ex){
            LOGGER.error("Unable to turn off external entity loading, This could be a security risk.", ex);
        }

        return xmlReader;
    }

    protected void validatePath(String path) throws StoreException {
        ValidationResult result = ValidationUtils.validateValue(pathValidator, path, "path");

        if (result.hasErrors()) {
            throw new StoreException(format("Validation of path '%s' failed. Errors: %s", path, result.getErrors()));
        }
    }

    /**
     * Returns the {@link Content} for the given file.
     */
    protected abstract Content getContent(Context context, CachingOptions cachingOptions,
                                          File file) throws InvalidContextException, StoreException;

    /**
     * Returns the {@link File} at the given path, returning null if not found.
     */
    protected abstract File findFile(Context context, CachingOptions cachingOptions,
                                     String path) throws InvalidContextException, StoreException;

    /**
     * Returns the list of children of the given directory.
     */
    protected abstract List<File> getChildren(Context context, CachingOptions cachingOptions,
                                              File dir) throws InvalidContextException, StoreException;

}
