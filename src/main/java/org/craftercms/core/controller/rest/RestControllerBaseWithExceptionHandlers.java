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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craftercms.commons.validation.ValidationException;
import org.craftercms.commons.validation.ValidationResult;
import org.craftercms.commons.validation.ValidationRuntimeException;
import org.craftercms.core.exception.AuthenticationException;
import org.craftercms.core.exception.ForbiddenPathException;
import org.craftercms.core.exception.InvalidContextException;
import org.craftercms.core.exception.PathNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Base class for Crafter REST services that also adds exception handlers for the service methods.
 *
 * @author avasquez
 */
public class RestControllerBaseWithExceptionHandlers extends RestControllerBase {

    private static final Log logger = LogFactory.getLog(RestControllerBase.class);

    @ExceptionHandler(InvalidContextException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handleInvalidContextException(HttpServletRequest request, InvalidContextException e) {
        return handleException(request, e);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public Map<String, Object> handleAuthenticationException(HttpServletRequest request, AuthenticationException e) {
        return handleException(request, e);
    }

    @ExceptionHandler(PathNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handlePathNotFoundException(HttpServletRequest request, PathNotFoundException e) {
        return handleException(request, e);
    }

    @ExceptionHandler(ForbiddenPathException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public Map<String, Object> handleForbiddenPathException(HttpServletRequest request, ForbiddenPathException e) {
        return handleException(request, e);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationResult handleValidationException(HttpServletRequest request, ValidationException e) {
        logger.error("Request for " + request.getRequestURI() + " failed", e);

        return e.getResult();
    }

    @ExceptionHandler(ValidationRuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationResult handleValidationRuntimeException(HttpServletRequest request, ValidationRuntimeException e) {
        logger.error("Request for " + request.getRequestURI() + " failed", e);

        return e.getResult();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Map<String, Object> handleException(HttpServletRequest request, Exception e) {
        logger.error("Request for " + request.getRequestURI() + " failed", e);

        return createResponseMessage(e.getMessage());
    }

}
