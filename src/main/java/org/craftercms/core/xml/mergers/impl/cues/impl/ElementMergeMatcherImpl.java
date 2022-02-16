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
package org.craftercms.core.xml.mergers.impl.cues.impl;

import org.craftercms.core.xml.mergers.impl.cues.ElementMergeMatcher;
import org.dom4j.Element;
import org.dom4j.QName;
import org.springframework.beans.factory.annotation.Required;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class ElementMergeMatcherImpl implements ElementMergeMatcher {

    protected QName idAttributeName;

    @Required
    public void setIdAttributeName(QName idAttributeName) {
        this.idAttributeName = idAttributeName;
    }

    @Override
    public boolean matchForMerge(Element parent, Element child) {
        String parentId = parent.attributeValue(idAttributeName);
        String childId = child.attributeValue(idAttributeName);

        if (parentId == null && childId == null) {
            return parent.getQualifiedName().equals(child.getQualifiedName());
        } else if (parentId != null) {
            return parentId.equals(childId);
        } else {
            return false;
        }
    }

}
