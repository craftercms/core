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
package org.craftercms.core.cache.impl;

import org.craftercms.core.cache.CacheLoader;

/**
 * Dummy {@link org.craftercms.core.cache.CacheLoader} that takes the the first string parameter and
 * converts it to upper case.
 *
 * @author Alfonso Vásquez
 */
public class DummyCacheLoader implements CacheLoader {

    public Object load(Object... parameters) throws Exception {
        return parameters[0].toString().toUpperCase();
    }
    
}
