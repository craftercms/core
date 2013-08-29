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
package org.craftercms.core.url.impl;

import javax.servlet.http.HttpServletRequest;

import org.craftercms.core.exception.UrlTransformationException;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.url.UrlTransformer;
import org.craftercms.core.util.HttpServletUtils;

/**
 * Prepends to a context relative url the schema, domain and port (if different than 80 and 443 in case of https) to URL, normally to get a
 * full url, e.g., /webapp/servlet/a => http://domain.com:8080/webapp/servlet/a
 *
 * @author Alfonso Vásquez
 */
public class AddSchemeAndDomainAndServerPortUrlTransformer implements UrlTransformer {

    @Override
    public String transformUrl(Context context, CachingOptions cachingOptions, String url) throws UrlTransformationException {
        HttpServletRequest currentRequest = HttpServletUtils.getCurrentRequest();
        String scheme = currentRequest.getScheme();
        String domain = currentRequest.getServerName();
        int serverPort = currentRequest.getServerPort();

        StringBuilder fullUrl = new StringBuilder();
        fullUrl.append(scheme).append("://").append(domain);

        if ( !(scheme.equals("http") && serverPort == 80) && !(scheme.equals("https") && serverPort == 443) ) {
            fullUrl.append(":").append(serverPort);
        }

        if ( !url.startsWith("/") ) {
            fullUrl.append("/");
        }

        fullUrl.append(url);

        return fullUrl.toString();
    }

}
