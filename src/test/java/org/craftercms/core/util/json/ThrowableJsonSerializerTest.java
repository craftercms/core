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
package org.craftercms.core.util.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.craftercms.core.util.json.gson.ThrowableJsonSerializer;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.craftercms.core.util.json.gson.ThrowableJsonSerializer.NEWLINE_AS_HTML;
import static org.craftercms.core.util.json.gson.ThrowableJsonSerializer.TAB_AS_HTML;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class ThrowableJsonSerializerTest {

    private static final String EXCEPTION_AS_JSON =
            "{" +
                    "\"type\":\"java.lang.Exception\"," +
                    "\"message\":\"This is a test exception\"," +
                    "\"stackTrace\":\"%s\"" +
            "}";

    private JsonSerializationContext serializationContext;

    @Before
    public void setUp() throws Exception {
        setUpTestSerializationContex();
    }

    @Test
    public void testSerializer() throws Exception {
        Exception ex = new Exception("This is a test exception");
        JsonElement json = ThrowableJsonSerializer.INSTANCE.serialize(ex, null, serializationContext);

        StringWriter stackTraceWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stackTraceWriter));

        String stackTraceStr = stackTraceWriter.toString();
        stackTraceStr = stackTraceStr.replace(System.getProperty("line.separator"), NEWLINE_AS_HTML);
        stackTraceStr = stackTraceStr.replace("\t", TAB_AS_HTML);

        String expectedJson = String.format(EXCEPTION_AS_JSON, stackTraceStr);

        assertEquals(expectedJson, json.toString());
    }

    private void setUpTestSerializationContex() {
        serializationContext = mock(JsonSerializationContext.class);
        when(serializationContext.serialize(Matchers.<Object>anyObject())).thenAnswer(new Answer<JsonElement>() {

            private Gson gson = new Gson();

            @Override
            public JsonElement answer(InvocationOnMock invocation) throws Throwable {
                return gson.toJsonTree(invocation.getArguments()[0]);
            }

        });
    }

}
