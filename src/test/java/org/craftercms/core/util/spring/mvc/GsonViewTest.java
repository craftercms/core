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
package org.craftercms.core.util.spring.mvc;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.craftercms.core.service.Item;
import org.craftercms.core.util.JsonUtils;
import org.craftercms.core.util.json.Dom4JDocumentJsonSerializerTest;
import org.craftercms.core.util.json.gson.Dom4JDocumentJsonSerializer;
import org.craftercms.core.util.json.gson.ThrowableJsonSerializer;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class GsonViewTest {

    private static final String ITEM_MODEL_ATTRIBUTE_NAME = "item";
    private static final String EXCEPTION_MODEL_ATTRIBUTE_NAME = "exception";
    
    private static final String ITEM_AS_JSON =
            "{" +
                    "\"name\":\"item\"," +
                    "\"url\":\"/folder/item\"," +
                    "\"descriptorUrl\":\"/folder/item.meta.xml\"," +
                    "\"descriptorDom\":%s," +
                    "\"properties\":{\"testProperty\":\"This is a test property\"}," +
                    "\"isFolder\":false" +
            "}";

    private static final String ITEM_MODEL_AS_JSON = "{\"item\":" + ITEM_AS_JSON + "}";

    private static final String EXCEPTION_MODEL_AS_JSON = "{\"exception\":%s}";

    private GsonView view;
    private JsonSerializationContext serializationContext;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private Map<String, Object> successModel;
    private Map<String, Object> failureModel;

    @Before
    public void setUp() throws Exception {
        setUpTestSerializationContex();
        setUpTestRequest();
        setUpTestResponse();
        setUpTestSuccessModel();
        setUpTestFailureModel();
        setUpTestView();
    }

    @Test
    public void testViewWithSuccessModel() throws Exception {
        testViewWithSuccessModel(false, false);
    }

    @Test
    public void testWithFailureModel() throws Exception {
        view.render(failureModel, request, response);

        String json = response.getContentAsString();

        Exception ex = (Exception) failureModel.get(EXCEPTION_MODEL_ATTRIBUTE_NAME);
        String exJson = ThrowableJsonSerializer.INSTANCE.serialize(ex, null, serializationContext).toString();
        String expectedJson = String.format(EXCEPTION_MODEL_AS_JSON, exJson);

        assertEquals(expectedJson, json);
    }

    @Test
    public void testPrefixJson() throws Exception {
        testViewWithSuccessModel(true, false);
    }

    @Test
    public void testRenderSingleAttributeAsRootObject() throws Exception {
        testViewWithSuccessModel(false, true);
    }

    private void testViewWithSuccessModel(boolean prefixJson, boolean renderSingleAttributeAsRootObject)
            throws Exception {
        view.setPrefixJson(prefixJson);
        view.setRenderSingleAttributeAsRootObject(renderSingleAttributeAsRootObject);

        view.render(successModel, request, response);

        String json = response.getContentAsString();

        Item item = (Item) successModel.get(ITEM_MODEL_ATTRIBUTE_NAME);
        String descriptorDomJson = Dom4JDocumentJsonSerializer.INSTANCE.serialize(item.getDescriptorDom(), null,
                serializationContext).toString();
        String expectedJson = String.format(renderSingleAttributeAsRootObject? ITEM_AS_JSON : ITEM_MODEL_AS_JSON,
                descriptorDomJson);

        if (prefixJson) {
            expectedJson = GsonView.JSON_ANTI_HIJACKING_PREFIX + expectedJson;
        }

        assertEquals(expectedJson, json);
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

    private void setUpTestRequest() {
        request = new MockHttpServletRequest();
    }

    private void setUpTestResponse() {
        response = new MockHttpServletResponse();
    }

    private void setUpTestSuccessModel() {
        Item item = new Item();
        item.setName("item");
        item.setUrl("/folder/" + item.getName());
        item.setDescriptorUrl(item.getUrl() + ".meta.xml");
        item.setProperty("testProperty", "This is a test property");
        item.setFolder(false);

        SAXReader reader = new SAXReader();
        try {
            item.setDescriptorDom(reader.read(new StringReader(Dom4JDocumentJsonSerializerTest.XML)));
        } catch (DocumentException e) {
        }

        successModel = new HashMap<String, Object>(2);
        successModel.put(ITEM_MODEL_ATTRIBUTE_NAME, item);
        successModel.put("ignoredAttibute", "ignoredValue");
    }

    private void setUpTestFailureModel() {
        failureModel = new HashMap<String, Object>(2);
        failureModel.put(EXCEPTION_MODEL_ATTRIBUTE_NAME, new Exception());
        failureModel.put("ignoredAttibute", "ignoredValue");
    }

    private void setUpTestView() {
        Set<String> renderedAttributes = new HashSet<String>(1);
        renderedAttributes.add(ITEM_MODEL_ATTRIBUTE_NAME);
        renderedAttributes.add(EXCEPTION_MODEL_ATTRIBUTE_NAME);

        view = new GsonView();
        // Disable HTML escaping for easier assertion
        view.setGson(JsonUtils.getDefaultGsonBuilder().disableHtmlEscaping().create());
        view.setRenderedAttributes(renderedAttributes);
    }

}
