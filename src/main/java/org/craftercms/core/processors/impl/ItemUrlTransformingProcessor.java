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
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.service.Item;
import org.craftercms.core.url.UrlTransformationEngine;
import org.springframework.beans.factory.annotation.Required;

/**
 * {@link org.craftercms.core.processors.ItemProcessor} that takes the item url, transforms it by calling
 * the transformation engine, and places the transformed url in the properties.
 *
 * @author Alfonso Vásquez
 */
public class ItemUrlTransformingProcessor implements ItemProcessor {

    protected String transformedUrlPropName;
    protected String transformerName;
    protected UrlTransformationEngine urlTransformationEngine;

    @Required
    public void setTransformedUrlPropName(String transformedUrlPropName) {
        this.transformedUrlPropName = transformedUrlPropName;
    }

    @Required
    public void setTransformerName(String transformerName) {
        this.transformerName = transformerName;
    }

    @Required
    public void setUrlTransformationEngine(UrlTransformationEngine urlTransformationEngine) {
        this.urlTransformationEngine = urlTransformationEngine;
    }

    @Override
    public Item process(Context context, CachingOptions cachingOptions, Item item) throws ItemProcessingException {
        String transformedUrl = urlTransformationEngine.transformUrl(context, transformerName, item.getUrl());
        item.setProperty(transformedUrlPropName, transformedUrl);

        return item;
    }

    @Override
    public String toString() {
        return "ItemUrlTransformingProcessor[" + "transformedUrlPropName='" + transformedUrlPropName + '\'' + ", " +
            "transformerName='" + transformerName + '\'' + ", urlTransformationEngine=" + urlTransformationEngine + ']';
    }

}
