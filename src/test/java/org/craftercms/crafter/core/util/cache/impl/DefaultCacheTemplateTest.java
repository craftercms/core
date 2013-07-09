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
package org.craftercms.crafter.core.util.cache.impl;

import org.craftercms.core.util.cache.impl.DefaultCacheTemplate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.craftercms.core.cache.CacheLoader;
import org.craftercms.core.service.CacheService;
import org.craftercms.core.service.Context;
import org.craftercms.core.util.cache.CacheCallback;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.craftercms.core.service.CachingOptions.DEFAULT_CACHING_OPTIONS;
import static org.craftercms.core.util.CacheUtils.generateKey;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class DefaultCacheTemplateTest {

    private static final String RANDOM_KEY_ELEM = "random";

    private static final long EXPIRE_AFTER = 1;
    private static final long REFRESH_FREQUENCY = 1;

    private DefaultCacheTemplate cacheTemplate;
    private CacheService cache;
    private CacheCallback<Long> cacheCallback;
    private Context context;

    @Before
    public void setUp() throws Exception {
        setUpTestContext();
        setUpTestCache();
        setUpTestCacheCallback();
        setUpTestCacheTemplate();
    }

    @Test
    public void testTemplate() throws Exception {
        Long time1  = cacheTemplate.execute(context, DEFAULT_CACHING_OPTIONS, cacheCallback, RANDOM_KEY_ELEM);

        Thread.sleep(100);

        Long time2 = cacheTemplate.execute(context, DEFAULT_CACHING_OPTIONS, cacheCallback, RANDOM_KEY_ELEM);

        assertEquals(time1, time2);

        Object key = generateKey(RANDOM_KEY_ELEM);

        verify(cache, times(2)).get(context, key);
        verify(cache).put(eq(context), eq(key), eq(time1), eq(DEFAULT_CACHING_OPTIONS), any(CacheLoader.class));
    }

    private void setUpTestContext() {
        context = mock(Context.class);
    }

    private void setUpTestCache() {
        cache = mock(CacheService.class);

        final Map<Object, Object> cache = new HashMap<Object, Object>();

        Answer<Object> getFromCacheAnswer = new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return cache.get(invocation.getArguments()[1]);
            }
        };

        Answer<Void> putInCacheAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                cache.put(invocation.getArguments()[1], invocation.getArguments()[2]);

                return null;
            }
        };

        Object key = generateKey(RANDOM_KEY_ELEM);

        when(this.cache.get(context, key)).thenAnswer(getFromCacheAnswer);
        doAnswer(putInCacheAnswer).when(this.cache).put(eq(context), eq(key), anyObject(), eq(DEFAULT_CACHING_OPTIONS), any(
                CacheLoader.class));
    }

    private void setUpTestCacheCallback() {
        cacheCallback = new CacheCallback<Long>() {

            @Override
            public Long doCacheable() {
                return System.currentTimeMillis();
            }

        };
    }

    private void setUpTestCacheTemplate() {
        cacheTemplate = new DefaultCacheTemplate();
        cacheTemplate.setCacheService(cache);
    }

}
