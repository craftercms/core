/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
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
package org.craftercms.core.util.cache.impl;

import org.craftercms.commons.concurrent.locks.KeyBasedLockFactory;
import org.craftercms.core.service.CacheService;
import org.craftercms.core.service.Context;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.locks.ReentrantLock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * @author joseross
 */
@RunWith(MockitoJUnitRunner.class)
public class NoopCacheTemplateTest {

    public static final String CACHE_KEY = "test";

    public static final String CACHE_VALUE = "Hello";

    @Mock
    private Context context;

    @Mock
    private CacheService cacheService;

    @Mock
    private KeyBasedLockFactory<ReentrantLock> lockFactory;

    @InjectMocks
    private NoopCacheTemplate cacheTemplate;

    @Test
    public void testCacheIsNotUsed() {
        cacheTemplate.getObject(context, () -> CACHE_VALUE, CACHE_KEY);

        verify(cacheService, never()).get(eq(context), any());
        verify(lockFactory, never()).getLock(any());
    }

}
