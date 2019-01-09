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
package org.craftercms.core.xml.mergers.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.craftercms.core.exception.XmlMergeException;
import org.craftercms.core.xml.mergers.DescriptorMerger;
import org.craftercms.core.xml.mergers.impl.cues.MergeCue;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of {@link org.craftercms.core.xml.mergers.DescriptorMerger}.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public class DescriptorMergerImpl implements DescriptorMerger {

    private MergeCue initialMergeCue;
    private Map<String, String> initialMergeCueParams;

    public DescriptorMergerImpl() {
        initialMergeCueParams = Collections.emptyMap();
    }

    @Required
    public void setInitialMergeCue(MergeCue initialMergeCue) {
        this.initialMergeCue = initialMergeCue;
    }

    public void setInitialMergeCueParams(Map<String, String> initialMergeCueParams) {
        this.initialMergeCueParams = initialMergeCueParams;
    }

    @Override
    public Document merge(List<Document> descriptorsToMerge) throws XmlMergeException {
        Document merged = DocumentHelper.createDocument();

        if (CollectionUtils.isNotEmpty(descriptorsToMerge)) {
            Element mergedRoot = descriptorsToMerge.get(0).getRootElement().createCopy();

            for (Iterator<Document> i = descriptorsToMerge.listIterator(1); i.hasNext(); ) {
                Element descriptorRoot = i.next().getRootElement().createCopy();

                mergedRoot = initialMergeCue.merge(mergedRoot, descriptorRoot, initialMergeCueParams);
            }

            merged.add(mergedRoot);
        }

        return merged;
    }

}
