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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.url.UrlTransformer;
import org.craftercms.core.util.UrlUtils;

/**
 * Class description goes HERE
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public class AddDebugParamUrlTransformer implements UrlTransformer {

    private static final Log logger = LogFactory.getLog(AddDebugParamUrlTransformer.class);

    public static final String DEFAULT_DEBUG_URL_PARAM = "debug";

    private String debugParam;

    public AddDebugParamUrlTransformer() {
        this.debugParam = DEFAULT_DEBUG_URL_PARAM;
    }

    public void setDebugParam(String debugParam) {
        this.debugParam = debugParam;
    }

    @Override
    public String transformUrl(Context context, CachingOptions cachingOptions, String url) {
        String result;
        int indexOfParamDelim = url.indexOf(UrlUtils.URL_PARAM_DELIM);

        if (indexOfParamDelim < 0) {
            // The URL doesn't have a param delim, add it and tack on the debug flag
            result = url + UrlUtils.URL_PARAM_DELIM + debugParam + "=true";
        } else {
            // The URL does have a param delim, add param separator and tack on the debug flag
            result = url + UrlUtils.URL_PARAM_SEPARATOR + debugParam + "=true";
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Transformation in: " + url + ", Transformation out: " + result);
        }

        return result;
    }

}
