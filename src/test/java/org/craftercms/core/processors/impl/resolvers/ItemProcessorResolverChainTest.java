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

import org.craftercms.core.processors.impl.resolvers.ItemProcessorResolverChain;
import org.junit.Before;
import org.junit.Test;
import org.craftercms.core.processors.ItemProcessor;
import org.craftercms.core.processors.ItemProcessorResolver;
import org.craftercms.core.service.Item;

import java.util.Arrays;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class ItemProcessorResolverChainTest {

    private static final String ITEM1_URL = "/item1.xml";
    private static final String ITEM2_URL = "/item2.xml";
    private static final String ITEM3_URL = "/item3.xml";

    private ItemProcessorResolverChain resolverChain;
    private Item item1;
    private Item item2;
    private Item item3;
    private ItemProcessor processor1;
    private ItemProcessor processor2;
    private ItemProcessor defaultProcessor;

    @Before
    public void setUp() throws Exception {
        setUpTestItems();
        setUpTestProcessors();
        setUpTestResolverChain();
    }

    @Test
    public void testResolverChain() throws Exception {
        ItemProcessor processor = resolverChain.getProcessor(item1);
        assertSame(processor1, processor);

        processor = resolverChain.getProcessor(item2);
        assertSame(processor2, processor);

        processor = resolverChain.getProcessor(item3);
        assertSame(defaultProcessor, processor);
    }

    private void setUpTestItems() {
        item1 = new Item();
        item1.setUrl(ITEM1_URL);

        item2 = new Item();
        item2.setUrl(ITEM2_URL);

        item3 = new Item();
        item3.setUrl(ITEM3_URL);
    }

    private void setUpTestProcessors() {
        processor1 = mock(ItemProcessor.class);
        processor2 = mock(ItemProcessor.class);
        defaultProcessor = mock(ItemProcessor.class);
    }

    private void setUpTestResolverChain() {
        ItemProcessorResolver resolver1 = mock(ItemProcessorResolver.class);
        when(resolver1.getProcessor(item1)).thenReturn(processor1);

        ItemProcessorResolver resolver2 = mock(ItemProcessorResolver.class);
        when(resolver1.getProcessor(item2)).thenReturn(processor2);

        resolverChain = new ItemProcessorResolverChain();
        resolverChain.setResolvers(Arrays.asList(resolver1, resolver2));
        resolverChain.setDefaultProcessor(defaultProcessor);
    }

}
