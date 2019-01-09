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
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.ContentStoreService;
import org.craftercms.core.service.Context;
import org.craftercms.core.service.Item;
import org.craftercms.core.service.ItemFilter;
import org.craftercms.core.service.Tree;
import org.craftercms.core.util.cache.CacheTemplate;
import org.craftercms.core.util.cache.impl.CachingAwareList;
import org.springframework.beans.factory.annotation.Required;

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
     * Sets the {@code CacheTemplate}, which is used as a helper for caching.
     */
    @Required
    public void setCacheTemplate(final CacheTemplate cacheTemplate) {
        this.cacheTemplate = cacheTemplate;
    }

    /**
     * Sets the default caching options to use when none are specified in the method. Can be null.
     */
    public void setDefaultCachingOptions(CachingOptions defaultCachingOptions) {
        this.defaultCachingOptions = defaultCachingOptions;
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

        }, context, url, CONST_KEY_ELEM_EXISTS);
    }

    @Override
    public Item findItem(Context context,
                         String url) throws InvalidContextException, XmlFileParseException, XmlMergeException,
        ItemProcessingException, StoreException {
        return findItem(context, null, url, null);
    }

    @Override
    public Item findItem(final Context context, final CachingOptions cachingOptions, final String url,
                         final ItemProcessor processor) throws InvalidContextException, XmlFileParseException,
        XmlMergeException, ItemProcessingException, StoreException {
        final CachingOptions actualCachingOptions = cachingOptions != null? cachingOptions: defaultCachingOptions;

        return cacheTemplate.getObject(context, actualCachingOptions, new Callback<Item>() {

            @Override
            public Item execute() {
                return doFindItem(context, actualCachingOptions, url, processor);
            }

            @Override
            public String toString() {
                return String.format(AbstractCachedContentStoreService.this.getClass().getName() +
                                     ".getItem(%s, %s, %s)", context, url, processor);
            }

        }, context, url, processor, CONST_KEY_ELEM_ITEM);
    }

    @Override
    public Item getItem(Context context,
                        String url) throws InvalidContextException, PathNotFoundException, XmlFileParseException,
        XmlMergeException, ItemProcessingException, StoreException {
        return getItem(context, null, url, null);
    }

    @Override
    public Item getItem(Context context, CachingOptions cachingOptions, String url,
                        ItemProcessor processor) throws InvalidContextException, PathNotFoundException,
        XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException {
        Item item = findItem(context, cachingOptions, url, processor);
        if (item != null) {
            return item;
        } else {
            throw new PathNotFoundException("No item found at " + url);
        }
    }

    @Override
    public List<Item> findChildren(Context context,
                                   String url) throws InvalidContextException, XmlFileParseException,
        XmlMergeException, ItemProcessingException, StoreException {
        return findChildren(context, null, url, null, null);
    }

    @Override
    public List<Item> findChildren(final Context context, final CachingOptions cachingOptions, final String url,
                                   final ItemFilter filter,
                                   final ItemProcessor processor) throws InvalidContextException,
        XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException {
        final CachingOptions actualCachingOptions = cachingOptions != null? cachingOptions: defaultCachingOptions;

        return cacheTemplate.getObject(context, actualCachingOptions, new Callback<List<Item>>() {

            @Override
            public List<Item> execute() {
                List<Item> children = doFindChildren(context, actualCachingOptions, url, filter, processor);
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
                                     ".getChildren(%s, %s, %s, %s)", context, url, filter, processor);
            }

        }, context, url, filter, processor, CONST_KEY_ELEM_CHILDREN);
    }

    @Override
    public List<Item> getChildren(Context context,
                                  String url) throws InvalidContextException, PathNotFoundException,
        XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException {
        return getChildren(context, null, url, null, null);
    }

    @Override
    public List<Item> getChildren(Context context, CachingOptions cachingOptions, String url, ItemFilter filter,
                                  ItemProcessor processor) throws InvalidContextException, PathNotFoundException,
        XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException {
        List<Item> children = findChildren(context, cachingOptions, url, filter, processor);
        if (children != null) {
            return children;
        } else {
            throw new PathNotFoundException("No folder found at " + url);
        }
    }

    @Override
    public Tree findTree(final Context context,
                         final String url) throws InvalidContextException, XmlFileParseException, XmlMergeException,
        ItemProcessingException, StoreException {
        return findTree(context, null, url, UNLIMITED_TREE_DEPTH, null, null);
    }

    @Override
    public Tree findTree(final Context context, final String url,
                         final int depth) throws InvalidContextException, XmlFileParseException, XmlMergeException,
        ItemProcessingException, StoreException {
        return findTree(context, null, url, depth, null, null);
    }

    @Override
    public Tree findTree(final Context context, final CachingOptions cachingOptions, final String url, final int depth,
                         final ItemFilter filter,
                         final ItemProcessor processor) throws InvalidContextException, XmlFileParseException,
        XmlMergeException, ItemProcessingException, StoreException {
        final CachingOptions actualCachingOptions = cachingOptions != null? cachingOptions: defaultCachingOptions;

        return cacheTemplate.getObject(context, actualCachingOptions, new Callback<Tree>() {

            @Override
            public Tree execute() {
                return doFindTree(context, actualCachingOptions, url, depth, filter, processor);
            }

            @Override
            public String toString() {
                return String.format(AbstractCachedContentStoreService.this.getClass().getName() +
                                     ".getTree(%s, %s, %d, %s, %s)", context, url, depth, filter, processor);
            }

        }, context, url, depth, filter, processor, CONST_KEY_ELEM_TREE);
    }

    @Override
    public Tree getTree(Context context,
                        String url) throws InvalidContextException, PathNotFoundException, XmlFileParseException,
        XmlMergeException, ItemProcessingException, StoreException {
        return getTree(context, null, url, UNLIMITED_TREE_DEPTH, null, null);
    }

    @Override
    public Tree getTree(Context context, String url,
                        int depth) throws InvalidContextException, PathNotFoundException, XmlFileParseException,
        XmlMergeException, ItemProcessingException, StoreException {
        return getTree(context, null, url, depth, null, null);
    }

    @Override
    public Tree getTree(Context context, CachingOptions cachingOptions, String url, int depth, ItemFilter filter,
                        ItemProcessor processor) throws InvalidContextException, PathNotFoundException,
        XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException {
        Tree tree = findTree(context, cachingOptions, url, depth, filter, processor);
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
                                                 ItemFilter filter,
                                                 ItemProcessor processor) throws InvalidContextException,
        XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException;

    protected abstract Tree doFindTree(Context context, CachingOptions cachingOptions, String url, int depth,
                                       ItemFilter filter,
                                       ItemProcessor processor) throws InvalidContextException, XmlFileParseException,
        XmlMergeException, ItemProcessingException, StoreException;

}
