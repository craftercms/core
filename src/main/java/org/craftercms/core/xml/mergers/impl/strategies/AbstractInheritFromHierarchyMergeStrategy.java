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

import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.xml.mergers.DescriptorMergeStrategy;
import org.craftercms.core.xml.mergers.MergeableDescriptor;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.xml.mergers.DescriptorMergeStrategy;
import org.craftercms.core.xml.mergers.MergeableDescriptor;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract {@link org.craftercms.core.xml.mergers.DescriptorMergeStrategy} that defines the base code for strategies that decide which descriptors
 * to "inherit" from upper levels in the folder hierarchy.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public abstract class AbstractInheritFromHierarchyMergeStrategy implements DescriptorMergeStrategy {

    public List<MergeableDescriptor> getDescriptors(Context context, CachingOptions cachingOptions, String primaryDescriptorUrl) {
        return getDescriptors(context, cachingOptions, primaryDescriptorUrl, false);
    }

    public List<MergeableDescriptor> getDescriptors(Context context, CachingOptions cachingOptions, String primaryDescriptorUrl,
                                                    boolean primaryDescriptorOptional) {
    	List<MergeableDescriptor> descriptors = new ArrayList<MergeableDescriptor>();

        // If the url is absolute (starts with '/'), the descriptors included will start from root (i.e. if url is
        // /folder/file.xml, first ones will start at '/'). If it's relative (doesn't start with '/), the descriptors
        // included start from the first folder in the url (i.e., if url is folder/file.xml, first ones will start at
        // folder/).
    	int k = primaryDescriptorUrl.indexOf('/');
    	while (k >= 0) {
            String folder = primaryDescriptorUrl.substring(0, k);

            addInheritedDescriptorsInFolder(context, cachingOptions, descriptors, folder, primaryDescriptorUrl);

            k = primaryDescriptorUrl.indexOf('/', ++k);
    	}

    	descriptors.add(new MergeableDescriptor(primaryDescriptorUrl, primaryDescriptorOptional));

    	return descriptors;
    }

    protected abstract void addInheritedDescriptorsInFolder(Context context, CachingOptions cachingOptions,
                                                            List<MergeableDescriptor> inheritedDescriptors, String folder,
                                                            String primaryDescriptorUrl);

}
