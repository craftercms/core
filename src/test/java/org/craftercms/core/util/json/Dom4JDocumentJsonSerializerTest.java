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
package org.craftercms.core.util.json;

import com.google.gson.JsonElement;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;
import org.craftercms.core.util.json.gson.Dom4JDocumentJsonSerializer;

import java.io.StringReader;

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
                    "<e7>text<a>text</a></e7>" +
                    "<e8>text<a>text</a>text</e8>" +
            "</root>";

    public static final String XML_AS_JSON =
            "{\"root\":{" +
                    "\"e1\":null," +
                    "\"e2\":\"text\"," +
                    "\"e3\":{\"name\":\"value\"}," +
                    "\"e4\":{\"name\":\"value\",\"text\":\"text\"}," +
                    "\"e5\":{\"a\":\"text\",\"b\":\"text\"}," +
                    "\"e6\":{\"a\":[\"text\",\"text\"]}," +
                    "\"e7\":{\"text\":\"text\",\"a\":\"text\"}," +
                    "\"e8\":{\"text\":[\"text\",\"text\"],\"a\":\"text\"}" +
                    "}" +
            "}";

    private Document document;

    @Before
    public void setUp() throws Exception {
        setUpTestDocument();
    }

    @Test
    public void testSerializer() {
        JsonElement json = Dom4JDocumentJsonSerializer.INSTANCE.serialize(document, null, null);

        assertEquals(XML_AS_JSON, json.toString());
    }

    private void setUpTestDocument() {
        SAXReader reader = new SAXReader();
        try {
            document = reader.read(new StringReader(XML));
        } catch (DocumentException e) {
        }
    }

}
