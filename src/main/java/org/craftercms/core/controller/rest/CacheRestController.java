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

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craftercms.commons.exceptions.InvalidManagementTokenException;
import org.craftercms.core.exception.CacheException;
import org.craftercms.core.exception.InvalidContextException;
import org.craftercms.core.service.ContentStoreService;
import org.craftercms.core.service.Context;
import org.craftercms.core.util.cache.CacheTemplate;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * REST service that provides several methods to handle Crafter's cache engine.
 *
 * @author avasquez
 * @author hyanghee
 */
@Controller
@RequestMapping(RestControllerBase.REST_BASE_URI + CacheRestController.URL_ROOT)
public class CacheRestController extends RestControllerBaseWithExceptionHandlers {

    private static final Log logger = LogFactory.getLog(CacheRestController.class);

    /**
     * rest URLs *
     */
    public static final String URL_ROOT = "/cache";
    public static final String URL_CLEAR_ALL_SCOPES = "/clear_all";
    public static final String URL_CLEAR_SCOPE = "/clear";

    public static final String REQUEST_PARAM_CONTEXT_ID = "contextId";

    private CacheTemplate cacheTemplate;
    private ContentStoreService storeService;

    private String authorizationToken;

    @Required
    public void setCacheTemplate(CacheTemplate cacheTemplate) {
        this.cacheTemplate = cacheTemplate;
    }

    @Required
    public void setStoreService(ContentStoreService storeService) {
        this.storeService = storeService;
    }

    @Required
    public void setAuthorizationToken(final String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    @RequestMapping(value = URL_CLEAR_ALL_SCOPES, method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> clearAllScopes(@RequestParam String token)
        throws CacheException, InvalidManagementTokenException {
        validateToken(token);
        cacheTemplate.getCacheService().clearAll();
        if (logger.isInfoEnabled()) {
            logger.info("[CACHE] All scopes have been cleared");
        }

        return createResponseMessage("All cache scopes have been cleared");
    }

    @RequestMapping(value = URL_CLEAR_SCOPE, method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> clearScope(@RequestParam(REQUEST_PARAM_CONTEXT_ID) String contextId,
                                          @RequestParam String token)
        throws InvalidContextException, CacheException, InvalidManagementTokenException {
        validateToken(token);
        Context context = storeService.getContext(contextId);
        if (context == null) {
            throw new InvalidContextException("No context found for ID " + contextId);
        }

        cacheTemplate.getCacheService().clearScope(context);
        if (logger.isInfoEnabled()) {
            logger.info("[CACHE] Scope for context " + context + " has been cleared");
        }

        return createResponseMessage("Cache scope for context '" + contextId + "' has been cleared");
    }

    protected void validateToken(String token) throws InvalidManagementTokenException {
        if (!StringUtils.equals(token, authorizationToken)) {
            throw new InvalidManagementTokenException("Management authorization failed, invalid token.");
        }
    }

}
