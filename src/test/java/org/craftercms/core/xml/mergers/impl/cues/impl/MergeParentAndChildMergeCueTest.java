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
package org.craftercms.core.xml.mergers.impl.cues.impl;

import java.io.StringReader;
import java.util.Collections;

import org.craftercms.core.xml.mergers.impl.cues.MergeCueContext;
import org.craftercms.core.xml.mergers.impl.cues.MergeCueResolver;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class MergeParentAndChildMergeCueTest {

    public static final String ID_ATTR_NAME = "id";

    public static final String MERGE_ORDER_PARAM_NAME = "order";
    public static final String DEFAULT_MERGE_ORDER = "after";
    public static final Logger LOGGER = LoggerFactory.getLogger(MergeParentAndChildMergeCueTest.class);

    private static final String PARENT_XML =
            "<group>" +
                    "<item id=\"1\">a</item>" +
                    "<item id=\"2\">b</item>" +
                    "<group id=\"3\"></group>" +
                    "<group id=\"4\">" +
                        "<item id=\"5\">c</item>" +
                    "</group>" +
            "</group>";

    private static final String CHILD_XML =
            "<group>" +
                    "<item id=\"1\">d</item>" +
                    "<item id=\"6\">e</item>" +
                    "<group id=\"3\">" +
                        "<item id=\"7\">d</item>" +
                    "</group>" +
                    "<group id=\"4\"></group>" +
            "</group>";

    private static final String MERGED_XML =
            "<group>" +
                    "<item id=\"1\">ad</item>" +
                    "<group id=\"3\">" +
                        "<item id=\"7\">d</item>" +
                    "</group>" +
                    "<group id=\"4\">" +
                        "<item id=\"5\">c</item>" +
                    "</group>" +
                    "<item id=\"2\">b</item>" +
                    "<item id=\"6\">e</item>" +
            "</group>";

    private MergeParentAndChildMergeCue mergeCue;
    private Document parentDoc;
    private Document childDoc;

    @Before
    public void setUp() throws Exception {
        setUpTestMergeCue();
        setUpTestDocuments();
    }

    @Test
    public void testMergeCue() throws Exception {
        Element parent = (Element) parentDoc.selectSingleNode("/group");
        Element child = (Element) childDoc.selectSingleNode("/group");

        Element merged = mergeCue.merge(parent, child, Collections.singletonMap(MERGE_ORDER_PARAM_NAME,
                                                                                DEFAULT_MERGE_ORDER));
        assertNotNull(merged);
        assertEquals(MERGED_XML, merged.asXML());
    }

    private void setUpTestMergeCue() {
        ElementMergeMatcherImpl elementMergeMatcher = new ElementMergeMatcherImpl();
        elementMergeMatcher.setIdAttributeName(new QName(ID_ATTR_NAME));

        MergeCueResolver mergeCueResolver = new MergeCueResolver() {

            @Override
            public MergeCueContext getMergeCue(Element parent, Element child) {
                return new MergeCueContext(mergeCue, parent, child, Collections.<String, String>emptyMap());
            }

        };

        mergeCue = new MergeParentAndChildMergeCue();
        mergeCue.setElementMergeMatcher(elementMergeMatcher);
        mergeCue.setMergeCueResolver(mergeCueResolver);
        mergeCue.setMergeOrderParamName(MERGE_ORDER_PARAM_NAME);
        mergeCue.setDefaultMergeOrder(DEFAULT_MERGE_ORDER);
    }

    private void setUpTestDocuments() throws DocumentException, SAXException {
        SAXReader reader = new SAXReader();

        reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
        reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

        parentDoc = reader.read(new StringReader(PARENT_XML));
        childDoc = reader.read(new StringReader(CHILD_XML));
    }

}
