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
package org.craftercms.core.processors.impl;

import org.apache.commons.lang3.ArrayUtils;
import org.craftercms.commons.lang.RegexUtils;
import org.craftercms.core.exception.ItemProcessingException;
import org.craftercms.core.processors.ItemProcessor;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.service.Item;
import org.springframework.beans.factory.annotation.Required;

/**
 * {@link ItemProcessor} that decorates another processor and only executes it if the item URL matches a pattern in
 * the list of includes and doesn't match one of the patterns in the list of excludes.
 *
 * @author avasquez
 * @since 3.1.4
 */
public class IncludesAndExcludesAwareProcessor implements ItemProcessor {

    protected String[] includeUrls;
    protected String[] excludeUrls;
    protected ItemProcessor actualProcessor;

    public void setIncludeUrls(String[] includeUrls) {
        this.includeUrls = includeUrls;
    }

    public void setExcludeUrls(String[] excludeUrls) {
        this.excludeUrls = excludeUrls;
    }

    @Required
    public void setActualProcessor(ItemProcessor actualProcessor) {
        this.actualProcessor = actualProcessor;
    }

    @Override
    public Item process(Context context, CachingOptions cachingOptions, Item item) throws ItemProcessingException {
        if (shouldInclude(item)) {
            return actualProcessor.process(context, cachingOptions, item);
        } else {
            return item;
        }
    }

    protected boolean shouldInclude(Item item) {
        return (ArrayUtils.isEmpty(includeUrls) || RegexUtils.matchesAny(item.getUrl(), includeUrls)) &&
               (ArrayUtils.isEmpty(excludeUrls) || !RegexUtils.matchesAny(item.getUrl(), excludeUrls));
    }

}
