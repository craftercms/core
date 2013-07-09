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
package org.craftercms.core.service.impl;

import java.util.List;

import org.craftercms.core.exception.*;
import org.craftercms.core.processors.ItemProcessor;
import org.craftercms.core.service.*;
import org.craftercms.core.util.cache.CacheCallback;
import org.craftercms.core.util.cache.CacheTemplate;
import org.craftercms.core.util.cache.impl.CachingAwareList;
import org.springframework.beans.factory.annotation.Required;

/**
 * Abstract {@link org.craftercms.core.service.ContentStoreService} that provides caching to actual implementations of the service. Subclasses
 * just have to implement the {@code do*} methods to provide the cacheable objects. Caching works the following way:
 * when a service method is called, and there's no cached object associated to the parameter-based compound key, the
 * do* method of the same name is called to execute the actual service code. The returned object is then cached with
 * the compound key.
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
     * Helper that uses an array of key elements (as the compound key) an a callback (when no cache object is found)
     * for caching.
     */
    protected CacheTemplate cacheTemplate;

    /**
     * Sets the {@code CacheTemplate}, which is used as a helper for caching.
     */
    @Required
    public void setCacheTemplate(CacheTemplate cacheTemplate) {
        this.cacheTemplate = cacheTemplate;
    }

    @Override
    public Item getItem(Context context, String url) throws InvalidContextException, PathNotFoundException, XmlFileParseException,
            XmlMergeException, ItemProcessingException, StoreException {
        return getItem(context, CachingOptions.DEFAULT_CACHING_OPTIONS, url, null);
    }

    @Override
    public Item getItem(Context context, CachingOptions cachingOptions, String url) throws InvalidContextException,
            PathNotFoundException, XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException {
        return getItem(context, cachingOptions, url, null);
    }

    @Override
    public Item getItem(Context context, String url, ItemProcessor processor) throws InvalidContextException, PathNotFoundException,
            XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException {
        return getItem(context, CachingOptions.DEFAULT_CACHING_OPTIONS, url, processor);
    }

    @Override
    public Item getItem(final Context context, final CachingOptions cachingOptions, final String url, final ItemProcessor processor)
            throws InvalidContextException, PathNotFoundException, XmlFileParseException, XmlMergeException, ItemProcessingException,
            StoreException {
        return cacheTemplate.execute(context, cachingOptions, new CacheCallback<Item>() {

            @Override
            public Item doCacheable() {
                return doGetItem(context, cachingOptions, url, processor);
            }

            @Override
            public String toString() {
                return String.format(AbstractCachedContentStoreService.this.getClass().getName() + ".getItem(%s, %s, %s)",
                        context, url, processor);
            }

        }, context, url, processor, CONST_KEY_ELEM_ITEM);
    }

    @Override
    public List<Item> getChildren(Context context, String url) throws InvalidContextException, PathNotFoundException,
            XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException {
        return getChildren(context, CachingOptions.DEFAULT_CACHING_OPTIONS, url, null, null);
    }

    @Override
    public List<Item> getChildren(Context context, CachingOptions cachingOptions, String url) throws InvalidContextException,
            PathNotFoundException, XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException {
        return getChildren(context, cachingOptions, url, null, null);
    }

    @Override
    public List<Item> getChildren(Context context, String url, ItemFilter filter) throws InvalidContextException, PathNotFoundException,
            XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException {
        return getChildren(context, CachingOptions.DEFAULT_CACHING_OPTIONS, url, filter, null);
    }

    @Override
    public List<Item> getChildren(Context context, CachingOptions cachingOptions, String url, ItemFilter filter)
            throws InvalidContextException, PathNotFoundException, XmlFileParseException, XmlMergeException, ItemProcessingException,
            StoreException {
        return getChildren(context, cachingOptions, url, filter, null);
    }

    @Override
    public List<Item> getChildren(Context context, String url, ItemProcessor processor) throws InvalidContextException,
            PathNotFoundException, XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException {
        return getChildren(context, CachingOptions.DEFAULT_CACHING_OPTIONS, url, null, processor);
    }

    @Override
    public List<Item> getChildren(Context context, CachingOptions cachingOptions, String url, ItemProcessor processor)
            throws InvalidContextException, PathNotFoundException, XmlFileParseException, XmlMergeException, ItemProcessingException,
            StoreException {
        return getChildren(context, cachingOptions, url, null, processor);
    }

    @Override
    public List<Item> getChildren(Context context, String url, ItemFilter filter, ItemProcessor processor)
            throws InvalidContextException, PathNotFoundException, XmlFileParseException, XmlMergeException, ItemProcessingException,
            StoreException {
        return getChildren(context, CachingOptions.DEFAULT_CACHING_OPTIONS, url, filter, processor);
    }

    @Override
    public List<Item> getChildren(final Context context, final CachingOptions cachingOptions, final String url, final ItemFilter filter,
                                  final ItemProcessor processor) throws InvalidContextException, PathNotFoundException,
            XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException {
        return cacheTemplate.execute(context, cachingOptions, new CacheCallback<List<Item>>() {

            @Override
            public List<Item> doCacheable() {
                List<Item> children = doGetChildren(context, cachingOptions, url, filter, processor);
                if ( children instanceof CachingAwareList ) {
                    return children;
                } else {
                    return new CachingAwareList<Item>(children);
                }
            }

            @Override
            public String toString() {
                return String.format(AbstractCachedContentStoreService.this.getClass().getName() + ".getChildren(%s, %s, %s, %s)",
                        context, url, filter, processor);
            }

        }, context, url, filter, processor, CONST_KEY_ELEM_CHILDREN);
    }

    @Override
    public Tree getTree(Context context, String url) throws InvalidContextException, PathNotFoundException, XmlFileParseException,
            XmlMergeException, ItemProcessingException, StoreException {
        return getTree(context, CachingOptions.DEFAULT_CACHING_OPTIONS, url, UNLIMITED_TREE_DEPTH, null, null);
    }

    @Override
    public Tree getTree(Context context, CachingOptions cachingOptions, String url) throws InvalidContextException,
            PathNotFoundException, XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException {
        return getTree(context, cachingOptions, url, UNLIMITED_TREE_DEPTH, null, null);
    }

    @Override
    public Tree getTree(Context context, String url, int depth) throws InvalidContextException, PathNotFoundException,
            XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException {
        return getTree(context, CachingOptions.DEFAULT_CACHING_OPTIONS, url, depth, null, null);
    }

    @Override
    public Tree getTree(Context context, CachingOptions cachingOptions, String url, int depth) throws InvalidContextException,
            PathNotFoundException, XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException {
        return getTree(context, cachingOptions, url, depth, null, null);
    }

    @Override
    public Tree getTree(Context context, String url, ItemFilter filter) throws InvalidContextException, PathNotFoundException,
            XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException {
        return getTree(context, CachingOptions.DEFAULT_CACHING_OPTIONS, url, UNLIMITED_TREE_DEPTH, filter, null);
    }

    @Override
    public Tree getTree(Context context, CachingOptions cachingOptions, String url, ItemFilter filter)
            throws InvalidContextException, PathNotFoundException, XmlFileParseException, XmlMergeException, ItemProcessingException,
            StoreException {
        return getTree(context, cachingOptions, url, UNLIMITED_TREE_DEPTH, filter, null);
    }

    @Override
    public Tree getTree(Context context, String url, int depth, ItemFilter filter) throws InvalidContextException, PathNotFoundException,
            XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException {
        return getTree(context, CachingOptions.DEFAULT_CACHING_OPTIONS, url, depth, filter, null);
    }

    @Override
    public Tree getTree(Context context, CachingOptions cachingOptions, String url, int depth, ItemFilter filter)
            throws InvalidContextException, PathNotFoundException, XmlFileParseException, XmlMergeException, ItemProcessingException,
            StoreException {
        return getTree(context, cachingOptions, url, depth, filter, null);
    }

    @Override
    public Tree getTree(Context context, String url, ItemProcessor processor) throws InvalidContextException, PathNotFoundException,
            XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException {
        return getTree(context, CachingOptions.DEFAULT_CACHING_OPTIONS, url, UNLIMITED_TREE_DEPTH, null, processor);
    }

    @Override
    public Tree getTree(Context context, CachingOptions cachingOptions, String url, ItemProcessor processor)
            throws InvalidContextException, PathNotFoundException, XmlFileParseException, XmlMergeException, ItemProcessingException,
            StoreException {
        return getTree(context, cachingOptions, url, UNLIMITED_TREE_DEPTH, null, processor);
    }

    @Override
    public Tree getTree(Context context, String url, int depth, ItemProcessor processor) throws InvalidContextException,
            PathNotFoundException, XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException {
        return getTree(context, CachingOptions.DEFAULT_CACHING_OPTIONS, url, depth, null, processor);
    }

    @Override
    public Tree getTree(Context context, CachingOptions cachingOptions, String url, int depth, ItemProcessor processor)
            throws InvalidContextException, PathNotFoundException, XmlFileParseException, XmlMergeException, ItemProcessingException,
            StoreException {
        return getTree(context, cachingOptions, url, depth, null, processor);
    }

    @Override
    public Tree getTree(Context context, String url, ItemFilter filter, ItemProcessor processor) throws InvalidContextException,
            PathNotFoundException, XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException {
        return getTree(context, CachingOptions.DEFAULT_CACHING_OPTIONS, url, UNLIMITED_TREE_DEPTH, filter, processor);
    }

    @Override
    public Tree getTree(Context context, CachingOptions cachingOptions, String url, ItemFilter filter, ItemProcessor processor)
            throws InvalidContextException, PathNotFoundException, XmlFileParseException, XmlMergeException, ItemProcessingException,
            StoreException {
        return getTree(context, cachingOptions, url, UNLIMITED_TREE_DEPTH, filter, processor);
    }

    @Override
    public Tree getTree(Context context, String url, int depth, ItemFilter filter, ItemProcessor processor) throws InvalidContextException,
            PathNotFoundException, XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException {
        return getTree(context, CachingOptions.DEFAULT_CACHING_OPTIONS, url, depth, filter, processor);
    }

    @Override
    public Tree getTree(final Context context, final CachingOptions cachingOptions, final String url, final int depth,
                        final ItemFilter filter, final ItemProcessor processor) throws InvalidContextException, PathNotFoundException,
            XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException {
        return cacheTemplate.execute(context, cachingOptions, new CacheCallback<Tree>() {

            @Override
            public Tree doCacheable() {
                return doGetTree(context, cachingOptions, url, depth, filter, processor);
            }

            @Override
            public String toString() {
                return String.format(AbstractCachedContentStoreService.this.getClass().getName() + ".getTree(%s, %s, %d, %s, %s)",
                        context, url, depth, filter, processor);
            }

        }, context, url, depth, filter, processor, CONST_KEY_ELEM_TREE);
    }

    protected abstract Item doGetItem(Context context, CachingOptions cachingOptions, String url, ItemProcessor processor)
            throws InvalidContextException, PathNotFoundException, XmlFileParseException, XmlMergeException, ItemProcessingException,
            StoreException;

    protected abstract List<Item> doGetChildren(Context context, CachingOptions cachingOptions, String url, ItemFilter filter,
                                                ItemProcessor processor) throws InvalidContextException, PathNotFoundException,
            XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException;

    protected abstract Tree doGetTree(Context context, CachingOptions cachingOptions, String url, int depth, ItemFilter filter,
                                      ItemProcessor processor) throws InvalidContextException, PathNotFoundException,
            XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException;

}
