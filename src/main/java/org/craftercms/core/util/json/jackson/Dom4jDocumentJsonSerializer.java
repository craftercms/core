/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
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

package org.craftercms.core.util.json.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * Custom Jackson serializer for {@link org.dom4j.Document}.
 *
 * The following are the conversion patterns used between XML and JSON:
 * <table>
 *     <caption>XML to JSON conversion patterns</caption>
 *     <tr>
 *         <th>XML</th>
 *         <th>JSON</th>
 *         <th>Access</th>
 * </tr>
 * <tr><td>&lt;e/></td><td>                             "e": null                           </td><td>            o.e</td></tr>
 * <tr><td>&lt;e>text&lt;/e></td><td>                      "e": "text"                       </td><td>              o.e</td></tr>
 * <tr><td>&lt;e name="value" /> </td><td>              "e": { "name": "value" }               </td><td>         o.e["name"]</td></tr>
 * <tr><td>&lt;e name="value">text&lt;/e>  </td><td>       "e": { "name": "value", "text": "text" }    </td><td>    o.e["name"] o.e["text"]</td></tr>
 * <tr><td>&lt;e>&lt;a>text&lt;/a>&lt;b>text&lt;/b>&lt;/e> </td><td>   "e": { "a": "text", "b": "text" }      </td><td>         o.e.a o.e.b</td></tr>
 * <tr><td>&lt;e>&lt;a>text&lt;/a>&lt;a>text&lt;/a>&lt;/e></td><td>	"e": { "a": ["text", "text"] }            </td><td>      o.e.a[0] o.e.a[1]</td></tr>
 * <tr><td>&lt;e>text&lt;a>text&lt;/a>&lt;/e>      </td><td>     "e": { "text": "text", "a": "text" }         </td><td>   o.e["text"] o.e.a</td></tr>
 * <tr><td>&lt;e>text&lt;a>text&lt;/a>text&lt;/e>   </td><td>    "e": { "text": ["text", "text"], "a": "text" } &nbsp;&nbsp;</td><td> o.e["text"][0] o.e["text"][1] o.e.a</td></tr>
 * </table>
 * <br/>
 * <b>IMPORTANT:</b> XML Namespaces are ALWAYS ignored.
 *
 * @author avasquez
 */
public class Dom4jDocumentJsonSerializer extends JsonSerializer<Document> {

    public static final String ITEM_LIST_ATTRIBUTE_NAME = "item-list";
    public static final String NO_DEFAULT_ATTRIBUTE_NAME = "no-default";
    public static final String[] IGNORABLE_ATTRIBUTES = { ITEM_LIST_ATTRIBUTE_NAME, NO_DEFAULT_ATTRIBUTE_NAME };

    public static final String TEXT_JSON_KEY = "text";

    @Override
    public void serialize(Document doc, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName(doc.getRootElement().getName());

        elementToJson(doc.getRootElement(), jsonGenerator);

        jsonGenerator.writeEndObject();
    }

    @Override
    public Class<Document> handledType() {
        return Document.class;
    }

    @SuppressWarnings("unchecked")
    private void elementToJson(Element element, JsonGenerator jsonGenerator) throws IOException {
        boolean objectStarted = false;

        if (element.attributeCount() > 0) {
            List<Attribute> attributes = element.attributes();


            for (Attribute attribute : attributes) {
                if (!ArrayUtils.contains(IGNORABLE_ATTRIBUTES, attribute.getName())) {
                    if (!objectStarted) {
                        jsonGenerator.writeStartObject();
                        objectStarted = true;
                    }
                    jsonGenerator.writeStringField(attribute.getName(), attribute.getValue());
                }
            }
        }

        if (!element.hasContent()) {
            if (!objectStarted) {
                jsonGenerator.writeNull();
            }
        } else if (element.isTextOnly()) {
            if (!objectStarted) {
                jsonGenerator.writeString(element.getText());
            } else {
                jsonGenerator.writeStringField(TEXT_JSON_KEY, element.getText());
            }
        } else {
            if (!objectStarted) {
                jsonGenerator.writeStartObject();

                objectStarted = true;
            }

            if (element.hasMixedContent()) {
                List<String> textContent = getTextContentFromMixedContent(element);

                if (textContent.size() > 1) {
                    jsonGenerator.writeArrayFieldStart(TEXT_JSON_KEY);

                    for (String text : textContent) {
                        jsonGenerator.writeString(text);
                    }

                    jsonGenerator.writeEndArray();
                } else if (textContent.size() == 1) {
                    jsonGenerator.writeStringField(TEXT_JSON_KEY, textContent.get(0));
                }
            }

            boolean itemList = isItemList(element);
            Map<String, List<Element>> children = getChildren(element);

            for (Map.Entry<String, List<Element>> entry : children.entrySet()) {
                if (itemList || entry.getValue().size() > 1) {
                    jsonGenerator.writeArrayFieldStart(entry.getKey());

                    for (Element child : entry.getValue()) {
                        elementToJson(child, jsonGenerator);
                    }

                    jsonGenerator.writeEndArray();
                } else {
                    jsonGenerator.writeFieldName(entry.getKey());

                    elementToJson(entry.getValue().get(0), jsonGenerator);
                }
            }
        }

        if (objectStarted) {
            jsonGenerator.writeEndObject();
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> getTextContentFromMixedContent(Element element) {
        List<Node> content = element.content();
        List<String> textContent = new ArrayList<>();

        for (Node node : content) {
            if (node.getNodeType() == Node.TEXT_NODE) {
                String text = node.getText();
                if (StringUtils.isNotBlank(text)) {
                    textContent.add(text);
                }
            }
        }

        return textContent;
    }

    @SuppressWarnings("unchecked")
    private Map<String, List<Element>> getChildren(Element element) {
        Map<String, List<Element>> groupedChildren = new LinkedHashMap<>();
        List<Element> children = element.elements();

        for (Element child : children) {
            if (groupedChildren.containsKey(child.getName())) {
                groupedChildren.get(child.getName()).add(child);
            } else {
                List<Element> elements = new ArrayList<>();
                elements.add(child);

                groupedChildren.put(child.getName(), elements);
            }
        }

        return groupedChildren;
    }

    private boolean isItemList(Element element) {
        return BooleanUtils.toBoolean(element.attributeValue(ITEM_LIST_ATTRIBUTE_NAME));
    }

}
