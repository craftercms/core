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

import org.craftercms.core.processors.impl.resolvers.MetaDataProcessorResolver;
import org.dom4j.Document;
import org.dom4j.Node;
import org.junit.Before;
import org.junit.Test;
import org.craftercms.core.processors.ItemProcessor;
import org.craftercms.core.service.Item;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class MetaDataProcessorResolverTest {

    public static final String PROCESSOR_ELEMENT_XPATH_QUERY = "//processor";

    private static final String PROCESSOR1_ELEMENT_VALUE = "processor1";
    private static final String PROCESSOR2_ELEMENT_VALUE = "processor2";

    private MetaDataProcessorResolver resolver;
    private ItemProcessor processor1;
    private ItemProcessor processor2;
    private Document descriptorDomProcessor1;
    private Document descriptorDomProcessor2;

    @Before
    public void setUp() throws Exception {
        setUpTestProcessors();
        setUpTestResolver();
        setUpTestDescriptorDoms();
    }

    @Test
    public void testGetProcessor() throws Exception {
        Item item = new Item();
        item.setDescriptorDom(descriptorDomProcessor1);

        ItemProcessor processor = resolver.getProcessor(item);
        assertSame(processor1, processor);

        item = new Item();
        item.setDescriptorDom(descriptorDomProcessor2);

        processor = resolver.getProcessor(item);
        assertSame(processor2, processor);
    }

    private void setUpTestProcessors() {
        processor1 = mock(ItemProcessor.class);
        processor2 = mock(ItemProcessor.class);
    }

    private void setUpTestResolver() {
        Map<String, ItemProcessor> processors = new HashMap<String, ItemProcessor>();
        processors.put(PROCESSOR1_ELEMENT_VALUE, processor1);
        processors.put(PROCESSOR2_ELEMENT_VALUE, processor2);

        resolver = new MetaDataProcessorResolver();
        resolver.setProcessorElementXPathQuery(PROCESSOR_ELEMENT_XPATH_QUERY);
        resolver.setElementValueToProcessorMappings(processors);
    }
    
    private void setUpTestDescriptorDoms() {
        Node processor1MergeProcessorElement = mock(Node.class);
        when(processor1MergeProcessorElement.getText()).thenReturn(PROCESSOR1_ELEMENT_VALUE);

        descriptorDomProcessor1 = mock(Document.class);
        when(descriptorDomProcessor1.selectSingleNode(PROCESSOR_ELEMENT_XPATH_QUERY)).thenReturn(
                processor1MergeProcessorElement);

        Node processor2MergeProcessorElement = mock(Node.class);
        when(processor2MergeProcessorElement.getText()).thenReturn(PROCESSOR2_ELEMENT_VALUE);

        descriptorDomProcessor2 = mock(Document.class);
        when(descriptorDomProcessor2.selectSingleNode(PROCESSOR_ELEMENT_XPATH_QUERY)).thenReturn(
                processor2MergeProcessorElement);
    }    

}
