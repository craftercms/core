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

import java.util.List;

import org.craftercms.core.exception.*;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Content;
import org.craftercms.core.service.Context;
import org.craftercms.core.service.Item;

/**
 * Adapter that provides path based access to a repository of some type.
 *
 * @author Sumer Jabri
 * @author Michiel Verkaik
 * @author Alfonso Vásquez
 */
public interface ContentStoreAdapter {

    Context createContext(String id, String storeServerUrl, String username, String password, String rootFolderPath,
                          boolean cacheOn,
                          int maxAllowedItemsInCache, boolean ignoreHiddenFiles) throws StoreException,
            AuthenticationException;

    void destroyContext(Context context) throws InvalidContextException, StoreException, AuthenticationException;

    Content getContent(Context context, CachingOptions cachingOptions, String path) throws InvalidScopeException,
            PathNotFoundException,
            StoreException;

    Item getItem(Context context, CachingOptions cachingOptions, String path,
                 boolean withDescriptor) throws InvalidScopeException,
            PathNotFoundException, XmlFileParseException, StoreException;

    List<Item> getItems(Context context, CachingOptions cachingOptions, String path,
                        boolean withDescriptor) throws InvalidScopeException,
            PathNotFoundException, XmlFileParseException, StoreException;

}
