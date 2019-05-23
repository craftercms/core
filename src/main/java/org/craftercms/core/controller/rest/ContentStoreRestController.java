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
package org.craftercms.core.controller.rest;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.craftercms.commons.lang.RegexUtils;
import org.craftercms.core.exception.*;
import org.craftercms.core.service.*;
import org.craftercms.core.service.impl.CompositeItemFilter;
import org.craftercms.core.service.impl.ExcludeByUrlItemFilter;
import org.craftercms.core.service.impl.IncludeByUrlItemFilter;
import org.craftercms.core.util.cache.impl.CachingAwareList;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * REST service that provides several methods to access the Crafter content store.
 *
 * @author avasquez
 */
@Controller
@RequestMapping(RestControllerBase.REST_BASE_URI + ContentStoreRestController.URL_ROOT)
public class ContentStoreRestController extends RestControllerBaseWithExceptionHandlers {

    public static final String URL_ROOT = "/content_store";
    public static final String CACHE_CONTROL_HEADER_NAME = "Cache-Control";
    public static final String MUST_REVALIDATE_HEADER_VALUE = "must-revalidate";
    public static final String REQUEST_PARAM_CONTEXT_ID = "contextId";
    public static final String REQUEST_PARAM_URL = "url";
    public static final String REQUEST_PARAM_TREE_DEPTH = "depth";
    public static final String URL_DESCRIPTOR = "/descriptor";
    public static final String URL_ITEM = "/item";
    public static final String URL_CHILDREN = "/children";
    public static final String URL_TREE = "/tree";
    public static final String MODEL_ATTR_DESCRIPTOR = "descriptor";
    public static final String MODEL_ATTR_ITEM = "item";
    public static final String MODEL_ATTR_CHILDREN = "children";
    public static final String MODEL_ATTR_TREE = "tree";

    private ContentStoreService storeService;
    private String[] allowedUrlPatterns;
    private String[] forbiddenUrlPatterns;

    private ItemFilter itemFilter;

    @Required
    public void setStoreService(ContentStoreService storeService) {
        this.storeService = storeService;
    }

    public void setAllowedUrlPatterns(String[] allowedUrlPatterns) {
        this.allowedUrlPatterns = allowedUrlPatterns;
    }

    public void setForbiddenUrlPatterns(String[] forbiddenUrlPatterns) {
        this.forbiddenUrlPatterns = forbiddenUrlPatterns;
    }

    @PostConstruct
    public void init() {
        CompositeItemFilter compositeItemFilter = new CompositeItemFilter();
        compositeItemFilter.setFilters(Arrays.asList(new IncludeByUrlItemFilter(allowedUrlPatterns),
                                                     new ExcludeByUrlItemFilter(forbiddenUrlPatterns)));

        itemFilter = compositeItemFilter;
    }

    @RequestMapping(value = URL_DESCRIPTOR, method = RequestMethod.GET)
    public Map<String, Object> getDescriptor(WebRequest request, HttpServletResponse response,
                                             @RequestParam(REQUEST_PARAM_CONTEXT_ID) String contextId,
                                             @RequestParam(REQUEST_PARAM_URL) String url)
            throws InvalidContextException, StoreException, PathNotFoundException, ForbiddenPathException,
                   ItemProcessingException, XmlMergeException, XmlFileParseException {
        Map<String, Object> model = getItem(request, response, contextId, url);

        if (MapUtils.isNotEmpty(model)) {
            Item item = (Item) model.remove(MODEL_ATTR_ITEM);
            model.put(MODEL_ATTR_DESCRIPTOR, item.getDescriptorDom());
        }

        return model;
    }

    @RequestMapping(value = URL_ITEM, method = RequestMethod.GET)
    public Map<String, Object> getItem(WebRequest request, HttpServletResponse response,
                                       @RequestParam(REQUEST_PARAM_CONTEXT_ID) String contextId,
                                       @RequestParam(REQUEST_PARAM_URL) String url)
            throws InvalidContextException, StoreException, PathNotFoundException, ForbiddenPathException,
                   ItemProcessingException, XmlMergeException, XmlFileParseException {
        checkIfUrlAllowed(url);

        Context context = storeService.getContext(contextId);
        if (context == null) {
            throw new InvalidContextException("No context found for ID " + contextId);
        }

        Item item = storeService.getItem(context, url);

        if (item.getCachingTime() != null && checkNotModified(item.getCachingTime(), request, response)) {
            return null;
        } else {
            return createSingletonModifiableMap(MODEL_ATTR_ITEM, item);
        }
    }

    @RequestMapping(value = URL_CHILDREN, method = RequestMethod.GET)
    public Map<String, Object> getChildren(WebRequest request, HttpServletResponse response,
                                           @RequestParam(REQUEST_PARAM_CONTEXT_ID) String contextId,
                                           @RequestParam(REQUEST_PARAM_URL) String url)
            throws InvalidContextException, StoreException, PathNotFoundException, ForbiddenPathException,
                   ItemProcessingException, XmlMergeException, XmlFileParseException {
        checkIfUrlAllowed(url);

        Context context = storeService.getContext(contextId);
        if (context == null) {
            throw new InvalidContextException("No context found for ID " + contextId);
        }

        CachingAwareList<Item> children =
                (CachingAwareList<Item>) storeService.getChildren(context, null, url, itemFilter, null);

        if (children.getCachingTime() != null && checkNotModified(children.getCachingTime(), request, response)) {
            return null;
        } else {
            return createSingletonModifiableMap(MODEL_ATTR_CHILDREN, new ArrayList<Item>(children));
        }
    }

    @RequestMapping(value = URL_TREE, method = RequestMethod.GET)
    public Map<String, Object> getTree(WebRequest request, HttpServletResponse response,
                                       @RequestParam(REQUEST_PARAM_CONTEXT_ID) String contextId,
                                       @RequestParam(REQUEST_PARAM_URL) String url,
                                       @RequestParam(value = REQUEST_PARAM_TREE_DEPTH, required = false) Integer depth)
            throws InvalidContextException, StoreException, PathNotFoundException, ForbiddenPathException,
                   ItemProcessingException, XmlMergeException, XmlFileParseException {
        checkIfUrlAllowed(url);

        Context context = storeService.getContext(contextId);
        if (context == null) {
            throw new IllegalArgumentException("No context found for ID " + contextId);
        }

        if (depth == null) {
            depth = ContentStoreService.UNLIMITED_TREE_DEPTH;
        }

        Tree tree = storeService.getTree(context, null, url, depth, itemFilter, null);

        if (tree.getCachingTime() != null && checkNotModified(tree.getCachingTime(), request, response)) {
            return null;
        } else {
            return createSingletonModifiableMap(MODEL_ATTR_TREE, tree);
        }
    }

    private boolean checkNotModified(long lastModifiedTimestamp, WebRequest request, HttpServletResponse response) {
        response.setHeader(CACHE_CONTROL_HEADER_NAME, MUST_REVALIDATE_HEADER_VALUE);

        return request.checkNotModified(lastModifiedTimestamp);
    }

    private boolean isUrlAllowed(String url) {
        return (ArrayUtils.isEmpty(allowedUrlPatterns) || RegexUtils.matchesAny(url, allowedUrlPatterns)) &&
               (ArrayUtils.isEmpty(forbiddenUrlPatterns) || !RegexUtils.matchesAny(url, forbiddenUrlPatterns));
    }

    private void checkIfUrlAllowed(String url) throws ForbiddenPathException {
        if (!isUrlAllowed(url)) {
            throw new ForbiddenPathException("Access denied to URL " + url);
        }
    }

}
