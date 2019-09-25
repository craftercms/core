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

import org.craftercms.core.exception.ItemProcessingException;
import org.craftercms.core.processors.ItemProcessor;
import org.craftercms.core.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * {@link ItemProcessor} that retrieves the {@link Content} for the item and adds it as a property of the
 * {@link Item}.
 *
 * @author avasquez
 * @since 3.1.4
 */
public class ContentRetrievingProcessor implements ItemProcessor {

    public static final Logger logger = LoggerFactory.getLogger(ContentRetrievingProcessor.class);

    public static final String CONTENT_PROPERTY_NAME = "content";

    protected ContentStoreService storeService;

    @Required
    public void setStoreService(ContentStoreService storeService) {
        this.storeService = storeService;
    }

    @Override
    public Item process(Context context, CachingOptions cachingOptions, Item item) throws ItemProcessingException {
        if (!item.isFolder()) {
            try {
                logger.debug("Retrieving content for item {}", item.getUrl());

                Content content = storeService.findContent(context, cachingOptions, item.getUrl());
                item.setProperty(CONTENT_PROPERTY_NAME, content);
            } catch (Exception e) {
                throw new ItemProcessingException("Unable to retrieve content for " + item, e);
            }
        }

        return item;
    }

}
