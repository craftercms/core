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
package org.craftercms.core.xml.mergers.impl;

import org.craftercms.core.xml.mergers.impl.cues.impl.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;
import org.craftercms.core.xml.mergers.impl.cues.MergeCue;

import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class DescriptorMergerImplTest {
    
    public static final String OVERRIDE_PARENT_MERGE_CUE_ATTR_NAME = "override-parent";
    public static final String DISALLOW_OVERRIDE_MERGE_CUE_ATTR_NAME = "disallow-override";
    public static final String USE_PARENT_IF_AVAILABLE_MERGE_CUE_ATTR_NAME = "use-parent-if-available";
    public static final String MERGE_WITH_PARENT_MERGE_CUE_ATTR_NAME = "merge-with-parent";
    public static final String MERGE_WITH_CHILD_MERGE_CUE_ATTR_NAME = "merge-with-child";

    public static final int OVERRIDE_PARENT_MERGE_CUE_PRIORITY = 5;
    public static final int DISALLOW_OVERRIDE_MERGE_CUE_PRIORITY = 5;
    public static final int USE_PARENT_IF_AVAILABLE_MERGE_CUE_PRIORITY = 5;
    public static final int MERGE_WITH_PARENT_MERGE_CUE_PRIORITY = 5;
    public static final int MERGE_WITH_CHILD_MERGE_CUE_PRIORITY = 5;
    
    public static final int DEFAULT_PARENT_MERGE_CUE_PRIORITY = 1;
    public static final int DEFAULT_CHILD_MERGE_CUE_PRIORITY = 1;

    private static final String DESCRIPTOR1_XML =
            "<root>" +
                    "<element id=\"1\">a</element>" +
                    "<element id=\"2\" disallow-override=\"true\">b</element>" +
                    "<element id=\"3\">c</element>" +
                    "<group id=\"4\">" +
                        "<element id=\"5\">d</element>" +
                    "</group>" +
                    "<group id=\"6\" merge-with-child=\"true\" merge-with-child-order=\"after\">" +
                        "<element id=\"7\">e</element>" +
                    "</group>" +
            "</root>";

    private static final String DESCRIPTOR2_XML =
            "<root>" +
                    "<element id=\"1\" override-parent=\"true\">f</element>" +
                    "<element id=\"2\">g</element>" +
                    "<element id=\"3\" use-parent-if-available=\"true\">h</element>" +
                    "<group id=\"4\" merge-with-parent=\"true\" merge-with-parent-order=\"before\">" +
                        "<element id=\"8\">i</element>" +
                    "</group>" +
                    "<group id=\"6\">" +
                        "<element id=\"9\">j</element>" +
                    "</group>" +
            "</root>";

    private static final String MERGED_XML =
            "<root>" +
                    "<element id=\"1\">f</element>" +
                    "<element id=\"2\">b</element>" +
                    "<element id=\"3\">c</element>" +
                    "<group id=\"4\">" +
                        "<element id=\"8\">i</element>" +
                        "<element id=\"5\">d</element>" +
                    "</group>" +
                    "<group id=\"6\">" +
                        "<element id=\"7\">e</element>" +
                        "<element id=\"9\">j</element>" +
                    "</group>" +
            "</root>";

    private DescriptorMergerImpl merger;
    private List<Document> descriptorsToMerge;

    @Before
    public void setUp() throws Exception {
        setUpTestMerger();
        setUpTestDescriptorsToMerge();
    }

    @Test
    public void testMerge() throws Exception {
        Document merged = merger.merge(descriptorsToMerge);
        assertNotNull(merged);
        assertEquals(MERGED_XML, merged.getRootElement().asXML());
    }

    private void setUpTestMerger() {
        MergeCueResolverImpl mergeCueResolver = new MergeCueResolverImpl();

        ElementMergeMatcherImpl elementMergeMatcher = new ElementMergeMatcherImpl();
        elementMergeMatcher.setIdAttributeName(new QName(MergeParentAndChildMergeCueTest.ID_ATTR_NAME));

        UseChildMergeCue overrideParentMergeCue = new UseChildMergeCue();
        overrideParentMergeCue.setPriority(OVERRIDE_PARENT_MERGE_CUE_PRIORITY);

        UseParentMergeCue disallowOverrideMergeCue = new UseParentMergeCue();
        disallowOverrideMergeCue.setPriority(DISALLOW_OVERRIDE_MERGE_CUE_PRIORITY);

        UseParentMergeCue useParentIfAvailableMergeCue = new UseParentMergeCue();
        useParentIfAvailableMergeCue.setPriority(USE_PARENT_IF_AVAILABLE_MERGE_CUE_PRIORITY);

        MergeParentAndChildMergeCue mergeWithParentMergeCue = new MergeParentAndChildMergeCue();
        mergeWithParentMergeCue.setPriority(MERGE_WITH_PARENT_MERGE_CUE_PRIORITY);
        mergeWithParentMergeCue.setElementMergeMatcher(elementMergeMatcher);
        mergeWithParentMergeCue.setMergeCueResolver(mergeCueResolver);
        mergeWithParentMergeCue.setMergeOrderParamName(MergeParentAndChildMergeCueTest.MERGE_ORDER_PARAM_NAME);
        mergeWithParentMergeCue.setDefaultMergeOrder(MergeParentAndChildMergeCueTest.DEFAULT_MERGE_ORDER);

        MergeParentAndChildMergeCue mergeWithChildMergeCue = new MergeParentAndChildMergeCue();
        mergeWithChildMergeCue.setPriority(MERGE_WITH_CHILD_MERGE_CUE_PRIORITY);
        mergeWithChildMergeCue.setElementMergeMatcher(elementMergeMatcher);
        mergeWithChildMergeCue.setMergeCueResolver(mergeCueResolver);
        mergeWithChildMergeCue.setMergeOrderParamName(MergeParentAndChildMergeCueTest.MERGE_ORDER_PARAM_NAME);
        mergeWithChildMergeCue.setDefaultMergeOrder(MergeParentAndChildMergeCueTest.DEFAULT_MERGE_ORDER);

        UseChildMergeCue defaultParentMergeCue = new UseChildMergeCue();
        defaultParentMergeCue.setPriority(DEFAULT_PARENT_MERGE_CUE_PRIORITY);

        UseChildMergeCue defaultChildMergeCue = new UseChildMergeCue();
        defaultParentMergeCue.setPriority(DEFAULT_CHILD_MERGE_CUE_PRIORITY);

        Map<QName, MergeCue> parentMergeCues = new HashMap<QName, MergeCue>(2);
        parentMergeCues.put(new QName(DISALLOW_OVERRIDE_MERGE_CUE_ATTR_NAME), disallowOverrideMergeCue);
        parentMergeCues.put(new QName(MERGE_WITH_CHILD_MERGE_CUE_ATTR_NAME), mergeWithChildMergeCue);

        Map<QName, MergeCue> childMergeCues = new HashMap<QName, MergeCue>(3);
        childMergeCues.put(new QName(OVERRIDE_PARENT_MERGE_CUE_ATTR_NAME), overrideParentMergeCue);
        childMergeCues.put(new QName(USE_PARENT_IF_AVAILABLE_MERGE_CUE_ATTR_NAME), useParentIfAvailableMergeCue);
        childMergeCues.put(new QName(MERGE_WITH_PARENT_MERGE_CUE_ATTR_NAME), mergeWithParentMergeCue);

        mergeCueResolver.setParentMergeCues(parentMergeCues);
        mergeCueResolver.setChildMergeCues(childMergeCues);
        mergeCueResolver.setDefaultParentMergeCue(defaultParentMergeCue);
        mergeCueResolver.setDefaultChildMergeCue(defaultChildMergeCue);

        merger = new DescriptorMergerImpl();
        merger.setInitialMergeCue(mergeWithChildMergeCue);
    }

    private void setUpTestDescriptorsToMerge() throws DocumentException {
        SAXReader reader = new SAXReader();

        Document descriptorDoc1 = reader.read(new StringReader(DESCRIPTOR1_XML));
        Document descriptorDoc2 = reader.read(new StringReader(DESCRIPTOR2_XML));

        descriptorsToMerge = Arrays.asList(descriptorDoc1, descriptorDoc2);
    }

}
