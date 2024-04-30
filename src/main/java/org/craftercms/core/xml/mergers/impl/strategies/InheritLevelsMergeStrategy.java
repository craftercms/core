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
package org.craftercms.core.xml.mergers.impl.strategies;

import java.util.List;

import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.xml.mergers.MergeableDescriptor;
import org.dom4j.Document;

/**
 * Implementation of {@link AbstractInheritFromHierarchyMergeStrategy}. Descriptors "inherited" are level descriptors
 * in upper folders in the hierarchy.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public class InheritLevelsMergeStrategy extends AbstractInheritFromHierarchyMergeStrategy {

    protected String levelDescriptorFileName;

    public InheritLevelsMergeStrategy(String levelDescriptorFileName) {
        this.levelDescriptorFileName = levelDescriptorFileName;
    }

    @Override
    protected void addInheritedDescriptorsInFolder(Context context, CachingOptions cachingOptions,
                                                   List<MergeableDescriptor> inheritedDescriptors, String folder,
                                                   String mainDescriptorUrl, Document mainDescriptorDom) {
        inheritedDescriptors.add(new MergeableDescriptor(folder + '/' + levelDescriptorFileName, true));
    }

}
