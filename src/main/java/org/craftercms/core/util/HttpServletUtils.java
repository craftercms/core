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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author Alfonso Vásquez
 */
public class HttpServletUtils {

    public static final int SCOPE_REQUEST = RequestAttributes.SCOPE_REQUEST;
    public static final int SCOPE_SESSION = RequestAttributes.SCOPE_SESSION;

    public static HttpServletRequest getCurrentRequest() throws IllegalStateException {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes)requestAttributes).getRequest();
        } else {
            throw new IllegalStateException("Current RequestAttributes isn't of type ServletRequestAttributes. Are " +
                "you sure you're" +
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
                        ((List<String>)queryParams.get(paramName)).add(paramValue);
                    } else {
                        List<String> paramValues = Arrays.asList((String)queryParams.get(paramName), paramValue);
                        queryParams.put(paramName, paramValues);
                    }
                } else {
                    queryParams.put(paramName, paramValue);
                }
            }
        }

        return queryParams;
    }

    public static String getQueryStringFromParams(Map<String, Object> queryParams,
                                                  String charset) throws UnsupportedEncodingException {
        StringBuilder queryString = new StringBuilder();

        if (MapUtils.isNotEmpty(queryParams)) {
            for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
                String paramName = URLEncoder.encode(entry.getKey(), charset);

                if (entry.getValue() instanceof List) {
                    for (String paramValue : (List<String>)entry.getValue()) {
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

                    String paramValue = URLEncoder.encode((String)entry.getValue(), charset);

                    queryString.append(paramName).append('=').append(paramValue);
                }
            }

            queryString.insert(0, '?');
        }

        return queryString.toString();
    }

    public static Map<String, Object> createRequestParamsMap(HttpServletRequest request) {
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        for (Enumeration paramNameEnum = request.getParameterNames(); paramNameEnum.hasMoreElements(); ) {
            String paramName = (String)paramNameEnum.nextElement();
            String[] paramValues = request.getParameterValues(paramName);

            if (paramValues.length == 1) {
                paramsMap.put(paramName, paramValues[0]);
            } else {
                paramsMap.put(paramName, paramValues);
            }
        }

        return paramsMap;
    }

    public static Map<String, Object> createRequestAttributesMap(HttpServletRequest request) {
        Map<String, Object> attributesMap = new HashMap<String, Object>();
        for (Enumeration attributeNameEnum = request.getAttributeNames(); attributeNameEnum.hasMoreElements(); ) {
            String attributeName = (String)attributeNameEnum.nextElement();

            attributesMap.put(attributeName, request.getAttribute(attributeName));
        }

        return attributesMap;
    }

    public static Map<String, Object> createHeadersMap(HttpServletRequest request) {
        Map<String, Object> headersMap = new HashMap<String, Object>();
        for (Enumeration headerNameEnum = request.getHeaderNames(); headerNameEnum.hasMoreElements(); ) {
            String headerName = (String)headerNameEnum.nextElement();
            List<String> headerValues = new ArrayList<String>();

            CollectionUtils.addAll(headerValues, request.getHeaders(headerName));

            if (headerValues.size() == 1) {
                headersMap.put(headerName, headerValues.get(0));
            } else {
                headersMap.put(headerName, headerValues.toArray(new String[headerValues.size()]));
            }
        }

        return headersMap;
    }

    public static Map<String, String> createCookiesMap(HttpServletRequest request) {
        Map<String, String> cookiesMap = new HashMap<String, String>();
        Cookie[] cookies = request.getCookies();

        if (ArrayUtils.isNotEmpty(cookies)) {
            for (Cookie cookie : request.getCookies()) {
                cookiesMap.put(cookie.getName(), cookie.getValue());
            }
        }

        return cookiesMap;
    }

    public static Map<String, Object> createSessionMap(HttpServletRequest request) {
        Map<String, Object> sessionMap = new HashMap<String, Object>();
        HttpSession session = request.getSession(false);

        if (session != null) {
            for (Enumeration attributeNameEnum = session.getAttributeNames(); attributeNameEnum.hasMoreElements(); ) {
                String attributeName = (String)attributeNameEnum.nextElement();
                sessionMap.put(attributeName, session.getAttribute(attributeName));
            }
        }

        return sessionMap;
    }

}
