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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.craftercms.core.service.Content;
import org.craftercms.core.util.cache.impl.CachingAwareObjectBase;

/**
 * Implementation of {@link org.craftercms.core.service.Content} that keeps all the content data and metadata in memory.
 *
 * @author Alfonso Vásquez
 */
public class CachedContent extends CachingAwareObjectBase implements Content {

    private byte[] data;
    private long lastModified;

    /**
     * Copy constructor. Performs a deep copy, copying the bytes of the passed {@code CachedContent} object if the
     * {@code deepCopy}
     * parameter is true.
     */
    public CachedContent(CachedContent content, boolean deepCopy) {
        super(content, deepCopy);

        lastModified = content.lastModified;

        if (deepCopy) {
            data = new byte[content.data.length];
            System.arraycopy(content.data, 0, data, 0, data.length);
        } else {
            data = content.data;
        }
    }

    /**
     * Constructor that creates the object with the specified data and last modified date.
     */
    public CachedContent(byte[] data, long lastModified) {
        this.data = data;
        this.lastModified = lastModified;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLastModified() {
        return lastModified;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLength() {
        return data.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(data);
    }

}
