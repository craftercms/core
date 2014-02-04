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

import org.craftercms.core.exception.AuthenticationException;
import org.craftercms.core.exception.InvalidContextException;
import org.craftercms.core.exception.PathNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all Crafter REST services. Handles exceptions thrown by service methods.
 *
 * @author Alfonso Vásquez
 */
public class RestControllerBase {

    public static final String REST_BASE_URI = "/api/1";
    public static final String EXCEPTION_MODEL_ATTRIBUTE_NAME = "exception";

    @ExceptionHandler(InvalidContextException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleInvalidContextException(InvalidContextException e) {
        return handleException(e);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object>  handleAuthenticationException(AuthenticationException e) {
        return handleException(e);
    }

    @ExceptionHandler(PathNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object>  handlePathNotFoundException(PathNotFoundException e) {
        return handleException(e);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleException(Exception e) {
        return createSingletonModel(EXCEPTION_MODEL_ATTRIBUTE_NAME, e);
    }

    protected Map<String, Object> createSingletonModel(String attributeName, Object attributeValue) {
        Map<String, Object> model = new HashMap<String, Object>(1);
        model.put(attributeName, attributeValue);

        return model;
    }

}
