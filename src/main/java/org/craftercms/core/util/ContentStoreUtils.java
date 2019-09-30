package org.craftercms.core.util;

import org.apache.commons.lang3.StringUtils;

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
