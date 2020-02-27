/*
 * Copyright (C) 2007-2020 Crafter Software Corporation. All Rights Reserved.
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

package org.craftercms.core.cache;

/**
 * Base class for all cache implementations to provide statistics.
 * @author joseross
 */
public class CacheStatistics {

    public static final CacheStatistics EMPTY = new CacheStatistics();

    protected long size;

    public CacheStatistics() {
    }

    public CacheStatistics(final long size) {
        this.size = size;
    }

    public long getSize() {
        return size;
    }

}
