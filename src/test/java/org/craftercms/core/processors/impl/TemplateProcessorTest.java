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

import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;
import org.craftercms.core.processors.impl.template.NodeTemplateModelFactory;
import org.craftercms.core.processors.impl.template.TemplateProcessor;
import org.craftercms.core.service.Context;
import org.craftercms.core.service.Item;
import org.craftercms.core.util.template.impl.freemarker.FreeMarkerStringTemplateCompiler;
import org.craftercms.core.util.xml.impl.XPathNodeScanner;
import org.xml.sax.SAXException;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class TemplateProcessorTest {

    private static final String DESCRIPTOR_URL = "/folder/descriptor.xml";

    private static final String DESCRIPTOR_XML =
            "<root>" +
                    "<body>${body}</body>" +
            "</root>";

    private static final String BODY_MODEL_VALUE = "Crafter Software";

    private TemplateProcessor processor;
    private XPathNodeScanner nodeScanner;
    private FreeMarkerStringTemplateCompiler templateCompiler;
    private NodeTemplateModelFactory modelFactory;
    private Item item;
    private Context context;

    @Before
    public void setUp() throws Exception {
        setUpTestItem();
        setUpTestTemplateCompiler();
        setUpTestNodeScanner();
        setUpTestModelFactory();
        setUpTestProcessor();
    }

    @Test
    public void testProcess() throws Exception {
        item = processor.process(null, null, item);
        assertNotNull(item.getDescriptorDom());

        Node body = item.getDescriptorDom().selectSingleNode("/root/body");
        assertNotNull(body);
        assertEquals(BODY_MODEL_VALUE, body.getText());
    }

    private void setUpTestItem() throws DocumentException, SAXException {
        SAXReader reader = new SAXReader();

        reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
        reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

        item = new Item();
        item.setDescriptorUrl(DESCRIPTOR_URL);
        item.setDescriptorDom(reader.read(new StringReader(DESCRIPTOR_XML)));
    }

    private void setUpTestTemplateCompiler() {
        templateCompiler = new FreeMarkerStringTemplateCompiler();
    }

    private void setUpTestNodeScanner() {
        nodeScanner = new XPathNodeScanner();
        nodeScanner.setXPathQueries("//body");
    }

    private void setUpTestModelFactory() {
        modelFactory = new NodeTemplateModelFactory() {
            @Override
            public Object getModel(Item item, Node node, String template) {
                Map<String, String> model = new HashMap<String, String>();
                model.put("body", BODY_MODEL_VALUE);

                return model;
            }
        };
    }

    private void setUpTestProcessor() {
        processor = new TemplateProcessor();
        processor.setTemplateNodeScanner(nodeScanner);
        processor.setTemplateCompiler(templateCompiler);
        processor.setModelFactory(modelFactory);
    }

}
