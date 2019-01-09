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
package org.craftercms.core.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;


/**
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public class XmlUtils {

    /**
     * Executes an XPath query to retrieve an object. Normally, if the XPath result doesn't have a result, an
     * empty collection is returned. This object checks that case and returns null accordingly.
     */
    public static Object selectObject(Node node, String xpathQuery) {
        Object result = node.selectObject(xpathQuery);
        if (result != null && result instanceof Collection && ((Collection) result).isEmpty()) {
            return null;
        } else {
            return result;
        }
    }

    /**
     * Executes the specified XPath query as a single node query, returning the text value of the resulting single node.
     */
    public static String selectSingleNodeValue(Node node, String xpathQuery) {
        Node resultNode = node.selectSingleNode(xpathQuery);
        if (resultNode != null) {
            return resultNode.getText();
        } else {
            return null;
        }
    }

    /**
     * Executes the specified namespace aware XPath query as a single node query,
     * returning the text value of the resulting single node.
     */
    public static String selectSingleNodeValue(Node node, String xpathQuery, Map<String, String> namespaceUris) {
        Node resultNode = selectSingleNode(node, xpathQuery, namespaceUris);
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
    @SuppressWarnings("unchecked")
    public static List<String> selectNodeValues(Node node, String xpathQuery) {
        List<Node> resultNodes = node.selectNodes(xpathQuery);

        return extractNodeValues(resultNodes);
    }

    /**
     * Executes the specified namespace aware XPath query as a multiple node query,
     * returning the text values of the resulting list of
     * nodes.
     */
    public static List<String> selectNodeValues(Node node, String xpathQuery, Map<String, String> namespaceUris) {
        List<Node> resultNodes = selectNodes(node, xpathQuery, namespaceUris);

        return extractNodeValues(resultNodes);
    }

    /**
     * Executes the specified namespace aware XPath query as a single node query, returning the resulting single node.
     */
    public static Node selectSingleNode(Node node, String xpathQuery, Map<String, String> namespaceUris) {
        XPath xpath = DocumentHelper.createXPath(xpathQuery);
        xpath.setNamespaceURIs(namespaceUris);

        return xpath.selectSingleNode(node);
    }

    /**
     * Executes the specified namespace aware XPath query as a multiple node query, returning the resulting list of nodes.
     */
    @SuppressWarnings("unchecked")
    public static List<Node> selectNodes(Node node, String xpathQuery, Map<String, String> namespaceUris) {
        XPath xpath = DocumentHelper.createXPath(xpathQuery);
        xpath.setNamespaceURIs(namespaceUris);

        return xpath.selectNodes(node);
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

    private static List<String> extractNodeValues(List<Node> nodes) {
        if (CollectionUtils.isNotEmpty(nodes)) {
            List<String> nodeValues = new ArrayList<String>(nodes.size());
            for (Node resultNode : nodes) {
                nodeValues.add(resultNode.getText());
            }

            return nodeValues;
        } else {
            return Collections.emptyList();
        }
    }

}
