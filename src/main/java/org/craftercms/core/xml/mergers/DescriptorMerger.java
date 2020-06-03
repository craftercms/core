/*
 * Copyright (C) 2007-2020 Crafter Software Corporation. All Rights Reserved.
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
package org.craftercms.core.xml.mergers;

import java.util.List;

import org.craftercms.core.exception.XmlMergeException;
import org.dom4j.Document;

/**
 * Merges a set of XML DOM descriptors into a new DOM.
 *
 * @author Alfonso Vásquez
 */
public interface DescriptorMerger {

    /**
     * Merges a set of XML DOM descriptors into a new DOM.
     *
     * @param descriptorsToMerge the XML DOMs of the descriptors to merge
     * @return the result of the merging
     * @throws XmlMergeException
     */
    Document merge(List<Document> descriptorsToMerge) throws XmlMergeException;

}
