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

import org.craftercms.core.processors.impl.resolvers.UrlPatternProcessorResolver;
import org.junit.Before;
import org.junit.Test;
import org.craftercms.core.processors.ItemProcessor;
import org.craftercms.core.service.Item;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class UrlPatternProcessorResolverTest {

    private static final String PAGES_PROCESSOR_PATTERN = "/site/pages/.*";
    private static final String STATIC_ASSETS_PROCESSOR_PATTERN = "/site/static-assets/.*";

    private static final String DESCRIPTOR_URL = "/site/pages/descriptor.xml";
    private static final String IMAGE_URL = "/site/static-assets/images/image.jpg";

    private UrlPatternProcessorResolver resolver;
    private ItemProcessor pagesProcessor;
    private ItemProcessor staticAssetsProcessor;

    @Before
    public void setUp() throws Exception {
        setUpTestProcessors();
        setUpTestResolver();
    }

    @Test
    public void testGetProcessor() throws Exception {
        Item item = new Item();
        item.setUrl(DESCRIPTOR_URL);

        ItemProcessor processor = resolver.getProcessor(item);
        assertSame(processor, pagesProcessor);

        item = new Item();
        item.setUrl(IMAGE_URL);

        processor = resolver.getProcessor(item);
        assertSame(processor, staticAssetsProcessor);
    }

    private void setUpTestProcessors() {
        pagesProcessor = mock(ItemProcessor.class);
        staticAssetsProcessor = mock(ItemProcessor.class);
    }

    private void setUpTestResolver() {
        Map<String, ItemProcessor> processors = new HashMap<String, ItemProcessor>();
        processors.put(PAGES_PROCESSOR_PATTERN, pagesProcessor);
        processors.put(STATIC_ASSETS_PROCESSOR_PATTERN, staticAssetsProcessor);

        resolver = new UrlPatternProcessorResolver();
        resolver.setPatternToProcessorMappings(processors);
    }

}
