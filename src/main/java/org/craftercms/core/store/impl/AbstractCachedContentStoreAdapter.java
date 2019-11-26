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
package org.craftercms.core.store.impl;

import java.util.List;

import org.craftercms.commons.lang.Callback;
import org.craftercms.core.exception.InvalidContextException;
import org.craftercms.core.exception.StoreException;
import org.craftercms.core.exception.XmlFileParseException;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Content;
import org.craftercms.core.service.Context;
import org.craftercms.core.service.Item;
import org.craftercms.core.store.ContentStoreAdapter;
import org.craftercms.core.util.cache.CacheTemplate;
import org.craftercms.core.util.cache.impl.CachingAwareList;
import org.springframework.beans.factory.annotation.Required;

/**
 * Abstract {@link ContentStoreAdapter} that provides caching to actual implementations. Subclasses just have to
 * implement the {@code do*} methods to provide the cacheable objects. Caching works the following way: when an
 * interface method is called, and there's no cached object associated to the parameter-based compound key, the do*
 * method of the same name is called to execute the actual method code. The returned object is then cached with the
 * compound key.
 *
 * @author Alfonso Vásquez
 */
public abstract class AbstractCachedContentStoreAdapter implements ContentStoreAdapter {

    public static final String CONST_KEY_ELEM_CONTENT = "contentStoreAdapter.content";
    public static final String CONST_KEY_ELEM_ITEM = "contentStoreAdapter.item";
    public static final String CONST_KEY_ELEM_ITEMS = "contentStoreAdapter.items";
    public static final String CONST_KEY_ELEM_EXISTS = "contentStoreAdapter.exists";

    protected CacheTemplate cacheTemplate;
    protected CachingOptions defaultCachingOptions;

    @Required
    public void setCacheTemplate(CacheTemplate cacheTemplate) {
        this.cacheTemplate = cacheTemplate;
    }

    public void setDefaultCachingOptions(CachingOptions defaultCachingOptions) {
        this.defaultCachingOptions = defaultCachingOptions;
    }

    @Override
    public boolean exists(final Context context, final CachingOptions cachingOptions, final String path)
        throws InvalidContextException, StoreException {
        final CachingOptions actualCachingOptions = cachingOptions != null? cachingOptions: defaultCachingOptions;

        return cacheTemplate.getObject(context, actualCachingOptions, new Callback<Boolean>() {

            @Override
            public Boolean execute() {
                return doExists(context, actualCachingOptions, path);
            }

            @Override
            public String toString() {
                return String.format(AbstractCachedContentStoreAdapter.this.getClass().getName() + ".exists(%s, %s)",
                    context, path);
            }

        }, context, path, CONST_KEY_ELEM_EXISTS);
    }

    @Override
    public Content findContent(final Context context, final CachingOptions cachingOptions,
                               final String path) throws InvalidContextException, StoreException {
        final CachingOptions actualCachingOptions = cachingOptions != null? cachingOptions: defaultCachingOptions;

        return cacheTemplate.getObject(context, actualCachingOptions, new Callback<Content>() {

            @Override
            public Content execute() {
                return doFindContent(context, actualCachingOptions, path);
            }

            @Override
            public String toString() {
                return String.format(AbstractCachedContentStoreAdapter.this.getClass().getName() +
                                     ".findContent(%s, %s)", context, path);
            }

        }, context, path, CONST_KEY_ELEM_CONTENT);
    }

    @Override
    public Item findItem(final Context context, final CachingOptions cachingOptions, final String path,
                         final boolean withDescriptor) throws InvalidContextException, XmlFileParseException,
                                                              StoreException {
        final CachingOptions actualCachingOptions = cachingOptions != null? cachingOptions: defaultCachingOptions;

        return cacheTemplate.getObject(context, actualCachingOptions, new Callback<Item>() {

            @Override
            public Item execute() {
                return doFindItem(context, actualCachingOptions, path, withDescriptor);
            }

            @Override
            public String toString() {
                return String.format(AbstractCachedContentStoreAdapter.this.getClass().getName() +
                                     ".findItem(%s, %s, %s)", context, path, withDescriptor);
            }

        }, context, path, withDescriptor, CONST_KEY_ELEM_ITEM);
    }

    @Override
    public List<Item> findItems(final Context context, final CachingOptions cachingOptions, final String path)
            throws InvalidContextException, XmlFileParseException, StoreException {
        final CachingOptions actualCachingOptions = cachingOptions != null? cachingOptions: defaultCachingOptions;

        return cacheTemplate.getObject(context, actualCachingOptions, new Callback<List<Item>>() {

            @Override
            public List<Item> execute() {
                List<Item> items = doFindItems(context, actualCachingOptions, path);
                if (items != null) {
                    if (items instanceof CachingAwareList) {
                        return items;
                    } else {
                        return new CachingAwareList<>(items);
                    }
                } else {
                    return null;
                }
            }

            @Override
            public String toString() {
                return String.format(AbstractCachedContentStoreAdapter.this.getClass().getName() +
                                     ".findItems(%s, %s, %s)", context, path);
            }

        }, context, path, CONST_KEY_ELEM_ITEMS);
    }

    protected abstract boolean doExists(Context context, CachingOptions cachingOptions, String path)
        throws InvalidContextException, StoreException;

    protected abstract Content doFindContent(Context context, CachingOptions cachingOptions,
                                             String path) throws InvalidContextException, StoreException;

    protected abstract Item doFindItem(Context context, CachingOptions cachingOptions, String path,
                                       boolean withDescriptor) throws InvalidContextException, XmlFileParseException,
        StoreException;

    protected abstract List<Item> doFindItems(Context context, CachingOptions cachingOptions, String path)
            throws InvalidContextException, XmlFileParseException, StoreException;

}
