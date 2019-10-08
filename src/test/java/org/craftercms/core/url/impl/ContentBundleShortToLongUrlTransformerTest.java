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
import java.util.regex.Pattern;

import org.craftercms.core.service.Context;
import org.craftercms.core.service.Item;
import org.craftercms.core.store.ContentStoreAdapter;
import org.craftercms.core.util.url.impl.RegexBasedContentBundleUrlParser;
import org.junit.Before;
import org.junit.Test;

import static org.craftercms.core.service.CachingOptions.DEFAULT_CACHING_OPTIONS;
import static org.craftercms.core.xml.mergers.impl.strategies.ContentBundleMergeStrategyTest.BASE_DELIMITER;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class ContentBundleShortToLongUrlTransformerTest {

    private static final String URL = "/folder/base_fr_es/index.html/";
    private static final String TRANSFORMED_URL = "/001_folder/base_fr_es/002_index.html/";

    private ContentBundleShortToLongUrlTransformer transformer;
    private Context context;

    @Before
    public void setUp() throws Exception {
        setUpTestContext();
        setUpTestTransformer();
    }

    @Test
    public void testTransformer() throws Exception {
        String transformedUrl = transformer.transformUrl(context, DEFAULT_CACHING_OPTIONS, URL);
        assertEquals(TRANSFORMED_URL, transformedUrl);
    }

    private void setUpTestContext() {
        context = mock(Context.class);

        ContentStoreAdapter storeAdapter = mock(ContentStoreAdapter.class);

        Item folderItem = new Item();
        folderItem.setName("001_folder");

        Item baseItem = new Item();
        baseItem.setName("base");

        Item baseFrItem = new Item();
        baseFrItem.setName("base_fr");

        Item baseFrEsItem = new Item();
        baseFrEsItem.setName("base_fr_es");

        Item indexItem = new Item();
        indexItem.setName("002_index.html");

        when(storeAdapter.findItems(context, DEFAULT_CACHING_OPTIONS, "/")).thenReturn(
            Arrays.asList(folderItem));
        when(storeAdapter.findItems(context, DEFAULT_CACHING_OPTIONS, "/" + folderItem.getName())).thenReturn(
            Arrays.asList(baseItem, baseFrItem, baseFrEsItem));
        when(storeAdapter.findItems(context, DEFAULT_CACHING_OPTIONS,
                                    "/" + folderItem.getName() + "/" + baseItem.getName()))
                .thenReturn(Arrays.asList(indexItem));

        when(context.getStoreAdapter()).thenReturn(storeAdapter);
    }

    private void setUpTestTransformer() {
        RegexBasedContentBundleUrlParser urlParser = new RegexBasedContentBundleUrlParser();
        urlParser.setPattern(Pattern.compile("^(.*/)(base[^/]*)(/.*)$"));
        urlParser.setPrefixGroup(1);
        urlParser.setBaseNameAndExtensionTokenGroup(2);
        urlParser.setSuffixGroup(3);

        transformer = new ContentBundleShortToLongUrlTransformer();
        transformer.setUrlParser(urlParser);
        transformer.setBaseDelimiter(BASE_DELIMITER);
    }

}
