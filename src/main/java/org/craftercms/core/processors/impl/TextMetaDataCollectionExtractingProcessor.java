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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.craftercms.core.exception.ItemProcessingException;
import org.craftercms.core.processors.ItemProcessor;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.service.Item;

/**
 * {@link org.craftercms.core.processors.ItemProcessor} that extracts a list of text values from descriptor
 * XPath-selected nodes and sets them as the
 * properties of the item. For extracting single values from a node use {@link TextMetaDataExtractingProcessor}.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public class TextMetaDataCollectionExtractingProcessor implements ItemProcessor {

    /**
     * Array of XPath queries for the nodes whose values should be extracted.
     */
    protected String[] metaDataNodesXPathQueries;

    /**
     * Default constructor. Sets the {@code metaDataNodesXPathQueries} to the provided argument.
     */
    public TextMetaDataCollectionExtractingProcessor(String... metaDataNodesXPathQueries) {
        this.metaDataNodesXPathQueries = metaDataNodesXPathQueries;
    }

    /**
     * For every XPath query provided in {@code metaDataNodesXPathQueries}, a list of nodes is selected and for each
     * one of these nodes its text value is extracted and added to a list that is later put in the item's properties.
     */
    @Override
    public Item process(Context context, CachingOptions cachingOptions, Item item) throws ItemProcessingException {
        for (String xpathQuery : metaDataNodesXPathQueries) {
            List<String> metaDataValues = item.queryDescriptorValues(xpathQuery);
            if (CollectionUtils.isNotEmpty(metaDataValues)) {
                item.setProperty(xpathQuery, metaDataValues);
            }
        }

        return item;
    }

    /**
     * Returns true if the specified {@code TextMetaDataCollectionExtractingProcessor}'s and this instance's
     * {@code metaDataNodesXPathQueries} are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TextMetaDataCollectionExtractingProcessor that = (TextMetaDataCollectionExtractingProcessor)o;

        if (!metaDataNodesXPathQueries.equals(that.metaDataNodesXPathQueries)) {
            return false;
        }

        return true;
    }

    /**
     * Returns the hash code for this instance, which is basically hash code of the list of XPath queries. As with any
     * other {@link ItemProcessor}, this method is defined because any processor which is passed in the method call of
     * a {@link org.craftercms.core.service.ContentStoreService} can be used as part of a key for caching.
     */
    @Override
    public int hashCode() {
        return metaDataNodesXPathQueries.hashCode();
    }

    @Override
    public String toString() {
        return "TextMetaDataCollectionExtractingProcessor[" +
            "metaDataNodesXPathQueries=" + Arrays.toString(metaDataNodesXPathQueries) +
            ']';
    }

}
