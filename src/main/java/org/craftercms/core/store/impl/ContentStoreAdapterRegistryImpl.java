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
package org.craftercms.core.store.impl;

import org.craftercms.core.store.ContentStoreAdapter;
import org.craftercms.core.store.ContentStoreAdapterRegistry;
import org.springframework.beans.factory.annotation.Required;

import java.util.Map;

/**
 * Default implementation of {@link ContentStoreAdapterRegistry}.
 *
 * @author avasque
 */
public class ContentStoreAdapterRegistryImpl implements ContentStoreAdapterRegistry {

    private Map<String, ContentStoreAdapter> adapters;

    @Required
    public void setAdapters(Map<String, ContentStoreAdapter> adapters) {
        this.adapters = adapters;
    }

    @Override
    public ContentStoreAdapter get(String storeType) {
        return adapters.get(storeType);
    }

}
