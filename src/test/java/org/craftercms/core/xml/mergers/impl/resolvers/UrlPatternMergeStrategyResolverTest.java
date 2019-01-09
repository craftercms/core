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

import org.craftercms.core.xml.mergers.impl.resolvers.UrlPatternMergeStrategyResolver;
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
public class UrlPatternMergeStrategyResolverTest {

    private static final String DESCRIPTOR_URL_PATTERN1 = ".*\\.strategy1\\.xml";
    private static final String DESCRIPTOR_URL_PATTERN2 = ".*\\.strategy2\\.xml";

    private static final String DESCRIPTOR_URL1 = "/descriptor.strategy1.xml";
    private static final String DESCRIPTOR_URL2 = "/descriptor.strategy2.xml";

    private UrlPatternMergeStrategyResolver resolver;
    private DescriptorMergeStrategy strategy1;
    private DescriptorMergeStrategy strategy2;

    @Before
    public void setUp() throws Exception {
        setUpTestStrategies();
        setUpTestResolver();
    }

    @Test
    public void testGetStrategy() throws Exception {
        DescriptorMergeStrategy strategy = resolver.getStrategy(DESCRIPTOR_URL1, null);
        assertSame(strategy1, strategy);

        strategy = resolver.getStrategy(DESCRIPTOR_URL2, null);
        assertSame(strategy2, strategy);
    }

    private void setUpTestStrategies() {
        strategy1 = mock(DescriptorMergeStrategy.class);
        strategy2 = mock(DescriptorMergeStrategy.class);
    }

    private void setUpTestResolver() {
        Map<String, DescriptorMergeStrategy> strategies = new HashMap<String, DescriptorMergeStrategy>();
        strategies.put(DESCRIPTOR_URL_PATTERN1, strategy1);
        strategies.put(DESCRIPTOR_URL_PATTERN2, strategy2);

        resolver = new UrlPatternMergeStrategyResolver();
        resolver.setUrlPatternToStrategyMappings(strategies);
    }

}
