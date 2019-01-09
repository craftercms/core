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

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craftercms.core.exception.UrlTransformationException;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.url.UrlTransformer;
import org.springframework.beans.factory.annotation.Required;

/**
 * Class description goes HERE
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public class UrlTransformationEngineImpl extends AbstractCachedUrlTransformationEngine {

    private static final Log logger = LogFactory.getLog(UrlTransformationEngineImpl.class);

    private Map<String, UrlTransformer> transformers;

    @Required
    public void setTransformers(Map<String, UrlTransformer> transformers) {
        this.transformers = transformers;
    }

    @Override
    protected String doTransformUrl(Context context, CachingOptions cachingOptions, String transformerName,
                                    String url) throws UrlTransformationException {
        UrlTransformer transformer = transformers.get(transformerName);
        if (transformer == null) {
            throw new UrlTransformationException("Url transformer " + transformerName + " not found");
        }

        String result = transformer.transformUrl(context, cachingOptions, url);
        if (StringUtils.isEmpty(result)) {
            result = "/";
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Transformation in: " + url + ", Transformation out: " + result);
        }

        return result;
    }

}
