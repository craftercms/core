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
package org.craftercms.core.processors.impl.resolvers;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.craftercms.core.exception.XmlException;
import org.craftercms.core.processors.ItemProcessor;
import org.craftercms.core.processors.ItemProcessorResolver;
import org.craftercms.core.service.Item;
import org.springframework.beans.factory.annotation.Required;

/**
 * {@link ItemProcessorResolver} that searches the item's descriptor for a processor element that defines the name of
 * the processor
 *
 * @author Alfonso Vásquez
 */
public class MetaDataProcessorResolver implements ItemProcessorResolver {

    /**
     * The XPath query for the processor element that defines the processor to use.
     */
    protected String processorElementXPathQuery;
    /**
     * Mappings of processor element values to processor names.
     */
    protected Map<String, ItemProcessor> elementValueToProcessorMappings;

    /**
     * Sets the XPath query for the processor element that defines the processor to use.
     */
    @Required
    public void setProcessorElementXPathQuery(String processorElementXPathQuery) {
        this.processorElementXPathQuery = processorElementXPathQuery;
    }

    /**
     * Sets the mappings of processor element values to processor names.
     */
    @Required
    public void setElementValueToProcessorMappings(Map<String, ItemProcessor> elementValueToProcessorMappings) {
        this.elementValueToProcessorMappings = elementValueToProcessorMappings;
    }

    /**
     * Looks for the processor element (by querying it with the {@code processorElementXPathQuery}) in the item's
     * descriptor. If the element is found, the element value is mapped to a processor and that processor is
     * returned.
     *
     * @throws XmlException if the element value doesn't refer to an existing processor
     */
    @Override
    public ItemProcessor getProcessor(Item item) throws XmlException {
        String processorElementValue = item.queryDescriptorValue(processorElementXPathQuery);
        if (StringUtils.isNotEmpty(processorElementValue)) {
            ItemProcessor processor = elementValueToProcessorMappings.get(processorElementValue);
            if (processor != null) {
                return processor;
            } else {
                throw new XmlException("Element value \"" + processorElementValue + "\" doesn't refer to a " +
                                       "registered processor");
            }
        } else {
            return null;
        }
    }

}
