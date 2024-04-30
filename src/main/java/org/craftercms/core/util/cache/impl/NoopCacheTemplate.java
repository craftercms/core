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
import org.craftercms.core.service.CacheService;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;

/**
 * Extension of {@link DefaultCacheTemplate} that executes the given {@link Callback} without additional checks.
 *
 * @author joseross
 * @since 3.1.18
 */
public class NoopCacheTemplate extends DefaultCacheTemplate {

    public NoopCacheTemplate(CacheService cacheService) {
        super(cacheService);
    }

    @Override
    public <T> T getObject(Context context, CachingOptions options, Callback<T> callback, Object... keyElements) {
        return callback.execute();
    }

}
