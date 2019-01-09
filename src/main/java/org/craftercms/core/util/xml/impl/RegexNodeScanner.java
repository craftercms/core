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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.craftercms.core.util.xml.NodeScanner;
import org.dom4j.Attribute;
import org.dom4j.CDATA;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.Text;
import org.dom4j.VisitorSupport;
import org.springframework.beans.factory.annotation.Required;

/**
 * {@link NodeScanner} implementation that scans the document to find nodes that match certain regex patterns.
 *
 * @author Alfonso Vásquez
 */
public class RegexNodeScanner implements NodeScanner {

    protected Pattern[] patterns;
    protected boolean matchEntireNodeText;

    public RegexNodeScanner() {
        matchEntireNodeText = true;
    }

    @Required
    public void setPatterns(Pattern... patterns) {
        this.patterns = patterns;
    }

    public void setMatchEntireNodeText(boolean matchEntireNodeText) {
        this.matchEntireNodeText = matchEntireNodeText;
    }

    @Override
    public List<Node> scan(Document document) {
        List<Node> matchingNodes = new ArrayList<Node>();

        for (Pattern pattern : patterns) {
            document.accept(new RegexMatcherVisitor(matchingNodes, pattern));
        }

        return matchingNodes;
    }

    protected class RegexMatcherVisitor extends VisitorSupport {

        protected List<Node> matchingNodes;
        protected Pattern pattern;

        public RegexMatcherVisitor(List<Node> matchingNodes, Pattern pattern) {
            this.matchingNodes = matchingNodes;
            this.pattern = pattern;
        }

        @Override
        public void visit(Text text) {
            if (matchNodeText(text)) {
                matchingNodes.add(text);
            }
        }

        @Override
        public void visit(CDATA cdata) {
            if (matchNodeText(cdata)) {
                matchingNodes.add(cdata);
            }
        }

        @Override
        public void visit(Attribute attribute) {
            if (matchNodeText(attribute)) {
                matchingNodes.add(attribute);
            }
        }

        protected boolean matchNodeText(Node node) {
            String text = node.getText();
            Matcher matcher = pattern.matcher(text);

            if (matchEntireNodeText) {
                return matcher.matches();
            } else {
                return matcher.find();
            }
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

        RegexNodeScanner that = (RegexNodeScanner)o;

        if (matchEntireNodeText != that.matchEntireNodeText) {
            return false;
        }
        if (!patterns.equals(that.patterns)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = patterns.hashCode();
        result = 31 * result + (matchEntireNodeText? 1: 0);
        return result;
    }

}
