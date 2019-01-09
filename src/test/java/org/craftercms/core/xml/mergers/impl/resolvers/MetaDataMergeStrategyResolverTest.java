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
package org.craftercms.core.xml.mergers.impl.resolvers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.craftercms.core.xml.mergers.impl.resolvers.MetaDataMergeStrategyResolver.*;

import org.craftercms.core.xml.mergers.impl.resolvers.MetaDataMergeStrategyResolver;
import org.dom4j.Document;
import org.dom4j.Node;
import org.junit.Before;
import org.junit.Test;
import org.craftercms.core.xml.mergers.DescriptorMergeStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class MetaDataMergeStrategyResolverTest {

    public static final String MERGE_STRATEGY_ELEMENT_XPATH_QUERY = "//merge-strategy";

    private static final String STRATEGY1_ELEMENT_VALUE = "strategy1";
    private static final String STRATEGY2_ELEMENT_VALUE = "strategy2";

    private MetaDataMergeStrategyResolver resolver;
    private DescriptorMergeStrategy strategy1;
    private DescriptorMergeStrategy strategy2;
    private Document descriptorDomStrategy1;
    private Document descriptorDomStrategy2;

    @Before
    public void setUp() throws Exception {
        setUpTestStrategies();
        setUpTestResolver();
        setUpTestDescriptorDoms();
    }

    @Test
    public void testGetStrategy() throws Exception {
        DescriptorMergeStrategy strategy = resolver.getStrategy(null, descriptorDomStrategy1);
        assertSame(strategy1, strategy);

        strategy = resolver.getStrategy(null, descriptorDomStrategy2);
        assertSame(strategy2, strategy);
    }

    private void setUpTestStrategies() {
        strategy1 = mock(DescriptorMergeStrategy.class);
        strategy2 = mock(DescriptorMergeStrategy.class);
    }

    private void setUpTestResolver() {
        Map<String, DescriptorMergeStrategy> strategies = new HashMap<String, DescriptorMergeStrategy>();
        strategies.put(STRATEGY1_ELEMENT_VALUE, strategy1);
        strategies.put(STRATEGY2_ELEMENT_VALUE, strategy2);

        resolver = new MetaDataMergeStrategyResolver();
        resolver.setMergeStrategyElementXPathQuery(MERGE_STRATEGY_ELEMENT_XPATH_QUERY);
        resolver.setElementValueToStrategyMappings(strategies);
    }

    private void setUpTestDescriptorDoms() {
        Node strategy1MergeStrategyElement = mock(Node.class);
        when(strategy1MergeStrategyElement.getText()).thenReturn(STRATEGY1_ELEMENT_VALUE);

        descriptorDomStrategy1 = mock(Document.class);
        when(descriptorDomStrategy1.selectSingleNode(MERGE_STRATEGY_ELEMENT_XPATH_QUERY)).thenReturn(
                strategy1MergeStrategyElement);

        Node strategy2MergeStrategyElement = mock(Node.class);
        when(strategy2MergeStrategyElement.getText()).thenReturn(STRATEGY2_ELEMENT_VALUE);

        descriptorDomStrategy2 = mock(Document.class);
        when(descriptorDomStrategy2.selectSingleNode(MERGE_STRATEGY_ELEMENT_XPATH_QUERY)).thenReturn(
                strategy2MergeStrategyElement);
    }

}
