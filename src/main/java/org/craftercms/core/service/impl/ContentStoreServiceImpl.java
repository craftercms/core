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
package org.craftercms.core.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craftercms.core.exception.AuthenticationException;
import org.craftercms.core.exception.CrafterException;
import org.craftercms.core.exception.InvalidContextException;
import org.craftercms.core.exception.InvalidScopeException;
import org.craftercms.core.exception.InvalidStoreTypeException;
import org.craftercms.core.exception.ItemProcessingException;
import org.craftercms.core.exception.PathNotFoundException;
import org.craftercms.core.exception.RootFolderNotFoundException;
import org.craftercms.core.exception.StoreException;
import org.craftercms.core.exception.XmlFileParseException;
import org.craftercms.core.exception.XmlMergeException;
import org.craftercms.core.processors.ItemProcessor;
import org.craftercms.core.processors.ItemProcessorResolver;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Content;
import org.craftercms.core.service.ContentStoreService;
import org.craftercms.core.service.Context;
import org.craftercms.core.service.Item;
import org.craftercms.core.service.ItemFilter;
import org.craftercms.core.service.Tree;
import org.craftercms.core.store.ContentStoreAdapter;
import org.craftercms.core.store.ContentStoreAdapterRegistry;
import org.craftercms.core.util.XmlUtils;
import org.craftercms.core.util.cache.impl.CachingAwareList;
import org.craftercms.core.xml.mergers.DescriptorMergeStrategy;
import org.craftercms.core.xml.mergers.DescriptorMergeStrategyResolver;
import org.craftercms.core.xml.mergers.DescriptorMerger;
import org.craftercms.core.xml.mergers.MergeableDescriptor;
import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of {@link org.craftercms.core.service.ContentStoreService}. Extends from
 * {@link AbstractCachedContentStoreService} to use caching.
 *
 * @author Alfonso Vásquez
 */
public class ContentStoreServiceImpl extends AbstractCachedContentStoreService {

    private static final Log logger = LogFactory.getLog(ContentStoreServiceImpl.class);
    /**
     * Registry of {@link ContentStoreAdapter}s.
     */
    protected ContentStoreAdapterRegistry storeAdapterRegistry;
    /**
     * Resolves the {@link org.craftercms.core.xml.mergers.DescriptorMergeStrategy} to use for a particular descriptor.
     */
    protected DescriptorMergeStrategyResolver mergeStrategyResolver;
    /**
     * Merges a bunch of descriptors
     */
    protected DescriptorMerger merger;
    /**
     * Resolves the {@link org.craftercms.core.processors.ItemProcessor} to use for a particular {@link Item}.
     */
    protected ItemProcessorResolver processorResolver;
    /**
     * Map of open {@link org.craftercms.core.service.Context}s
     */
    protected Map<String, Context> contexts;

    /**
     * Default constructor. Creates the map of open {@link Context}s.
     */
    public ContentStoreServiceImpl() {
        contexts = new ConcurrentHashMap<String, Context>();
    }

    /**
     * Registry of {@link ContentStoreAdapter}s.
     */
    @Required
    public void setStoreAdapterRegistry(ContentStoreAdapterRegistry storeAdapterRegistry) {
        this.storeAdapterRegistry = storeAdapterRegistry;
    }

    /**
     * Sets the {@link DescriptorMergeStrategyResolver}, which resolves the {@link org.craftercms.core.xml.mergers
     * .DescriptorMergeStrategy} to use for a particular
     * descriptor.
     */
    @Required
    public void setMergeStrategyResolver(DescriptorMergeStrategyResolver mergeStrategyResolver) {
        this.mergeStrategyResolver = mergeStrategyResolver;
    }

    /**
     * Sets the {@link DescriptorMerger}, which merges the primary descriptor with a list of other descriptors,
     * according to
     * the merge strategy.
     */
    @Required
    public void setMerger(DescriptorMerger merger) {
        this.merger = merger;
    }

    /**
     * Sets the {@link ItemProcessorResolver}, which resolves the {@link org.craftercms.core.processors
     * .ItemProcessor} to use for a particular {@link Item}.
     */
    @Required
    public void setProcessorResolver(ItemProcessorResolver processorResolver) {
        this.processorResolver = processorResolver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Context getContext(String contextId) {
        return contexts.get(contextId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Context getContext(String tag, String storeType, String rootFolderPath, boolean mergingOn,
                              boolean cacheOn, int maxAllowedItemsInCache, boolean ignoreHiddenFiles)
        throws InvalidStoreTypeException, RootFolderNotFoundException, StoreException, AuthenticationException {
        String id = createContextId(tag, storeType, rootFolderPath, cacheOn, maxAllowedItemsInCache, ignoreHiddenFiles);

        if (!contexts.containsKey(id)) {
            ContentStoreAdapter storeAdapter = storeAdapterRegistry.get(storeType);
            if (storeAdapter == null) {
                throw new InvalidStoreTypeException("No registered content store adapter for store type " + storeType);
            }

            Context context = storeAdapter.createContext(id, rootFolderPath, mergingOn, cacheOn,
                                                         maxAllowedItemsInCache, ignoreHiddenFiles);

            cacheTemplate.getCacheService().addScope(context);

            contexts.put(id, context);

            return context;
        } else {
            return contexts.get(id);
        }
    }

    @Override
    public boolean validate(Context context) throws StoreException, AuthenticationException {
        return context.getStoreAdapter().validate(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean destroyContext(Context context) throws InvalidContextException, StoreException, AuthenticationException {
        if (contexts.containsKey(context.getId())) {
            context.getStoreAdapter().destroyContext(context);

            cacheTemplate.getCacheService().removeScope(context);

            contexts.remove(context.getId());

            return true;
        } else {
            return false;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean doExists(Context context, CachingOptions cachingOptions, String url)
        throws InvalidContextException, PathNotFoundException, StoreException {
        return context.getStoreAdapter().exists(context, cachingOptions, StringUtils.prependIfMissing(url, "/"));
    }

    @Override
    public Content findContent(Context context, String url) throws InvalidContextException, StoreException {
        return findContent(context, null, url);
    }

    @Override
    public Content findContent(Context context, CachingOptions cachingOptions, String url)
        throws InvalidContextException, StoreException {
        return context.getStoreAdapter().findContent(context, cachingOptions, url);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Content getContent(Context context, String url) throws InvalidScopeException, PathNotFoundException,
        StoreException {
        return getContent(context, null, url);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Content getContent(Context context, CachingOptions cachingOptions, String url) throws InvalidScopeException,
        PathNotFoundException, StoreException {
        Content content = findContent(context, cachingOptions, url);
        if (content != null) {
            return content;
        } else {
            throw new PathNotFoundException("No file found at " + url);
        }
    }

    /**
     * Returns the content store item for the given url, returning null if not found.
     * <p/>
     * <p>After acquiring the item from the {@link ContentStoreAdapter}, the item's descriptor is merged (according
     * to its {@link org.craftercms.core.xml.mergers.DescriptorMergeStrategy}) with related descriptors, and the
     * final item is then processed.</p>
     */
    @Override
    protected Item doFindItem(Context context, CachingOptions cachingOptions, String url, ItemProcessor processor)
        throws InvalidContextException, XmlFileParseException, XmlMergeException, ItemProcessingException,
        StoreException {
        // Add a leading slash if not present at the beginning of the url. This is done because although the store
        // adapter normally ignores a leading slash, the merge strategies don't, and they need it to return the
        // correct set of descriptor files to merge (like all the impl of AbstractInheritFromHierarchyMergeStrategy).
        if (!url.startsWith("/")) {
            url = "/" + url;
        }

        Item item = context.getStoreAdapter().findItem(context, cachingOptions, url, true);
        if (item != null) {
            // Create a copy of the item, since it will be modified
            item = new Item(item);
            if (item.getDescriptorDom() != null) {
                item = doMerging(context, cachingOptions, item);
                item = doProcessing(context, cachingOptions, item, processor);
            } else {
                item = doProcessing(context, cachingOptions, item, processor);
            }
        }

        return item;
    }

    @Override
    protected List<Item> doFindChildren(Context context, CachingOptions cachingOptions, String url, ItemFilter filter,
                                        ItemProcessor processor) throws InvalidContextException,
        XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException {
        // Add a leading slash if not present at the beginning of the url. This is done because although the store
        // adapter normally ignores a leading slash, the merge strategies don't, and they need it to return the
        // correct set of descriptor files to merge (like all the impl of AbstractInheritFromHierarchyMergeStrategy).
        if (!url.startsWith("/")) {
            url = "/" + url;
        }

        return doFindChildren(context, cachingOptions, url, null, filter, processor);
    }

    @Override
    protected Tree doFindTree(Context context, CachingOptions cachingOptions, String url, int depth, ItemFilter filter,
                              ItemProcessor processor) throws InvalidContextException, XmlFileParseException,
        XmlMergeException, ItemProcessingException, StoreException {
        // Add a leading slash if not present at the beginning of the url. This is done because although the store
        // adapter normally ignores a leading slash, the merge strategies don't, and they need it to return the
        // correct set of descriptor files to merge (like all the impl of AbstractInheritFromHierarchyMergeStrategy).
        if (!url.startsWith("/")) {
            url = "/" + url;
        }

        Item item = findItem(context, cachingOptions, url, processor);
        if (item != null) {
            Tree tree = new Tree(item);
            if (depth == ContentStoreService.UNLIMITED_TREE_DEPTH || depth >= 1) {
                if (depth >= 1) {
                    depth--;
                }

                CachingAwareList<Item> treeChildren = (CachingAwareList<Item>)doFindChildren(context, cachingOptions,
                                                                                             url, depth, filter,
                                                                                             processor);
                if (treeChildren != null) {
                    tree.setChildren(treeChildren.getActualList());
                }
            }

            return tree;
        } else {
            return null;
        }
    }

    /**
     * Does the following:
     * <p/>
     * <ol>
     * <li>Retrieves the children from the underlying repository (without their descriptors).</li>
     * <li>Filters the returned list if {@link ItemFilter#runBeforeProcessing()} returns <code>true</code>.</li>
     * <li>Calls {@link #getTree(Context, String)} or {@link #getItem(Context, String)} for each item in the list
     * (depending on whether the item is a folder or not, and if <code>depth</code> is not null), to obtain the
     * merged and processed version of each item.</li>
     * <li>Filters the processed list if {@link ItemFilter#runAfterProcessing()} returns <code>true</code>.</li>
     * <li>Returns the final list of processed items.</li>
     * </ol>
     */
    protected List<Item> doFindChildren(Context context, CachingOptions cachingOptions, String url, Integer depth,
                                        ItemFilter filter, ItemProcessor processor) throws InvalidContextException,
        XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException {
        List<Item> children = context.getStoreAdapter().findItems(context, cachingOptions, url);
        if (children != null) {
            if (filter != null && filter.runBeforeProcessing()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Running filter " + filter + " before processing for " + url + "...");
                }

                children = doFilter(children, filter, true);
            }

            List<Item> processedChildren = new ArrayList<>(children.size());

            for (Item child : children) {
                Item processedChild;
                if (depth != null && child.isFolder()) {
                    processedChild = getTree(context, cachingOptions, child.getUrl(), depth, filter, processor);
                } else {
                    processedChild = getItem(context, cachingOptions, child.getUrl(), processor);
                }

                processedChildren.add(processedChild);
            }

            if (filter != null && filter.runAfterProcessing()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Running filter " + filter + " after processing for " + url + "...");
                }

                processedChildren = doFilter(processedChildren, filter, false);
            }

            processedChildren.sort(CompareByItemNameComparator.instance);

            return new CachingAwareList<>(processedChildren);
        } else {
            return null;
        }
    }

    /**
     * Executes merging for the specified {@link Item}:
     * <p/>
     * <ol>
     * <li>Gets the {@link org.craftercms.core.xml.mergers.DescriptorMergeStrategy} for the item's descriptor from
     * the {@link DescriptorMergeStrategyResolver}.</li>
     * <li>Gets the actual descriptors to merge from the returned merge strategy.</li>
     * <li>Retrieves the descriptor documents from the underlying repository.</li>
     * <li>Merges the descriptor documents.</li>
     * <li>Returns the item with the merged descriptor document.</li>
     * </ol>
     */
    protected Item doMerging(Context context, CachingOptions cachingOptions, Item item) throws CrafterException {
        if (context.isMergingOn()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Doing merge for " + item + "...");
            }

            String mainDescriptorUrl = item.getDescriptorUrl();
            Document mainDescriptorDom = item.getDescriptorDom();

            DescriptorMergeStrategy strategy = mergeStrategyResolver.getStrategy(mainDescriptorUrl, mainDescriptorDom);
            if (strategy == null) {
                logger.warn("No merge strategy was found for " + mainDescriptorUrl + ". Merging skipped");

                return item;
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Merge strategy for " + mainDescriptorUrl + ": " + strategy);
            }

            List<MergeableDescriptor> descriptorsToMerge = strategy.getDescriptors(context, cachingOptions, mainDescriptorUrl,
                                                                                   mainDescriptorDom);

            if (descriptorsToMerge == null) {
                throw new XmlMergeException("There aren't any descriptors to merge for " + mainDescriptorUrl);
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Descriptors to merge for " + mainDescriptorUrl + ": " + descriptorsToMerge);
            }

            List<Document> documentsToMerge = new ArrayList<>(descriptorsToMerge.size());

            for (MergeableDescriptor descriptorToMerge : descriptorsToMerge) {
                String descriptorUrl = descriptorToMerge.getUrl();
                Item descriptorItem = context.getStoreAdapter().findItem(context, cachingOptions, descriptorUrl, true);

                if (descriptorItem != null) {
                    Document descriptorDom = descriptorItem.getDescriptorDom();
                    if (descriptorDom == null && !descriptorToMerge.isOptional()) {
                        throw new XmlMergeException(
                            "Descriptor file " + descriptorUrl + " not found and is marked as " + "required for merging");
                    }

                    documentsToMerge.add(descriptorDom);
                } else if (!descriptorToMerge.isOptional()) {
                    throw new XmlMergeException("Descriptor file " + descriptorUrl + " not found and is marked as required for merging");
                }
            }

            Document mergedDoc = merger.merge(documentsToMerge);

            if (logger.isDebugEnabled()) {
                logger.debug("Merged descriptor DOM for " + item + ":\n" + XmlUtils.documentToPrettyString(mergedDoc));
            }

            item.setDescriptorDom(mergedDoc);
        }

        return item;
    }

    /**
     * Executes processing for the specified {@link Item}:
     * <p/>
     * <ol>
     * <li>Gets the main {@link ItemProcessor} for the item from the {@link ItemProcessorResolver}.</li>
     * <li>Calls the main processor's <code>process</code> method to process the item.</li>
     * <li>If an additional processor was passed to this method, the additional processor is also called.</li>
     * <li>Returns the processed item.</li>
     * </ol>
     */
    protected Item doProcessing(Context context, CachingOptions cachingOptions, Item item,
                                ItemProcessor additionalProcessor) throws ItemProcessingException {
        if (logger.isDebugEnabled()) {
            logger.debug("Doing processing for " + item + "...");
        }

        ItemProcessor mainProcessor = processorResolver.getProcessor(item);
        if (mainProcessor != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Main processor found for " + item + ": " + mainProcessor);
            }

            item = mainProcessor.process(context, cachingOptions, item);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("No main processor was found for " + item);
            }
        }

        if (additionalProcessor != null) {
            item = additionalProcessor.process(context, cachingOptions, item);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Processed item: " + item);
            if (item.getDescriptorDom() != null) {
                logger.debug("Processed descriptor DOM for " + item + ":\n" + XmlUtils.documentToPrettyString(item.getDescriptorDom()));
            }
        }

        return item;
    }

    /**
     * Filters the given list of items by using the specified filter. The <code>runningBeforeProcessing</code> flag
     * is passed to indicated the filter in which phase it is being executed (after or before processing).
     */
    protected List<Item> doFilter(List<Item> items, ItemFilter filter, boolean runningBeforeProcessing) {
        List<Item> acceptedItems = new ArrayList<>();
        List<Item> rejectedItems = new ArrayList<>();

        for (Item item : items) {
            if (filter.accepts(item, acceptedItems, rejectedItems, runningBeforeProcessing)) {
                acceptedItems.add(item);
            } else {
                rejectedItems.add(item);
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Items filtered from " + items + " by " + filter + ": " + acceptedItems);
        }

        return acceptedItems;
    }

    protected String createContextId(String tag, String storeType, String rootFolderPath, boolean cacheOn,
                                     int maxAllowedItemsInCache, boolean ignoreHiddenFiles) {
        String unHashedId = "tag='" + (tag != null ? tag : "") + "'" +
                            ", storeType='" + storeType + '\'' +
                            ", rootFolderPath='" + rootFolderPath + '\'' +
                            ", cacheOn=" + cacheOn +
                            ", maxAllowedItemsInCache=" + maxAllowedItemsInCache +
                            ", ignoreHiddenFiles=" + ignoreHiddenFiles;

        return DigestUtils.md5Hex(unHashedId);
    }

    /**
     * {@link Comparator} implementation that compares to {@link Item}s by comparing their names.
     */
    private static class CompareByItemNameComparator implements Comparator<Item> {

        public static final CompareByItemNameComparator instance = new CompareByItemNameComparator();

        private CompareByItemNameComparator() {
        }

        @Override
        public int compare(Item item1, Item item2) {
            return item1.getName().compareTo(item2.getName());
        }

    }

}
