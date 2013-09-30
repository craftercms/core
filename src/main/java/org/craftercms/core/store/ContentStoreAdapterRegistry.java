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
package org.craftercms.core.store;

import java.util.HashMap;
import java.util.Map;

import org.craftercms.core.util.spring.AbstractBeanIdBasedRegistry;

/**
 * {@link AbstractBeanIdBasedRegistry} for {@link ContentStoreAdapter}.
 *
 * @author Alfonso Vásquez
 */
public class ContentStoreAdapterRegistry extends AbstractBeanIdBasedRegistry<ContentStoreAdapter> {

    @Override
    protected Class<ContentStoreAdapter> getRegistryType() {
        return ContentStoreAdapter.class;
    }

    @Override
    protected String getBeanNameIdPrefix() {
        return "crafter.contentStoreAdapter.";
    }

    @Override
    protected Map<String, ContentStoreAdapter> createRegistry() {
        return new HashMap<String, ContentStoreAdapter>();
    }

}
