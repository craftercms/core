/*
* Copyright (C) 2007-2013 Crafter Software Corporation.
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

import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craftercms.core.exception.ItemProcessingException;
import org.craftercms.core.processors.ItemProcessor;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.ContentStoreService;
import org.craftercms.core.service.Context;
import org.craftercms.core.service.Item;
import org.craftercms.core.util.UrlUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Required;

/**
 * {@link org.craftercms.core.processors.ItemProcessor} that replaces special "include" tags found in a descriptor
 * document with the document tree of
 * descriptors specified in these "include" tags.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public class IncludeDescriptorsProcessor implements ItemProcessor {

    private static final Log logger = LogFactory.getLog(IncludeDescriptorsProcessor.class);

    /**
     * XPath query for the include element.
     */
    protected String includeElementXPathQuery;
    /**
     * The content store service, used to retrieve the descriptors to include.
     */
    protected ContentStoreService contentStoreService;

    /**
     * Sets the XPath query used to retrieve the include elements.
     */
    @Required
    public void setIncludeElementXPathQuery(String includeElementXPathQuery) {
        this.includeElementXPathQuery = includeElementXPathQuery;
    }

    /**
     * Sets the content store service, used to retrieve the descriptors to include.
     */
    @Required
    public void setContentStoreService(ContentStoreService contentStoreService) {
        this.contentStoreService = contentStoreService;
    }

    /**
     * Replaces special include tags found in a descriptor document with the document tree of descriptors specified in
     * these include tags. If the include tag specifies a XPath query expression (through the select attribute), only
     * the elements returned by the  query will be included.
     *
     * @throws org.craftercms.core.exception.ItemProcessingException
     *          if there was an error while trying to perform an include
     */
    @Override
    public Item process(Context context, CachingOptions cachingOptions, Item item) throws ItemProcessingException {
        includeDescriptors(context, cachingOptions, item);

        return item;
    }

    /**
     * Does the actual include:
     * <p/>
     * <ol>
     * <li>Queries for all the include elements in the item's descriptor.</li>
     * <li>Retrieves the descriptors to include by the url attribute.</li>
     * <li>Includes the descriptor into the current item's descriptor</li>
     * <li>Adds those descriptors' items as dependencies of the current item.</li>
     * </ol>
     *
     * @throws ItemProcessingException
     */
    protected void includeDescriptors(Context context, CachingOptions cachingOptions,
                                      Item item) throws ItemProcessingException {
        String descriptorUrl = item.getDescriptorUrl();
        Document descriptorDom = item.getDescriptorDom();

        List<Element> includeElements = descriptorDom.selectNodes(includeElementXPathQuery);
        if (CollectionUtils.isEmpty(includeElements)) {
            return;
        }

        for (Element includeElement : includeElements) {
            String includeSrcPath = includeElement.getTextTrim();
            if (StringUtils.isEmpty(includeSrcPath)) {
                throw new ItemProcessingException("No path provided in the <" + includeElement.getName() + "> element");
            }

            includeSrcPath = fromRelativeToAbsoluteUrl(descriptorUrl, includeSrcPath);

            if (logger.isDebugEnabled()) {
                logger.debug("Include found in " + descriptorUrl + ": " + includeSrcPath);
            }

            Item includeSrcItem = getIncludeSrcItem(context, cachingOptions, includeSrcPath);
            doInclude(includeElement, includeSrcPath, includeSrcItem.getDescriptorDom());
        }
    }

    /**
     * If path is relative, makes it absolute (resolves references to '.' and '..).
     */
    protected String fromRelativeToAbsoluteUrl(String descriptorUrl, String includeSrcPath) throws
        ItemProcessingException {
        try {
            return UrlUtils.resolveRelative(descriptorUrl, includeSrcPath);
        } catch (URISyntaxException e) {
            throw new ItemProcessingException("Invalid relative URL " + includeSrcPath, e);
        }
    }

    protected Item getIncludeSrcItem(Context context, CachingOptions cachingOptions,
                                     String includeSrcPath) throws ItemProcessingException {
        try {
            return contentStoreService.getItem(context, cachingOptions, includeSrcPath, null);
        } catch (Exception e) {
            throw new ItemProcessingException("Unable to load descriptor " + includeSrcPath + " from the underlying " +
                "repository", e);
        }
    }

    /**
     * Replaces the specified {@code includeElement} with the root of the {@code includeSrc} document
     *
     * @throws ItemProcessingException
     */
    protected void doInclude(Element includeElement, String includeSrcPath,
                             Document includeSrc) throws ItemProcessingException {
        List<Node> includeElementParentChildren = includeElement.getParent().content();
        int includeElementIdx = includeElementParentChildren.indexOf(includeElement);
        Element includeSrcRootElement = includeSrc.getRootElement().createCopy();

        // Remove the <include> element
        includeElementParentChildren.remove(includeElementIdx);

        // Add the src root element
        includeElementParentChildren.add(includeElementIdx, includeSrcRootElement);
    }

    /**
     * Returns true if the specified {@code IncludeDescriptorsProcessor}'s and this instance's fields are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        IncludeDescriptorsProcessor that = (IncludeDescriptorsProcessor)o;

        if (!includeElementXPathQuery.equals(that.includeElementXPathQuery)) {
            return false;
        }
        if (!contentStoreService.equals(that.contentStoreService)) {
            return false;
        }

        return true;
    }

    /**
     * Returns the hash code for this instance, which is basically the combination of the hash code of each field.
     * As with any other {@link ItemProcessor}, this method is defined because any processor which is passed in the
     * method call of a {@link org.craftercms.core.service.ContentStoreService} can be used as part of a
     * key for caching.
     */
    @Override
    public int hashCode() {
        int result = includeElementXPathQuery.hashCode();
        result = 31 * result + contentStoreService.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "IncludeDescriptorsProcessor[" +
            "contentStoreService=" + contentStoreService +
            ", includeElementXPathQuery='" + includeElementXPathQuery + '\'' +
            ']';
    }

}
