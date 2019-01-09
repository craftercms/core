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
package org.craftercms.core.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Sumer Jabri
 */
public class UrlUtils {

    public static final char URL_PARAM_DELIM = '?';
    public static final char URL_PARAM_SEPARATOR = '&';

    /**
     * Returns the short name representation of a long name.
     *
     * @param longName
     * @param containsShortNameRegex the regex that identifies whether the long name contains a short name. This
     *                               regex should also contain
     *                               a group expression that can be use to capture for the short name (see the
     *                               Pattern class javadoc).
     * @param shortNameRegexGroup    the index of the captured group that represents the short name (see the Pattern
     *                               class javadoc)
     * @return the short name, or the long name if there was no short name match
     * @see Pattern
     */
    public static String getShortName(String longName, String containsShortNameRegex, int shortNameRegexGroup) {
        Pattern pattern = Pattern.compile(containsShortNameRegex);
        Matcher matcher = pattern.matcher(longName);

        if (matcher.matches()) {
            return matcher.group(shortNameRegexGroup);
        } else {
            return longName;
        }
    }

}
