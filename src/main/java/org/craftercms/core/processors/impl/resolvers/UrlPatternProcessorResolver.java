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

import org.craftercms.core.processors.ItemProcessor;
import org.craftercms.core.processors.ItemProcessorResolver;
import org.craftercms.core.service.Item;
import org.springframework.beans.factory.annotation.Required;

/**
 * {@link org.craftercms.core.processors.ItemProcessorResolver} that returns certain
 * {@link org.craftercms.core.processors.ItemProcessor}s for certain url patterns.
 *
 * @author Alfonso Vásquez
 */
public class UrlPatternProcessorResolver implements ItemProcessorResolver {

    /**
     * Mappings of url patterns to processor names.
     */
    protected Map<String, ItemProcessor> patternToProcessorMappings;

    /**
     * Sets the mappings of url patterns to processor names.
     */
    @Required
    public void setPatternToProcessorMappings(Map<String, ItemProcessor> patternToProcessorMappings) {
        this.patternToProcessorMappings = patternToProcessorMappings;
    }

    /**
     * If the item url matches one of the patterns defined in {@code patternToProcessorMappings}, the processor
     * mapped to that pattern is returned. If not, null is returned.
     */
    @Override
    public ItemProcessor getProcessor(Item item) {
        for (Map.Entry<String, ItemProcessor> mapping : patternToProcessorMappings.entrySet()) {
            if (item.getUrl().matches(mapping.getKey())) {
                return mapping.getValue();
            }
        }

        return null;
    }

}
