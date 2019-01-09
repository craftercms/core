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
package org.craftercms.core.url.impl;

import javax.servlet.http.HttpServletRequest;

import org.craftercms.commons.http.RequestContext;
import org.craftercms.core.exception.UrlTransformationException;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.url.UrlTransformer;

/**
 * Prepends to a context relative url the schema, domain and port (if different than 80 and 443 in case of https) to
 * URL, normally to get a
 * full url, e.g., /webapp/servlet/a => http://domain.com:8080/webapp/servlet/a
 *
 * @author Alfonso Vásquez
 */
public class AddSchemeAndDomainAndServerPortUrlTransformer implements UrlTransformer {

    public static final String HTTP_SCHEME =        "http";
    public static final String HTTPS_SCHEME =       "https";
    public static final int DEFAULT_HTTP_PORT =     80;
    public static final int DEFAULT_HTTPS_PORT =    443;

    protected boolean forceHttps;
    protected int httpsPort;

    public AddSchemeAndDomainAndServerPortUrlTransformer() {
        forceHttps = false;
        httpsPort = DEFAULT_HTTPS_PORT;
    }

    public void setForceHttps(boolean forceHttps) {
        this.forceHttps = forceHttps;
    }

    public void setHttpsPort(int httpsPort) {
        this.httpsPort = httpsPort;
    }

    @Override
    public String transformUrl(Context context, CachingOptions cachingOptions,
                               String url) throws UrlTransformationException {
        HttpServletRequest currentRequest = RequestContext.getCurrent().getRequest();
        String scheme = currentRequest.getScheme();
        String domain = currentRequest.getServerName();
        int serverPort = currentRequest.getServerPort();

        if (forceHttps) {
            scheme = HTTPS_SCHEME;
            serverPort = httpsPort;
        }

        StringBuilder fullUrl = new StringBuilder();
        fullUrl.append(scheme).append("://").append(domain);

        if (!(scheme.equals(HTTP_SCHEME) && serverPort == DEFAULT_HTTP_PORT) &&
            !(scheme.equals(HTTPS_SCHEME) && serverPort == DEFAULT_HTTPS_PORT)) {
            fullUrl.append(":").append(serverPort);
        }

        if (!url.startsWith("/")) {
            fullUrl.append("/");
        }

        fullUrl.append(url);

        return fullUrl.toString();
    }

}
