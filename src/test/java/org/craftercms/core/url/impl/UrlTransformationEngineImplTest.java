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

import org.craftercms.core.url.impl.UrlTransformationEngineImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.craftercms.core.exception.UrlTransformationException;
import org.craftercms.core.service.CacheService;
import org.craftercms.core.service.Context;
import org.craftercms.core.util.cache.CacheTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.craftercms.core.service.Context.DEFAULT_CACHE_ON;
import static org.craftercms.core.service.Context.DEFAULT_MAX_ALLOWED_ITEMS_IN_CACHE;
import static org.craftercms.core.url.impl.AbstractCachedUrlTransformationEngine.TRANSFORMED_URL_CONST_KEY_ELEM;
import static org.craftercms.core.url.impl.AddDebugParamUrlTransformer.DEFAULT_DEBUG_URL_PARAM;

/**
 * @author Alfonso Vásquez
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/contexts/UrlTransformationEngineImplTest.xml")
public class UrlTransformationEngineImplTest {

    private static final String URL = "/index.html";
    private static final String TRANSFORMED_URL = "/index.xml?" + DEFAULT_DEBUG_URL_PARAM + "=true";
    private static final String TRANSFORMER_NAME = "mainPipeline";

    @Autowired
    private UrlTransformationEngineImpl transformationEngine;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private CacheTemplate cacheTemplate;
    private Context context;

    @Before
    public void setUp() throws Exception {
        setUpTestContext();
    }

    @Test
    public void testTransformationEngine() throws Exception {
        String transformedUrl = transformationEngine.transformUrl(context, TRANSFORMER_NAME, URL);
        assertEquals(TRANSFORMED_URL, transformedUrl);
        assertTrue(cacheService.hasKey(context, cacheTemplate.getKey(context, TRANSFORMER_NAME, URL, TRANSFORMED_URL_CONST_KEY_ELEM)));

        try {
            transformationEngine.transformUrl(context, "invalidPipeline", URL);
            fail();
        } catch (UrlTransformationException e) {
            // expected
        }
    }

    private void setUpTestContext() {
        context = mock(Context.class);
        when(context.getId()).thenReturn("0");
        when(context.getCacheScope()).thenReturn("0");
        when(context.getMaxAllowedItemsInCache()).thenReturn(DEFAULT_MAX_ALLOWED_ITEMS_IN_CACHE);
        when(context.isCacheOn()).thenReturn(DEFAULT_CACHE_ON);

        cacheService.addScope(context);
    }

}
