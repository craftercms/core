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

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.craftercms.core.processors.ItemProcessor;
import org.craftercms.core.processors.ItemProcessorResolver;
import org.craftercms.core.service.Item;
import org.springframework.beans.factory.annotation.Required;

/**
 * Composite {@link org.craftercms.core.processors.ItemProcessorResolver}, that iterates through a list of resolvers
 * until one of them provides a non-null {@link org.craftercms.core.processors.ItemProcessor}.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public class ItemProcessorResolverChain implements ItemProcessorResolver {

    /**
     * The default processor to use if no resolver returns a processor.
     */
    protected ItemProcessor defaultProcessor;
    /**
     * The chain of resolvers.
     */
    protected List<ItemProcessorResolver> resolvers;

    /**
     * Sets the default to use if no resolver returns a processor.
     */
    @Required
    public void setDefaultProcessor(ItemProcessor defaultProcessor) {
        this.defaultProcessor = defaultProcessor;
    }

    /**
     * Sets the chain of resolvers.
     */
    @Required
    public void setResolvers(List<ItemProcessorResolver> resolvers) {
        this.resolvers = resolvers;
    }

    /**
     * Returns the {@link ItemProcessor} to use for the given item. Iterates through the chain of resolvers until one
     * of them returns a non-null processor. If non of them returns a processor, the {@code defaultProcessor} will be
     * returned.
     */
    @Override
    public ItemProcessor getProcessor(Item item) {
        ItemProcessor processor;

        if (CollectionUtils.isNotEmpty(resolvers)) {
            for (ItemProcessorResolver resolver : resolvers) {
                processor = resolver.getProcessor(item);
                if (processor != null) {
                    return processor;
                }
            }
        }

        return defaultProcessor;
    }

}
