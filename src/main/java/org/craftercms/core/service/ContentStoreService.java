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
package org.craftercms.core.service;

import java.util.List;

import org.craftercms.core.exception.AuthenticationException;
import org.craftercms.core.exception.InvalidContextException;
import org.craftercms.core.exception.InvalidStoreTypeException;
import org.craftercms.core.exception.ItemProcessingException;
import org.craftercms.core.exception.PathNotFoundException;
import org.craftercms.core.exception.RootFolderNotFoundException;
import org.craftercms.core.exception.StoreException;
import org.craftercms.core.exception.XmlFileParseException;
import org.craftercms.core.exception.XmlMergeException;
import org.craftercms.core.processors.ItemProcessor;

/**
 * Main Crafter content access API. Besides providing an interface to a content store or repository, implementations
 * of this service also should provide the following facilities:
 * <p/>
 * <ul>
 * <li>
 * <b>Merging:</b>By using a certain merge strategy, the service is able to merge a collection of XML
 * descriptor documents into a single XML document for an item.
 * </li>
 * <li>
 * <b>Processing:</b>The service passes an item through one or multiple "processors" that can manipulate
 * and change the item as they need. For example, there is a processor implementation that includes other
 * descriptor documents into the item's descriptor document (see {@link org.craftercms.core.processors
 * .impl.IncludeDescriptorsProcessor}, an another one that processes element text of descriptor documents as
 * templates (see {@link org.craftercms.core.processors.impl.template.TemplateProcessor}).
 * </li>
 * </ul>
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 * @see org.craftercms.core.xml.mergers.DescriptorMergeStrategy
 * @see org.craftercms.core.xml.mergers.DescriptorMergeStrategyResolver
 * @see org.craftercms.core.xml.mergers.DescriptorMerger
 * @see ItemProcessor
 * @see org.craftercms.core.processors.ItemProcessorResolver
 */
public interface ContentStoreService {

    /**
     * Constant used to indicate that the depth of the tree should be unlimited. This means that the tree will be
     * built until the bottom of the folder hierarchy.
     */
    int UNLIMITED_TREE_DEPTH = -1;

    /**
     * Returns the open {@link Context} for the given ID, or null if no context found for the ID.
     */
    Context getContext(String contextId);

    /**
     * Returns a context with the specified parameters, if the context doesn't exist it will be created.
     * @param tag a tag that's used when creating the ID to differentiate this context
     * @param storeType the type of content store to use
     * @param rootFolderPath the root folder path for the context
     * @param mergingOn indicates if content merging should be enabled
     * @param cacheOn indicates if content caching should be enabled
     * @param maxAllowedItemsInCache the maximum number of items to hold in the cache
     * @param ignoreHiddenFiles indicates if hidden files should be ignored
     * @return the {@link Context} object
     * @throws InvalidStoreTypeException if the provided store type is invalid
     * @throws RootFolderNotFoundException if the provided root folder path doesn't exist
     * @throws StoreException if there is any unexpected error during the creation the context
     * @throws AuthenticationException if there is any authentication error during the creation of the context
     */
    Context getContext(String tag, String storeType, String rootFolderPath, boolean mergingOn, boolean cacheOn,
                       int maxAllowedItemsInCache, boolean ignoreHiddenFiles)
        throws InvalidStoreTypeException, RootFolderNotFoundException, StoreException, AuthenticationException;

    /**
     * Returns true if the specified context is still valid and usable.
     */
    boolean validate(Context context) throws StoreException, AuthenticationException;

    /**
     * Destroys the specified context, login out of any remote repository (if a login was issued) and destroying the
     * cache scope associated to the context.
     *
     * @return true if the context was successfully destroyed, false otherwise.
     */
    boolean destroyContext(Context context) throws StoreException, AuthenticationException;

    /**
     * Returns true if the file or folder at the specified URL exists
     *
     * @param context the context with the store configuration (required)
     * @param cachingOptions the caching options for any caching operation done inside this service call (optional)
     * @param url     the url of the file (required)
     * @return true if the file or folder exists, false otherwise
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the file the url points to can't be found
     * @throws StoreException          if an error occurred while accessing the content store
     */
    boolean exists(Context context, CachingOptions cachingOptions, String url)
        throws InvalidContextException, PathNotFoundException, StoreException;


    /**
     * Returns true if the file or folder at the specified URL exists
     *
     * @param context the context with the store configuration (required)
     * @param url     the url of the file (required)
     * @return true if the file or folder exists, false otherwise
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the file the url points to can't be found
     * @throws StoreException          if an error occurred while accessing the content store
     */
    boolean exists(Context context, String url)
        throws InvalidContextException, PathNotFoundException, StoreException;

    /**
     * Returns the content of the file for the given url, returning null if not found.
     *
     * @param context the context with the store configuration (required)
     * @param url     the url of the file (required)
     * @return the file content
     * @throws InvalidContextException if the context is invalid
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Content findContent(Context context, String url) throws InvalidContextException, StoreException;

    /**
     * Returns the content of the file for the given url.
     *
     * @param context the context with the store configuration (required)
     * @param url     the url of the file (required)
     * @return the file content
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the file the url points to can't be found
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Content getContent(Context context, String url) throws InvalidContextException, PathNotFoundException,
                                                           StoreException;

    /**
     * Returns the content of the file for the given url, returning null if not found.
     *
     * @param context        the context with the store configuration (required)
     * @param cachingOptions the caching options for any caching operation done inside this service call (optional)
     * @param url            the url of the file (required)
     * @return the file content
     * @throws InvalidContextException if the context is invalid
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Content findContent(Context context, CachingOptions cachingOptions, String url) throws InvalidContextException,
                                                                                           StoreException;

    /**
     * Returns the content of the file for the given url.
     *
     * @param context        the context with the store configuration (required)
     * @param cachingOptions the caching options for any caching operation done inside this service call (optional)
     * @param url            the url of the file (required)
     * @return the file content
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the file the url points to can't be found
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Content getContent(Context context, CachingOptions cachingOptions,
                       String url) throws InvalidContextException, PathNotFoundException, StoreException;

    /**
     * Returns the content store item for the given url, returning null if not found. See {@link Item} for the
     * types of files an item can be.
     *
     * @param context the context with the store configuration (required)
     * @param url     the url of the item (required)
     * @return the item
     * @throws InvalidContextException if the context is invalid
     * @throws XmlFileParseException   if there was an error while parsing the item's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing the item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Item findItem(Context context,
                  String url) throws InvalidContextException, XmlFileParseException, XmlMergeException,
                                     ItemProcessingException, StoreException;

    /**
     * Returns the content store item for the given url. See {@link Item} for the types of files an item can be.
     *
     * @param context the context with the store configuration (required)
     * @param url     the url of the item (required)
     * @return the item
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the file the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing the item's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing the item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Item getItem(Context context,
                 String url) throws InvalidContextException, PathNotFoundException, XmlFileParseException,
                                    XmlMergeException, ItemProcessingException, StoreException;

    /**
     * Returns the content store item for the given url, returning null if not found. See {@link Item} for the types of
     * files an item can be.
     *
     * @param context        the context to the content store (required)
     * @param cachingOptions the caching options for any caching operation done inside this service call (optional)
     * @param url            the url of the item (optional, can be null)
     * @param processor      additional {@link ItemProcessor} (optional, can be null)
     * @return the item
     * @throws InvalidContextException if the context is invalid
     * @throws XmlFileParseException   if there was an error while parsing the item's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing the item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Item findItem(Context context, CachingOptions cachingOptions, String url,
                  ItemProcessor processor) throws InvalidContextException, XmlFileParseException, XmlMergeException,
        ItemProcessingException, StoreException;

    /**
     * Returns the content store item for the given url. See {@link Item} for the types of files an item can be.
     *
     * @param context        the context to the content store (required)
     * @param cachingOptions the caching options for any caching operation done inside this service call (optional)
     * @param url            the url of the item (optional, can be null)
     * @param processor      additional {@link ItemProcessor} (optional, can be null)
     * @return the item
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the file the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing the item's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing the item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Item getItem(Context context, CachingOptions cachingOptions, String url,
                 ItemProcessor processor) throws InvalidContextException, PathNotFoundException, XmlFileParseException,
        XmlMergeException, ItemProcessingException, StoreException;

    /**
     * Returns the children of the folder at the given url, returning null if the folder can't be found.
     *
     * @param context the context to the content store (required)
     * @param url     the url of the folder (required)
     * @return the children of the folder
     * @throws InvalidContextException if the context is invalid
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    List<Item> findChildren(Context context,
                            String url) throws InvalidContextException, XmlFileParseException, XmlMergeException,
        ItemProcessingException, StoreException;

    /**
     * Returns the children of the folder at the given url.
     *
     * @param context the context to the content store (required)
     * @param url     the url of the folder (required)
     * @return the children of the folder
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the folder the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    List<Item> getChildren(Context context,
                           String url) throws InvalidContextException, PathNotFoundException, XmlFileParseException,
                                              XmlMergeException, ItemProcessingException, StoreException;

    /**
     * Returns the children of the folder at the given url, returning null if the folder can't be found.
     *
     * @param context        the context to the content store (required)
     * @param cachingOptions the caching options for any caching operation done inside this service call (optional)
     * @param url            the url of the folder (required)
     * @param filter         an {@link ItemFilter} to filter out undesired children (optional, can be null)
     * @param processor      additional {@link ItemProcessor} for the children (optional, can be null)
     * @return the children of the folder
     * @throws InvalidContextException if the context is invalid
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    List<Item> findChildren(Context context, CachingOptions cachingOptions, String url, ItemFilter filter,
                            ItemProcessor processor) throws InvalidContextException, XmlFileParseException,
                                                            XmlMergeException, ItemProcessingException, StoreException;

    /**
     * Returns the children of the folder at the given url.
     *
     * @param context        the context to the content store (required)
     * @param cachingOptions the caching options for any caching operation done inside this service call (optional)
     * @param url            the url of the folder (required)
     * @param filter         an {@link ItemFilter} to filter out undesired children (optional, can be null)
     * @param processor      additional {@link ItemProcessor} for the children (optional, can be null)
     * @return the children of the folder
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the folder the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    List<Item> getChildren(Context context, CachingOptions cachingOptions, String url, ItemFilter filter,
                           ItemProcessor processor) throws InvalidContextException, PathNotFoundException,
                                                           XmlFileParseException, XmlMergeException,
                                                           ItemProcessingException, StoreException;

    /**
     * Returns the folder at the given url as a tree, or null if the folder wasn't found. The tree children are
     * returned as {@link Item}s when they're files and as {@code Tree}s when they're folders. The tree depth is
     * {@link #UNLIMITED_TREE_DEPTH}.
     *
     * @param context the context to the content store (required)
     * @param url     the url of the folder (required)
     * @return the folder as a tree
     * @throws InvalidContextException if the context is invalid
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Tree findTree(Context context,
                  String url) throws InvalidContextException, XmlFileParseException, XmlMergeException,
                                     ItemProcessingException, StoreException;

    /**
     * Returns the folder at the given url as a tree. The tree children are returned as {@link Item}s when they're
     * files and as {@code Tree}s when they're folders. The tree depth is {@link #UNLIMITED_TREE_DEPTH}.
     *
     * @param context the context to the content store (required)
     * @param url     the url of the folder (required)
     * @return the folder as a tree
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the folder the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Tree getTree(Context context,
                 String url) throws InvalidContextException, PathNotFoundException, XmlFileParseException,
                                    XmlMergeException, ItemProcessingException, StoreException;

    /**
     * Returns the folder at the given url as a tree, or null if the folder wasn't found. The tree children are
     * returned as {@link Item}s when they're files and as {@code Tree}s when they're folders.
     *
     * @param context the context to the content store (required)
     * @param url     the url of the folder (required)
     * @param depth   the number of levels the tree should have. Use #UNLIMITED_TREE_DEPTH when you want the tree to
     *                include all files/folder until the bottom of the folder hierarchy (required)
     * @return the folder as a tree
     * @throws InvalidContextException if the context is invalid
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Tree findTree(Context context, String url,
                  int depth) throws InvalidContextException, XmlFileParseException, XmlMergeException,
                                    ItemProcessingException, StoreException;

    /**
     * Returns the folder at the given url as a tree. The tree children are returned as {@link Item}s when they're
     * files and as {@code Tree}s when they're folders.
     *
     * @param context the context to the content store (required)
     * @param url     the url of the folder (required)
     * @param depth   the number of levels the tree should have. Use #UNLIMITED_TREE_DEPTH when you want the tree to
     *                include all files/folder until the bottom of the folder hierarchy (required)
     * @return the folder as a tree
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the folder the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Tree getTree(Context context, String url,
                 int depth) throws InvalidContextException, PathNotFoundException, XmlFileParseException,
                                   XmlMergeException, ItemProcessingException, StoreException;

    /**
     * Returns the folder at the given url as a tree, or null if the folder wasn't found. The tree children are
     * returned as {@link Item}s when they're files and as {@code Tree}s when they're folders.
     *
     * @param context        the context to the content store (required)
     * @param cachingOptions the caching options for any caching operation done inside this service call (optional)
     * @param url            the url of the folder (required)
     * @param depth          the number of levels the tree should have. Use #UNLIMITED_TREE_DEPTH when you want the
     *                       tree to include all files/folder until the bottom of the folder hierarchy (required)
     * @param filter         an {@link ItemFilter} to filter out undesired items in the tree (optional, can be null)
     * @param processor      additional {@link ItemProcessor} for the tree's items (optional, can be null)
     * @return the folder as a tree
     * @throws InvalidContextException if the context is invalid
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Tree findTree(Context context, CachingOptions cachingOptions, String url, int depth, ItemFilter filter,
                  ItemProcessor processor) throws InvalidContextException, XmlFileParseException, XmlMergeException,
                                                  ItemProcessingException, StoreException;

    /**
     * Returns the folder at the given url as a tree. The tree children are returned as {@link Item}s when they're
     * files and as {@code Tree}s when they're folders.
     *
     * @param context        the context to the content store (required)
     * @param cachingOptions the caching options for any caching operation done inside this service call (optional)
     * @param url            the url of the folder (required)
     * @param depth          the number of levels the tree should have. Use #UNLIMITED_TREE_DEPTH when you want the
     *                       tree to include all files/folder until the bottom of the folder hierarchy (required)
     * @param filter         an {@link ItemFilter} to filter out undesired items in the tree (optional, can be null)
     * @param processor      additional {@link ItemProcessor} for the tree's items (optional, can be null)
     * @return the folder as a tree
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the folder the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Tree getTree(Context context, CachingOptions cachingOptions, String url, int depth, ItemFilter filter,
                 ItemProcessor processor) throws InvalidContextException, PathNotFoundException, XmlFileParseException,
                                                 XmlMergeException, ItemProcessingException, StoreException;

}
