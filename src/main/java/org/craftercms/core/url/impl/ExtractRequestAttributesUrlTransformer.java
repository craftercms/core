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

import java.util.Map;

import org.craftercms.commons.http.RequestContext;
import org.craftercms.core.exception.UrlTransformationException;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.url.UrlTransformer;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.util.UriTemplate;

/**
 * Extracts a request attribute from the URL, and finally removes it from the URL. E.g.: with a URI template like
 * /site/website/{event}/details/index.xml and a url like /site/website/1/details/index.xml, the resulting url will be
 * /site/website/details/index.xml, with request attribute event = 1.
 *
 * @author Praveen Elineni
 * @author Alfonso Vásquez
 */
public class ExtractRequestAttributesUrlTransformer implements UrlTransformer {

    private UriTemplate uriTemplate;

    @Required
    public void setUriTemplate(String uriTemplate) {
        this.uriTemplate = new UriTemplate(uriTemplate);
    }

    @Override
    public String transformUrl(Context context, CachingOptions cachingOptions,
                               String url) throws UrlTransformationException {
        if (uriTemplate.matches(url)) {
            Map<String, String> variables = uriTemplate.match(url);
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                RequestContext.getCurrent().getRequest().setAttribute(entry.getKey(), entry.getValue());
            }

            url = uriTemplate.toString().replaceAll("\\{[^{}]+\\}", "");
            url = url.replace("//", "/");
        }

        return url;
    }

}
