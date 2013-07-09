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
package org.craftercms.core.store.impl;

import org.craftercms.core.service.Content;

/**
 * Abstraction of a file in a content store. Used by adapters to have direct access to a file, whether it's local or remote. Also,
 * returned as {@link Content} when the content shouldn't be cached.
 *
 * @author Alfonso Vásquez
 */
public interface File extends Content {

    String getName();

    String getPath();

    boolean isFile();

    boolean isDirectory();

}
