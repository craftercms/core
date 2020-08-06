/*
 * Copyright (C) 2007-2020 Crafter Software Corporation. All Rights Reserved.
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
package org.craftercms.core.processors.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craftercms.core.exception.ItemProcessingException;
import org.craftercms.core.processors.ItemProcessor;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.service.Item;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * Created by alfonso on 2/21/17.
 */
public class AttributeAddingProcessor implements ItemProcessor {

    private static final Log logger = LogFactory.getLog(AttributeAddingProcessor.class);

    protected Map<String, Map<String, String>> attributeMappings;

    public void setAttributeMappings(Map<String, Map<String, String>> attributeMappings) {
        this.attributeMappings = attributeMappings;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Item process(Context context, CachingOptions cachingOptions, Item item) throws ItemProcessingException {
        Document document = item.getDescriptorDom();
        if (document != null && MapUtils.isNotEmpty(attributeMappings)) {
            for (Map.Entry<String, Map<String, String>> mapping : attributeMappings.entrySet()) {
                String xpath = mapping.getKey();
                Map<String, String> attributes = mapping.getValue();

                if (logger.isDebugEnabled()) {
                    logger.debug("Adding attributes " + attributes + " to nodes that match " + xpath + " for descriptor of " + item);
                }

                if (MapUtils.isNotEmpty(attributes)) {
                    List<Node> nodes = document.selectNodes(xpath);

                    if (logger.isDebugEnabled()) {
                        logger.debug("Number of matching nodes: " + nodes.size());
                    }

                    if (CollectionUtils.isNotEmpty(nodes)) {
                        for (Node node : nodes) {
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                Element element = (Element)node;
                                for (Map.Entry<String, String> attribute : attributes.entrySet()) {
                                    addAttribute(attribute, element);
                                }
                            } else {
                                logger.info("Unable to execute against a non-XML-element: " + node.getUniquePath());
                            }
                        }
                    }
                }
            }
        }

        return item;
    }

    protected void addAttribute(Map.Entry<String, String> attribute, Element element) {
        String name = attribute.getKey();
        String value = attribute.getValue();

        if (logger.isDebugEnabled()) {
            logger.debug("Adding attribute " + name + "=" + value + " to element " + element.getUniquePath());
        }

        element.add(DocumentHelper.createAttribute(element, name, value));
    }
}
