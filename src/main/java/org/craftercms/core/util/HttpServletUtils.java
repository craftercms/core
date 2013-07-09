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
package org.craftercms.core.util;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alfonso Vásquez
 */
public class HttpServletUtils {

    public static final int SCOPE_REQUEST = RequestAttributes.SCOPE_REQUEST;
    public static final int SCOPE_SESSION = RequestAttributes.SCOPE_SESSION;

    public static HttpServletRequest getCurrentRequest() throws IllegalStateException {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        } else {
            throw new IllegalStateException("Current RequestAttributes isn't of type ServletRequestAttributes. Are you sure you're" +
                    "running in a Servlet environment?");
        }
    }

    public static Object getAttribute(String name, int scope) throws IllegalStateException {
        return RequestContextHolder.currentRequestAttributes().getAttribute(name, scope);
    }

    public static void setAttribute(String name, Object value, int scope) throws IllegalStateException {
        RequestContextHolder.currentRequestAttributes().setAttribute(name, value, scope);
    }

    public static void removeAttribute(String name, int scope) throws IllegalStateException {
        RequestContextHolder.currentRequestAttributes().removeAttribute(name, scope);
    }

    public static Cookie getCookie(String name, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (ArrayUtils.isNotEmpty(cookies)) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie;
                }
            }
        }

        return null;
    }

    public static Map<String, Object> getParamsFromQueryString(String queryString) {
        Map<String, Object> queryParams = new HashMap<String, Object>();

        if (StringUtils.isNotEmpty(queryString)) {
            String[] params = queryString.split("&");
            for (String param : params) {
                String[] splitParam = param.split("=");
                String paramName = splitParam[0];
                String paramValue = splitParam[1];

                if (queryParams.containsKey(paramName)) {
                    if (queryParams.get(paramName) instanceof List) {
                        ((List<String>) queryParams.get(paramName)).add(paramValue);
                    } else {
                        List<String> paramValues = Arrays.asList((String) queryParams.get(paramName), paramValue);
                        queryParams.put(paramName, paramValues);
                    }
                } else {
                    queryParams.put(paramName, paramValue);
                }
            }
        }

        return queryParams;
    }

    public static String getQueryStringFromParams(Map<String, Object> queryParams, String charset) throws UnsupportedEncodingException {
        StringBuilder queryString = new StringBuilder();

        if (MapUtils.isNotEmpty(queryParams)) {
            for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
                String paramName = URLEncoder.encode(entry.getKey(), charset);

                if (entry.getValue() instanceof List) {
                    for (String paramValue : (List<String>) entry.getValue()) {
                        if (queryString.length() > 0) {
                            queryString.append('&');
                        }

                        paramValue = URLEncoder.encode(paramValue, charset);

                        queryString.append(paramName).append('=').append(paramValue);
                    }
                } else {
                    if (queryString.length() > 0) {
                        queryString.append('&');
                    }

                    String paramValue = URLEncoder.encode((String) entry.getValue(), charset);

                    queryString.append(paramName).append('=').append(paramValue);
                }
            }

            queryString.insert(0, '?');
        }

        return queryString.toString();
    }

}
