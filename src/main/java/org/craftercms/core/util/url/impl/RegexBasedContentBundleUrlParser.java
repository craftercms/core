/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.craftercms.core.util.url.ContentBundleUrl;
import org.craftercms.core.util.url.ContentBundleUrlParser;

/**
 * @author Alfonso Vásquez
 */
public class RegexBasedContentBundleUrlParser implements ContentBundleUrlParser {

    private int prefixGroup;
    private int baseNameAndExtensionTokenGroup;
    private int suffixGroup;

    private Pattern pattern;

    public RegexBasedContentBundleUrlParser(int prefixGroup, int baseNameAndExtensionTokenGroup,
                                            int suffixGroup, Pattern pattern) {
        this.prefixGroup = prefixGroup;
        this.baseNameAndExtensionTokenGroup = baseNameAndExtensionTokenGroup;
        this.suffixGroup = suffixGroup;
        this.pattern = pattern;
    }

    @Override
    public ContentBundleUrl getContentBundleUrl(String rawUrl) {
        ContentBundleUrlImpl parsedUrl = new ContentBundleUrlImpl();
        Matcher matcher = pattern.matcher(rawUrl);

        if (matcher.matches()) {
            try {
                parsedUrl.setPrefix(matcher.group(prefixGroup));
            } catch (IndexOutOfBoundsException e) {
            }
            try {
                parsedUrl.setBaseNameAndExtensionToken(matcher.group(baseNameAndExtensionTokenGroup));
            } catch (IndexOutOfBoundsException e) {
            }
            try {
                parsedUrl.setSuffix(matcher.group(suffixGroup));
            } catch (IndexOutOfBoundsException e) {
            }
        }

        return parsedUrl;
    }

}
