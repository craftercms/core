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
package org.craftercms.core.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Utility methods for content store.
 *
 * @author avasquez
 * @since 3.1.4
 */
public class ContentStoreUtils {

    private ContentStoreUtils() {
    }

    /**
     * Normalize the path: this means, append a leading '/' at the beginning and remove any trailing '/' (unless
     * the path is root). This is done for consistency in handling paths.
     *
     * @param path
     * @return the normalized path (with a leading '/' and without a trailing '/')
     */
    public static final String normalizePath(String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (!path.equals("/")) {
            path = StringUtils.stripEnd(path, "/");
        }

        return path;
    }

}
