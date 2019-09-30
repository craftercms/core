package org.craftercms.core.store.impl.filesystem;

import org.craftercms.core.service.Content;

import java.io.*;

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
