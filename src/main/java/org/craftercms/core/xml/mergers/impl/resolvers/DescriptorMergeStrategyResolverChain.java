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

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.craftercms.core.xml.mergers.DescriptorMergeStrategy;
import org.craftercms.core.xml.mergers.DescriptorMergeStrategyResolver;
import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Required;

/**
 * Chain of {@link DescriptorMergeStrategyResolver}s. The strategy returned is the first non-null one returned by a
 * <code>DescriptorMergeStrategyMapper</code> of the chain.
 *
 * @author Alfonso Vásquez
 */
public class DescriptorMergeStrategyResolverChain implements DescriptorMergeStrategyResolver {

    private DescriptorMergeStrategy defaultStrategy;
    private List<DescriptorMergeStrategyResolver> resolvers;

    @Required
    public void setDefaultStrategy(DescriptorMergeStrategy defaultStrategy) {
        this.defaultStrategy = defaultStrategy;
    }

    public void setResolvers(List<DescriptorMergeStrategyResolver> resolvers) {
        this.resolvers = resolvers;
    }

    /**
     * Returns the first non-null strategy returned by a {@link DescriptorMergeStrategyResolver} of the chain.
     * If there a no resolvers in the chain, or non of resolvers returns a {@link DescriptorMergeStrategy}, a
     * default strategy is returned.
     *
     * @param descriptorUrl the URL that identifies the descriptor
     * @param descriptorDom the XML DOM of the descriptor
     * @return the first non-null strategy returned by a {@link DescriptorMergeStrategyResolver} of the chain,
     *         or a default one if all the resolvers returned null.
     */
    public DescriptorMergeStrategy getStrategy(String descriptorUrl, Document descriptorDom) {
        DescriptorMergeStrategy strategy;

        if (CollectionUtils.isNotEmpty(resolvers)) {
            for (DescriptorMergeStrategyResolver resolver : resolvers) {
                strategy = resolver.getStrategy(descriptorUrl, descriptorDom);
                if (strategy != null) {
                    return strategy;
                }
            }
        }

        return defaultStrategy;
    }

}
