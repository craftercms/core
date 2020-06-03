/*
 * Copyright (C) 2007-2020 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
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

import java.io.StringReader;

import org.craftercms.core.service.ContentStoreService;
import org.craftercms.core.service.Context;
import org.craftercms.core.service.Item;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import static org.craftercms.core.service.CachingOptions.DEFAULT_CACHING_OPTIONS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class IncludeDescriptorsProcessorTest {

    public static final String INCLUDE_ELEM_XPATH_QUERY = "//include";
    public static final String DISABLED_INCLUDE_NODE_XPATH_QUERY = "@disabled";

    private static final String DESCRIPTOR1_URL = "/folder/sub-folder/descriptor1.xml";
    private static final String DESCRIPTOR2_URL = "/folder/descriptor2.xml";
    private static final String DESCRIPTOR3_URL = "/folder/descriptor3.xml";
    private static final String DESCRIPTOR4_URL = "/folder/descriptor4.xml";

    private static final String DESCRIPTOR1_XML =   "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                                                    "<page>" +
                                                        "<include>" + DESCRIPTOR2_URL + "</include>" +
                                                    "</page>";

    private static final String DESCRIPTOR2_XML =   "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                                                    "<component>" +
                                                        "<element>a</element>" +
                                                        "<include>" + DESCRIPTOR3_URL + "</include>" +
                                                        "<include disabled='true'>" + DESCRIPTOR4_URL + "</include>" +
                                                    "</component>";

    private static final String DESCRIPTOR3_XML =   "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                                                    "<component>" +
                                                        "<element>b</element>" +
                                                        "<include>" + DESCRIPTOR1_URL + "</include>" +
                                                    "</component>";

    private static final String EXPECTED_XML =      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                                                    "<page>" +
                                                        "<include>" + DESCRIPTOR2_URL + "</include>" +
                                                        "<component>" +
                                                            "<element>a</element>" +
                                                            "<include>" + DESCRIPTOR3_URL + "</include>" +
                                                            "<component>" +
                                                                "<element>b</element>" +
                                                                "<include>" + DESCRIPTOR1_URL + "</include>" +
                                                            "</component>" +
                                                            "<include disabled=\"true\">" + DESCRIPTOR4_URL + "</include>" +
                                                        "</component>" +
                                                    "</page>";

    private IncludeDescriptorsProcessor processor;
    private ContentStoreService storeService;
    private Document descriptorDom1;
    private Document descriptorDom2;
    private Document descriptorDom3;
    private Context context;

    @Before
    public void setUp() throws Exception {
        // Create first to avoid circular dependency problems
        processor = new IncludeDescriptorsProcessor();

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
        assertEquals(EXPECTED_XML, item.getDescriptorDom().asXML().replace("\n", ""));
    }

    private void setUpTestContext() {
        context = mock(Context.class);
    }

    private void setUpTestDescriptorDoms() throws DocumentException, SAXException {
        SAXReader reader = new SAXReader();

        reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
        reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

        descriptorDom1 = reader.read(new StringReader(DESCRIPTOR1_XML));
        descriptorDom2 = reader.read(new StringReader(DESCRIPTOR2_XML));
        descriptorDom3 = reader.read(new StringReader(DESCRIPTOR3_XML));
    }

    private void setUpTestStoreService() {
        storeService = mock(ContentStoreService.class);

        when(storeService.findItem(context, DEFAULT_CACHING_OPTIONS, DESCRIPTOR2_URL, processor)).thenAnswer(
            invocationOnMock -> {
                Item item2 = new Item();
                item2.setDescriptorUrl(DESCRIPTOR2_URL);
                item2.setDescriptorDom(descriptorDom2);

                return processor.process(context, DEFAULT_CACHING_OPTIONS, item2);
            }
        );
        when(storeService.findItem(context, DEFAULT_CACHING_OPTIONS, DESCRIPTOR3_URL, processor)).thenAnswer(
            invocationOnMock -> {
                Item item3 = new Item();
                item3.setDescriptorUrl(DESCRIPTOR3_URL);
                item3.setDescriptorDom(descriptorDom3);

                return processor.process(context, DEFAULT_CACHING_OPTIONS, item3);
            }
        );
    }

    private void setUpTestProcessor() {
        processor.setIncludeElementXPathQuery(INCLUDE_ELEM_XPATH_QUERY);
        processor.setDisabledIncludeNodeXPathQuery(DISABLED_INCLUDE_NODE_XPATH_QUERY);
        processor.setContentStoreService(storeService);
        processor.setIncludedItemsProcessor(processor);
    }

}
