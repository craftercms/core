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
package org.craftercms.core.xml.mergers.impl.resolvers;

import org.craftercms.core.xml.mergers.DescriptorMergeStrategy;
import org.craftercms.core.xml.mergers.DescriptorMergeStrategyResolver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import java.util.Arrays;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class DescriptorMergeStrategyResolverChainTest {

    private static final String DESCRIPTOR_URL1 = "/descriptor1.xml";
    private static final String DESCRIPTOR_URL2 = "/descriptor2.xml";
    private static final String DESCRIPTOR_URL3 = "/descriptor3.xml";

    private DescriptorMergeStrategyResolverChain resolverChain;
    private DescriptorMergeStrategy strategy1;
    private DescriptorMergeStrategy strategy2;
    private DescriptorMergeStrategy defaultStrategy;

    @Before
    public void setUp() throws Exception {
        setUpTestStrategies();
        setUpTestResolverChain();
    }

    @Test
    public void testGetStrategy() throws Exception {
        DescriptorMergeStrategy strategy = resolverChain.getStrategy(DESCRIPTOR_URL1, null);
        assertSame(strategy1, strategy);

        strategy = resolverChain.getStrategy(DESCRIPTOR_URL2, null);
        assertSame(strategy2, strategy);

        strategy = resolverChain.getStrategy(DESCRIPTOR_URL3, null);
        assertSame(defaultStrategy, strategy);
    }

    private void setUpTestStrategies() {
        strategy1 = mock(DescriptorMergeStrategy.class);
        strategy2 = mock(DescriptorMergeStrategy.class);
        defaultStrategy = mock(DescriptorMergeStrategy.class);
    }

    private void setUpTestResolverChain() {
        DescriptorMergeStrategyResolver resolver1 = mock(DescriptorMergeStrategyResolver.class);
        when(resolver1.getStrategy(eq(DESCRIPTOR_URL1), ArgumentMatchers.any())).thenReturn(strategy1);

        DescriptorMergeStrategyResolver resolver2 = mock(DescriptorMergeStrategyResolver.class);
        when(resolver2.getStrategy(eq(DESCRIPTOR_URL2), ArgumentMatchers.any())).thenReturn(strategy2);

        resolverChain = new DescriptorMergeStrategyResolverChain();
        resolverChain.setResolvers(Arrays.asList(resolver1, resolver2));
        resolverChain.setDefaultStrategy(defaultStrategy);
    }

}
