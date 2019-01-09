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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.craftercms.core.exception.ItemProcessingException;
import org.craftercms.core.processors.ItemProcessor;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.service.Item;

/**
 * Pipeline of {@link org.craftercms.core.processors.ItemProcessor}s. The output of each processor's
 * {@link org.craftercms.core.processors.ItemProcessor#process(org.craftercms.core.service.Context,
 * org.craftercms.core.service.CachingOptions, Item)} call is passed as input to the next processor.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public class ItemProcessorPipeline implements ItemProcessor {

    /**
     * List of processors which conforms the pipeline
     */
    protected List<ItemProcessor> processors;

    /**
     * Default no-args constructor.
     */
    public ItemProcessorPipeline() {
    }

    /**
     * Constructor that receives the list of processors which conform the pipeline.
     */
    public ItemProcessorPipeline(List<ItemProcessor> processors) {
        this.processors = processors;
    }

    /**
     * Constructor that receives the list of processors (as an array) which conform the pipeline.
     */
    public ItemProcessorPipeline(ItemProcessor... processors) {
        this.processors = Arrays.asList(processors);
    }

    /**
     * Sets the list of processors.
     */
    public void setProcessors(List<ItemProcessor> processors) {
        this.processors = processors;
    }

    /**
     * Adds a processor to the pipeline of processors.
     */
    public void addProcessor(ItemProcessor processor) {
        if (processors == null) {
            processors = new ArrayList<>();
        }

        processors.add(processor);
    }

    /**
     * Adds several processors to the pipeline of processors.
     */
    public void addProcessors(Collection<ItemProcessor> processors) {
        if (processors == null) {
            processors = new ArrayList<>();
        }

        processors.addAll(processors);
    }

    /**
     * Removes a processor from the pipeline of processors.
     *
     * @return true if the processor was removed
     */
    public boolean removeProcessor(ItemProcessor processor) {
        if (processors != null) {
            return processors.remove(processor);
        } else {
            return false;
        }
    }

    /**
     * Processes the given {@link Item}, by calling a pipeline of processors. The output of each processor's
     * {@link ItemProcessor#process(org.craftercms.core.service.Context, org.craftercms.core.service.CachingOptions,
     * Item)} call is passed as input to the next processor.
     *
     * @return the result of the final processor in the pipeline.
     * @throws ItemProcessingException if one of the processors in the pipeline couldn't process the item
     */
    @Override
    public Item process(Context context, CachingOptions cachingOptions, Item item) throws ItemProcessingException {
        if (CollectionUtils.isNotEmpty(processors)) {
            for (ItemProcessor processor : processors) {
                item = processor.process(context, cachingOptions, item);
            }
        }

        return item;
    }

    /**
     * Returns true if the specified {@code ItemProcessorPipeline}'s and this instance's list of processors are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ItemProcessorPipeline that = (ItemProcessorPipeline)o;

        if (processors != null? !processors.equals(that.processors): that.processors != null) {
            return false;
        }

        return true;
    }

    /**
     * Returns the hash code for this instance, which is basically the hash code of the list of processors. As with any
     * other {@link ItemProcessor}, this method is defined because any processor which is passed in the method call of
     * a {@link org.craftercms.core.service.ContentStoreService} can be used as part of a key for caching.
     */
    @Override
    public int hashCode() {
        return processors != null? processors.hashCode(): 0;
    }

    @Override
    public String toString() {
        return "ItemProcessorPipeline[" +
            "processors=" + processors +
            ']';
    }

}
