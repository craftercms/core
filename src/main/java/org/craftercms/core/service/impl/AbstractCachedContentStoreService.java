/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
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
package org.craftercms.core.service.impl;

import java.util.List;

import org.craftercms.commons.lang.Callback;
import org.craftercms.core.exception.InvalidContextException;
import org.craftercms.core.exception.ItemProcessingException;
import org.craftercms.core.exception.PathNotFoundException;
import org.craftercms.core.exception.StoreException;
import org.craftercms.core.exception.XmlFileParseException;
import org.craftercms.core.exception.XmlMergeException;
import org.craftercms.core.processors.ItemProcessor;
import org.craftercms.core.processors.impl.ItemProcessorPipeline;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.ContentStoreService;
import org.craftercms.core.service.Context;
import org.craftercms.core.service.Item;
import org.craftercms.core.service.ItemFilter;
import org.craftercms.core.service.Tree;
import org.craftercms.core.util.cache.CacheTemplate;
import org.craftercms.core.util.cache.impl.CachingAwareList;

/**
 * Abstract {@link ContentStoreService} that provides caching to actual implementations. Subclasses just have to
 * implement the {@code do*} methods to provide the cacheable objects. Caching works the following way: when an
 * interface method is called, and there's no cached object associated to the parameter-based compound key, the do*
 * method of the same name is called to execute the actual method code. The returned object is then cached with the
 * compound key.
 *
 * @author Alfonso Vásquez
 */
public abstract class AbstractCachedContentStoreService implements ContentStoreService {

    /**
     * A constant added to all {@code getItem()} cache keys.
     */
    public static final String CONST_KEY_ELEM_ITEM = "contentStoreService.item";
    /**
     * A constant added to all {@code getChildren()} cache keys.
     */
    public static final String CONST_KEY_ELEM_CHILDREN = "contentStoreService.children";
    /**
     * A constant added to all {@code getTree()} cache keys.
     */
    public static final String CONST_KEY_ELEM_TREE = "contentStoreService.tree";

    /**
     * A constant added to all {@code exists()} cache keys.
     */
    public static final String CONST_KEY_ELEM_EXISTS = "contentStoreService.exists";

    /**
     * Helper that uses an array of key elements (as the compound key) an a callback (when no cache object is found)
     * for caching.
     */
    protected CacheTemplate cacheTemplate;
    /**
     * The default caching options to use when none are specified in the method. Can be null.
     */
    protected CachingOptions defaultCachingOptions;

    /**
     * Item processor used for flattening descriptors
     */
    protected ItemProcessor flatteningProcessor;

    /**
     * Sets the default caching options to use when none are specified in the method. Can be null.
     */
    public void setDefaultCachingOptions(CachingOptions defaultCachingOptions) {
        this.defaultCachingOptions = defaultCachingOptions;
    }

    public void setFlatteningProcessor(ItemProcessor flatteningProcessor) {
        this.flatteningProcessor = flatteningProcessor;
    }

    public AbstractCachedContentStoreService(CacheTemplate cacheTemplate) {
        this.cacheTemplate = cacheTemplate;
    }

    @Override
    public boolean exists(final Context context, final String url)
        throws InvalidContextException, PathNotFoundException, StoreException {
        return exists(context, null, url);
    }

    @Override
    public boolean exists(final Context context, final CachingOptions cachingOptions, final String url)
        throws InvalidContextException, PathNotFoundException, StoreException {
        final CachingOptions actualCachingOptions = cachingOptions != null? cachingOptions: defaultCachingOptions;

        return cacheTemplate.getObject(context, actualCachingOptions, new Callback<Boolean>() {

            @Override
            public Boolean execute() {
                return doExists(context, actualCachingOptions, url);
            }

            @Override
            public String toString() {
                return String.format(AbstractCachedContentStoreService.this.getClass().getName() + ".exists(%s, %s)",
                    context, url);
            }

        }, url, CONST_KEY_ELEM_EXISTS);
    }

    protected ItemProcessor getProcessor(ItemProcessor processor, boolean flatten) {
        if (!flatten) {
            return processor;
        }
        if (processor == null) {
            return  flatteningProcessor;
        } else {
            return new ItemProcessorPipeline(flatteningProcessor, processor);
        }
    }

    @Override
    public Item findItem(final Context context, final CachingOptions cachingOptions, final String url,
                         final ItemProcessor processor, final boolean flatten)
            throws InvalidContextException, XmlFileParseException,
        XmlMergeException, ItemProcessingException, StoreException {
        final CachingOptions actualCachingOptions = cachingOptions != null? cachingOptions: defaultCachingOptions;
        var actualProcessor = getProcessor(processor, flatten);

        return cacheTemplate.getObject(context, actualCachingOptions, new Callback<>() {

            @Override
            public Item execute() {
                return doFindItem(context, actualCachingOptions, url, actualProcessor);
            }

            @Override
            public String toString() {
                return String.format(AbstractCachedContentStoreService.this.getClass().getName() +
                                     ".getItem(%s, %s, %s)", context, url, actualProcessor);
            }

        }, url, actualProcessor, CONST_KEY_ELEM_ITEM);
    }

    @Override
    public Item getItem(Context context, CachingOptions cachingOptions, String url,
                        ItemProcessor processor, boolean flatten) throws InvalidContextException, PathNotFoundException,
        XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException {
        Item item = findItem(context, cachingOptions, url, processor, flatten);
        if (item != null) {
            return item;
        } else {
            throw new PathNotFoundException("No item found at " + url);
        }
    }

    @Override
    public List<Item> findChildren(final Context context, final CachingOptions cachingOptions, final String url,
                                   final ItemFilter filter, final ItemProcessor processor, final boolean flatten)
            throws InvalidContextException, XmlFileParseException, XmlMergeException, ItemProcessingException,
            StoreException {
        final CachingOptions actualCachingOptions = cachingOptions != null? cachingOptions: defaultCachingOptions;
        var actualProcessor = getProcessor(processor, flatten);

        return cacheTemplate.getObject(context, actualCachingOptions, new Callback<>() {

            @Override
            public List<Item> execute() {
                var children = doFindChildren(context, actualCachingOptions, url, filter, actualProcessor, flatten);
                if (children != null) {
                    if (children instanceof CachingAwareList) {
                        return children;
                    } else {
                        return new CachingAwareList<>(children);
                    }
                } else {
                    return null;
                }
            }

            @Override
            public String toString() {
                return String.format(AbstractCachedContentStoreService.this.getClass().getName() +
                                     ".getChildren(%s, %s, %s, %s)", context, url, filter, actualProcessor);
            }

        }, url, filter, actualProcessor, CONST_KEY_ELEM_CHILDREN);
    }

    @Override
    public List<Item> getChildren(Context context, CachingOptions cachingOptions, String url, ItemFilter filter,
                                  ItemProcessor processor, boolean flatten) throws InvalidContextException,
            PathNotFoundException, XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException {
        List<Item> children = findChildren(context, cachingOptions, url, filter, processor, flatten);
        if (children != null) {
            return children;
        } else {
            throw new PathNotFoundException("No folder found at " + url);
        }
    }

    @Override
    public Tree findTree(final Context context, final CachingOptions cachingOptions, final String url, final int depth,
                         final ItemFilter filter, final ItemProcessor processor, final boolean flatten)
            throws InvalidContextException, XmlFileParseException, XmlMergeException, ItemProcessingException,
            StoreException {
        final CachingOptions actualCachingOptions = cachingOptions != null? cachingOptions: defaultCachingOptions;
        var actualProcessor = getProcessor(processor, flatten);

        return cacheTemplate.getObject(context, actualCachingOptions, new Callback<>() {

            @Override
            public Tree execute() {
                return doFindTree(context, actualCachingOptions, url, depth, filter, actualProcessor, flatten);
            }

            @Override
            public String toString() {
                return String.format(AbstractCachedContentStoreService.this.getClass().getName() +
                                     ".getTree(%s, %s, %d, %s, %s)", context, url, depth, filter, actualProcessor);
            }

        }, url, depth, filter, actualProcessor, CONST_KEY_ELEM_TREE);
    }

    @Override
    public Tree getTree(Context context, CachingOptions cachingOptions, String url, int depth, ItemFilter filter,
                        ItemProcessor processor, boolean flatten) throws InvalidContextException, PathNotFoundException,
        XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException {
        Tree tree = findTree(context, cachingOptions, url, depth, filter, processor, flatten);
        if (tree != null) {
            return tree;
        } else {
            throw new PathNotFoundException("No folder found at " + url);
        }
    }

    protected abstract boolean doExists(Context context, CachingOptions cachingOptions, String url)
        throws InvalidContextException, PathNotFoundException, StoreException;

    protected abstract Item doFindItem(Context context, CachingOptions cachingOptions, String url,
                                       ItemProcessor processor) throws InvalidContextException,
        XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException;

    protected abstract List<Item> doFindChildren(Context context, CachingOptions cachingOptions, String url,
                                                 ItemFilter filter, ItemProcessor processor, boolean flatten)
            throws InvalidContextException, XmlFileParseException, XmlMergeException, ItemProcessingException,
            StoreException;

    protected abstract Tree doFindTree(Context context, CachingOptions cachingOptions, String url, int depth,
                                       ItemFilter filter, ItemProcessor processor, boolean flatten)
            throws InvalidContextException, XmlFileParseException, XmlMergeException, ItemProcessingException,
            StoreException;

}
