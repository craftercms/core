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
package org.craftercms.core.service;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents the content of a file in the content store.
 *
 * @author Alfonso Vásquez
 */
public interface Content {

    /**
     * Returns the last modified date of the content.
     */
    long getLastModified();

    /**
     * Returns the content length.
     */
    long getLength();

    /**
     * Returns a {@link InputStream} for the data.
     */
    InputStream getInputStream() throws IOException;

}
