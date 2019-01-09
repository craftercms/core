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
package org.craftercms.core.util.xml.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.craftercms.core.exception.XmlException;
import org.craftercms.core.util.xml.NodeScanner;
import org.dom4j.Document;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Required;

/**
 * {@link NodeScanner} implementation that scans the document by executing XPath queries.
 *
 * @author Alfonso Vásquez
 */
public class XPathNodeScanner implements NodeScanner {

    protected String[] xpathQueries;

    @Required
    public void setXPathQueries(String... xpathQueries) {
        this.xpathQueries = xpathQueries;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Node> scan(Document document) throws XmlException {
        try {
            List<Node> nodes = new ArrayList<Node>();

            for (String xpathQuery : xpathQueries) {
                List<Node> queryResult = document.selectNodes(xpathQuery);
                if (CollectionUtils.isNotEmpty(queryResult)) {
                    nodes.addAll(queryResult);
                }
            }

            return nodes;
        } catch (Exception e) {
            throw new XmlException(xpathQueries + " query failed", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        XPathNodeScanner that = (XPathNodeScanner)o;

        if (!xpathQueries.equals(that.xpathQueries)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return xpathQueries.hashCode();
    }

}
