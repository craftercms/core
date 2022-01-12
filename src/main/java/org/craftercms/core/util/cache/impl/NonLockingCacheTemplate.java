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

import org.craftercms.commons.lang.Callback;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;

/**
 * Extension of {@link DefaultCacheTemplate} that skips locking when loading items into the cache.
 *
 * @author joseross
 * @since 3.1.18
 */
public class NonLockingCacheTemplate extends DefaultCacheTemplate {

    @Override
    protected <T> T loadAndPutInCache(Context context, CachingOptions options, Callback<T> callback, Object key) {
        T obj = doGet(context, callback, key);
        if (obj == null) {
            obj = callback.execute();
            if (obj != null) {
                if (options == null) {
                    options = CachingOptions.DEFAULT_CACHING_OPTIONS;
                }

                obj = doPut(context, options, callback, key, obj);
            }
        }

        return obj;
    }

}
