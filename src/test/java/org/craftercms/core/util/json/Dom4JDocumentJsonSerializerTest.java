/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
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

    private static final String XML =
            """
                    <root>
                        <e1 />
                        <e2>text</e2>
                        <e3 name='value' />
                        <e4 name='value'>text</e4>
                        <e5><a>text</a><b>text</b></e5>
                        <e6><a>text</a><a>text</a></e6>
                        <e7><a name='value'>text</a><a name='value'>text</a></e7>
                        <e8>text<a>text</a></e8>
                        <e9>text<a>text</a>text</e9>
                        <e10 item-list="true" />
                        <e11 item-list="true">
                            <item>
                                <key>e11_key</key>
                                <value>e11_value</value>
                                <include>e11_include</include>
                            </item>
                        </e11>
                        <e12 />
                        <e13 no-default="true" />
                        <e14 crafter-source="/site/website/crafter-level-descriptor.level.xml"
                                crafter-source-content-type-id="/component/level-descriptor">inherited</e14>
                    </root>""";

    private static final String XML_AS_JSON =
            """
                    {
                        "root":{
                            "e1":null,
                            "e2":"text",
                            "e3":{"name":"value"},
                            "e4":{"name":"value","text":"text"},
                            "e5":{"a":"text","b":"text"},
                            "e6":{"a":["text","text"]},
                            "e7":{"a":[{"name":"value","text":"text"},{"name":"value","text":"text"}]},
                            "e8":{"text":"text","a":"text"},
                            "e9":{"text":["text","text"],"a":"text"},
                            "e10":{},
                            "e11": {
                                "item": [
                                    {"key": "e11_key", "value": "e11_value", "include": "e11_include"}
                                ]
                            },
                            "e12":null,
                            "e13":null,
                            "e14": {
                                "crafter-source": "/site/website/crafter-level-descriptor.level.xml",
                                "crafter-source-content-type-id": "/component/level-descriptor",
                                "text": "inherited"}
                        }
                    }""".replaceAll("[\\n\\t\\s]+", "");
    private static final String XML_AS_JSON_NO_ATTRIBUTES =
            """
                    {
                        "root":{
                            "e1":null,
                            "e2":"text",
                            "e3":null,
                            "e4":"text",
                            "e5":{"a":"text","b":"text"},
                            "e6":{"a":["text","text"]},
                            "e7":{"a":["text","text"]},
                            "e8":{"text":"text","a":"text"},
                            "e9":{"text":["text","text"],"a":"text"},
                            "e10":{},
                            "e11": {
                                "item": [
                                    {"key": "e11_key", "value": "e11_value", "include": "e11_include"}
                                ]
                            },
                            "e12":null,
                            "e13":null,
                            "e14": "inherited"
                        }
                    }""".replaceAll("[\\n\\t\\s]+", "");

    private Document document;

    @Before
    public void setUp() throws Exception {
        setUpTestDocument();
    }

    @Test
    public void testRenderAttributes() throws Exception {
        String json = getTestObjectMapper(true).writeValueAsString(document);

        assertEquals(XML_AS_JSON, json.toString());
    }

    @Test
    public void testNoRenderAttributes() throws Exception {
        String json = getTestObjectMapper(false).writeValueAsString(document);

        assertEquals(XML_AS_JSON_NO_ATTRIBUTES, json.toString());
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

    private CustomSerializationObjectMapper getTestObjectMapper(boolean renderAttributes) {
        Dom4jDocumentJsonSerializer serializer = new Dom4jDocumentJsonSerializer(renderAttributes);
        List<JsonSerializer<?>> serializers = new ArrayList<>();

        serializers.add(serializer);

        CustomSerializationObjectMapper objectMapper = new CustomSerializationObjectMapper();
        objectMapper.setSerializers(serializers);
        objectMapper.afterPropertiesSet();
        return objectMapper;
    }

}
