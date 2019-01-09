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
package org.craftercms.core.xml.mergers;

import java.util.List;

import org.craftercms.core.exception.XmlMergeException;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.dom4j.Document;

/**
 * Strategy for merging descriptor files.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public interface DescriptorMergeStrategy {

    /**
     * Returns the list of descriptors in the order they should be merged, given the URI of the primary descriptor.
     *
     * @param context           the current context
     * @param cachingOptions    caching options in case you need access to items
     * @param mainDescriptorUrl the URL of the main descriptor
     * @param mainDescriptorDom the DOM of the main descriptor
     *
     * @return a list of {@link MergeableDescriptor}s representing the descriptors to merge
     */
    List<MergeableDescriptor> getDescriptors(Context context, CachingOptions cachingOptions,
                                             String mainDescriptorUrl, Document mainDescriptorDom)
        throws XmlMergeException;

    /**
     * Returns the list of descriptors in the order they should be merged, given the URI of the primary descriptor.
     *
     * @param context                   the current context
     * @param cachingOptions            caching options in case you need access to items
     * @param mainDescriptorUrl         the URL of the primary descriptor
     * @param mainDescriptorDom         the DOM of the main descriptor
     * @param mainDescriptorOptional    if the primary descriptor should be marked as optional
     *
     * @return a list of {@link MergeableDescriptor}s representing the descriptors to merge
     */
    List<MergeableDescriptor> getDescriptors(Context context, CachingOptions cachingOptions,
                                             String mainDescriptorUrl, Document mainDescriptorDom,
                                             boolean mainDescriptorOptional) throws XmlMergeException;

}
