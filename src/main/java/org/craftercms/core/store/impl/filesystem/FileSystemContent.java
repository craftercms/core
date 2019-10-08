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
package org.craftercms.core.store.impl.filesystem;

import org.craftercms.core.service.Content;

import java.io.*;

/**
 * Gives access to the content of a file in the local filesystem
 *
 * @author avasquez
 * @since 3.1.4
 */
public class FileSystemContent implements Content {

    private java.io.File file;

    public FileSystemContent(File file) {
        this.file = file;
    }

    @Override
    public long getLastModified() {
        return file.lastModified();
    }

    @Override
    public long getLength() {
        return file.length();
    }

    @Override
    public InputStream getInputStream() throws FileNotFoundException {
        return new BufferedInputStream(new FileInputStream(file));
    }

}
