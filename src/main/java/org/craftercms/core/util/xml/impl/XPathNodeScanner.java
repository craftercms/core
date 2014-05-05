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
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class XPathNodeScanner implements NodeScanner {

    protected String[] xPathQueries;

    @Required
    public void setXPathQueries(String... xPathQueries) {
        this.xPathQueries = xPathQueries;
    }

    @Override
    public List<Node> scan(Document document) throws XmlException {
        try {
            List<Node> nodes = new ArrayList<Node>();

            for (String xPathQuery : xPathQueries) {
                List<Node> queryResult = document.selectNodes(xPathQuery);
                if (CollectionUtils.isNotEmpty(queryResult)) {
                    nodes.addAll(queryResult);
                }
            }

            return nodes;
        } catch (Exception e) {
            throw new XmlException(xPathQueries + " query failed", e);
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

        if (!xPathQueries.equals(that.xPathQueries)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return xPathQueries.hashCode();
    }

}
