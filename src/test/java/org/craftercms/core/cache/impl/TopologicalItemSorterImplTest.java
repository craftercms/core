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
package org.craftercms.core.cache.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.craftercms.core.cache.impl.CacheItemImpl;
import org.craftercms.core.cache.impl.TopologicalCacheItemSorterImpl;
import org.junit.Before;
import org.junit.Test;
import org.craftercms.core.cache.Cache;
import org.craftercms.core.cache.CacheItem;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class TopologicalItemSorterImplTest {
    
    private static final String SCOPE = "test";
    private static final int MAX_ITEMS_IN_MEMORY = 10;    

    private static final int ITEM1_KEY = 1;
    private static final String ITEM1_VALUE = "Test value #1";
    private static final int ITEM2_KEY = 2;
    private static final String ITEM2_VALUE = "Test value #2";
    private static final int ITEM3_KEY = 3;
    private static final String ITEM3_VALUE = "Test value #3";
    private static final int ITEM4_KEY = 4;
    private static final String ITEM4_VALUE = "Test value #4";
    private static final int ITEM5_KEY = 5;
    private static final String ITEM5_VALUE = "Test value #5";
    private static final int ITEM6_KEY = 6;
    private static final String ITEM6_VALUE = "Test value #6";
    private static final int ITEM7_KEY = 7;
    private static final String ITEM7_VALUE = "Test value #7";
    private static final int ITEM8_KEY = 8;
    private static final String ITEM8_VALUE = "Test value #8";

    private static final List<Object> DEPENDENCY_KEYS_ITEM1 = Arrays.<Object>asList(ITEM4_KEY, ITEM5_KEY);
    private static final List<Object> DEPENDENCY_KEYS_ITEM2 = Arrays.<Object>asList(ITEM4_KEY);
    private static final List<Object> DEPENDENCY_KEYS_ITEM3 = Arrays.<Object>asList(ITEM5_KEY, ITEM8_KEY);
    private static final List<Object> DEPENDENCY_KEYS_ITEM4 = Arrays.<Object>asList(ITEM6_KEY, ITEM7_KEY, ITEM8_KEY);
    private static final List<Object> DEPENDENCY_KEYS_ITEM5 = Arrays.<Object>asList(ITEM7_KEY);
    
    private TopologicalCacheItemSorterImpl sorter;
    private Cache cache;
    private CacheItem item1;
    private CacheItem item2;
    private CacheItem item3;
    private CacheItem item4;
    private CacheItem item5;
    private CacheItem item6;
    private CacheItem item7;
    private CacheItem item8;

    @Before
    public void setUp() throws Exception {
        setUpTestItems();
        setUpTestCache();
        setUpTestSorter();
    }

    @Test
    public void testSorter() throws Exception {
        // Let's just test with 6 items. The result shouldn't contain more or less than these items. Items 4 and 7
        // should be used for the sorting, but shouldn't be part of the returned list.
        List<CacheItem> items = Arrays.asList(item1, item2, item3, item5, item6, item8);
        Collections.shuffle(items);

        items = sorter.sortTopologically(items, cache);
        assertNotNull(items);
        assertEquals(6, items.size());

        int item1Idx = items.indexOf(item1);
        int item2Idx = items.indexOf(item2);
        int item3Idx = items.indexOf(item3);
        int item5Idx = items.indexOf(item5);
        int item6Idx = items.indexOf(item6);
        int item8Idx = items.indexOf(item8);

        assertTrue(item1Idx > item5Idx && item1Idx > item6Idx && item1Idx > item8Idx);
        assertTrue(item2Idx > item6Idx && item2Idx > item8Idx);
        assertTrue(item3Idx > item5Idx && item3Idx > item8Idx);
    }

    private void setUpTestItems() {
        item1 = new CacheItemImpl(SCOPE, 1, ITEM1_KEY, ITEM1_VALUE, 1, 1, 1, DEPENDENCY_KEYS_ITEM1, null, null);
        item2 = new CacheItemImpl(SCOPE, 1, ITEM2_KEY, ITEM2_VALUE, 1, 1, 1, DEPENDENCY_KEYS_ITEM2, null, null);
        item3 = new CacheItemImpl(SCOPE, 1, ITEM3_KEY, ITEM3_VALUE, 1, 1, 1, DEPENDENCY_KEYS_ITEM3, null, null);
        item4 = new CacheItemImpl(SCOPE, 1, ITEM4_KEY, ITEM4_VALUE, 1, 1, 1, DEPENDENCY_KEYS_ITEM4, null, null);
        item5 = new CacheItemImpl(SCOPE, 1, ITEM5_KEY, ITEM5_VALUE, 1, 1, 1, DEPENDENCY_KEYS_ITEM5, null, null);
        item6 = new CacheItemImpl(SCOPE, 1, ITEM6_KEY, ITEM6_VALUE, 1, 1, 1, null, null, null);
        item7 = new CacheItemImpl(SCOPE, 1, ITEM7_KEY, ITEM7_VALUE, 1, 1, 1, null, null, null);
        item8 = new CacheItemImpl(SCOPE, 1, ITEM8_KEY, ITEM8_VALUE, 1, 1, 1, null, null, null);
    }
    
    private void setUpTestCache() {
        cache = mock(Cache.class);
        when(cache.get(SCOPE, ITEM1_KEY)).thenReturn(item1);
        when(cache.get(SCOPE, ITEM2_KEY)).thenReturn(item2);
        when(cache.get(SCOPE, ITEM3_KEY)).thenReturn(item3);
        when(cache.get(SCOPE, ITEM4_KEY)).thenReturn(item4);
        when(cache.get(SCOPE, ITEM5_KEY)).thenReturn(item5);
        when(cache.get(SCOPE, ITEM6_KEY)).thenReturn(item6);
        when(cache.get(SCOPE, ITEM7_KEY)).thenReturn(item7);
        when(cache.get(SCOPE, ITEM8_KEY)).thenReturn(item8);
    }

    private void setUpTestSorter() {
        sorter = new TopologicalCacheItemSorterImpl();
    }

}
