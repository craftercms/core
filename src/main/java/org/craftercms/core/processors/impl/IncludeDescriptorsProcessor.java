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

import java.net.URISyntaxException;
import java.util.List;
import java.util.Stack;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
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
 * {@link org.craftercms.core.processors.ItemProcessor} that finds special "include" tags found in a descriptor
 * document and inserts there the document tree of descriptors specified in these "include" tags.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public class IncludeDescriptorsProcessor implements ItemProcessor {

    private static final Log logger = LogFactory.getLog(IncludeDescriptorsProcessor.class);

    protected static final ThreadLocal<Stack<String>> includedItemsStack = ThreadLocal.withInitial(Stack::new);

    /**
     * XPath query for the include element.
     */
    protected String includeElementXPathQuery;
    /**
     * Flag to indicate if the include element should be removed (false by default).
     */
    protected boolean removeIncludeElement;
    /**
     * XPath query relative to include elements for nodes tha specify if the include is disabled or not.
     */
    protected String disabledIncludeNodeXPathQuery;
    /**
     * The content store service, used to retrieve the descriptors to include.
     */
    protected ContentStoreService contentStoreService;
    /**
     * Processor to use for included items.
     */
    protected ItemProcessor includedItemsProcessor;

    /**
     * Sets the XPath query used to retrieve the include elements.
     */
    @Required
    public void setIncludeElementXPathQuery(String includeElementXPathQuery) {
        this.includeElementXPathQuery = includeElementXPathQuery;
    }

    /**
     * Sets the flag to indicate if the include element should be removed (false by default).
     */
    public void setRemoveIncludeElement(boolean removeIncludeElement) {
        this.removeIncludeElement = removeIncludeElement;
    }

    /**
     * Sets the XPath query relative to include elements for nodes tha specify if the include is disabled or not.
     */
    @Required
    public void setDisabledIncludeNodeXPathQuery(String disabledIncludeNodeXPathQuery) {
        this.disabledIncludeNodeXPathQuery = disabledIncludeNodeXPathQuery;
    }

    /**
     * Sets the content store service, used to retrieve the descriptors to include.
     */
    @Required
    public void setContentStoreService(ContentStoreService contentStoreService) {
        this.contentStoreService = contentStoreService;
    }

    /**
     * Sets the processor to use for included items.
     */
    public void setIncludedItemsProcessor(ItemProcessor includedItemsProcessor) {
        this.includedItemsProcessor = includedItemsProcessor;
    }

    /**
     * Replaces special include tags found in a descriptor document with the document tree of descriptors specified in
     * these include tags. If the include tag specifies a XPath query expression (through the select attribute), only
     * the elements returned by the  query will be included.
     *
     * @throws org.craftercms.core.exception.ItemProcessingException if there was an error while trying to perform an
     * include
     */
    @Override
    public Item process(Context context, CachingOptions cachingOptions, Item item) throws ItemProcessingException {
        if (item.getDescriptorDom() != null) {
            includeDescriptors(context, cachingOptions, item);
        }

        return item;
    }

    @SuppressWarnings("unchecked")
    protected void includeDescriptors(Context context, CachingOptions cachingOptions, Item item) throws ItemProcessingException {
        String descriptorUrl = item.getDescriptorUrl();

        includedItemsStack.get().push(descriptorUrl);
        try {
            Document descriptorDom = item.getDescriptorDom();
            List<Element> includeElements = descriptorDom.selectNodes(includeElementXPathQuery);

            if (CollectionUtils.isEmpty(includeElements)) {
                return;
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Processing includes of item @ " + descriptorUrl);
            }

            for (Element includeElement : includeElements) {
                String itemToIncludePath = includeElement.getTextTrim();

                if (StringUtils.isEmpty(itemToIncludePath)) {
                    continue;
                }

                if (!isIncludeDisabled(includeElement)) {
                    if (!includedItemsStack.get().contains(itemToIncludePath)) {
                        Item itemToInclude = getItemToInclude(context, cachingOptions, itemToIncludePath);
                        if (itemToInclude != null && itemToInclude.getDescriptorDom() != null) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Include found in " + descriptorUrl + ": " + itemToIncludePath);
                            }

                            doInclude(item, includeElement, itemToInclude);
                        } else {
                            logger.debug("No descriptor item found @ " + itemToIncludePath);
                        }
                    } else {
                        logger.debug("Circular inclusion detected. Item " + itemToIncludePath + " already included");
                    }
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Ignoring include " + itemToIncludePath + ". It's currently disabled");
                    }
                }
            }
        } finally {
            includedItemsStack.get().pop();
        }
    }

    protected boolean isIncludeDisabled(Element includeElement) {
        Node disabledIncludeNode = includeElement.selectSingleNode(disabledIncludeNodeXPathQuery);

        return disabledIncludeNode != null && BooleanUtils.toBoolean(disabledIncludeNode.getText());
    }

    protected Item getItemToInclude(Context context, CachingOptions cachingOptions, String includeSrcPath) throws ItemProcessingException {
        try {
            return contentStoreService.findItem(context, cachingOptions, includeSrcPath, includedItemsProcessor);
        } catch (Exception e) {
            throw new ItemProcessingException("Unable to retrieve descriptor " + includeSrcPath + " from the underlying repository", e);
        }
    }

    @SuppressWarnings("unchecked")
    protected void doInclude(Item item, Element includeElement, Item itemToInclude) throws ItemProcessingException {
        List<Node> includeElementParentChildren = includeElement.getParent().content();
        int includeElementIdx = includeElementParentChildren.indexOf(includeElement);
        Element itemToIncludeRootElement = itemToInclude.getDescriptorDom().getRootElement().createCopy();

        if (removeIncludeElement) {
            // Remove the <include> element
            includeElementParentChildren.remove(includeElementIdx);
            // Add the item's root element
            includeElementParentChildren.add(includeElementIdx, itemToIncludeRootElement);
        } else {
            // Add the item's root element
            includeElementParentChildren.add(includeElementIdx + 1, itemToIncludeRootElement);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Item " + itemToInclude.getDescriptorUrl() + " included into " + item.getDescriptorUrl());
        }
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
