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
package org.craftercms.core.util.url.impl;

import org.craftercms.core.util.url.ContentBundleUrl;

/**
 * @author Alfonso Vásquez
 */
public class ContentBundleUrlImpl implements ContentBundleUrl {

    private String prefix;
    private String baseNameAndExtensionToken;
    private String suffix;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getBaseNameAndExtensionToken() {
        return baseNameAndExtensionToken;
    }

    public void setBaseNameAndExtensionToken(String baseNameAndExtensionToken) {
        this.baseNameAndExtensionToken = baseNameAndExtensionToken;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

}
