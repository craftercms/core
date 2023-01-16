/*
 * Copyright (C) 2007-2023 Crafter Software Corporation. All Rights Reserved.
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
package org.craftercms.core.controller.rest;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for Crafter REST services
 *
 * @author avasquez
 */
public class RestControllerBase {

    public static final String REST_BASE_URI = "${crafter.core.rest.base.uri}";
    public static final String MESSAGE_MODEL_ATTRIBUTE_NAME = "message";

    public static Map<String, Object> createResponseMessage(String message) {
        return createSingletonModifiableMap(MESSAGE_MODEL_ATTRIBUTE_NAME, message);
    }

    public static Map<String, Object> createSingletonModifiableMap(String attributeName, Object attributeValue) {
        Map<String, Object> model = new HashMap<>(1);
        model.put(attributeName, attributeValue);

        return model;
    }

}
