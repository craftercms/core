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
package org.craftercms.core.util.cache.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.craftercms.core.util.cache.CachingAwareObject;

/**
 * Base abstract implementation of {@link CachingAwareObject}
 *
 * @author Alfonso Vásquez
 */
public abstract class AbstractCachingAwareObject implements CachingAwareObject {

    protected transient String scope;
    protected transient Object key;
    protected transient Long cachingTime;

    protected AbstractCachingAwareObject() {
    }

    protected AbstractCachingAwareObject(CachingAwareObject cachingAwareObject) {
        this.scope = cachingAwareObject.getScope();
        this.key = cachingAwareObject.getKey();
        this.cachingTime = cachingAwareObject.getCachingTime();
    }

    @JsonIgnore
    @Override
    public String getScope() {
        return scope;
    }

    @JsonIgnore
    @Override
    public void setScope(String scope) {
        this.scope = scope;
    }

    @JsonIgnore
    @Override
    public Object getKey() {
        return key;
    }

    @Override
    public void setKey(Object key) {
        this.key = key;
    }

    @JsonIgnore
    @Override
    public Long getCachingTime() {
        return cachingTime;
    }

    @JsonIgnore
    @Override
    public void setCachingTime(Long cachingTime) {
        this.cachingTime = cachingTime;
    }

}
