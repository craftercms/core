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

import org.craftercms.core.processors.impl.TextMetaDataExtractingProcessor;
import org.dom4j.Document;
import org.dom4j.Node;
import org.junit.Before;
import org.junit.Test;
import org.craftercms.core.service.Item;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class TextMetaDataExtractingProcessorTest {
    
    private static final String CSS = "css.css";
    private static final String JS = "js.js";

    private String[] testMetaDataNodesXPathQueries = { "//css", "//js" };

    private TextMetaDataExtractingProcessor processor;
    private Item item;

    @Before
    public void setUp() throws Exception {
        setUpTestProcessor();
        setUpTestItem();
    }

    @Test
    public void testProcessor() throws Exception {
        processor.process(null, null, item);

        assertEquals(CSS, item.getProperty(testMetaDataNodesXPathQueries[0]));
        assertEquals(JS, item.getProperty(testMetaDataNodesXPathQueries[1]));
    }

    private void setUpTestProcessor() {
        processor = new TextMetaDataExtractingProcessor(testMetaDataNodesXPathQueries);
    }

    private void setUpTestItem() {
        Node cssNode = mock(Node.class);
        when(cssNode.getText()).thenReturn(CSS);

        Node jsNode = mock(Node.class);
        when(jsNode.getText()).thenReturn(JS);

        Document descriptorDom = mock(Document.class);
        when(descriptorDom.selectSingleNode(testMetaDataNodesXPathQueries[0])).thenReturn(cssNode);
        when(descriptorDom.selectSingleNode(testMetaDataNodesXPathQueries[1])).thenReturn(jsNode);

        item = new Item();
        item.setDescriptorDom(descriptorDom);
    }    
    
}
