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

import org.craftercms.core.processors.impl.resolvers.SingleProcessorResolver;
import org.junit.Before;
import org.junit.Test;
import org.craftercms.core.processors.ItemProcessor;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class SingleProcessorResolverTest {

    private SingleProcessorResolver resolver;
    private ItemProcessor processor;

    @Before
    public void setUp() throws Exception {
        setUpTestProcessor();
        setUpTestResolver();
    }

    @Test
    public void testResolver() throws Exception {
        ItemProcessor processor = resolver.getProcessor(null);
        assertSame(this.processor, processor);
    }

    private void setUpTestProcessor() {
        processor = mock(ItemProcessor.class);
    }

    private void setUpTestResolver() {
        resolver = new SingleProcessorResolver();
        resolver.setProcessor(processor);
    }

}
