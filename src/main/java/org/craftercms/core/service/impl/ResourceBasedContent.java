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
package org.craftercms.core.service.impl;

import org.craftercms.core.service.Content;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation of {@link Content} that relies on a {@link Resource}
 *
 * @author joseross
 * @since 3.1.6
 */
public class ResourceBasedContent implements Content {

    /**
     * The resource
     */
    protected Resource resource;

    public ResourceBasedContent(Resource resource) {
        this.resource = resource;
    }

    @Override
    public long getLastModified() {
        try {
            return resource.lastModified();
        } catch (IOException e) {
            return -1;
        }
    }

    @Override
    public long getLength() {
        try {
            return resource.contentLength();
        } catch (IOException e) {
            return 0;
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return resource.getInputStream();
    }

}
