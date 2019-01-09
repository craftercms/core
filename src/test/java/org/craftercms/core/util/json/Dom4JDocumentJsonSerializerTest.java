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
package org.craftercms.core.util.json;

import com.fasterxml.jackson.databind.JsonSerializer;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.craftercms.commons.jackson.CustomSerializationObjectMapper;
import org.craftercms.core.util.json.jackson.Dom4jDocumentJsonSerializer;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import static org.junit.Assert.assertEquals;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class Dom4JDocumentJsonSerializerTest {

    public static final String XML =
            "<root>" +
                    "<e1 />" +
                    "<e2>text</e2>" +
                    "<e3 name='value' />" +
                    "<e4 name='value'>text</e4>" +
                    "<e5><a>text</a><b>text</b></e5>" +
                    "<e6><a>text</a><a>text</a></e6>" +
                    "<e7><a name='value'>text</a><a name='value'>text</a></e7>" +
                    "<e8>text<a>text</a></e8>" +
                    "<e9>text<a>text</a>text</e9>" +
            "</root>";

    public static final String XML_AS_JSON =
            "{\"root\":{" +
                    "\"e1\":null," +
                    "\"e2\":\"text\"," +
                    "\"e3\":{\"name\":\"value\"}," +
                    "\"e4\":{\"name\":\"value\",\"text\":\"text\"}," +
                    "\"e5\":{\"a\":\"text\",\"b\":\"text\"}," +
                    "\"e6\":{\"a\":[\"text\",\"text\"]}," +
                    "\"e7\":{\"a\":[{\"name\":\"value\",\"text\":\"text\"},{\"name\":\"value\",\"text\":\"text\"}]}," +
                    "\"e8\":{\"text\":\"text\",\"a\":\"text\"}," +
                    "\"e9\":{\"text\":[\"text\",\"text\"],\"a\":\"text\"}" +
                    "}" +
            "}";

    private Document document;
    private CustomSerializationObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        setUpTestDocument();
        setUpTestObjectMapper();
    }

    @Test
    public void testSerializer() throws Exception {
        String json = objectMapper.writeValueAsString(document);

        assertEquals(XML_AS_JSON, json.toString());
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

    private void setUpTestObjectMapper() {
        Dom4jDocumentJsonSerializer serializer = new Dom4jDocumentJsonSerializer();
        List<JsonSerializer<?>> serializers = new ArrayList<>();

        serializers.add(serializer);

        objectMapper = new CustomSerializationObjectMapper();
        objectMapper.setSerializers(serializers);
        objectMapper.init();
    }

}
