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

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craftercms.core.exception.UrlTransformationException;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.service.Item;
import org.craftercms.core.url.UrlTransformer;
import org.craftercms.core.util.UrlUtils;

/**
 * Class description goes HERE
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public class ShortToLongUrlTransformer implements UrlTransformer {

    private static final Log logger = LogFactory.getLog(ShortToLongUrlTransformer.class);

    private String containsShortNameRegex;
    private int shortNameRegexGroup;

    public ShortToLongUrlTransformer() {
        containsShortNameRegex = LongToShortUrlTransformer.URL_NUMBERED_NAME_REGEX;
        shortNameRegexGroup = LongToShortUrlTransformer.URL_NUMBERED_NAME_REGEX_SHORT_NAME_GROUP;
    }

    public void setContainsShortNameRegex(String containsShortNameRegex) {
        this.containsShortNameRegex = containsShortNameRegex;
    }

    public void setShortNameRegexGroup(int shortNameRegexGroup) {
        this.shortNameRegexGroup = shortNameRegexGroup;
    }

    @Override
    public String transformUrl(Context context, CachingOptions cachingOptions,
                               String url) throws UrlTransformationException {
        String result = getLongUrl(context, cachingOptions, url, true);

        if (logger.isDebugEnabled()) {
            logger.debug("Transformation in: " + url + ", Transformation out: " + result);
        }

        return result;
    }

    protected String getLongName(Context context, CachingOptions cachingOptions, String folderPath,
                                 String shortName) throws UrlTransformationException {
        try {
            List<Item> items = context.getStoreAdapter().findItems(context, cachingOptions, folderPath);
            if (CollectionUtils.isNotEmpty(items)) {
                for (Item item : items) {
                    String itemName = item.getName();
                    if (UrlUtils.getShortName(itemName, containsShortNameRegex, shortNameRegexGroup)
                        .equalsIgnoreCase(shortName)) {
                        return itemName;
                    }
                }
            }
        } catch (Exception e) {
            throw new UrlTransformationException("An error occurred while retrieving the items at " + folderPath +
                                                 " and trying to map the short name '" + shortName + "' to an " +
                                                 "item's name (long name)", e);
        }

        return null;
    }

    protected String getLongUrl(Context context, CachingOptions cachingOptions, String shortUrl,
                                boolean useShortNameIfLongNameNotFound) throws UrlTransformationException {
        String[] levels = StringUtils.strip(shortUrl, "/").split("/");
        StringBuilder result = new StringBuilder();

        if (ArrayUtils.isNotEmpty(levels)) {
            for (String level : levels) {
                String folderPath = result.toString();
                folderPath = StringUtils.isNotEmpty(folderPath)? folderPath: "/";

                String longName = getLongName(context, cachingOptions, folderPath, level);
                if (StringUtils.isNotEmpty(longName)) {
                    result.append("/").append(longName);
                } else if (useShortNameIfLongNameNotFound) {
                    result.append("/").append(level);
                } else {
                    return null;
                }
            }
        }

        if (shortUrl.endsWith("/")) {
            result.append("/");
        }

        return result.toString();
    }

}
