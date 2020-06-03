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
package org.craftercms.core.xml.mergers.impl.strategies;

import java.util.List;

import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.xml.mergers.MergeableDescriptor;
import org.dom4j.Document;

/**
 * Implementation of {@link AbstractInheritFromHierarchyMergeStrategy} that delegates to several other
 * <code>AbstractInheritFromHierarchyMergeStrategy</code>s to determine the descriptors to "inherit" from upper
 * levels in the folder hierarchy.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public class CompositeInheritFromHierarchyMergeStrategy extends AbstractInheritFromHierarchyMergeStrategy {

    private List<AbstractInheritFromHierarchyMergeStrategy> strategies;

    public CompositeInheritFromHierarchyMergeStrategy(List<AbstractInheritFromHierarchyMergeStrategy> strategies) {
        this.strategies = strategies;
    }

    @Override
    protected void addInheritedDescriptorsInFolder(Context context, CachingOptions cachingOptions,
                                                   List<MergeableDescriptor> inheritedDescriptors, String folder,
                                                   String mainDescriptorUrl, Document mainDescriptorDom) {
        for (AbstractInheritFromHierarchyMergeStrategy strategy : strategies) {
            strategy.addInheritedDescriptorsInFolder(context, cachingOptions, inheritedDescriptors, folder,
                                                     mainDescriptorUrl, mainDescriptorDom);
        }
    }

}
