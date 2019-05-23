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

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

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

/**
 * Base class for Crafter REST services
 *
 * @author avasquez
 */
public class RestControllerBase {

    public static final String REST_BASE_URI = "${crafter.core.rest.base.uri}";
    public static final String MESSAGE_MODEL_ATTRIBUTE_NAME = "message";

    protected Map<String, Object> createResponseMessage(String message) {
        return createSingletonModifiableMap(MESSAGE_MODEL_ATTRIBUTE_NAME, message);
    }

    protected Map<String, Object> createSingletonModifiableMap(String attributeName, Object attributeValue) {
        Map<String, Object> model = new HashMap<>(1);
        model.put(attributeName, attributeValue);

        return model;
    }

}
