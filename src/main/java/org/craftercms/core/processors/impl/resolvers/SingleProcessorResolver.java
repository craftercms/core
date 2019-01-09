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

import org.craftercms.core.processors.ItemProcessor;
import org.craftercms.core.processors.ItemProcessorResolver;
import org.craftercms.core.service.Item;
import org.springframework.beans.factory.annotation.Required;

/**
 * {@link ItemProcessorResolver} that returns a fixed {@link org.craftercms.core.processors.ItemProcessor}
 *
 * @author Alfonso Vásquez
 */
public class SingleProcessorResolver implements ItemProcessorResolver {

    /**
     * The processor to always return.
     */
    private ItemProcessor processor;

    /**
     * Sets the processor to always return.
     */
    @Required
    public void setProcessor(ItemProcessor processor) {
        this.processor = processor;
    }

    /**
     * Returns the same processor for any item.
     */
    @Override
    public ItemProcessor getProcessor(Item item) {
        return processor;
    }

}
