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
package org.craftercms.core.service.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.craftercms.core.exception.PathNotFoundException;
import org.craftercms.core.processors.impl.ItemProcessorPipeline;
import org.craftercms.core.processors.impl.TextMetaDataCollectionExtractingProcessor;
import org.craftercms.core.processors.impl.TextMetaDataExtractingProcessor;
import org.craftercms.core.service.CacheService;
import org.craftercms.core.service.ContentStoreService;
import org.craftercms.core.service.Context;
import org.craftercms.core.service.Item;
import org.craftercms.core.service.ItemFilter;
import org.craftercms.core.service.Tree;
import org.craftercms.core.store.impl.filesystem.FileSystemContentStoreAdapter;
import org.craftercms.core.util.cache.CachingAwareObject;
import org.craftercms.core.util.cache.impl.CachingAwareList;
import org.craftercms.core.xml.mergers.impl.strategies.InheritLevelsMergeStrategyTest;
import org.dom4j.Element;
import org.dom4j.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.craftercms.core.service.CachingOptions.DEFAULT_CACHING_OPTIONS;
import static org.craftercms.core.service.ContentStoreService.UNLIMITED_TREE_DEPTH;
import static org.craftercms.core.service.Context.DEFAULT_CACHE_ON;
import static org.craftercms.core.service.Context.DEFAULT_IGNORE_HIDDEN_FILES;
import static org.craftercms.core.service.Context.DEFAULT_MAX_ALLOWED_ITEMS_IN_CACHE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/contexts/ContentStoreServiceImplTest.xml")
public class ContentStoreServiceImplTest {

    private static final String DESCRIPTOR_FILE_EXTENSION = ".xml";
    private static final String METADATA_FILE_EXTENSION = ".meta.xml";

    private static final String CLASSPATH_STORE_ROOT_FOLDER_PATH =
        "classpath:stores/" + ContentStoreServiceImplTest.class.getSimpleName();

    private static final String ROOT_FOLDER_NAME = "";
    private static final String ROOT_FOLDER_PATH = "/";

    private static final String INVALID_PATH = "no_file";

    private static final String BUNDLE_FOLDER_NAME = "bundle";
    private static final String BUNDLE_FOLDER_PATH = "/" + BUNDLE_FOLDER_NAME;
    private static final String BUNDLE_FOLDER_META_FILE_PATH = BUNDLE_FOLDER_PATH + METADATA_FILE_EXTENSION;

    private static final String BUNDLE_FR_EXTENSION = "fr";
    private static final String BUNDLE_FR_ES_EXTENSION = "fr_es";

    private static final String CRAFTER_LEVEL_DESCRIPTOR_NAME = InheritLevelsMergeStrategyTest.LEVEL_DESCRIPTOR_NAME;
    private static final String CRAFTER_LEVEL_DESCRIPTOR_PATH = "/" + CRAFTER_LEVEL_DESCRIPTOR_NAME;

    private static final String CRAFTER_CMS_LOGO_NAME = "craftercms_logo.png";
    private static final String CRAFTER_CMS_LOGO_PATH = "/" + CRAFTER_CMS_LOGO_NAME;
    private static final String CRAFTER_CMS_LOGO_META_FILE_PATH = "/craftercms_logo" + METADATA_FILE_EXTENSION;

    private static final String CRAFTER_CMS_LOGO_SIZE = "6.63kb";
    private static final int CRAFTER_CMS_LOGO_RESOLUTION_WIDTH = 346;
    private static final int CRAFTER_CMS_LOGO_RESOLUTION_HEIGHT = 96;

    private static final String SYSTEM_INFO_COMPONENT_NAME = "system-info-component.xml";
    private static final String SYSTEM_INFO_COMPONENT_PATH = "/" + SYSTEM_INFO_COMPONENT_NAME;

    private static final String CONTENT_FOLDER_NAME = "content";
    private static final String CONTENT_FOLDER_PATH = BUNDLE_FOLDER_PATH + "/" + CONTENT_FOLDER_NAME;
    private static final String CONTENT_FOLDER_META_FILE_PATH = CONTENT_FOLDER_PATH + METADATA_FILE_EXTENSION;

    private static final String CONTENT_DESCRIPTOR_NAME = "descriptor.xml";
    private static final String CONTENT_DESCRIPTOR_PATH = CONTENT_FOLDER_PATH + "/" + CONTENT_DESCRIPTOR_NAME;

    private static final String CONTENT_FR_FOLDER_NAME = "content_fr";
    private static final String CONTENT_FR_FOLDER_PATH = BUNDLE_FOLDER_PATH + "/" + CONTENT_FR_FOLDER_NAME;
    private static final String CONTENT_FR_FOLDER_META_FILE_PATH = CONTENT_FR_FOLDER_PATH + METADATA_FILE_EXTENSION;

    private static final String CONTENT_FR_DESCRIPTOR_NAME = "descriptor.xml";
    private static final String CONTENT_FR_DESCRIPTOR_PATH = CONTENT_FR_FOLDER_PATH + "/" + CONTENT_FR_DESCRIPTOR_NAME;

    private static final String CONTENT_FR_ES_FOLDER_NAME = "content_fr_es";
    private static final String CONTENT_FR_ES_FOLDER_PATH = BUNDLE_FOLDER_PATH + "/" + CONTENT_FR_ES_FOLDER_NAME;
    private static final String CONTENT_FR_ES_FOLDER_META_FILE_PATH =
        CONTENT_FR_ES_FOLDER_PATH + METADATA_FILE_EXTENSION;

    private static final String CONTENT_FR_ES_DESCRIPTOR_NAME = "descriptor.xml";
    private static final String CONTENT_FR_ES_DESCRIPTOR_PATH =
        CONTENT_FR_ES_FOLDER_PATH + "/" + CONTENT_FR_ES_DESCRIPTOR_NAME;

    private static final String FIRST_QUOTE_EN = "I haven't failed. I've just found 10,000 ways that won't work. -- " +
                                                 "Thomas Edison";
    private static final String SECOND_QUOTE_EN = "Don't hate, it's too big a burden to bear. -- Martin Luther King, " +
                                                  "Jr.";
    private static final String THIRD_QUOTE_EN =
        "If I have seen a little further it is by standing on the shoulders of giants. " + "-- Issac Newton";
    private static final String SECOND_QUOTE_FR = "Ne haïssez pas, c'est un fardeau trop lourd à supporter. -- Martin" +
                                                  " Luther King, Jr.";
    private static final String THIRD_QUOTE_FR =
        "Si j'ai vu un peu plus loin c'est en me tenant sur ​​les épaules de géants. " + "-- Issac Newton";
    private static final String THIRD_QUOTE_ES =
        "Si he visto un poco más lejos es porque lo hecho parado sobre los hombros de gigantes. " + "-- Issac Newton";

    @Autowired
    private ContentStoreService contentStoreService;
    @Autowired
    private CacheService cache;
    private Context context;

    private static String getJavaVersion() {
        return System.getProperty("java.version");
    }

    private static String getOsName() {
        return System.getProperty("os.name");
    }

    private static String getOsVersion() {
        return System.getProperty("os.version");
    }

    @Before
    public void setUp() throws Exception {
        context = contentStoreService.createContext(FileSystemContentStoreAdapter.STORE_TYPE, null, null, null,
                                                    CLASSPATH_STORE_ROOT_FOLDER_PATH, DEFAULT_CACHE_ON,
                                                    DEFAULT_MAX_ALLOWED_ITEMS_IN_CACHE, DEFAULT_IGNORE_HIDDEN_FILES);
    }

    @After
    public void tearDown() throws Exception {
        contentStoreService.destroyContext(context);
    }

    @Test
    public void testGetDescriptorItem() throws Exception {
        Item item = contentStoreService.findItem(context, CONTENT_FR_ES_DESCRIPTOR_PATH);
        assertContentFrEsDescriptorItem(item, false);

        // Sleep so that we get a different caching time in the next call if the caching is being done wrong.
        Thread.sleep(100);

        Item cachedItem = contentStoreService.getItem(context, CONTENT_FR_ES_DESCRIPTOR_PATH);
        assertEquals(item, cachedItem);
        assertCaching(item, cachedItem);

        item = contentStoreService.findItem(context, INVALID_PATH);
        assertNull(item);

        try {
            contentStoreService.getItem(context, INVALID_PATH);
            fail("Expected " + PathNotFoundException.class.getName());
        } catch (PathNotFoundException e) {
        }

        TextMetaDataExtractingProcessor extractor = new TextMetaDataExtractingProcessor("//first-quote",
                                                                                        "//second-quote",
                                                                                        "//third-quote");

        item = contentStoreService.findItem(context, DEFAULT_CACHING_OPTIONS, CONTENT_FR_ES_DESCRIPTOR_PATH, extractor);
        assertContentFrEsDescriptorItem(item, true);

        // Sleep so that we get a different caching time in the next call if the caching is being done wrong.
        Thread.sleep(100);

        cachedItem = contentStoreService.getItem(context, DEFAULT_CACHING_OPTIONS, CONTENT_FR_ES_DESCRIPTOR_PATH,
                                                 extractor);
        assertEquals(item, cachedItem);
        assertCaching(item, cachedItem);

        item = contentStoreService.findItem(context, DEFAULT_CACHING_OPTIONS, INVALID_PATH, extractor);
        assertNull(item);

        try {
            contentStoreService.getItem(context, DEFAULT_CACHING_OPTIONS, INVALID_PATH, extractor);
            fail("Expected " + PathNotFoundException.class.getName());
        } catch (PathNotFoundException e) {
        }
    }

    @Test
    public void testGetFolderItem() throws Exception {
        Item item = contentStoreService.findItem(context, BUNDLE_FOLDER_PATH);
        assertBundleFolderItem(item, false);

        // Sleep so that we get a different caching time in the next call if the caching is being done wrong.
        Thread.sleep(100);

        Item cachedItem = contentStoreService.getItem(context, BUNDLE_FOLDER_PATH);
        assertEquals(item, cachedItem);
        assertCaching(item, cachedItem);

        TextMetaDataCollectionExtractingProcessor extractor = new TextMetaDataCollectionExtractingProcessor
            ("//extension");

        item = contentStoreService.findItem(context, DEFAULT_CACHING_OPTIONS, BUNDLE_FOLDER_PATH, extractor);
        assertBundleFolderItem(item, true);

        // Sleep so that we get a different caching time in the next call if the caching is being done wrong.
        Thread.sleep(100);

        cachedItem = contentStoreService.getItem(context, DEFAULT_CACHING_OPTIONS, BUNDLE_FOLDER_PATH, extractor);
        assertEquals(item, cachedItem);
        assertCaching(item, cachedItem);
    }

    @Test
    public void testGetStaticAssetItem() throws Exception {
        Item item = contentStoreService.findItem(context, CRAFTER_CMS_LOGO_PATH);
        assertCrafterCMSLogoItem(item, false);

        // Sleep so that we get a different caching time in the next call if the caching is being done wrong.
        Thread.sleep(100);

        Item cachedItem = contentStoreService.getItem(context, CRAFTER_CMS_LOGO_PATH);
        assertEquals(item, cachedItem);
        assertCaching(item, cachedItem);

        TextMetaDataExtractingProcessor extractor = new TextMetaDataExtractingProcessor("//size");

        item = contentStoreService.findItem(context, DEFAULT_CACHING_OPTIONS, CRAFTER_CMS_LOGO_PATH, extractor);
        assertCrafterCMSLogoItem(item, true);

        // Sleep so that we get a different caching time in the next call if the caching is being done wrong.
        Thread.sleep(100);

        cachedItem = contentStoreService.getItem(context, DEFAULT_CACHING_OPTIONS, CRAFTER_CMS_LOGO_PATH, extractor);
        assertEquals(item, cachedItem);
        assertCaching(item, cachedItem);
    }

    @Test
    public void testGetChildren() throws Exception {
        CachingAwareList<Item> children = (CachingAwareList<Item>)contentStoreService.findChildren(context,
                                                                                                   ROOT_FOLDER_PATH);
        assertRootFolderChildren(children, false, false);

        // Sleep so that we get a different caching time in the next call if the caching is being done wrong.
        Thread.sleep(100);

        CachingAwareList<Item> cachedChildren = (CachingAwareList<Item>)contentStoreService.getChildren(
            context, ROOT_FOLDER_PATH);

        assertEquals(children, cachedChildren);
        assertListCaching(children, cachedChildren);

        children = (CachingAwareList<Item>)contentStoreService.findChildren(context, INVALID_PATH);
        assertNull(children);

        try {
            contentStoreService.getChildren(context, INVALID_PATH);
            fail("Expected " + PathNotFoundException.class.getName());
        } catch (PathNotFoundException e) {
        }

        ItemProcessorPipeline extractorPipeline = new ItemProcessorPipeline();
        extractorPipeline.addProcessor(new TextMetaDataCollectionExtractingProcessor("//extension"));
        extractorPipeline.addProcessor(new TextMetaDataExtractingProcessor("//size"));

        children = (CachingAwareList<Item>)contentStoreService.findChildren(context, DEFAULT_CACHING_OPTIONS,
                                                                            ROOT_FOLDER_PATH,
                                                                            OnlyNonDescriptorsFilter.instance,
                                                                            extractorPipeline);
        assertRootFolderChildren(children, true, true);

        // Sleep so that we get a different caching time in the next call if the caching is being done wrong.
        Thread.sleep(100);

        cachedChildren = (CachingAwareList<Item>)contentStoreService.getChildren(context, DEFAULT_CACHING_OPTIONS,
                                                                                 ROOT_FOLDER_PATH,
                                                                                 OnlyNonDescriptorsFilter.instance,
                                                                                 extractorPipeline);
        assertEquals(children, cachedChildren);
        assertListCaching(children, cachedChildren);

        children = (CachingAwareList<Item>)contentStoreService.findChildren(context, DEFAULT_CACHING_OPTIONS,
                                                                            INVALID_PATH,
                                                                            OnlyNonDescriptorsFilter.instance,
                                                                            extractorPipeline);
        assertNull(children);

        try {
            contentStoreService.getChildren(context, DEFAULT_CACHING_OPTIONS, INVALID_PATH, OnlyNonDescriptorsFilter
                .instance, extractorPipeline);
            assertNull(children);
            fail("Expected " + PathNotFoundException.class.getName());
        } catch (PathNotFoundException e) {
        }
    }

    @Test
    public void testGetTree() throws Exception {
        Tree tree = contentStoreService.findTree(context, ROOT_FOLDER_PATH);
        assertRootFolderTree(tree, UNLIMITED_TREE_DEPTH, false, false);

        // Sleep so that we get a different caching time in the next call if the caching is being done wrong.
        Thread.sleep(100);

        Tree cachedTree = contentStoreService.getTree(context, ROOT_FOLDER_PATH);
        assertEquals(tree, cachedTree);
        assertTreeCaching(tree, cachedTree);

        tree = contentStoreService.findTree(context, INVALID_PATH);
        assertNull(tree);

        try {
            contentStoreService.getTree(context, INVALID_PATH);
            fail("Expected " + PathNotFoundException.class.getName());
        } catch (PathNotFoundException e) {
        }

        tree = contentStoreService.findTree(context, ROOT_FOLDER_PATH, 1);
        assertRootFolderTree(tree, 1, false, false);

        // Sleep so that we get a different caching time in the next call if the caching is being done wrong.
        Thread.sleep(100);

        cachedTree = contentStoreService.getTree(context, ROOT_FOLDER_PATH, 1);
        assertEquals(tree, cachedTree);
        assertTreeCaching(tree, cachedTree);

        tree = contentStoreService.findTree(context, INVALID_PATH, 1);
        assertNull(tree);

        try {
            contentStoreService.getTree(context, INVALID_PATH, 1);
            fail("Expected " + PathNotFoundException.class.getName());
        } catch (PathNotFoundException e) {
        }

        ItemProcessorPipeline extractorPipeline = new ItemProcessorPipeline();
        extractorPipeline.addProcessor(new TextMetaDataCollectionExtractingProcessor("//extension"));
        extractorPipeline.addProcessor(new TextMetaDataExtractingProcessor("//first-quote", "//second-quote",
                                                                           "//third-quote", "//size"));

        tree = contentStoreService.findTree(context, DEFAULT_CACHING_OPTIONS, ROOT_FOLDER_PATH, 1,
                                            OnlyNonDescriptorsFilter.instance, extractorPipeline);
        assertRootFolderTree(tree, 1, true, true);

        // Sleep so that we get a different caching time in the next call if the caching is being done wrong.
        Thread.sleep(100);

        cachedTree = contentStoreService.getTree(context, DEFAULT_CACHING_OPTIONS, ROOT_FOLDER_PATH, 1,
                                                 OnlyNonDescriptorsFilter.instance, extractorPipeline);
        assertEquals(tree, cachedTree);
        assertTreeCaching(tree, cachedTree);

        tree = contentStoreService.findTree(context, DEFAULT_CACHING_OPTIONS, INVALID_PATH, 1,
                                            OnlyNonDescriptorsFilter.instance, extractorPipeline);
        assertNull(tree);

        try {
            contentStoreService.getTree(context, DEFAULT_CACHING_OPTIONS, INVALID_PATH, 1,
                                        OnlyNonDescriptorsFilter.instance, extractorPipeline);
            fail("Expected " + PathNotFoundException.class.getName());
        } catch (PathNotFoundException e) {
        }
    }

    private void assertSystemInfoProperties(Item item) {
        assertEquals(getJavaVersion(), item.getProperty("//system-info/java-version"));
        assertEquals(getOsName(), item.getProperty("//system-info/os-name"));
        assertEquals(getOsVersion(), item.getProperty("//system-info/os-version"));
    }

    private void assertRootFolderItem(Item item) {
        assertNotNull(item);
        assertEquals(ROOT_FOLDER_NAME, item.getName());
        assertEquals(ROOT_FOLDER_PATH, item.getUrl());
        assertNull(item.getDescriptorDom());
    }

    private void assertBundleFolderItem(Item item, boolean processorSpecified) {
        assertNotNull(item);
        assertEquals(BUNDLE_FOLDER_NAME, item.getName());
        assertEquals(BUNDLE_FOLDER_PATH, item.getUrl());
        assertTrue(item.isFolder());
        assertEquals(BUNDLE_FOLDER_META_FILE_PATH, item.getDescriptorUrl());
        assertNotNull(item.getDescriptorDom());
        assertSystemInfoProperties(item);

        if (processorSpecified) {
            List<String> bundleExtensions = (List<String>)item.getProperty("//extension");
            assertEquals(2, bundleExtensions.size());
            assertEquals(BUNDLE_FR_EXTENSION, bundleExtensions.get(0));
            assertEquals(BUNDLE_FR_ES_EXTENSION, bundleExtensions.get(1));
        } else {
            List<Element> bundleExtensions = item.getDescriptorDom().selectNodes("//extension");
            assertNotNull(bundleExtensions);
            assertEquals(2, bundleExtensions.size());
            assertEquals(BUNDLE_FR_EXTENSION, bundleExtensions.get(0).getText());
            assertEquals(BUNDLE_FR_ES_EXTENSION, bundleExtensions.get(1).getText());
        }
    }

    private void assertCrafterLevelDescriptorItem(Item item) {
        assertNotNull(item);
        assertEquals(CRAFTER_LEVEL_DESCRIPTOR_NAME, item.getName());
        assertEquals(CRAFTER_LEVEL_DESCRIPTOR_PATH, item.getUrl());
        assertFalse(item.isFolder());
        assertEquals(CRAFTER_LEVEL_DESCRIPTOR_PATH, item.getDescriptorUrl());
        assertNotNull(item.getDescriptorDom());
        assertSystemInfoProperties(item);
    }

    private void assertCrafterCMSLogoItem(Item item, boolean processorSpecified) {
        assertNotNull(item);
        assertEquals(CRAFTER_CMS_LOGO_NAME, item.getName());
        assertEquals(CRAFTER_CMS_LOGO_PATH, item.getUrl());
        assertFalse(item.isFolder());
        assertEquals(CRAFTER_CMS_LOGO_META_FILE_PATH, item.getDescriptorUrl());
        assertNotNull(item.getDescriptorDom());
        assertSystemInfoProperties(item);
        assertEquals(CRAFTER_CMS_LOGO_RESOLUTION_WIDTH, Integer.parseInt((String)item.getProperty
            ("//resolution/width")));
        assertEquals(CRAFTER_CMS_LOGO_RESOLUTION_HEIGHT, Integer.parseInt((String)item.getProperty
            ("//resolution/height")));

        if (processorSpecified) {
            String size = (String)item.getProperty("//size");
            assertEquals(CRAFTER_CMS_LOGO_SIZE, size);
        } else {
            Node size = item.getDescriptorDom().selectSingleNode("//size");
            assertNotNull(size);
            assertEquals(CRAFTER_CMS_LOGO_SIZE, size.getText());
        }
    }

    private void assertSystemInfoComponentItem(Item item) {
        assertNotNull(item);
        assertEquals(SYSTEM_INFO_COMPONENT_NAME, item.getName());
        assertEquals(SYSTEM_INFO_COMPONENT_PATH, item.getUrl());
        assertFalse(item.isFolder());
        assertEquals(SYSTEM_INFO_COMPONENT_PATH, item.getDescriptorUrl());
        assertNotNull(item.getDescriptorDom());
        assertSystemInfoProperties(item);
    }

    private void assertContentFolderItem(Item item) {
        assertNotNull(item);
        assertEquals(CONTENT_FOLDER_NAME, item.getName());
        assertEquals(CONTENT_FOLDER_PATH, item.getUrl());
        assertEquals(CONTENT_FOLDER_META_FILE_PATH, item.getDescriptorUrl());
        assertNull(item.getDescriptorDom());
    }

    private void assertContentDescriptorItem(Item item, boolean processorSpecified) {
        assertNotNull(item);
        assertEquals(CONTENT_DESCRIPTOR_NAME, item.getName());
        assertEquals(CONTENT_DESCRIPTOR_PATH, item.getUrl());
        assertFalse(item.isFolder());
        assertEquals(CONTENT_DESCRIPTOR_PATH, item.getDescriptorUrl());
        assertNotNull(item.getDescriptorDom());
        assertSystemInfoProperties(item);

        if (processorSpecified) {
            assertEquals(FIRST_QUOTE_EN, item.getProperty("//first-quote"));
            assertEquals(SECOND_QUOTE_EN, item.getProperty("//second-quote"));
            assertEquals(THIRD_QUOTE_EN, item.getProperty("//third-quote"));
        } else {
            Node firstQuote = item.getDescriptorDom().selectSingleNode("//first-quote");
            assertNotNull(firstQuote);
            assertEquals(FIRST_QUOTE_EN, firstQuote.getText());

            Node secondQuote = item.getDescriptorDom().selectSingleNode("//second-quote");
            assertNotNull(secondQuote);
            assertEquals(SECOND_QUOTE_EN, secondQuote.getText());

            Node thirdQuote = item.getDescriptorDom().selectSingleNode("//third-quote");
            assertNotNull(thirdQuote);
            assertEquals(THIRD_QUOTE_EN, thirdQuote.getText());
        }
    }

    private void assertContentFrFolderItem(Item item) {
        assertNotNull(item);
        assertEquals(CONTENT_FR_FOLDER_NAME, item.getName());
        assertEquals(CONTENT_FR_FOLDER_PATH, item.getUrl());
        assertEquals(CONTENT_FR_FOLDER_META_FILE_PATH, item.getDescriptorUrl());
        assertNull(item.getDescriptorDom());
    }

    private void assertContentFrDescriptorItem(Item item, boolean processorSpecified) {
        assertNotNull(item);
        assertEquals(CONTENT_FR_DESCRIPTOR_NAME, item.getName());
        assertEquals(CONTENT_FR_DESCRIPTOR_PATH, item.getUrl());
        assertFalse(item.isFolder());
        assertEquals(CONTENT_FR_DESCRIPTOR_PATH, item.getDescriptorUrl());
        assertNotNull(item.getDescriptorDom());
        assertSystemInfoProperties(item);

        if (processorSpecified) {
            assertEquals(FIRST_QUOTE_EN, item.getProperty("//first-quote"));
            assertEquals(SECOND_QUOTE_FR, item.getProperty("//second-quote"));
            assertEquals(THIRD_QUOTE_FR, item.getProperty("//third-quote"));
        } else {
            Node firstQuote = item.getDescriptorDom().selectSingleNode("//first-quote");
            assertNotNull(firstQuote);
            assertEquals(FIRST_QUOTE_EN, firstQuote.getText());

            Node secondQuote = item.getDescriptorDom().selectSingleNode("//second-quote");
            assertNotNull(secondQuote);
            assertEquals(SECOND_QUOTE_FR, secondQuote.getText());

            Node thirdQuote = item.getDescriptorDom().selectSingleNode("//third-quote");
            assertNotNull(thirdQuote);
            assertEquals(THIRD_QUOTE_FR, thirdQuote.getText());
        }
    }

    private void assertContentFrEsFolderItem(Item item) {
        assertNotNull(item);
        assertEquals(CONTENT_FR_ES_FOLDER_NAME, item.getName());
        assertEquals(CONTENT_FR_ES_FOLDER_PATH, item.getUrl());
        assertEquals(CONTENT_FR_ES_FOLDER_META_FILE_PATH, item.getDescriptorUrl());
        assertNull(item.getDescriptorDom());
    }

    private void assertContentFrEsDescriptorItem(Item item, boolean processorSpecified) {
        assertNotNull(item);
        assertEquals(CONTENT_FR_ES_DESCRIPTOR_NAME, item.getName());
        assertEquals(CONTENT_FR_ES_DESCRIPTOR_PATH, item.getUrl());
        assertFalse(item.isFolder());
        assertEquals(CONTENT_FR_ES_DESCRIPTOR_PATH, item.getDescriptorUrl());
        assertNotNull(item.getDescriptorDom());
        assertSystemInfoProperties(item);

        if (processorSpecified) {
            assertEquals(FIRST_QUOTE_EN, item.getProperty("//first-quote"));
            assertEquals(SECOND_QUOTE_FR, item.getProperty("//second-quote"));
            assertEquals(THIRD_QUOTE_ES, item.getProperty("//third-quote"));
        } else {
            Node firstQuote = item.getDescriptorDom().selectSingleNode("//first-quote");
            assertNotNull(firstQuote);
            assertEquals(FIRST_QUOTE_EN, firstQuote.getText());

            Node secondQuote = item.getDescriptorDom().selectSingleNode("//second-quote");
            assertNotNull(secondQuote);
            assertEquals(SECOND_QUOTE_FR, secondQuote.getText());

            Node thirdQuote = item.getDescriptorDom().selectSingleNode("//third-quote");
            assertNotNull(thirdQuote);
            assertEquals(THIRD_QUOTE_ES, thirdQuote.getText());
        }
    }

    private void assertRootFolderChildren(List<Item> children, boolean filterSpecified, boolean processorSpecified) {
        assertNotNull(children);

        if (filterSpecified) {
            assertEquals(2, children.size());
            assertBundleFolderItem(children.get(0), processorSpecified);
            assertCrafterCMSLogoItem(children.get(1), processorSpecified);
        } else {
            assertEquals(4, children.size());
            assertBundleFolderItem(children.get(0), processorSpecified);
            assertCrafterLevelDescriptorItem(children.get(1));
            assertCrafterCMSLogoItem(children.get(2), processorSpecified);
            assertSystemInfoComponentItem(children.get(3));
        }
    }

    private void assertBundleFolderChildren(List<Item> children) {
        assertNotNull(children);
        assertEquals(3, children.size());
        assertContentFolderItem(children.get(0));
        assertContentFrFolderItem(children.get(1));
        assertContentFrEsFolderItem(children.get(2));
    }

    private void assertContentFolderChildren(List<Item> children, boolean filterSpecified, boolean processorSpecified) {
        assertNotNull(children);

        if (filterSpecified) {
            assertEquals(0, children.size());
        } else {
            assertEquals(1, children.size());
            assertContentDescriptorItem(children.get(0), processorSpecified);
        }
    }

    private void assertContentFrFolderChildren(List<Item> children, boolean filterSpecified,
                                               boolean processorSpecified) {
        assertNotNull(children);

        if (filterSpecified) {
            assertEquals(0, children.size());
        } else {
            assertEquals(1, children.size());
            assertContentFrDescriptorItem(children.get(0), processorSpecified);
        }
    }

    private void assertContentFrEsFolderChildren(List<Item> children, boolean filterSpecified,
                                                 boolean processorSpecified) {
        assertNotNull(children);

        if (filterSpecified) {
            assertEquals(0, children.size());
        } else {
            assertEquals(1, children.size());
            assertContentFrEsDescriptorItem(children.get(0), processorSpecified);
        }
    }

    private void assertRootFolderTree(Tree tree, int depth, boolean filterSpecified, boolean processorSpecified) {
        assertNotNull(tree);
        assertRootFolderItem(tree);

        if (depth == UNLIMITED_TREE_DEPTH || depth >= 1) {
            List<Item> rootFolderChildren = tree.getChildren();
            assertRootFolderChildren(rootFolderChildren, filterSpecified, processorSpecified);

            if (depth == UNLIMITED_TREE_DEPTH || depth >= 2) {
                List<Item> bundleFolderChildren = ((Tree)rootFolderChildren.get(0)).getChildren();
                assertBundleFolderChildren(bundleFolderChildren);

                if (depth == UNLIMITED_TREE_DEPTH || depth >= 3) {
                    List<Item> contentFolderChildren = ((Tree)bundleFolderChildren.get(0)).getChildren();
                    assertContentFolderChildren(contentFolderChildren, filterSpecified, processorSpecified);

                    List<Item> contentFrFolderChildren = ((Tree)bundleFolderChildren.get(1)).getChildren();
                    assertContentFrFolderChildren(contentFrFolderChildren, filterSpecified, processorSpecified);

                    List<Item> contentFrEsFolderChildren = ((Tree)bundleFolderChildren.get(2)).getChildren();
                    assertContentFrEsFolderChildren(contentFrEsFolderChildren, filterSpecified, processorSpecified);
                } else {
                    assertTrue(CollectionUtils.isEmpty(((Tree)bundleFolderChildren.get(0)).getChildren()));
                    assertTrue(CollectionUtils.isEmpty(((Tree)bundleFolderChildren.get(1)).getChildren()));
                    assertTrue(CollectionUtils.isEmpty(((Tree)bundleFolderChildren.get(2)).getChildren()));
                }
            } else {
                assertTrue(CollectionUtils.isEmpty(((Tree)rootFolderChildren.get(0)).getChildren()));
            }
        } else {
            assertTrue(CollectionUtils.isEmpty(tree.getChildren()));
        }
    }

    private void assertCaching(CachingAwareObject expected, CachingAwareObject actual) {
        assertEquals(expected.getScope(), actual.getScope());
        assertEquals(expected.getKey(), actual.getKey());
        assertEquals(expected.getCachingTime(), actual.getCachingTime());

        assertTrue(cache.hasKey(context, actual.getKey()));
    }

    private void assertListCaching(CachingAwareList<Item> expected, CachingAwareList<Item> actual) {
        assertCaching(expected, actual);

        int count = expected.size();
        for (int i = 0; i < count; i++) {
            assertCaching(expected.get(i), actual.get(i));
        }

        assertTrue(cache.hasKey(context, actual.getKey()));
    }

    private void assertTreeCaching(Tree expected, Tree actual) {
        assertCaching(expected, actual);

        List<Item> expectedChildren = expected.getChildren();
        List<Item> actualChildren = actual.getChildren();

        if (CollectionUtils.isNotEmpty(expectedChildren)) {
            int childrenCount = expectedChildren.size();
            for (int i = 0; i < childrenCount; i++) {
                Item expectedChild = expectedChildren.get(i);
                Item actualChild = actualChildren.get(i);

                if (expectedChild instanceof Tree) {
                    assertTreeCaching((Tree)expectedChild, (Tree)actualChild);
                } else {
                    assertCaching(expectedChild, actualChild);
                }
            }
        }
    }

    private static class OnlyNonDescriptorsFilter implements ItemFilter {

        public static final OnlyNonDescriptorsFilter instance = new OnlyNonDescriptorsFilter();

        private OnlyNonDescriptorsFilter() {
        }

        @Override
        public boolean runBeforeProcessing() {
            return true;
        }

        @Override
        public boolean runAfterProcessing() {
            return false;
        }

        @Override
        public boolean accepts(Item item, boolean runningBeforeProcessing) {
            return !item.getName().endsWith(DESCRIPTOR_FILE_EXTENSION);
        }

    }

}
