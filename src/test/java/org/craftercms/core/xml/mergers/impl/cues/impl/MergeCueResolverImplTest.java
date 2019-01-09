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
import java.util.HashMap;
import java.util.Map;

import org.craftercms.core.xml.mergers.impl.cues.MergeCue;
import org.craftercms.core.xml.mergers.impl.cues.MergeCueContext;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class MergeCueResolverImplTest {

    private static final String PARENT_XML =
            "<root>" +
                "<element1 hp=\"true\" hp-param=\"hi-parent\">value</element1>" +
                "<element2 lp=\"true\" lp-param=\"lo-parent\">value</element2>" +
                "<element3 hp=\"true\">value</element3>" +
                "<element4>value</element4>" +
            "</root>";

    private static final String CHILD_XML =
            "<root>" +
                "<element1 lp=\"true\" lp-param=\"lo-child\">value</element1>" +
                "<element2 hp=\"true\" hp-param=\"hi-child\">value</element2>" +
                "<element3>value</element3>" +
                "<element4 hp=\"true\">value</element4>" +
            "</root>";

    private MergeCueResolverImpl resolver;
    private MergeCue hiPriorityParentMergeCue;
    private MergeCue loPriorityParentMergeCue;
    private MergeCue hiPriorityChildMergeCue;
    private MergeCue loPriorityChildMergeCue;
    private Map<String, String> hiPriorityParentMergeCueParams;
    private Map<String, String> hiPriorityChildMergeCueParams;
    private Document parentDoc;
    private Document childDoc;

    @Before
    public void setUp() throws Exception {
        setUpTestMergeCues();
        setUpTestMergeCueParams();
        setUpTestResolver();
        setUpTestDocuments();
    }

    @Test
    public void testResolver() throws Exception {
        Element parentElement1 = (Element) parentDoc.selectSingleNode("/root/element1");
        Element parentElement2 = (Element) parentDoc.selectSingleNode("/root/element2");
        Element parentElement3 = (Element) parentDoc.selectSingleNode("/root/element3");
        Element parentElement4 = (Element) parentDoc.selectSingleNode("/root/element4");
        Element childElement1 = (Element) childDoc.selectSingleNode("/root/element1");
        Element childElement2 = (Element) childDoc.selectSingleNode("/root/element2");
        Element childElement3 = (Element) childDoc.selectSingleNode("/root/element3");
        Element childElement4 = (Element) childDoc.selectSingleNode("/root/element4");

        MergeCueContext context = resolver.getMergeCue(parentElement1, childElement1);
        assertNotNull(context);
        assertSame(parentElement1, context.getParent());
        assertSame(childElement1, context.getChild());
        assertSame(hiPriorityParentMergeCue, context.getMergeCue());
        assertEquals(hiPriorityParentMergeCueParams, context.getMergeCueParams());

        context = resolver.getMergeCue(parentElement2, childElement2);
        assertNotNull(context);
        assertSame(parentElement2, context.getParent());
        assertSame(childElement2, context.getChild());
        assertSame(hiPriorityChildMergeCue, context.getMergeCue());
        assertEquals(hiPriorityChildMergeCueParams, context.getMergeCueParams());

        context = resolver.getMergeCue(parentElement3, childElement3);
        assertNotNull(context);
        assertSame(parentElement3, context.getParent());
        assertSame(childElement3, context.getChild());
        assertSame(hiPriorityParentMergeCue, context.getMergeCue());
        assertEquals(0, context.getMergeCueParams().size());

        context = resolver.getMergeCue(parentElement4, childElement4);
        assertNotNull(context);
        assertSame(parentElement4, context.getParent());
        assertSame(childElement4, context.getChild());
        assertSame(hiPriorityChildMergeCue, context.getMergeCue());
        assertEquals(0, context.getMergeCueParams().size());
    }

    private void setUpTestMergeCues() {
        hiPriorityParentMergeCue = mock(MergeCue.class);
        loPriorityParentMergeCue = mock(MergeCue.class);
        hiPriorityChildMergeCue = mock(MergeCue.class);
        loPriorityChildMergeCue = mock(MergeCue.class);

        when(hiPriorityParentMergeCue.getPriority()).thenReturn(10);
        when(loPriorityParentMergeCue.getPriority()).thenReturn(1);
        when(hiPriorityChildMergeCue.getPriority()).thenReturn(10);
        when(loPriorityChildMergeCue.getPriority()).thenReturn(1);
    }

    private void setUpTestMergeCueParams() {
        hiPriorityParentMergeCueParams = new HashMap<>(1);
        hiPriorityChildMergeCueParams = new HashMap<>(1);

        hiPriorityParentMergeCueParams.put("param", "hi-parent");
        hiPriorityChildMergeCueParams.put("param", "hi-child");
    }

    private void setUpTestResolver() {
        Map<QName, MergeCue> parentMergeCues = new HashMap<QName, MergeCue>(2);
        parentMergeCues.put(new QName("hp"), hiPriorityParentMergeCue);
        parentMergeCues.put(new QName("lp"), loPriorityParentMergeCue);

        Map<QName, MergeCue> childMergeCues = new HashMap<QName, MergeCue>(2);
        childMergeCues.put(new QName("hp"), hiPriorityChildMergeCue);
        childMergeCues.put(new QName("lp"), loPriorityChildMergeCue);

        resolver = new MergeCueResolverImpl();
        resolver.setParentMergeCues(parentMergeCues);
        resolver.setChildMergeCues(childMergeCues);
        resolver.setDefaultParentMergeCue(loPriorityParentMergeCue);
        resolver.setDefaultChildMergeCue(loPriorityChildMergeCue);
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
