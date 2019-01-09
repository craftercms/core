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

import org.craftercms.core.exception.XmlMergeException;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.service.Item;
import org.craftercms.core.xml.mergers.DescriptorMergeStrategy;
import org.craftercms.core.xml.mergers.DescriptorMergeStrategyResolver;
import org.craftercms.core.xml.mergers.MergeableDescriptor;
import org.dom4j.Document;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Required;

/**
 * Implementation of {@link DescriptorMergeStrategy} that merges the descriptor with an explicitly named parent. The
 * parent is also checked for any other descriptors to merge.
 *
 * @author avasquez
 */
public class ExplicitParentMergeStrategy implements DescriptorMergeStrategy {

    private DescriptorMergeStrategyResolver mergeStrategyResolver;
    private String parentDescriptorElementXPathQuery;

    @Required
    public void setMergeStrategyResolver(DescriptorMergeStrategyResolver mergeStrategyResolver) {
        this.mergeStrategyResolver = mergeStrategyResolver;
    }

    @Required
    public void setParentDescriptorElementXPathQuery(String parentDescriptorElementXPathQuery) {
        this.parentDescriptorElementXPathQuery = parentDescriptorElementXPathQuery;
    }

    @Override
    public List<MergeableDescriptor> getDescriptors(Context context, CachingOptions cachingOptions,
                                                    String mainDescriptorUrl,
                                                    Document mainDescriptorDom) throws XmlMergeException {
        return getDescriptors(context, cachingOptions, mainDescriptorUrl, mainDescriptorDom, false);
    }

    @Override
    public List<MergeableDescriptor> getDescriptors(Context context, CachingOptions cachingOptions,
                                                    String mainDescriptorUrl, Document mainDescriptorDom,
                                                    boolean mainDescriptorOptional) throws XmlMergeException {
        Node parentDescriptorElem = mainDescriptorDom.selectSingleNode(parentDescriptorElementXPathQuery);
        List<MergeableDescriptor> descriptors = new ArrayList<>();

        if (parentDescriptorElem != null) {
            String parentDescriptorUrl = parentDescriptorElem.getText();
            Document parentDescriptorDom = getDescriptorDom(context, cachingOptions, parentDescriptorUrl);

            if (parentDescriptorDom != null) {
                DescriptorMergeStrategy parentMergeStrategy = mergeStrategyResolver.getStrategy(parentDescriptorUrl,
                                                                                                parentDescriptorDom);
                if (parentMergeStrategy != null) {
                    descriptors.addAll(parentMergeStrategy.getDescriptors(context, cachingOptions, parentDescriptorUrl,
                                                                          parentDescriptorDom, true));
                }
            } else {
                throw new XmlMergeException("No parent descriptor found at " + parentDescriptorUrl);
            }
        } else {
            throw new XmlMergeException("No parent descriptor element specified");
        }

        descriptors.add(new MergeableDescriptor(mainDescriptorUrl, mainDescriptorOptional));

        return descriptors;
    }

    protected Document getDescriptorDom(Context context, CachingOptions cachingOptions, String url) {
        Item item = context.getStoreAdapter().findItem(context, cachingOptions, url, true);
        if (item != null) {
            return item.getDescriptorDom();
        } else {
            return null;
        }
    }

}
