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

import org.craftercms.core.xml.mergers.impl.resolvers.SingleMergeStrategyResolver;
import org.junit.Before;
import org.junit.Test;
import org.craftercms.core.xml.mergers.DescriptorMergeStrategy;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class SingleMergeStrategyResolverTest {

    private SingleMergeStrategyResolver resolver;
    private DescriptorMergeStrategy strategy;

    @Before
    public void setUp() throws Exception {
        setUpTestStrategy();
        setUpTestResolver();
    }

    @Test
    public void testResolver() throws Exception {
        DescriptorMergeStrategy strategy = resolver.getStrategy(null, null);
        assertSame(this.strategy, strategy);
    }

    private void setUpTestStrategy() {
        strategy = mock(DescriptorMergeStrategy.class);
    }

    private void setUpTestResolver() {
        resolver = new SingleMergeStrategyResolver();
        resolver.setStrategy(strategy);
    }

}
