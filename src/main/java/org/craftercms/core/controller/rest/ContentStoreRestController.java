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
package org.craftercms.core.controller.rest;

import java.util.ArrayList;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.craftercms.core.exception.*;
import org.craftercms.core.processors.ItemProcessor;
import org.craftercms.core.service.*;
import org.craftercms.core.util.cache.impl.CachingAwareList;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

/**
 * REST service that provides several methods to access the Crafter content store.
 *
 * @author Alfonso Vásquez
 */
@Controller
@RequestMapping(RestControllerBase.REST_BASE_URI + ContentStoreRestController.URL_ROOT)
public class ContentStoreRestController extends RestControllerBase implements ApplicationContextAware {

    public static final String URL_ROOT = "/content_store";
    public static final String CACHE_CONTROL_HEADER_NAME = "Cache-Control";
    public static final String MUST_REVALIDATE_HEADER_VALUE = "must-revalidate";
    public static final String REQUEST_PARAM_STORE_TYPE = "storeType";
    public static final String REQUEST_PARAM_STORE_SERVER_URL = "storeServerUrl";
    public static final String REQUEST_PARAM_USERNAME = "username";
    public static final String REQUEST_PARAM_PASSWORD = "password";
    public static final String REQUEST_PARAM_ROOT_FOLDER_PATH = "rootFolderPath";
    public static final String REQUEST_PARAM_CACHE_ON = "cacheOn";
    public static final String REQUEST_PARAM_MAX_ALLOWED_ITEMS_IN_CACHE = "maxAllowedItemsInCache";
    public static final String REQUEST_PARAM_IGNORE_HIDDEN_FILES = "ignoreHiddenFiles";
    public static final String REQUEST_PARAM_CONTEXT_ID = "contextId";
    public static final String REQUEST_PARAM_DO_CACHING = "doCaching";
    public static final String REQUEST_PARAM_EXPIRE_AFTER = "expireAfter";
    public static final String REQUEST_PARAM_REFRESH_FREQUENCY = "refreshFrequency";
    public static final String REQUEST_PARAM_URL = "url";
    public static final String REQUEST_PARAM_FILTER = "filter";
    public static final String REQUEST_PARAM_PROCESSOR = "processor";
    public static final String REQUEST_PARAM_TREE_DEPTH = "depth";
    public static final String URL_CREATE_CONTEXT = "/create_context";
    public static final String URL_DESTROY_CONTEXT = "/destroy_context";
    public static final String URL_DESCRIPTOR = "/descriptor";
    public static final String URL_ITEM = "/item";
    public static final String URL_CHILDREN = "/children";
    public static final String URL_TREE = "/tree";
    public static final String MODEL_ATTR_CONTEXT_ID = "contextId";
    public static final String MODEL_ATTR_DESCRIPTOR = "descriptor";
    public static final String MODEL_ATTR_ITEM = "item";
    public static final String MODEL_ATTR_CHILDREN = "children";
    public static final String MODEL_ATTR_TREE = "tree";
    private ContentStoreService storeService;
    private ApplicationContext applicationContext;

    @Required
    public void setStoreService(ContentStoreService storeService) {
        this.storeService = storeService;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @RequestMapping(value = URL_CREATE_CONTEXT, method = RequestMethod.GET)
    public ModelAndView createContext(@RequestParam(REQUEST_PARAM_STORE_TYPE) String storeType,
                                      @RequestParam(REQUEST_PARAM_STORE_SERVER_URL) String storeServerUrl,
                                      @RequestParam(REQUEST_PARAM_USERNAME) String username,
                                      @RequestParam(REQUEST_PARAM_PASSWORD) String password,
                                      @RequestParam(REQUEST_PARAM_ROOT_FOLDER_PATH) String rootFolderPath,
                                      @RequestParam(REQUEST_PARAM_CACHE_ON) boolean cacheOn,
                                      @RequestParam(REQUEST_PARAM_MAX_ALLOWED_ITEMS_IN_CACHE) int
                                              maxAllowedItemsInCache,
                                      @RequestParam(REQUEST_PARAM_IGNORE_HIDDEN_FILES) boolean ignoreHiddenFiles)
            throws StoreException,
            AuthenticationException {
        Context context = storeService.createContext(storeType, storeServerUrl, username, password, rootFolderPath,
                cacheOn,
                maxAllowedItemsInCache, ignoreHiddenFiles);

        return new ModelAndView(REST_VIEW_NAME, MODEL_ATTR_CONTEXT_ID, context.getId());
    }

    @RequestMapping(value = URL_DESTROY_CONTEXT, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void destroyContext(@RequestParam(REQUEST_PARAM_CONTEXT_ID) String contextId) throws
            InvalidContextException, StoreException,
            AuthenticationException {
        Context context = storeService.getContext(contextId);
        if ( context == null ) {
            throw new InvalidContextException("No context found for ID " + contextId);
        }

        storeService.destroyContext(context);
    }

    @RequestMapping(value = URL_DESCRIPTOR, method = RequestMethod.GET)
    public ModelAndView getDescriptor(WebRequest request, HttpServletResponse response,
                                      @RequestParam(REQUEST_PARAM_CONTEXT_ID) String contextId,
                                      @RequestParam(value = REQUEST_PARAM_DO_CACHING,
                                              required = false) Boolean doCaching,
                                      @RequestParam(value = REQUEST_PARAM_EXPIRE_AFTER,
                                              required = false) Long expireAfter,
                                      @RequestParam(value = REQUEST_PARAM_REFRESH_FREQUENCY,
                                              required = false) Long refreshFrequency,
                                      @RequestParam(REQUEST_PARAM_URL) String url,
                                      @RequestParam(value = REQUEST_PARAM_PROCESSOR, required = false) String processor)
            throws InvalidContextException, StoreException, PathNotFoundException, ItemProcessingException,
            XmlMergeException,
            XmlFileParseException {
        ModelAndView modelAndView = getItem(request, response, contextId, doCaching, expireAfter, refreshFrequency,
                url, processor);
        ModelMap modelMap = modelAndView.getModelMap();

        if ( MapUtils.isNotEmpty(modelMap) ) {
            Item item = (Item) modelMap.remove(MODEL_ATTR_ITEM);
            modelMap.put(MODEL_ATTR_DESCRIPTOR, item.getDescriptorDom());
        }

        return modelAndView;
    }

    @RequestMapping(value = URL_ITEM, method = RequestMethod.GET)
    public ModelAndView getItem(WebRequest request, HttpServletResponse response,
                                @RequestParam(REQUEST_PARAM_CONTEXT_ID) String contextId,
                                @RequestParam(value = REQUEST_PARAM_DO_CACHING, required = false) Boolean doCaching,
                                @RequestParam(value = REQUEST_PARAM_EXPIRE_AFTER, required = false) Long expireAfter,
                                @RequestParam(value = REQUEST_PARAM_REFRESH_FREQUENCY,
                                        required = false) Long refreshFrequency,
                                @RequestParam(REQUEST_PARAM_URL) String url,
                                @RequestParam(value = REQUEST_PARAM_PROCESSOR, required = false) String processor)
            throws InvalidContextException, StoreException, PathNotFoundException, ItemProcessingException,
            XmlMergeException,
            XmlFileParseException {
        Context context = storeService.getContext(contextId);
        if ( context == null ) {
            throw new InvalidContextException("No context found for ID " + contextId);
        }

        CachingOptions cachingOptions = new CachingOptions();
        if ( doCaching != null ) {
            cachingOptions.setDoCaching(doCaching);
        }
        if ( expireAfter != null ) {
            cachingOptions.setExpireAfter(expireAfter);
        }
        if ( refreshFrequency != null ) {
            cachingOptions.setRefreshFrequency(refreshFrequency);
        }

        Item item = storeService.getItem(context, cachingOptions, url, getProcessor(processor));

        if ( checkNotModified(item.getCachingTime(), request, response) ) {
            return new ModelAndView(REST_VIEW_NAME);
        } else {
            return new ModelAndView(REST_VIEW_NAME, MODEL_ATTR_ITEM, item);
        }
    }

    @RequestMapping(value = URL_CHILDREN, method = RequestMethod.GET)
    public ModelAndView getChildren(WebRequest request, HttpServletResponse response,
                                    @RequestParam(REQUEST_PARAM_CONTEXT_ID) String contextId,
                                    @RequestParam(value = REQUEST_PARAM_DO_CACHING, required = false) Boolean doCaching,
                                    @RequestParam(value = REQUEST_PARAM_EXPIRE_AFTER,
                                            required = false) Long expireAfter,
                                    @RequestParam(value = REQUEST_PARAM_REFRESH_FREQUENCY,
                                            required = false) Long refreshFrequency,
                                    @RequestParam(REQUEST_PARAM_URL) String url,
                                    @RequestParam(value = REQUEST_PARAM_FILTER, required = false) String filter,
                                    @RequestParam(value = REQUEST_PARAM_PROCESSOR, required = false) String processor)
            throws InvalidContextException, StoreException, PathNotFoundException, ItemProcessingException,
            XmlMergeException,
            XmlFileParseException {
        Context context = storeService.getContext(contextId);
        if ( context == null ) {
            throw new InvalidContextException("No context found for ID " + contextId);
        }

        CachingOptions cachingOptions = new CachingOptions();
        if ( doCaching != null ) {
            cachingOptions.setDoCaching(doCaching);
        }
        if ( expireAfter != null ) {
            cachingOptions.setExpireAfter(expireAfter);
        }
        if ( refreshFrequency != null ) {
            cachingOptions.setRefreshFrequency(refreshFrequency);
        }

        CachingAwareList<Item> children = (CachingAwareList<Item>) storeService.getChildren(context, cachingOptions,
                url,
                getFilter(filter), getProcessor(processor));

        if ( checkNotModified(children.getCachingTime(), request, response) ) {
            return new ModelAndView(REST_VIEW_NAME);
        } else {
            return new ModelAndView(REST_VIEW_NAME, MODEL_ATTR_CHILDREN, new ArrayList<Item>(children));
        }
    }

    @RequestMapping(value = URL_TREE, method = RequestMethod.GET)
    public ModelAndView getTree(WebRequest request, HttpServletResponse response,
                                @RequestParam(REQUEST_PARAM_CONTEXT_ID) String contextId,
                                @RequestParam(value = REQUEST_PARAM_DO_CACHING, required = false) Boolean doCaching,
                                @RequestParam(value = REQUEST_PARAM_EXPIRE_AFTER, required = false) Long expireAfter,
                                @RequestParam(value = REQUEST_PARAM_REFRESH_FREQUENCY,
                                        required = false) Long refreshFrequency,
                                @RequestParam(REQUEST_PARAM_URL) String url,
                                @RequestParam(value = REQUEST_PARAM_TREE_DEPTH, required = false) Integer depth,
                                @RequestParam(value = REQUEST_PARAM_FILTER, required = false) String filter,
                                @RequestParam(value = REQUEST_PARAM_PROCESSOR, required = false) String processor)
            throws InvalidContextException, StoreException, PathNotFoundException, ItemProcessingException,
            XmlMergeException,
            XmlFileParseException {
        Context context = storeService.getContext(contextId);
        if ( context == null ) {
            throw new IllegalArgumentException("No context found for ID " + contextId);
        }

        CachingOptions cachingOptions = new CachingOptions();
        if ( doCaching != null ) {
            cachingOptions.setDoCaching(doCaching);
        }
        if ( expireAfter != null ) {
            cachingOptions.setExpireAfter(expireAfter);
        }
        if ( refreshFrequency != null ) {
            cachingOptions.setRefreshFrequency(refreshFrequency);
        }

        Tree tree = storeService.getTree(context, cachingOptions, url, depth != null ? depth : ContentStoreService
                .UNLIMITED_TREE_DEPTH,
                getFilter(filter), getProcessor(processor));

        if ( checkNotModified(tree.getCachingTime(), request, response) ) {
            return new ModelAndView(REST_VIEW_NAME);
        } else {
            return new ModelAndView(REST_VIEW_NAME, MODEL_ATTR_TREE, tree);
        }
    }

    private boolean checkNotModified(long lastModifiedTimestamp, WebRequest request, HttpServletResponse response) {
        response.setHeader(CACHE_CONTROL_HEADER_NAME, MUST_REVALIDATE_HEADER_VALUE);

        return request.checkNotModified(lastModifiedTimestamp);
    }

    private ItemFilter getFilter(String filterName) {
        if ( StringUtils.isNotEmpty(filterName) ) {
            return applicationContext.getBean(filterName, ItemFilter.class);
        } else {
            return null;
        }
    }

    private ItemProcessor getProcessor(String processorName) {
        if ( StringUtils.isNotEmpty(processorName) ) {
            return applicationContext.getBean(processorName, ItemProcessor.class);
        } else {
            return null;
        }
    }

}
