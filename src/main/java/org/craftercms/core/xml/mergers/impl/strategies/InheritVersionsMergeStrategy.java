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
package org.craftercms.core.xml.mergers.impl.strategies;

import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.xml.mergers.MergeableDescriptor;

/**
 * Implementation of {@link AbstractInheritFromHierarchyMergeStrategy}. Descriptors "inherited" are versions of the
 * same file in upper folders in the hierarchy.
 *
 * @author Dejan Brkic
 * @author Michiel Verkaik
 * @author Alfonso Vásquez
 */
public class InheritVersionsMergeStrategy extends AbstractInheritFromHierarchyMergeStrategy {

    @Override
    protected void addInheritedDescriptorsInFolder(Context context, CachingOptions cachingOptions,
                                                   List<MergeableDescriptor> inheritedDescriptors, String folder,
                                                   String primaryDescriptorUrl) {
        String primaryDescriptorFilename = FilenameUtils.getName(primaryDescriptorUrl);
        String inheritedDescriptorUrl = folder + '/' + primaryDescriptorFilename;

        // Avoid adding the primary descriptor twice.
        if ( !inheritedDescriptorUrl.equals(primaryDescriptorUrl) ) {
            inheritedDescriptors.add(new MergeableDescriptor(inheritedDescriptorUrl, true));
        }
    }

}
