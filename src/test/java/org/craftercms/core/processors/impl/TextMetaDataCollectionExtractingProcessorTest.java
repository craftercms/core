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

import org.craftercms.core.processors.impl.TextMetaDataCollectionExtractingProcessor;
import org.dom4j.Document;
import org.dom4j.Node;
import org.junit.Before;
import org.junit.Test;
import org.craftercms.core.service.Item;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class TextMetaDataCollectionExtractingProcessorTest {

    private static final String CSS1 = "css1.css";
    private static final String CSS2 = "css2.css";
    private static final String JS1 = "js1.js";
    private static final String JS2 = "js2.js";

    private String[] testMetaDataNodesXPathQueries = { "//css", "//js" };

    private TextMetaDataCollectionExtractingProcessor processor;
    private Item item;

    @Before
    public void setUp() throws Exception {
        setUpTestProcessor();
        setUpTestItem();
    }

    @Test
    public void testProcess() throws Exception {
        processor.process(null, null, item);

        assertEquals(Arrays.asList(CSS1, CSS2), item.getProperty(testMetaDataNodesXPathQueries[0]));
        assertEquals(Arrays.asList(JS1, JS2), item.getProperty(testMetaDataNodesXPathQueries[1]));
    }

    private void setUpTestProcessor() {
        processor = new TextMetaDataCollectionExtractingProcessor(testMetaDataNodesXPathQueries);
    }

    private void setUpTestItem() {
        Node css1Node = mock(Node.class);
        when(css1Node.getText()).thenReturn(CSS1);

        Node css2Node = mock(Node.class);
        when(css2Node.getText()).thenReturn(CSS2);

        Node js1Node = mock(Node.class);
        when(js1Node.getText()).thenReturn(JS1);

        Node js2Node = mock(Node.class);
        when(js2Node.getText()).thenReturn(JS2);

        Document descriptorDom = mock(Document.class);
        when(descriptorDom.selectNodes(testMetaDataNodesXPathQueries[0])).thenReturn(Arrays.asList(css1Node, css2Node));
        when(descriptorDom.selectNodes(testMetaDataNodesXPathQueries[1])).thenReturn(Arrays.asList(js1Node, js2Node));

        item = new Item();
        item.setDescriptorDom(descriptorDom);
    }

}
