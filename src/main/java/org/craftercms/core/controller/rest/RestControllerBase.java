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

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craftercms.core.exception.AuthenticationException;
import org.craftercms.core.exception.InvalidContextException;
import org.craftercms.core.exception.PathNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Base class for all Crafter REST services. Handles exceptions thrown by service methods.
 *
 * @author Alfonso Vásquez
 */
public class RestControllerBase {

    private static final Log logger = LogFactory.getLog(RestControllerBase.class);

    public static final String REST_BASE_URI = "/api/1";
    public static final String MESSAGE_MODEL_ATTRIBUTE_NAME = "message";

    @ExceptionHandler(InvalidContextException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleInvalidContextException(HttpServletRequest request, InvalidContextException e) {
        return handleException(request, e);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object>  handleAuthenticationException(HttpServletRequest request, AuthenticationException e) {
        return handleException(request, e);
    }

    @ExceptionHandler(PathNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object>  handlePathNotFoundException(HttpServletRequest request, PathNotFoundException e) {
        return handleException(request, e);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleException(HttpServletRequest request, Exception e) {
        logger.error("Request for " + request.getRequestURI() + " failed", e);

        return createSingletonModel(MESSAGE_MODEL_ATTRIBUTE_NAME, e.getMessage());
    }

    protected Map<String, Object> createSingletonModel(String attributeName, Object attributeValue) {
        Map<String, Object> model = new HashMap<>(1);
        model.put(attributeName, attributeValue);

        return model;
    }

}
