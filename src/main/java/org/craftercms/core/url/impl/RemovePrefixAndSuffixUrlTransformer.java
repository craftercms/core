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
public class RemovePrefixAndSuffixUrlTransformer implements UrlTransformer {

    private String prefix;
    private String suffix;

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public String transformUrl(Context context, CachingOptions cachingOptions,
                               String url) throws UrlTransformationException {
        if (StringUtils.isNotEmpty(prefix)) {
            url = StringUtils.removeStart(url, prefix);
        }
        if (StringUtils.isNotEmpty(suffix)) {
            url = StringUtils.removeEnd(url, suffix);
        }

        return url;
    }

}
