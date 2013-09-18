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

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public class XmlUtils {

    public static final String XML_ELEMENT_TEXT_JSON_KEY = "text";

    /**
     * Executes the specified XPath query as a single node query, returning the text value of the resulting single node.
     */
    public static String selectSingleNodeValue(Node node, String xPathQuery) {
        Node resultNode = node.selectSingleNode(xPathQuery);
        if (resultNode != null) {
            return resultNode.getText();
        } else {
            return null;
        }
    }

    /**
     * Executes the specified namespace aware XPath query as a single node query, returning the text value of the resulting single node.
     */
    public static String selectSingleNodeValue(Node node, String xPathQuery, Map<String, String> namespaceUris) {
        Node resultNode = selectSingleNode(node, xPathQuery, namespaceUris);
        if (resultNode != null) {
            return resultNode.getText();
        } else {
            return null;
        }
    }

    /**
     * Executes the specified XPath query as a multiple node query, returning the text values of the resulting list of
     * nodes.
     */
    public static List<String> selectNodeValues(Node node, String xPathQuery) {
        List<Node> resultNodes = node.selectNodes(xPathQuery);
        if (CollectionUtils.isNotEmpty(resultNodes)) {
            List<String> resultNodeValues = new ArrayList<String>(resultNodes.size());
            for (Node resultNode : resultNodes) {
                resultNodeValues.add(resultNode.getText());
            }

            return resultNodeValues;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Executes the specified namespace aware XPath query as a multiple node query, returning the text values of the resulting list of
     * nodes.
     */
    public static List<String> selectNodeValues(Node node, String xPathQuery, Map<String, String> namespaceUris) {
        List<Node> resultNodes = selectNodes(node, xPathQuery, namespaceUris);
        if (CollectionUtils.isNotEmpty(resultNodes)) {
            List<String> resultNodeValues = new ArrayList<String>(resultNodes.size());
            for (Node resultNode : resultNodes) {
                resultNodeValues.add(resultNode.getText());
            }

            return resultNodeValues;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Executes the specified namespace aware XPath query as a single node query, returning the resulting single node.
     */
    public static Node selectSingleNode(Node node, String xPathQuery, Map<String, String> namespaceUris) {
        XPath xPath = DocumentHelper.createXPath(xPathQuery);
        xPath.setNamespaceURIs(namespaceUris);

        return xPath.selectSingleNode(node);
    }

    /**
     * Executes the specified namespace aware XPath query as a multiple node query, returning the resulting list of nodes.
     */
    public static List<Node> selectNodes(Node node, String xPathQuery, Map<String, String> namespaceUris) {
        XPath xPath = DocumentHelper.createXPath(xPathQuery);
        xPath.setNamespaceURIs(namespaceUris);

        return xPath.selectNodes(node);
    }

    /**
     * Returns the given document as a XML string in a "pretty" format.
     *
     * @param document
     * @return the document as an XML string
     */
    public static String documentToPrettyString(Document document) {
		StringWriter stringWriter = new StringWriter();
        OutputFormat prettyPrintFormat = OutputFormat.createPrettyPrint();
        XMLWriter xmlWriter = new XMLWriter(stringWriter, prettyPrintFormat);

        try {
            xmlWriter.write(document);
        } catch (IOException e) {
            // Ignore, shouldn't happen.
        }

        return stringWriter.toString();
    }

    /**
     * Returns the given document as a JSON object.
     *
     * The following are the conversion patterns used between XML and JSON:
     *
     * XML                              JSON                                            Access
     * ---                              ----                                            ------
     * <e/>                             "e": null                                       o.e
     * <e>text</e>                      "e": "text"                                     o.e
     * <e name="value" />               "e": { "name": "value" }                        o.e["name"]
     * <e name="value">text</e>         "e": { "name": "value", "text": "text" }        o.e["name"] o.e["text"]
     * <e><a>text</a><b>text</b></e>    "e": { "a": "text", "b": "text" }               o.e.a o.e.b
     * <e><a>text</a><a>text</a></e>	"e": { "a": ["text", "text"] }                  o.e.a[0] o.e.a[1]
     * <e>text<a>text</a></e>           "e": { "text": "text", "a": "text" }            o.e["text"] o.e.a
     * <e>text<a>text</a>text</e>       "e": { "text": ["text", "text"], "a": "text" }  o.e["text"][0] o.e["text"][1] o.e.a
     *
     * <b>IMPORTANT:</b> XML Namespaces are ALWAYS ignored.
     *
     * @param document
     * @return the document as an JSON object
     */
    public static JsonElement documentToJson(Document document) {
        JsonObject json = new JsonObject();

        elementToJson(document.getRootElement(), json);

        return json;
    }

    private static void elementToJson(Element element, JsonObject parentJson) {
        JsonObject elementJson = null;

        if (element.attributeCount() > 0) {
            elementJson = new JsonObject();

            List<Attribute> attributes = element.attributes();
            for (Attribute attribute : attributes) {
                JsonUtils.setOrAccumulate(elementJson, attribute.getName(), new JsonPrimitive(attribute.getValue()));
            }
        }

        if (!element.hasContent()) {
            if (elementJson == null) {
                addElementTextToJson(parentJson, elementJson, element.getName(), null);
            }
        } else if (element.isTextOnly()) {
            addElementTextToJson(parentJson, elementJson, element.getName(), element.getText());
        } else {
            if (elementJson == null) {
                elementJson = new JsonObject();
            }

            if (element.hasMixedContent()) {
                List<String> textContent = getTextContentFromMixedContent(element);
                for (String text : textContent) {
                    addElementTextToJson(parentJson, elementJson, element.getName(), text);
                }
            }

            List<Element> children = element.elements();
            for (Element child : children) {
                elementToJson(child, elementJson);
            }
        }

        if (elementJson != null) {
            JsonUtils.setOrAccumulate(parentJson, element.getName(), elementJson);
        }
    }

    private static void addElementTextToJson(JsonObject parentJson, JsonObject elementJson, String elementName, String text) {
        JsonElement value;
        if (text != null) {
            value = new JsonPrimitive(text);
        } else {
            value = JsonNull.INSTANCE;
        }

        if (elementJson != null) {
            JsonUtils.setOrAccumulate(elementJson, XML_ELEMENT_TEXT_JSON_KEY, value);
        } else {
            JsonUtils.setOrAccumulate(parentJson, elementName, value);
        }
    }

    private static List<String> getTextContentFromMixedContent(Element element) {
        List<Node> content = element.content();
        List<String> textContent = new ArrayList<String>();

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

}
