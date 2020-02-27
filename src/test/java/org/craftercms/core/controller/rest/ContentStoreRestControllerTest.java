/*
 * Copyright (C) 2007-2020 Crafter Software Corporation. All Rights Reserved.
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
package org.craftercms.core.controller.rest;

import java.util.ArrayList;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletResponse;

import org.craftercms.core.exception.ForbiddenPathException;
import org.craftercms.core.processors.ItemProcessor;
import org.craftercms.core.service.*;
import org.craftercms.core.store.ContentStoreAdapter;
import org.craftercms.core.util.cache.CachingAwareObject;
import org.craftercms.core.util.cache.impl.CachingAwareList;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import static org.craftercms.core.controller.rest.ContentStoreRestController.CACHE_CONTROL_HEADER_NAME;
import static org.craftercms.core.controller.rest.ContentStoreRestController.MUST_REVALIDATE_HEADER_VALUE;
import static org.craftercms.core.service.ContentStoreService.UNLIMITED_TREE_DEPTH;
import static org.craftercms.core.service.Context.DEFAULT_CACHE_ON;
import static org.craftercms.core.service.Context.DEFAULT_IGNORE_HIDDEN_FILES;
import static org.craftercms.core.service.Context.DEFAULT_MAX_ALLOWED_ITEMS_IN_CACHE;
import static org.craftercms.core.service.Context.DEFAULT_MERGING_ON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
* Class description goes HERE
*
* @author Alfonso Vásquez
*/
public class ContentStoreRestControllerTest {

    private static final String LAST_MODIFIED_HEADER_NAME = "Last-Modified";
    private static final String IF_MODIFIED_SINCE_HEADER_NAME = "If-Modified-Since";

    private static final String FOLDER_URL = "/folder";
    private static final String ITEM_URL = FOLDER_URL + "/item";
    private static final String PROTECTED_URL = "/protected/folder";

    private ContentStoreRestController storeRestController;
    private ContentStoreService storeService;
    private Item item;
    private CachingAwareList<Item> children;
    private Tree tree;
    private Context context;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private WebRequest webRequest;

    @Before
    public void setUp() throws Exception {
        setUpTestContext();
        setUpTestItems();
        setUpTestRequest();
        setUpTestResponse();
        setUpTestWebRequest();
        setUpTestStoreService();
        setUpTestStoreRestController();
    }

    @Test
    public void testGetItemNotModified() throws Exception {
        testNotModified(item, () -> storeRestController.getItem(webRequest, response, context.getId(), ITEM_URL));

        verify(storeService).getItem(context, ITEM_URL);
    }

    @Test
    public void testGetItemModified() throws Exception {
        testModified(item, () -> storeRestController.getItem(webRequest, response, context.getId(), ITEM_URL));

        verify(storeService).getItem(context, ITEM_URL);
    }

    @Test(expected = ForbiddenPathException.class)
    public void testGetItemProtected() {
        storeRestController.getItem(webRequest, response, context.getId(), PROTECTED_URL);
        fail("Expected " + ForbiddenPathException.class.getName() + " exception");
    }

    @Test
    public void testGetChildrenNotModified() throws Exception {
        testNotModified(children, () -> storeRestController.getChildren(webRequest, response, context.getId(),
                                                                        FOLDER_URL));

        verify(storeService).getChildren(eq(context),
                                         isNull(CachingOptions.class),
                                         eq(FOLDER_URL),
                                         any(ItemFilter.class),
                                         isNull(ItemProcessor.class));
    }

    @Test(expected = ForbiddenPathException.class)
    public void testGetChildrenProtected() {
        storeRestController.getChildren(webRequest, response, context.getId(), PROTECTED_URL);
        fail("Expected " + ForbiddenPathException.class.getName() + " exception");
    }

    @Test
    public void testGetChildrenModified() throws Exception {
        testModified(children, () -> storeRestController.getChildren(webRequest, response, context.getId(),
            FOLDER_URL));

        verify(storeService).getChildren(eq(context),
                                         isNull(CachingOptions.class),
                                         eq(FOLDER_URL),
                                         any(ItemFilter.class),
                                         isNull(ItemProcessor.class));
    }

    @Test
    public void testGetTreeNotModified() throws Exception {
        testNotModified(tree, () -> storeRestController.getTree(webRequest, response, context.getId(), FOLDER_URL,
                                                                UNLIMITED_TREE_DEPTH));

        verify(storeService).getTree(eq(context),
                                     isNull(CachingOptions.class),
                                     eq(FOLDER_URL),
                                     eq(UNLIMITED_TREE_DEPTH),
                                     any(ItemFilter.class),
                                     isNull(ItemProcessor.class));
    }

    @Test
    public void testGetTreeModified() throws Exception {
        testModified(tree, () -> storeRestController.getTree(webRequest, response, context.getId(), FOLDER_URL,
            UNLIMITED_TREE_DEPTH));

        verify(storeService).getTree(eq(context),
                                     isNull(CachingOptions.class),
                                     eq(FOLDER_URL),
                                     eq(UNLIMITED_TREE_DEPTH),
                                     any(ItemFilter.class),
                                     isNull(ItemProcessor.class));
    }

    @Test(expected = ForbiddenPathException.class)
    public void testGetTreeProtected() {
        storeRestController.getTree(webRequest, response, context.getId(), PROTECTED_URL, UNLIMITED_TREE_DEPTH);
        fail("Expected " + ForbiddenPathException.class.getName() + " exception");
    }

    private void testNotModified(CachingAwareObject cachingAwareObject, Supplier<Object> supplier) {
        cachingAwareObject.setCachingTime(System.currentTimeMillis());
        request.addHeader(IF_MODIFIED_SINCE_HEADER_NAME, cachingAwareObject.getCachingTime());

        Object object = supplier.get();
        assertNull(object);
        assertEquals(HttpServletResponse.SC_NOT_MODIFIED, response.getStatus());
        assertEquals(MUST_REVALIDATE_HEADER_VALUE, response.getHeader(CACHE_CONTROL_HEADER_NAME));
    }

    private void testModified(CachingAwareObject cachingAwareObject, Supplier<Object> supplier) throws Exception {
        request.addHeader(IF_MODIFIED_SINCE_HEADER_NAME, System.currentTimeMillis());

        Thread.sleep(1000);

        cachingAwareObject.setCachingTime(System.currentTimeMillis());

        Object object = supplier.get();
        assertEquals(cachingAwareObject, object);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        //Remove the nano precession,
        assertEquals(new Long(cachingAwareObject.getCachingTime()/1000),
                     new Long(response.getDateHeader(LAST_MODIFIED_HEADER_NAME)/1000));
        assertEquals(MUST_REVALIDATE_HEADER_VALUE, response.getHeader(CACHE_CONTROL_HEADER_NAME));
    }

    private void setUpTestItems() {
        item = new Item();
        children = new CachingAwareList<>(new ArrayList<>());
        tree = new Tree();
    }

    private void setUpTestContext() {
        ContentStoreAdapter storeAdapter = mock(ContentStoreAdapter.class);

        context = new ContextImpl("0", storeAdapter, "/", DEFAULT_MERGING_ON, DEFAULT_CACHE_ON,
                                  DEFAULT_MAX_ALLOWED_ITEMS_IN_CACHE, DEFAULT_IGNORE_HIDDEN_FILES);
    }

    private void setUpTestRequest() {
        request = new MockHttpServletRequest();
        request.setMethod("GET");
    }

    private void setUpTestResponse() {
        response = new MockHttpServletResponse();
    }

    private void setUpTestWebRequest() {
        webRequest = new ServletWebRequest(request, response);
    }

    private void setUpTestStoreService() {
        storeService = mock(ContentStoreService.class);
        try {
            when(storeService.getContext(context.getId())).thenReturn(context);
            when(storeService.getItem(context, ITEM_URL)).thenReturn(item);
            when(storeService.getChildren(eq(context),
                                          isNull(CachingOptions.class),
                                          eq(FOLDER_URL),
                                          any(ItemFilter.class),
                                          isNull(ItemProcessor.class)))
                    .thenReturn(children);
            when(storeService.getTree(eq(context),
                                      isNull(CachingOptions.class),
                                      eq(FOLDER_URL),
                                      eq(UNLIMITED_TREE_DEPTH),
                                      any(ItemFilter.class),
                                      isNull(ItemProcessor.class)))
                    .thenReturn(tree);
        } catch (Exception e) {
        }
    }

    private void setUpTestStoreRestController() {
        storeRestController = new ContentStoreRestController();
        storeRestController.setStoreService(storeService);
        storeRestController.setForbiddenUrlPatterns(new String[] {"^/?protected(/.+)?$"});
        storeRestController.init();
    }

}
