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
package org.craftercms.core.url.impl;

import java.util.Arrays;

import org.craftercms.core.service.Context;
import org.craftercms.core.service.Item;
import org.craftercms.core.store.ContentStoreAdapter;
import org.junit.Before;
import org.junit.Test;

import static org.craftercms.core.service.CachingOptions.DEFAULT_CACHING_OPTIONS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class ShortToLongUrlTransformerTest {

    private static final String SHORT_URL = "/folder/subfolder/index.html/";
    private static final String LONG_URL = "/001_folder/002_subfolder/003_index.html/";

    private ShortToLongUrlTransformer transformer;
    private Context context;

    @Before
    public void setUp() throws Exception {
        setUpTestContext();
        setUpTestTransformer();
    }

    @Test
    public void testTransformer() throws Exception {
        String transformedUrl = transformer.transformUrl(context, DEFAULT_CACHING_OPTIONS, SHORT_URL);
        assertEquals(LONG_URL, transformedUrl);
    }

    private void setUpTestContext() {
        context = mock(Context.class);

        ContentStoreAdapter storeAdapter = mock(ContentStoreAdapter.class);

        Item folderItem = new Item();
        folderItem.setName("001_folder");

        Item subFolderItem = new Item();
        subFolderItem.setName("002_subfolder");

        Item indexItem = new Item();
        indexItem.setName("003_index.html");

        when(storeAdapter.findItems(context, DEFAULT_CACHING_OPTIONS, "/")).thenReturn(
            Arrays.asList(folderItem));
        when(storeAdapter.findItems(context, DEFAULT_CACHING_OPTIONS, "/" + folderItem.getName())).thenReturn(
            Arrays.asList(subFolderItem));
        when(storeAdapter.findItems(context, DEFAULT_CACHING_OPTIONS,
                                    "/" + folderItem.getName() + "/" + subFolderItem.getName()))
                .thenReturn(Arrays.asList(indexItem));

        when(context.getStoreAdapter()).thenReturn(storeAdapter);
    }

    private void setUpTestTransformer() {
        transformer = new ShortToLongUrlTransformer();
    }

}
