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

import org.craftercms.commons.lang.Callback;
import org.craftercms.core.exception.UrlTransformationException;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.url.UrlTransformationEngine;
import org.craftercms.core.util.cache.CacheTemplate;
import org.springframework.beans.factory.annotation.Required;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public abstract class AbstractCachedUrlTransformationEngine implements UrlTransformationEngine {

    public static final String TRANSFORMED_URL_CONST_KEY_ELEM = "urlTransformationEngine.transformedUrl";

    protected CacheTemplate cacheTemplate;

    @Required
    public void setCacheTemplate(CacheTemplate cacheTemplate) {
        this.cacheTemplate = cacheTemplate;
    }

    @Override
    public String transformUrl(Context context, String transformerName, String url) throws UrlTransformationException {
        return transformUrl(context, CachingOptions.DEFAULT_CACHING_OPTIONS, transformerName, url);
    }

    @Override
    public String transformUrl(final Context context, final CachingOptions cachingOptions,
                               final String transformerName, final String url) throws UrlTransformationException {
        return cacheTemplate.getObject(context, cachingOptions, new Callback<String>() {

            @Override
            public String execute() {
                return doTransformUrl(context, cachingOptions, transformerName, url);
            }

            @Override
            public String toString() {
                return String.format(AbstractCachedUrlTransformationEngine.this.getClass().getName() +
                                     ".transformUrl(%s, %s, %s)", context, transformerName, url);
            }

        }, context, transformerName, url, TRANSFORMED_URL_CONST_KEY_ELEM);
    }

    protected abstract String doTransformUrl(Context context, CachingOptions cachingOptions, String transformerName,
                                             String url) throws UrlTransformationException;

}
