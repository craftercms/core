/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
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

import org.craftercms.core.processors.impl.IncludeDescriptorsProcessor;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;
import org.craftercms.core.service.ContentStoreService;
import org.craftercms.core.service.Context;
import org.craftercms.core.service.Item;

import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.craftercms.core.service.CachingOptions.DEFAULT_CACHING_OPTIONS;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class IncludeDescriptorsProcessorTest {

    public static final String INCLUDE_ELEM_XPATH_QUERY = "//include";

    private static final String DESCRIPTOR1_URL = "/folder/sub-folder/descriptor1.xml";
    private static final String DESCRIPTOR2_URL = "/folder/descriptor2.xml";

    private static final String DESCRIPTOR1_XML =
            "<page>" +
                    "<include>../descriptor2.xml</include>" +
            "</page>";

    private static final String DESCRIPTOR2_XML =
            "<component>" +
                    "<element>a</element>" +
            "</component>";

    private IncludeDescriptorsProcessor processor;
    private ContentStoreService storeService;
    private Document descriptorDom1;
    private Document descriptorDom2;
    private Context context;

    @Before
    public void setUp() throws Exception {
        setUpTestContext();
        setUpTestDescriptorDoms();
        setUpTestStoreService();
        setUpTestProcessor();
    }

    @Test
    public void testProcess() throws Exception {
        Item item = new Item();
        item.setDescriptorUrl(DESCRIPTOR1_URL);
        item.setDescriptorDom(descriptorDom1);

        item = processor.process(context, DEFAULT_CACHING_OPTIONS, item);
        assertNotNull(item.getDescriptorDom());

        Node element = item.getDescriptorDom().selectSingleNode("/page/component/element");
        assertNotNull(element);
        assertEquals("a", element.getText());
    }

    private void setUpTestContext() {
        context = mock(Context.class);
    }

    private void setUpTestDescriptorDoms() {
        SAXReader reader = new SAXReader();

        try {
            descriptorDom1 = reader.read(new StringReader(DESCRIPTOR1_XML));
        } catch (DocumentException e) {
        }

        try {
            descriptorDom2 = reader.read(new StringReader(DESCRIPTOR2_XML));
        } catch (DocumentException e) {
        }
    }

    private void setUpTestStoreService() {
        storeService = mock(ContentStoreService.class);

        Item item = new Item();
        item.setDescriptorUrl(DESCRIPTOR2_URL);
        item.setDescriptorDom(descriptorDom2);

        when(storeService.getItem(context, DEFAULT_CACHING_OPTIONS, DESCRIPTOR2_URL)).thenReturn(item);
    }

    private void setUpTestProcessor() {
        processor = new IncludeDescriptorsProcessor();
        processor.setIncludeElementXPathQuery(INCLUDE_ELEM_XPATH_QUERY);
        processor.setContentStoreService(storeService);
    }

}
