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
package org.craftercms.core.store;

import java.util.List;

import org.craftercms.core.exception.AuthenticationException;
import org.craftercms.core.exception.InvalidContextException;
import org.craftercms.core.exception.RootFolderNotFoundException;
import org.craftercms.core.exception.StoreException;
import org.craftercms.core.exception.XmlFileParseException;
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

    Context createContext(String id, String rootFolderPath, boolean mergingOn, boolean cacheOn,
                          int maxAllowedItemsInCache, boolean ignoreHiddenFiles)
            throws RootFolderNotFoundException, StoreException, AuthenticationException;

    boolean validate(Context context) throws StoreException, AuthenticationException;

    void destroyContext(Context context) throws StoreException, AuthenticationException;

    boolean exists(Context context, CachingOptions cachingOptions, String path)
        throws InvalidContextException, StoreException;

    Content findContent(Context context, CachingOptions cachingOptions, String path)
            throws InvalidContextException, StoreException;

    Item findItem(Context context, CachingOptions cachingOptions, String path, boolean withDescriptor)
            throws InvalidContextException, XmlFileParseException, StoreException;

    List<Item> findItems(Context context, CachingOptions cachingOptions, String path)
            throws InvalidContextException, XmlFileParseException, StoreException;

}
