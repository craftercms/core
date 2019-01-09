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

import java.util.List;

import org.craftercms.core.xml.mergers.MergeableDescriptor;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class InheritVersionsMergeStrategyTest {

    private static final String ROOT_VERSION_DESCRIPTOR_URL = "/descriptor.xml";
    private static final String FOLDER_VERSION_DESCRIPTOR_URL = "/folder/descriptor.xml";
    private static final String MAIN_DESCRIPTOR_URL = "/folder/sub-folder/descriptor.xml";

    private InheritVersionsMergeStrategy strategy;

    @Before
    public void setUp() throws Exception {
        setUpTestStrategy();
    }

    @Test
    public void testGetDescriptors() throws Exception {
        List<MergeableDescriptor> descriptors = strategy.getDescriptors(null, null, MAIN_DESCRIPTOR_URL, null);
        assertEquals(3, descriptors.size());
        assertEquals(ROOT_VERSION_DESCRIPTOR_URL, descriptors.get(0).getUrl());
        assertTrue(descriptors.get(0).isOptional());
        assertEquals(FOLDER_VERSION_DESCRIPTOR_URL, descriptors.get(1).getUrl());
        assertTrue(descriptors.get(1).isOptional());
        assertEquals(MAIN_DESCRIPTOR_URL, descriptors.get(2).getUrl());
        assertFalse(descriptors.get(2).isOptional());
    }

    private void setUpTestStrategy() {
        strategy = new InheritVersionsMergeStrategy();
    }

}
