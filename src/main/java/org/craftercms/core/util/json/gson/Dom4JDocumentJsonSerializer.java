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
package org.craftercms.core.util.json.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.craftercms.core.util.XmlUtils;
import org.dom4j.Document;
import org.craftercms.core.util.XmlUtils;

import java.lang.reflect.Type;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class Dom4JDocumentJsonSerializer implements JsonSerializer<Document> {

    public static final Dom4JDocumentJsonSerializer INSTANCE = new Dom4JDocumentJsonSerializer();

    private Dom4JDocumentJsonSerializer() {
    }

    @Override
    public JsonElement serialize(Document src, Type typeOfSrc, JsonSerializationContext context) {
        return XmlUtils.documentToJson(src);
    }

}
