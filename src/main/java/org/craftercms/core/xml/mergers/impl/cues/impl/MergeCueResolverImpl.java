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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.craftercms.core.xml.mergers.impl.cues.MergeCue;
import org.craftercms.core.xml.mergers.impl.cues.MergeCueContext;
import org.craftercms.core.xml.mergers.impl.cues.MergeCueResolver;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.QName;
import org.springframework.beans.factory.annotation.Required;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class MergeCueResolverImpl implements MergeCueResolver {

    protected Map<QName, MergeCue> parentMergeCues;
    protected Map<QName, MergeCue> childMergeCues;
    protected MergeCue defaultParentMergeCue;
    protected MergeCue defaultChildMergeCue;

    @Required
    public void setParentMergeCues(Map<QName, MergeCue> parentMergeCues) {
        this.parentMergeCues = parentMergeCues;
    }

    @Required
    public void setChildMergeCues(Map<QName, MergeCue> childMergeCues) {
        this.childMergeCues = childMergeCues;
    }

    @Required
    public void setDefaultParentMergeCue(MergeCue defaultParentMergeCue) {
        this.defaultParentMergeCue = defaultParentMergeCue;
    }

    @Required
    public void setDefaultChildMergeCue(MergeCue defaultChildMergeCue) {
        this.defaultChildMergeCue = defaultChildMergeCue;
    }

    @Override
    public MergeCueContext getMergeCue(Element parent, Element child) {
        MergeCue parentMergeCue;
        MergeCue childMergeCue;

        Attribute parentMergeCueAttribute = getMergeCueAttribute(parent, parentMergeCues);
        if (parentMergeCueAttribute != null) {
            parentMergeCue = parentMergeCues.get(parentMergeCueAttribute.getQName());
        } else {
            parentMergeCue = defaultParentMergeCue;
        }

        Attribute childMergeCueAttribute = getMergeCueAttribute(child, childMergeCues);
        if (childMergeCueAttribute != null) {
            childMergeCue = childMergeCues.get(childMergeCueAttribute.getQName());
        } else {
            childMergeCue = defaultChildMergeCue;
        }

        MergeCue chosenMergeCue;
        Map<String, String> mergeCueParams;

        if (parentMergeCue.getPriority() > childMergeCue.getPriority()) {
            chosenMergeCue = parentMergeCue;

            if (parentMergeCueAttribute != null) {
                mergeCueParams = getMergeCueParams(parent, parentMergeCueAttribute);
            } else {
                mergeCueParams = Collections.emptyMap();
            }
        } else {
            chosenMergeCue = childMergeCue;

            if (childMergeCueAttribute != null) {
                mergeCueParams = getMergeCueParams(child, childMergeCueAttribute);
            } else {
                mergeCueParams = Collections.emptyMap();
            }
        }

        return new MergeCueContext(chosenMergeCue, parent, child, mergeCueParams);
    }

    @SuppressWarnings("unchecked")
    protected Attribute getMergeCueAttribute(Element element, Map<QName, MergeCue> mergeCues) {
        List<Attribute> attributes = element.attributes();
        for (Iterator<Attribute> i = attributes.iterator(); i.hasNext(); ) {
            Attribute attribute = i.next();
            if (mergeCues.containsKey(attribute.getQName())) {
                i.remove();

                return attribute;
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    protected Map<String, String> getMergeCueParams(Element element, Attribute mergeCueAttribute) {
        Map<String, String> params = new HashMap<String, String>();
        String paramsPrefix = mergeCueAttribute.getQualifiedName() + "-";
        List<Attribute> attributes = element.attributes();

        for (Iterator<Attribute> i = attributes.iterator(); i.hasNext(); ) {
            Attribute attribute = i.next();
            String attributeQualifiedName = attribute.getQualifiedName();
            if (attributeQualifiedName.startsWith(paramsPrefix)) {
                i.remove();

                String paramName = attributeQualifiedName.substring(paramsPrefix.length());
                String paramValue = attribute.getValue();

                params.put(paramName, paramValue);
            }
        }

        return params;
    }

}
