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

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.craftercms.core.util.json.gson.ThrowableJsonSerializer;
import org.dom4j.Document;
import org.craftercms.core.util.json.gson.Dom4JDocumentJsonSerializer;
import org.craftercms.core.util.json.gson.ThrowableJsonSerializer;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class JsonUtils {

    public static void setOrAccumulate(JsonObject jsonObject, String property, JsonElement value) {
        if (jsonObject.has(property)) {
            JsonElement propertyValue = jsonObject.get(property);
            JsonArray propertyValueArray;

            if (propertyValue instanceof JsonArray) {
                propertyValueArray = (JsonArray) propertyValue;
            } else {
                propertyValueArray = new JsonArray();
                propertyValueArray.add(propertyValue);

                jsonObject.add(property, propertyValueArray);
            }

            propertyValueArray.add(value);
        } else {
            jsonObject.add(property, value);
        }
    }

    public static GsonBuilder getDefaultGsonBuilder() {
        return new GsonBuilder()
                .registerTypeHierarchyAdapter(Document.class, Dom4JDocumentJsonSerializer.INSTANCE)
                .registerTypeHierarchyAdapter(Throwable.class, ThrowableJsonSerializer.INSTANCE)
                .serializeNulls();
    }

}
