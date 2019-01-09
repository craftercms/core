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

import org.craftercms.core.xml.mergers.DescriptorMergeStrategy;
import org.craftercms.core.xml.mergers.DescriptorMergeStrategyResolver;
import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Required;

/**
 * Resolves the {@link DescriptorMergeStrategy} to use for a given descriptor by matching the descriptor URL
 * to a pattern.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public class UrlPatternMergeStrategyResolver implements DescriptorMergeStrategyResolver {

    private Map<String, DescriptorMergeStrategy> urlPatternToStrategyMappings;

    @Required
    public void setUrlPatternToStrategyMappings(Map<String, DescriptorMergeStrategy> urlPatternToStrategyMappings) {
        this.urlPatternToStrategyMappings = urlPatternToStrategyMappings;
    }

    /**
     * Returns a {@link DescriptorMergeStrategy} for a given descriptor, picked by matching the descriptor URL to a
     * pattern associated to the strategy.
     *
     * @param descriptorUrl the URL that identifies the descriptor
     * @param descriptorDom the XML DOM of the descriptor
     * @return the {@link DescriptorMergeStrategy} for the descriptor, or null if the descriptor URL doesn't
     *         match any pattern.
     */
    public DescriptorMergeStrategy getStrategy(String descriptorUrl, Document descriptorDom) {
        for (Map.Entry<String, DescriptorMergeStrategy> entry : urlPatternToStrategyMappings.entrySet()) {
            if (descriptorUrl.matches(entry.getKey())) {
                return entry.getValue();
            }
        }

        return null;
    }

}
