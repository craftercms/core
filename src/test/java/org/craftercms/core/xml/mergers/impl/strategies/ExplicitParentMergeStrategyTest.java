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

package org.craftercms.core.xml.mergers.impl.strategies;

import java.util.Arrays;
import java.util.List;

import org.craftercms.core.service.Context;
import org.craftercms.core.service.Item;
import org.craftercms.core.store.ContentStoreAdapter;
import org.craftercms.core.xml.mergers.DescriptorMergeStrategy;
import org.craftercms.core.xml.mergers.DescriptorMergeStrategyResolver;
import org.craftercms.core.xml.mergers.MergeableDescriptor;
import org.dom4j.Document;
import org.dom4j.Node;
import org.junit.Before;
import org.junit.Test;

import static org.craftercms.core.service.CachingOptions.DEFAULT_CACHING_OPTIONS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ExplicitParentMergeStrategy}.
 *
 * @author avasquez
 */
public class ExplicitParentMergeStrategyTest {

    private static final String LEVEL_DESCRIPTOR_URL = "/crafter-level-descriptor.level.xml";
    private static final String PARENT_DESCRIPTOR_URL = "/parent.xml";
    private static final String MAIN_DESCRIPTOR_URL = "/main.xml";

    private static final String PARENT_DESCRIPTOR_ELEM_XPATH_QUERY = "*/parent-descriptor";

    private Context context;
    private Document descriptorDom;
    private ExplicitParentMergeStrategy strategy;

    @Before
    public void setUp() throws Exception {
        setUpTestContext();
        setUpTestDescriptorDom();
        setUpTestStrategy();
    }

    @Test
    public void testGetDescriptors() throws Exception {
        List<MergeableDescriptor> descriptors = strategy.getDescriptors(context, DEFAULT_CACHING_OPTIONS,
                                                                        MAIN_DESCRIPTOR_URL, descriptorDom);
        assertEquals(3, descriptors.size());
        assertEquals(LEVEL_DESCRIPTOR_URL, descriptors.get(0).getUrl());
        assertTrue(descriptors.get(0).isOptional());
        assertEquals(PARENT_DESCRIPTOR_URL, descriptors.get(1).getUrl());
        assertTrue(descriptors.get(1).isOptional());
        assertEquals(MAIN_DESCRIPTOR_URL, descriptors.get(2).getUrl());
        assertFalse(descriptors.get(2).isOptional());
    }

    private void setUpTestContext() {
        context = mock(Context.class);

        Item item = new Item();
        item.setDescriptorDom(mock(Document.class));

        ContentStoreAdapter storeAdapter = mock(ContentStoreAdapter.class);
        when(storeAdapter.findItem(context, DEFAULT_CACHING_OPTIONS, PARENT_DESCRIPTOR_URL, true)).thenReturn(item);

        when(context.getStoreAdapter()).thenReturn(storeAdapter);
    }

    private void setUpTestDescriptorDom() {
        Node parentDescriptorElem = mock(Node.class);
        when(parentDescriptorElem.getText()).thenReturn(PARENT_DESCRIPTOR_URL);

        descriptorDom = mock(Document.class);
        when(descriptorDom.selectSingleNode(PARENT_DESCRIPTOR_ELEM_XPATH_QUERY)).thenReturn(parentDescriptorElem);
    }

    private void setUpTestStrategy() {
        DescriptorMergeStrategy parentStrategy = mock(DescriptorMergeStrategy.class);
        when(parentStrategy.getDescriptors(any(Context.class), eq(DEFAULT_CACHING_OPTIONS), eq(PARENT_DESCRIPTOR_URL),
                                          any(Document.class), eq(true)))
            .thenReturn(Arrays.asList(new MergeableDescriptor(LEVEL_DESCRIPTOR_URL, true),
                                      new MergeableDescriptor(PARENT_DESCRIPTOR_URL, true)));

        DescriptorMergeStrategyResolver mergeStrategyResolver = mock(DescriptorMergeStrategyResolver.class);
        when(mergeStrategyResolver.getStrategy(eq(PARENT_DESCRIPTOR_URL), any(Document.class)))
            .thenReturn(parentStrategy);

        strategy = new ExplicitParentMergeStrategy();
        strategy.setMergeStrategyResolver(mergeStrategyResolver);
        strategy.setParentDescriptorElementXPathQuery(PARENT_DESCRIPTOR_ELEM_XPATH_QUERY);
    }

}
