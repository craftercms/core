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
package org.craftercms.core.xml.mergers.impl.strategies;

import java.util.Arrays;
import java.util.List;

import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.service.Item;
import org.craftercms.core.store.ContentStoreAdapter;
import org.craftercms.core.util.url.ContentBundleUrl;
import org.craftercms.core.util.url.ContentBundleUrlParser;
import org.craftercms.core.xml.mergers.DescriptorMergeStrategy;
import org.craftercms.core.xml.mergers.DescriptorMergeStrategyResolver;
import org.craftercms.core.xml.mergers.MergeableDescriptor;
import org.dom4j.Document;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.craftercms.core.service.CachingOptions.DEFAULT_CACHING_OPTIONS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class ContentBundleMergeStrategyTest {

    public static final String BASE_DELIMITER = "_";

    private static final String PRIMARY_DESCRIPTOR_URL = "/folder1/folder2_es/file.xml";
    private static final String PRIMARY_DESCRIPTOR_URL_PREFIX = "/folder1/";
    private static final String PRIMARY_DESCRIPTOR_URL_BASE_NAME_AND_EXT_TOKEN = "folder2_es";
    private static final String PRIMARY_DESCRIPTOR_URL_SUFFIX = "/file.xml";
    private static final String NO_PREFIX_DESCRIPTOR_URL = "folder2_es/file.xml";
    private static final String BASE_DESCRIPTOR_URL = "/folder1/folder2/file.xml";

    private ContentBundleMergeStrategy strategy;
    private Context context;

    @Before
    public void setUp() throws Exception {
        setUpTestContext();
        setUpTestStrategy();
    }

    @Test
    public void testGetDescriptors() throws Exception {
        List<MergeableDescriptor> descriptors = strategy.getDescriptors(context, DEFAULT_CACHING_OPTIONS,
                                                                        PRIMARY_DESCRIPTOR_URL);
        assertDescriptors(descriptors, false);

        descriptors = strategy.getDescriptors(context, DEFAULT_CACHING_OPTIONS, PRIMARY_DESCRIPTOR_URL, false);
        assertDescriptors(descriptors, false);

        descriptors = strategy.getDescriptors(context, DEFAULT_CACHING_OPTIONS, PRIMARY_DESCRIPTOR_URL, true);
        assertDescriptors(descriptors, true);
    }

    private void setUpTestContext() {
        context = mock(Context.class);

        Item item = new Item();
        item.setDescriptorDom(mock(Document.class));

        ContentStoreAdapter storeAdapter = mock(ContentStoreAdapter.class);
        when(storeAdapter.findItem(context, DEFAULT_CACHING_OPTIONS, BASE_DESCRIPTOR_URL, true)).thenReturn(item);

        when(context.getStoreAdapter()).thenReturn(storeAdapter);
    }

    private void setUpTestStrategy() {
        ContentBundleUrl contentBundleUrl = mock(ContentBundleUrl.class);
        when(contentBundleUrl.getPrefix()).thenReturn(PRIMARY_DESCRIPTOR_URL_PREFIX);
        when(contentBundleUrl.getBaseNameAndExtensionToken()).thenReturn(PRIMARY_DESCRIPTOR_URL_BASE_NAME_AND_EXT_TOKEN);
        when(contentBundleUrl.getSuffix()).thenReturn(PRIMARY_DESCRIPTOR_URL_SUFFIX);

        ContentBundleUrlParser contentBundleUrlParser = mock(ContentBundleUrlParser.class);
        when(contentBundleUrlParser.getContentBundleUrl(PRIMARY_DESCRIPTOR_URL)).thenReturn(contentBundleUrl);

        DescriptorMergeStrategy baseStrategy = mock(DescriptorMergeStrategy.class);
        when(baseStrategy.getDescriptors(eq(context), eq(DEFAULT_CACHING_OPTIONS), eq(BASE_DESCRIPTOR_URL),
                                         anyBoolean())).thenAnswer(new Answer<List<MergeableDescriptor>>() {
            @Override
            public List<MergeableDescriptor> answer(InvocationOnMock invocation) throws Throwable {
                boolean isOptionalForMerging = (Boolean)invocation.getArguments()[3];
                return Arrays.asList(new MergeableDescriptor(BASE_DESCRIPTOR_URL, isOptionalForMerging));
            }
        });

        DescriptorMergeStrategyResolver baseResolver = mock(DescriptorMergeStrategyResolver.class);
        when(baseResolver.getStrategy(eq(BASE_DESCRIPTOR_URL), Matchers.<Document>anyObject())).thenReturn(baseStrategy);

        DescriptorMergeStrategy regularStrategy = mock(DescriptorMergeStrategy.class);
        when(regularStrategy.getDescriptors(any(Context.class), any(CachingOptions.class),
                                            eq(NO_PREFIX_DESCRIPTOR_URL), anyBoolean())).thenAnswer(
            new Answer<List<MergeableDescriptor>>() {
            @Override
            public List<MergeableDescriptor> answer(InvocationOnMock invocation) throws Throwable {
                boolean isOptionalForMerging = (Boolean)invocation.getArguments()[3];
                return Arrays.asList(new MergeableDescriptor(NO_PREFIX_DESCRIPTOR_URL, isOptionalForMerging));
            }
        });

        strategy = new ContentBundleMergeStrategy();
        strategy.setUrlParser(contentBundleUrlParser);
        strategy.setBaseDelimiter(BASE_DELIMITER);
        strategy.setBaseMergeStrategyResolver(baseResolver);
        strategy.setRegularMergeStrategy(regularStrategy);
    }

    private void assertDescriptors(List<MergeableDescriptor> descriptors, boolean primaryDescriptorOptional) {
        assertEquals(2, descriptors.size());
        assertEquals(BASE_DESCRIPTOR_URL, descriptors.get(0).getUrl());
        assertEquals(true, descriptors.get(0).isOptional());
        assertEquals(PRIMARY_DESCRIPTOR_URL, descriptors.get(1).getUrl());
        assertEquals(primaryDescriptorOptional, descriptors.get(1).isOptional());
    }

}
