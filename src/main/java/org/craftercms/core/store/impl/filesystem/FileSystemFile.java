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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.craftercms.core.service.Content;
import org.craftercms.core.store.impl.File;

/**
 * Adapter to a filesystem {@link java.io.File}, used by {@link org.craftercms.core.store.ContentStoreAdapter}s;
 *
 * @author Alfonso Vásquez
 */
public class FileSystemFile implements File {

    private java.io.File file;

    public FileSystemFile(String path) {
        file = new java.io.File(path);
    }

    public FileSystemFile(FileSystemFile parent, String child) {
        file = new java.io.File(parent.getFile(), child);
    }

    public FileSystemFile(java.io.File file) {
        this.file = file;
    }

    public java.io.File getFile() {
        return file;
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public String getPath() {
        return file.getPath();
    }

    @Override
    public boolean isFile() {
        return file.isFile();
    }

    @Override
    public boolean isDirectory() {
        return file.isDirectory();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FileSystemFile other = (FileSystemFile)o;

        if (!file.equals(other.file)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return file.hashCode();
    }

    @Override
    public String toString() {
        return file.toString();
    }

}
