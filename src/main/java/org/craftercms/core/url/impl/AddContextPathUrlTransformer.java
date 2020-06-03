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
package org.craftercms.core.url.impl;

import javax.servlet.ServletContext;

import org.craftercms.core.exception.UrlTransformationException;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.url.UrlTransformer;
import org.springframework.web.context.ServletContextAware;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class AddContextPathUrlTransformer implements UrlTransformer, ServletContextAware {

    private ServletContext servletContext;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public String transformUrl(Context context, CachingOptions cachingOptions,
                               String url) throws UrlTransformationException {
        if (servletContext == null) {
            throw new IllegalStateException("No ServletContext was set. Are you sure you're running in a servlet " +
                "environment?");
        }

        String contextPath = servletContext.getContextPath();
        if (contextPath.equals("/") && url.startsWith("/")) {
            return url;
        } else {
            return contextPath + url;
        }
    }

}
