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
import java.util.Collections;
import java.util.Map;

import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.service.Item;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * Created by alfonso on 2/21/17.
 */
public class AttributeAddingProcessorTest {

    private static final String INPUT_XML =     "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                                                "<page>" +
                                                    "<name>test.xml</name>" +
                                                    "<title>Test</title>" +
                                                    "<date>11/10/2015 00:00:00</date>" +
                                                "</page>";
    private static final String EXPECTED_XML =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                                                "<page>" +
                                                    "<name>test.xml</name>" +
                                                    "<title>Test</title>" +
                                                    "<date format=\"MM/DD/YYY HH:MM:SS\">11/10/2015 00:00:00</date>" +
                                                "</page>";

    private AttributeAddingProcessor processor;

    @Before
    public void setUp() throws Exception {
        setUpProcessor();
    }

    @Test
    public void testProcess() throws Exception {
        Item item = new Item();
        item.setDescriptorDom(readInputXml());

        item = processor.process(mock(Context.class), CachingOptions.DEFAULT_CACHING_OPTIONS, item);
        assertNotNull(item.getDescriptorDom());
        assertEquals(EXPECTED_XML, item.getDescriptorDom().asXML().replace("\n", ""));
    }

    private void setUpProcessor() {
        Map<String, String> attributes = Collections.singletonMap("format", "MM/DD/YYY HH:MM:SS");

        processor = new AttributeAddingProcessor();
        processor.setAttributeMappings(Collections.singletonMap("//date", attributes));
    }

    private Document readInputXml() throws DocumentException, SAXException {
        SAXReader reader = new SAXReader();

        reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
        reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

        return reader.read(new StringReader(INPUT_XML));
    }

}
