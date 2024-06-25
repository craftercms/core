/*
 * Copyright (C) 2007-2024 Crafter Software Corporation. All Rights Reserved.
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

import org.craftercms.core.exception.ForbiddenPathException;
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

import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Supplier;

import static org.craftercms.core.controller.rest.ContentStoreRestController.CACHE_CONTROL_HEADER_NAME;
import static org.craftercms.core.controller.rest.ContentStoreRestController.MUST_REVALIDATE_HEADER_VALUE;
import static org.craftercms.core.service.ContentStoreService.TREE_DEPTH_HARD_LIMIT;
import static org.craftercms.core.service.Context.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
    private static final String CONTEXT_NAME = "contextName";
    private static final String CONTEXT_NAME_VALUE = "crafter-test";

    private ContentStoreRestController storeRestController;
    private ContentStoreService storeService;
    private Item item;
    private CachingAwareList<Item> children;
    private Tree tree;
    private Context context;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private WebRequest webRequest;
    private Map<String, String> contextConfigVariables;

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
    public void testContextConfigVariables() {
        storeRestController.getItem(webRequest, response, context.getId(), ITEM_URL, false);
        verify(storeService).getItem(argThat(context-> {
            Map<String, String> contextVariables = context.getConfigLookupVariables();
            return contextConfigVariables.entrySet().stream().allMatch(entry -> entry.getValue().equals(contextVariables.get(entry.getKey())));
        }), any(), eq(ITEM_URL), any(), anyBoolean());
    }

    @Test
    public void testGetItemNotModified() throws Exception {
        testNotModified(item, 
                () -> storeRestController.getItem(webRequest, response, context.getId(), ITEM_URL, false));

        verify(storeService).getItem(context, null, ITEM_URL, null, false);
    }

    @Test
    public void testGetItemModified() throws Exception {
        testModified(item, () -> storeRestController.getItem(webRequest, response, context.getId(), ITEM_URL, false));

        verify(storeService).getItem(context, null, ITEM_URL, null, false);
    }

    @Test(expected = ForbiddenPathException.class)
    public void testGetItemProtected() {
        storeRestController.getItem(webRequest, response, context.getId(), PROTECTED_URL, false);
        fail("Expected " + ForbiddenPathException.class.getName() + " exception");
    }

    @Test
    public void testGetChildrenNotModified() throws Exception {
        testNotModified(children, () -> storeRestController.getChildren(webRequest, response, context.getId(),
                                                                        FOLDER_URL, false));

        verify(storeService).getChildren(eq(context),
                                         isNull(),
                                         eq(FOLDER_URL),
                                         any(ItemFilter.class),
                                         isNull(),
                                         eq(false));
    }

    @Test(expected = ForbiddenPathException.class)
    public void testGetChildrenProtected() {
        storeRestController.getChildren(webRequest, response, context.getId(), PROTECTED_URL, false);
        fail("Expected " + ForbiddenPathException.class.getName() + " exception");
    }

    @Test
    public void testGetChildrenModified() throws Exception {
        testModified(children, () -> storeRestController.getChildren(webRequest, response, context.getId(),
            FOLDER_URL, false));

        verify(storeService).getChildren(eq(context),
                                         isNull(),
                                         eq(FOLDER_URL),
                                         any(ItemFilter.class),
                                         isNull(),
                                         eq(false));
    }

    @Test
    public void testGetTreeNotModified() throws Exception {
        testNotModified(tree, () -> storeRestController.getTree(webRequest, response, context.getId(), FOLDER_URL,
                                                                TREE_DEPTH_HARD_LIMIT, false));

        verify(storeService).getTree(eq(context),
                                     isNull(),
                                     eq(FOLDER_URL),
                                     eq(TREE_DEPTH_HARD_LIMIT),
                                     any(ItemFilter.class),
                                     isNull(),
                                     eq(false));
    }

    @Test
    public void testGetTreeModified() throws Exception {
        testModified(tree, () -> storeRestController.getTree(webRequest, response, context.getId(), FOLDER_URL,
                TREE_DEPTH_HARD_LIMIT, false));

        verify(storeService).getTree(eq(context),
                                     isNull(),
                                     eq(FOLDER_URL),
                                     eq(TREE_DEPTH_HARD_LIMIT),
                                     any(ItemFilter.class),
                                     isNull(),
                                     eq(false));
    }

    @Test(expected = ForbiddenPathException.class)
    public void testGetTreeProtected() {
        storeRestController.getTree(webRequest, response, context.getId(), PROTECTED_URL, TREE_DEPTH_HARD_LIMIT, false);
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
        assertEquals(cachingAwareObject.getCachingTime()/1000,
                     response.getDateHeader(LAST_MODIFIED_HEADER_NAME)/1000);
        assertEquals(MUST_REVALIDATE_HEADER_VALUE, response.getHeader(CACHE_CONTROL_HEADER_NAME));
    }

    private void setUpTestItems() {
        item = new Item();
        children = new CachingAwareList<>(new ArrayList<>());
        tree = new Tree();
    }

    private void setUpTestContext() {
        ContentStoreAdapter storeAdapter = mock(ContentStoreAdapter.class);

        contextConfigVariables = Map.of(CONTEXT_NAME, CONTEXT_NAME_VALUE);
        context = new ContextImpl("0", storeAdapter, "/", DEFAULT_MERGING_ON, DEFAULT_CACHE_ON,
                                  DEFAULT_MAX_ALLOWED_ITEMS_IN_CACHE, DEFAULT_IGNORE_HIDDEN_FILES, contextConfigVariables);
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
            when(storeService.getItem(context, null, ITEM_URL, null, false)).thenReturn(item);
            when(storeService.getChildren(eq(context),
                                          isNull(),
                                          eq(FOLDER_URL),
                                          any(ItemFilter.class),
                                          isNull(),
                                          eq(false)))
                    .thenReturn(children);
            when(storeService.getTree(eq(context),
                                      isNull(),
                                      eq(FOLDER_URL),
                                      eq(TREE_DEPTH_HARD_LIMIT),
                                      any(ItemFilter.class),
                                      isNull(),
                                      eq(false)))
                    .thenReturn(tree);
        } catch (Exception e) {
        }
    }

    private void setUpTestStoreRestController() {
        storeRestController = new ContentStoreRestController(storeService, TREE_DEPTH_HARD_LIMIT);
        storeRestController.setForbiddenUrlPatterns(new String[] {"^/?protected(/.+)?$"});
        storeRestController.afterPropertiesSet();
    }

}
