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
package org.craftercms.core.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Alfonso Vásquez
 */
public class XmlUtilsTest {

    public static final String XML =
            "<root xmlns=\"http://namespace-a.com\" xmlns:b=\"http://namespace-b.com\">" +
                    "<e1>test1</e1>" +
                    "<b:e2>test2</b:e2>" +
                    "<e3 xmlns=\"http://namespace-c.com\">" +
                        "<e4>test3</e4>" +
                        "<e4>test4</e4>" +
                    "</e3>" +
            "</root>";

    private Document document;

    @Before
    public void setUp() throws Exception {
        setUpTestDocument();
    }

    @Test
    public void testSelectSingleNodeValue() throws Exception {
        Map<String, String> namespaceUris = new HashMap<String, String>();
        namespaceUris.put("a", "http://namespace-a.com");
        namespaceUris.put("b", "http://namespace-b.com");
        namespaceUris.put("c", "http://namespace-c.com");

        String e1 = XmlUtils.selectSingleNodeValue(document, "/a:root/a:e1", namespaceUris);
        String e2 = XmlUtils.selectSingleNodeValue(document, "/a:root/b:e2", namespaceUris);

        assertEquals("test1", e1);
        assertEquals("test2", e2);
    }

    @Test
    public void testSelectNodeValues() throws Exception {
        Map<String, String> namespaceUris = new HashMap<String, String>();
        namespaceUris.put("a", "http://namespace-a.com");
        namespaceUris.put("b", "http://namespace-b.com");
        namespaceUris.put("c", "http://namespace-c.com");

        List<String> e4s = XmlUtils.selectNodeValues(document, "/a:root/c:e3/c:e4", namespaceUris);

        assertEquals(2, e4s.size());
        assertEquals("test3", e4s.get(0));
        assertEquals("test4", e4s.get(1));
    }

    private void setUpTestDocument() throws SAXException {
        SAXReader reader = new SAXReader();

        reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
        reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

        try {
            document = reader.read(new StringReader(XML));
        } catch (DocumentException e) {
        }
    }

}
