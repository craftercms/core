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
package org.craftercms.core.xml.mergers.impl.cues.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.craftercms.core.exception.XmlMergeException;
import org.craftercms.core.xml.mergers.impl.cues.ElementMergeMatcher;
import org.craftercms.core.xml.mergers.impl.cues.MergeCueContext;
import org.craftercms.core.xml.mergers.impl.cues.MergeCueResolver;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Required;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class MergeParentAndChildMergeCue extends AbstractMergeCue {

    protected ElementMergeMatcher elementMergeMatcher;
    protected MergeCueResolver mergeCueResolver;
    protected String mergeOrderParamName;
    protected String defaultMergeOrder;

    @Required
    public void setElementMergeMatcher(ElementMergeMatcher elementMergeMatcher) {
        this.elementMergeMatcher = elementMergeMatcher;
    }

    @Required
    public void setMergeCueResolver(MergeCueResolver mergeCueResolver) {
        this.mergeCueResolver = mergeCueResolver;
    }

    @Required
    public void setMergeOrderParamName(String mergeOrderParamName) {
        this.mergeOrderParamName = mergeOrderParamName;
    }

    @Required
    public void setDefaultMergeOrder(String defaultMergeOrder) {
        this.defaultMergeOrder = defaultMergeOrder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Element merge(Element parent, Element child, Map<String, String> params) throws XmlMergeException {
        Element merged = DocumentHelper.createElement(child.getQName());
        org.craftercms.core.util.CollectionUtils.move(child.attributes(), merged.attributes());

        if (parent.isTextOnly() && child.isTextOnly()) {
            String parentText = parent.getText();
            String childText = child.getText();

            if (getMergeOrder(params).equalsIgnoreCase("after")) {
                merged.setText(parentText + childText);
            } else {
                merged.setText(childText + parentText);
            }
        } else {
            List<Element> parentElements = parent.elements();
            List<Element> childElements = child.elements();
            List<Element> mergedElements = merged.elements();

            if (CollectionUtils.isNotEmpty(parentElements) && CollectionUtils.isNotEmpty(childElements)) {
                for (Iterator<Element> i = parentElements.iterator(); i.hasNext(); ) {
                    Element parentElement = i.next();
                    boolean elementsMerged = false;

                    for (Iterator<Element> j = childElements.iterator(); !elementsMerged && j.hasNext(); ) {
                        Element childElement = j.next();
                        if (elementMergeMatcher.matchForMerge(parentElement, childElement)) {
                            MergeCueContext context = mergeCueResolver.getMergeCue(parentElement, childElement);
                            if (context != null) {
                                i.remove();
                                j.remove();

                                Element mergedElement = context.doMerge();
                                mergedElements.add(mergedElement);

                                elementsMerged = true;
                            } else {
                                throw new XmlMergeException("No merge cue was resolved for matching elements " +
                                                            parentElement + " (parent) and " + childElement +
                                                            " (child)");
                            }
                        }
                    }
                }
            }

            if (getMergeOrder(params).equalsIgnoreCase("after")) {
                org.craftercms.core.util.CollectionUtils.move(parentElements, mergedElements);
                org.craftercms.core.util.CollectionUtils.move(childElements, mergedElements);
            } else {
                org.craftercms.core.util.CollectionUtils.move(childElements, mergedElements);
                org.craftercms.core.util.CollectionUtils.move(parentElements, mergedElements);
            }
        }

        return merged;
    }

    protected String getMergeOrder(Map<String, String> mergeParams) throws XmlMergeException {
        String mergeOrder = mergeParams.get(mergeOrderParamName);
        if (mergeOrder != null) {
            return mergeOrder;
        } else {
            return defaultMergeOrder;
        }
    }

}
