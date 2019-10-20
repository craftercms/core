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
package org.craftercms.core.xml.mergers.impl.strategies;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.craftercms.core.exception.XmlMergeException;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.util.ContentStoreUtils;
import org.craftercms.core.xml.mergers.DescriptorMergeStrategy;
import org.craftercms.core.xml.mergers.MergeableDescriptor;
import org.dom4j.Document;

/**
 * Abstract {@link org.craftercms.core.xml.mergers.DescriptorMergeStrategy} that defines the base code for strategies
 * that decide which descriptors to "inherit" from upper levels in the folder hierarchy.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public abstract class AbstractInheritFromHierarchyMergeStrategy implements DescriptorMergeStrategy {

    protected String[] baseFolders;

    public void setBaseFolders(String[] baseFolders) {
        for (int i = 0; i < baseFolders.length; i++) {
            baseFolders[i] = ContentStoreUtils.normalizePath(baseFolders[i]);
        }

        this.baseFolders = baseFolders;
    }

    @Override
    public List<MergeableDescriptor> getDescriptors(Context context, CachingOptions cachingOptions,
                                                    String mainDescriptorUrl, Document mainDescriptorDom)
        throws XmlMergeException {
        return getDescriptors(context, cachingOptions, mainDescriptorUrl, mainDescriptorDom, false);
    }

    @Override
    public List<MergeableDescriptor> getDescriptors(Context context, CachingOptions cachingOptions,
                                                    String mainDescriptorUrl, Document mainDescriptorDom,
                                                    boolean mainDescriptorOptional) throws XmlMergeException {
        List<MergeableDescriptor> descriptors = new ArrayList<>();

        int k;

        if (ArrayUtils.isNotEmpty(baseFolders)) {
            // If base folders are specified, start looking for descriptors after the base folders.
            k = getIndexAfterBaseFolder(mainDescriptorUrl);
        } else {
            // If the url is absolute (starts with '/'), the descriptors included will start from root (i.e. if url is
            // /folder/file.xml, first ones will start at '/'). If it's relative (doesn't start with '/), the descriptors
            // included start from the first folder in the url (i.e., if url is folder/file.xml, first ones will start at
            // folder/).
            k = mainDescriptorUrl.indexOf('/');
        }

        while (k >= 0) {
            String folder = mainDescriptorUrl.substring(0, k);

            addInheritedDescriptorsInFolder(context, cachingOptions, descriptors, folder, mainDescriptorUrl,
                                            mainDescriptorDom);

            k = mainDescriptorUrl.indexOf('/', ++k);
        }

        descriptors.add(new MergeableDescriptor(mainDescriptorUrl, mainDescriptorOptional));

        return descriptors;
    }

    protected int getIndexAfterBaseFolder(String url) {
        for (String baseFolder : baseFolders) {
            if (url.startsWith(baseFolder)) {
                return baseFolder.length();
            }
        }

        return -1;
    }

    protected abstract void addInheritedDescriptorsInFolder(Context context, CachingOptions cachingOptions,
                                                            List<MergeableDescriptor> inheritedDescriptors,
                                                            String folder, String mainDescriptorUrl,
                                                            Document mainDescriptorDom);

}
