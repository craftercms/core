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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class ThrowableJsonSerializer implements JsonSerializer<Throwable> {

    public static final ThrowableJsonSerializer INSTANCE = new ThrowableJsonSerializer();

    public static final String NEWLINE_AS_HTML = "<br />";
    public static final String TAB_AS_HTML = "&nbsp;&nbsp;&nbsp;&nbsp;";

    private String newLine;

    private ThrowableJsonSerializer() {
        newLine = System.getProperty("line.separator");
    }

    @Override
    public JsonElement serialize(Throwable src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject exJson = new JsonObject();

        exJson.addProperty("type", src.getClass().getName());
        exJson.addProperty("message", src.getMessage());

        StringWriter stackTraceWriter = new StringWriter();
        src.printStackTrace(new PrintWriter(stackTraceWriter));

        String stackTraceStr = stackTraceWriter.toString();
        stackTraceStr = stackTraceStr.replace(newLine, NEWLINE_AS_HTML);
        stackTraceStr = stackTraceStr.replace("\t", TAB_AS_HTML);

        exJson.addProperty("stackTrace", stackTraceStr);

        return exJson;
    }

}
