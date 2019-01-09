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
package org.craftercms.core.cache.impl;

import java.util.Arrays;
import java.util.List;

import org.craftercms.core.cache.CacheItem;
import org.craftercms.core.cache.CacheLoader;

/**
 * Default implementation of {@link org.craftercms.core.cache.CacheItem}.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public class CacheItemImpl implements CacheItem {

    protected final String scope;
    protected final long ticksAtCreation;

    protected final Object key;
    protected final Object value;

    protected final long ticksToExpire;
    protected final long ticksToRefresh;

    protected final CacheLoader loader;
    protected final Object[] loaderParams;

    /**
     * Value constructor.
     */
    public CacheItemImpl(final String scope, final long ticksAtCreation, final Object key, final Object value,
                         final long ticksToExpire, final long ticksToRefresh, final CacheLoader loader,
                         final Object[] loaderParams) {
        this.scope = scope;
        this.ticksAtCreation = ticksAtCreation;

        this.key = key;
        this.value = value;

        this.ticksToExpire = ticksToExpire;
        this.ticksToRefresh = ticksToRefresh;

        this.loader = loader;
        this.loaderParams = loaderParams;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getScope() {
        return this.scope;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getKey() {
        return this.key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue() {
        return this.value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getTicksAtCreation() {
        return this.ticksAtCreation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getTicksToExpire() {
        return this.ticksToExpire;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getTicksToRefresh() {
        return this.ticksToRefresh;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CacheLoader getLoader() {
        return this.loader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] getLoaderParams() {
        return this.loaderParams;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isExpired(long currentTicks) {
        return this.ticksToExpire != NEVER_EXPIRE && currentTicks >= (this.ticksAtCreation + this.ticksToExpire);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean needsRefresh(long currentTicks) {
        return this.ticksToRefresh != NEVER_REFRESH && currentTicks >= (this.ticksAtCreation + this.ticksToRefresh);
    }

    /**
     * Returns true if the specified {@code CacheItemImpl}'s and this instance's key and scope are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CacheItemImpl item = (CacheItemImpl)o;

        if (!this.key.equals(item.key)) {
            return false;
        }
        if (!this.scope.equals(item.scope)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = this.scope.hashCode();
        result = 31 * result + this.key.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CacheItemImpl[" +
            "scope='" + this.scope + '\'' +
            ", ticksAtCreation=" + this.ticksAtCreation +
            ", key=" + this.key +
            ", value=" + this.value +
            ", ticksToExpire=" + this.ticksToExpire +
            ", ticksToRefresh=" + this.ticksToRefresh +
            ", loader=" + this.loader +
            ", loaderParams=" + (this.loaderParams == null? null: Arrays.asList(this.loaderParams)) +
            ']';
    }

}
