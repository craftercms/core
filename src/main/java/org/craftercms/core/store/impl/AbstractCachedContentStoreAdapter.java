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

import java.util.List;

import org.craftercms.core.exception.*;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Content;
import org.craftercms.core.service.Context;
import org.craftercms.core.service.Item;
import org.craftercms.core.store.ContentStoreAdapter;
import org.craftercms.core.util.cache.CacheCallback;
import org.craftercms.core.util.cache.CacheTemplate;
import org.craftercms.core.util.cache.impl.CachingAwareList;
import org.springframework.beans.factory.annotation.Required;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public abstract class AbstractCachedContentStoreAdapter implements ContentStoreAdapter {

    public static final String CONST_KEY_ELEM_CONTENT = "contentStoreAdapter.content";
    public static final String CONST_KEY_ELEM_ITEM = "contentStoreAdapter.item";
    public static final String CONST_KEY_ELEM_ITEMS = "contentStoreAdapter.items";

    protected CacheTemplate cacheTemplate;

    @Required
    public void setCacheTemplate(CacheTemplate cacheTemplate) {
        this.cacheTemplate = cacheTemplate;
    }

    @Override
    public Content getContent(final Context context, final CachingOptions cachingOptions, final String path)
            throws InvalidScopeException, PathNotFoundException, StoreException {
        return cacheTemplate.execute(context, cachingOptions, new CacheCallback<Content>() {

            @Override
            public Content doCacheable() {
                return doGetContent(context, cachingOptions, path);
            }

            @Override
            public String toString() {
                return String.format(AbstractCachedContentStoreAdapter.this.getClass().getName() + ".doGetContent(%s," +
                        " %s)", context, path);
            }

        }, context, path, CONST_KEY_ELEM_CONTENT);
    }

    @Override
    public Item getItem(final Context context, final CachingOptions cachingOptions, final String path,
                        final boolean withDescriptor)
            throws InvalidContextException, PathNotFoundException, XmlFileParseException, StoreException {
        return cacheTemplate.execute(context, cachingOptions, new CacheCallback<Item>() {

            @Override
            public Item doCacheable() {
                return doGetItem(context, cachingOptions, path, withDescriptor);
            }

            @Override
            public String toString() {
                return String.format(AbstractCachedContentStoreAdapter.this.getClass().getName() + ".doGetItem(%s, " +
                        "%s, %s)", context,
                        path, withDescriptor);
            }

        }, context, path, withDescriptor, CONST_KEY_ELEM_ITEM);
    }

    @Override
    public List<Item> getItems(final Context context, final CachingOptions cachingOptions, final String path,
                               final boolean withDescriptor)
            throws InvalidContextException, PathNotFoundException, XmlFileParseException, StoreException {
        return cacheTemplate.execute(context, cachingOptions, new CacheCallback<List<Item>>() {

            @Override
            public List<Item> doCacheable() {
                List<Item> items = doGetItems(context, cachingOptions, path, withDescriptor);
                if ( items instanceof CachingAwareList ) {
                    return items;
                } else {
                    return new CachingAwareList<Item>(items);
                }
            }

            @Override
            public String toString() {
                return String.format(AbstractCachedContentStoreAdapter.this.getClass().getName() + ".doGetItems(%s, " +
                        "%s, %s)", context,
                        path, withDescriptor);
            }

        }, context, path, withDescriptor, CONST_KEY_ELEM_ITEMS);
    }

    protected abstract Content doGetContent(Context context, CachingOptions cachingOptions,
                                            String path) throws InvalidContextException,
            PathNotFoundException, StoreException;

    protected abstract Item doGetItem(Context context, CachingOptions cachingOptions, String path,
                                      boolean withDescriptor)
            throws InvalidContextException, PathNotFoundException, XmlFileParseException, StoreException;

    protected abstract List<Item> doGetItems(Context context, CachingOptions cachingOptions, String path, boolean withDescriptor)
            throws InvalidContextException, PathNotFoundException, XmlFileParseException, StoreException;

}
