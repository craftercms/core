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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craftercms.core.exception.PathNotFoundException;
import org.craftercms.core.exception.XmlMergeException;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.util.url.ContentBundleUrl;
import org.craftercms.core.util.url.ContentBundleUrlParser;
import org.craftercms.core.xml.mergers.DescriptorMergeStrategy;
import org.craftercms.core.xml.mergers.DescriptorMergeStrategyResolver;
import org.craftercms.core.xml.mergers.MergeableDescriptor;
import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Required;

/**
 * {@link DescriptorMergeStrategy} that returns the level descriptors in the hierarchy and the base descriptors
 * when some of the parent folders are part of a content bundle (which is similar to a resource bundle, but instead
 * of simple properties, a content bundle is formed by XML content (descriptors)). E.g., for a descriptor path
 * <code>folder1/folder2_es/file.xml</code>, the descriptor <code>folder1/folder2/file.xml</code> is returned for
 * the merge.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public class ContentBundleMergeStrategy implements DescriptorMergeStrategy {

    private static final Log logger = LogFactory.getLog(DescriptorMergeStrategy.class);

    private ContentBundleUrlParser urlParser;
    private String baseDelimiter;
    private DescriptorMergeStrategyResolver baseMergeStrategyResolver;
    private DescriptorMergeStrategy regularMergeStrategy;

    @Required
    public void setUrlParser(ContentBundleUrlParser urlParser) {
        this.urlParser = urlParser;
    }

    @Required
    public void setBaseDelimiter(String baseDelimiter) {
        this.baseDelimiter = baseDelimiter;
    }

    @Required
    public void setBaseMergeStrategyResolver(DescriptorMergeStrategyResolver baseMergeStrategyResolver) {
        this.baseMergeStrategyResolver = baseMergeStrategyResolver;
    }

    @Required
    public void setRegularMergeStrategy(DescriptorMergeStrategy regularMergeStrategy) {
        this.regularMergeStrategy = regularMergeStrategy;
    }

    public List<MergeableDescriptor> getDescriptors(Context context, CachingOptions cachingOptions, String primaryDescriptorUrl)
            throws XmlMergeException {
        return getDescriptors(context, cachingOptions, primaryDescriptorUrl, false);
    }

    public List<MergeableDescriptor> getDescriptors(Context context, CachingOptions cachingOptions, String primaryDescriptorUrl,
                                                    boolean primaryDescriptorOptional)
            throws XmlMergeException {
        List<MergeableDescriptor> descriptors = new ArrayList<MergeableDescriptor>();
        List<MergeableDescriptor> tmp;

        ContentBundleUrl parsedUrl = urlParser.getContentBundleUrl(primaryDescriptorUrl);
        String prefix = parsedUrl.getPrefix(); // prefix = folder1/
        String baseNameAndExtensionToken = parsedUrl.getBaseNameAndExtensionToken(); // baseNameAndExtensionToken = folder2_es
        String suffix = parsedUrl.getSuffix(); // suffix = /file.xml

        // If the prefix is the same length as the initial URI, ignore, otherwise process
        if ( prefix.length() < primaryDescriptorUrl.length() ) {
            // Get the index of the delimiter that separates the base name from the extension token (the _ in
            // folder2_es).
            String baseName = baseNameAndExtensionToken;
            int delimiterIdx = baseName.lastIndexOf(baseDelimiter);
            boolean baseFound = false;

            while (delimiterIdx > 0 && !baseFound) {
                baseName = baseName.substring(0, delimiterIdx); // baseName = folder2
                String baseDescriptor = prefix + baseName + suffix; // baseDescriptor = folder1/folder2/file.xml
                Document baseDescriptorDom = null;

                try {
                    baseDescriptorDom = getDescriptorDom(context, cachingOptions, baseDescriptor);
                    baseFound = true;
                } catch (PathNotFoundException e) {
                    if ( logger.isDebugEnabled() ) {
                        logger.debug("No base descriptor " + baseDescriptor + " was found");
                    }
                }

                if ( baseFound ) {
                    // This can recurse if the selected strategy is also an ContentBundleMergeStrategy and the base
                    // descriptor path has more families.
                    DescriptorMergeStrategy baseMergeStrategy = baseMergeStrategyResolver.getStrategy(baseDescriptor, baseDescriptorDom);
                    if ( baseMergeStrategy == null ) {
                        throw new XmlMergeException("No merge strategy for descriptor " + baseDescriptor);
                    }

                    tmp = baseMergeStrategy.getDescriptors(context, cachingOptions, baseDescriptor, true);

                    descriptors.addAll(tmp);
                } else {
                    delimiterIdx = baseName.lastIndexOf(baseDelimiter);
                }
            }

            // Keep only after the prefix (folder2_es/file.xml)
            String noPrefix = primaryDescriptorUrl.substring(prefix.length(), primaryDescriptorUrl.length());
            // Add all level descriptors after the prefix using the regular strategy (this is up to us/part of our spec)
            tmp = regularMergeStrategy.getDescriptors(context, cachingOptions, noPrefix, primaryDescriptorOptional);

            // Add the stem back since the above won't include it and add the descriptor file to the results only
            // if it's not already in the results.
            for (MergeableDescriptor descriptor : tmp) {
                descriptor.setUrl(prefix + descriptor.getUrl());
                if ( !descriptors.contains(descriptor) ) {
                    descriptors.add(descriptor);
                }
            }
        } else {
            descriptors.add(new MergeableDescriptor(primaryDescriptorUrl, primaryDescriptorOptional));
        }

        if ( logger.isDebugEnabled() ) {
            logger.debug("Final merge list for " + primaryDescriptorUrl + ": " + descriptors);
        }

        return descriptors;
    }

    protected Document getDescriptorDom(Context context, CachingOptions cachingOptions, String url) {
        return context.getStoreAdapter().getItem(context, cachingOptions, url, true).getDescriptorDom();
    }

}
