/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craftercms.core.exception.UrlTransformationException;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.url.UrlTransformer;

/**
 * Class description goes HERE
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public class ReplacePatternAllUrlTransformer implements UrlTransformer {

    private static final Log logger = LogFactory.getLog(ReplacePatternAllUrlTransformer.class);

    protected String patternToReplace;
    protected String replacement;

    public ReplacePatternAllUrlTransformer(String patternToReplace, String replacement) {
        this.patternToReplace = patternToReplace;
        this.replacement = replacement;
    }

    @Override
    public String transformUrl(Context context, CachingOptions cachingOptions,
                               String url) throws UrlTransformationException {
        String result = doReplacing(url);

        if (logger.isDebugEnabled()) {
            logger.debug("Transformation in: " + url + ", Transformation out: " + result);
        }

        return result;
    }

    protected String doReplacing(String url) {
        return url.replaceAll(patternToReplace, replacement);
    }

}
