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
package org.craftercms.core.xml.mergers.impl.resolvers;

import java.util.Map;

import org.craftercms.core.exception.XmlException;
import org.craftercms.core.xml.mergers.DescriptorMergeStrategy;
import org.craftercms.core.xml.mergers.DescriptorMergeStrategyResolver;
import org.dom4j.Document;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Required;

/**
 * Resolves the {@link DescriptorMergeStrategy} to use for a given descriptor, based on the value of an element in
 * the descriptor document.
 *
 * @author Sumer Jabri
 * @author Alfonso Vasquez
 */
public class MetaDataMergeStrategyResolver implements DescriptorMergeStrategyResolver {

    private String mergeStrategyElementXPathQuery;
    private Map<String, DescriptorMergeStrategy> elementValueToStrategyMappings;

    @Required
    public void setMergeStrategyElementXPathQuery(String mergeStrategyElementXPathQuery) {
        this.mergeStrategyElementXPathQuery = mergeStrategyElementXPathQuery;
    }

    @Required
    public void setElementValueToStrategyMappings(Map<String, DescriptorMergeStrategy> elementValueToStrategyMappings) {
        this.elementValueToStrategyMappings = elementValueToStrategyMappings;
    }

    /**
     * Returns a {@link DescriptorMergeStrategy} for a given descriptor. The strategy chosen is the one defined
     * in the descriptor document.
     *
     * @param descriptorUrl the URL that identifies the descriptor
     * @param descriptorDom the XML DOM of the descriptor
     * @return the {@link DescriptorMergeStrategy} for the descriptor, or null if the DOM is null or if there's no
     *         element in the DOM that defines the merge strategy to use.
     * @throws XmlException if the element value doesn't refer to an existing strategy
     */
    public DescriptorMergeStrategy getStrategy(String descriptorUrl, Document descriptorDom) throws XmlException {
        if (descriptorDom != null) {
            Node element = descriptorDom.selectSingleNode(mergeStrategyElementXPathQuery);
            if (element != null) {
                DescriptorMergeStrategy strategy = elementValueToStrategyMappings.get(element.getText());
                if (strategy != null) {
                    return strategy;
                } else {
                    throw new XmlException("Element value \"" + element.getText() + "\" doesn't refer to an " +
                                           "registered strategy");
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

}
