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
package org.craftercms.core.url.impl;

import org.apache.commons.lang3.StringUtils;
import org.craftercms.core.exception.UrlTransformationException;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.url.UrlTransformer;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class AddPrefixAndSuffixUrlTransformer implements UrlTransformer {

    private String prefix;
    private String suffix;
    private boolean prefixPathSeparator;
    private boolean suffixPathSeparator;

    public AddPrefixAndSuffixUrlTransformer() {
        prefixPathSeparator = true;
        suffixPathSeparator = false;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setAddPrefixPathSeparator(boolean addPrefixPathSeparator) {
        this.prefixPathSeparator = addPrefixPathSeparator;
    }

    public void setAddSuffixPathSeparator(boolean addSuffixPathSeparator) {
        this.suffixPathSeparator = addSuffixPathSeparator;
    }

    public void setPrefixPathSeparator(boolean prefixPathSeparator) {
        this.prefixPathSeparator = prefixPathSeparator;
    }

    public void setSuffixPathSeparator(boolean suffixPathSeparator) {
        this.suffixPathSeparator = suffixPathSeparator;
    }

    @Override
    public String transformUrl(Context context, CachingOptions cachingOptions,
                               String url) throws UrlTransformationException {
        StringBuilder urlBuf = new StringBuilder(url);

        if (StringUtils.isNotEmpty(prefix)) {
            if (prefixPathSeparator && !url.startsWith("/")) {
                urlBuf.insert(0, '/');
            }

            urlBuf.insert(0, prefix);
        }
        if (StringUtils.isNotEmpty(suffix)) {
            if (suffixPathSeparator && !url.endsWith("/")) {
                urlBuf.append('/');
            }

            urlBuf.append(suffix);
        }

        return urlBuf.toString();
    }

}
