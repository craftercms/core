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
package org.craftercms.core.service;

import java.util.List;

import org.craftercms.core.exception.*;
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
    public static final int UNLIMITED_TREE_DEPTH = -1;

    /**
     * Returns the open {@link Context} for the given ID, or null if no context found for the ID.
     */
    Context getContext(String contextId);

    /**
     * Returns a new context with the specified params, login in to a remote repository (if needed) and creating a
     * new cache scope
     * associated to the context. If a context with the same params was already created, an exception is thrown.
     */
    Context createContext(String storeType, String storeServerUrl, String username, String password,
                          String rootFolderPath, boolean cacheOn,
                          int maxAllowedItemsInCache, boolean ignoreHiddenFiles) throws InvalidStoreTypeException,
            StoreException,
            AuthenticationException;

    /**
     * Destroys the specified context, login out of any remote repository (if a login was issued) and destroying the
     * cache scope
     * associated to the context.
     */
    void destroyContext(Context context) throws InvalidContextException, StoreException, AuthenticationException;

    /**
     * Returns the content of the file for the given url.
     *
     * @param context the context with the store configuration
     * @param url     the url of the file
     * @return the file content
     * @throws InvalidContextException if the context is invalid
     * @throws org.craftercms.core.exception.PathNotFoundException
     *                                 if the file the url points to can't be found
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Content getContent(Context context, String url) throws InvalidScopeException, PathNotFoundException, StoreException;

    /**
     * Returns the content of the file for the given url.
     *
     * @param context        the context with the store configuration
     * @param cachingOptions the caching options for any caching operation done inside this service call
     * @param url            the url of the file
     * @return the file content
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the file the url points to can't be found
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Content getContent(Context context, CachingOptions cachingOptions, String url) throws InvalidScopeException,
            PathNotFoundException,
            StoreException;

    /**
     * Returns the content store item for the given url. See {@link Item} for the types of files an item can be.
     *
     * @param context the context with the store configuration
     * @param url     the url of the item
     * @return the item
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the file the url points to can't be found
     * @throws org.craftercms.core.exception.XmlFileParseException
     *                                 if there was an error while parsing the item's XML descriptor
     * @throws org.craftercms.core.exception.XmlMergeException
     *                                 if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing the item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Item getItem(Context context, String url) throws InvalidContextException, PathNotFoundException,
            XmlFileParseException,
            XmlMergeException, ItemProcessingException, StoreException;

    /**
     * Returns the content store item for the given url. See {@link Item} for the types of files an item can be.
     *
     * @param context        the context with the store configuration
     * @param cachingOptions the caching options for any caching operation done inside this service call
     * @param url            the url of the item
     * @return the item
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the file the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing the item's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing the item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Item getItem(Context context, CachingOptions cachingOptions, String url) throws InvalidContextException,
            PathNotFoundException,
            XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException;

    /**
     * Returns the content store item for the given url. See {@link Item} for the types of files an item can be.
     *
     * @param context   the context to the content store
     * @param url       the url of the item
     * @param processor additional {@link ItemProcessor}
     * @return the item
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the file the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing the item's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing the item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Item getItem(Context context, String url, ItemProcessor processor) throws InvalidContextException,
            PathNotFoundException,
            XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException;

    /**
     * Returns the content store item for the given url. See {@link Item} for the types of files an item can be.
     *
     * @param context        the context to the content store
     * @param cachingOptions the caching options for any caching operation done inside this service call
     * @param url            the url of the item
     * @param processor      additional {@link ItemProcessor}
     * @return the item
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the file the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing the item's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing the item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Item getItem(Context context, CachingOptions cachingOptions, String url, ItemProcessor processor) throws
            InvalidContextException,
            PathNotFoundException, XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException;

    /**
     * Returns the children of the folder at the given url.
     *
     * @param context the context to the content store
     * @param url     the url of the folder
     * @return the children of the folder
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the folder the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    List<Item> getChildren(Context context, String url) throws InvalidContextException, PathNotFoundException,
            XmlFileParseException,
            XmlMergeException, ItemProcessingException, StoreException;

    /**
     * Returns the children of the folder at the given url.
     *
     * @param context        the context to the content store
     * @param cachingOptions the caching options for any caching operation done inside this service call
     * @param url            the url of the folder
     * @return the children of the folder
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the folder the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    List<Item> getChildren(Context context, CachingOptions cachingOptions, String url) throws InvalidContextException,
            PathNotFoundException, XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException;

    /**
     * Returns the children of the folder at the given url.
     *
     * @param context the context to the content store
     * @param url     the url of the folder
     * @param filter  an {@link ItemFilter} to filter out undesired children
     * @return the children of the folder
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the folder the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    List<Item> getChildren(Context context, String url, ItemFilter filter) throws InvalidContextException,
            PathNotFoundException,
            XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException;

    /**
     * Returns the children of the folder at the given url.
     *
     * @param context        the context to the content store
     * @param cachingOptions the caching options for any caching operation done inside this service call
     * @param url            the url of the folder
     * @param filter         an {@link ItemFilter} to filter out undesired children
     * @return the children of the folder
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the folder the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    List<Item> getChildren(Context context, CachingOptions cachingOptions, String url,
                           ItemFilter filter) throws InvalidContextException,
            PathNotFoundException, XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException;

    /**
     * Returns the children of the folder at the given url.
     *
     * @param context   the context to the content store
     * @param url       the url of the folder
     * @param processor additional {@link ItemProcessor} for the children
     * @return the children of the folder
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the folder the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    List<Item> getChildren(Context context, String url, ItemProcessor processor) throws InvalidContextException,
            PathNotFoundException,
            XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException;

    /**
     * Returns the children of the folder at the given url.
     *
     * @param context        the context to the content store
     * @param cachingOptions the caching options for any caching operation done inside this service call
     * @param url            the url of the folder
     * @param processor      additional {@link ItemProcessor} for the children
     * @return the children of the folder
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the folder the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    List<Item> getChildren(Context context, CachingOptions cachingOptions, String url, ItemProcessor processor)
            throws InvalidContextException, PathNotFoundException, XmlFileParseException, XmlMergeException,
            ItemProcessingException,
            StoreException;

    /**
     * Returns the children of the folder at the given url.
     *
     * @param context   the context to the content store
     * @param url       the url of the folder
     * @param filter    an {@link ItemFilter} to filter out undesired children
     * @param processor additional {@link ItemProcessor} for the children
     * @return the children of the folder
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the folder the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    List<Item> getChildren(Context context, String url, ItemFilter filter, ItemProcessor processor) throws
            InvalidContextException,
            PathNotFoundException, XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException;

    /**
     * Returns the children of the folder at the given url.
     *
     * @param context        the context to the content store
     * @param cachingOptions the caching options for any caching operation done inside this service call
     * @param url            the url of the folder
     * @param filter         an {@link ItemFilter} to filter out undesired children
     * @param processor      additional {@link ItemProcessor} for the children
     * @return the children of the folder
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the folder the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    List<Item> getChildren(Context context, CachingOptions cachingOptions, String url, ItemFilter filter,
                           ItemProcessor processor)
            throws InvalidContextException, PathNotFoundException, XmlFileParseException, XmlMergeException,
            ItemProcessingException,
            StoreException;

    /**
     * Returns the folder at the given url as a tree. The tree children are returned as {@link Item}s when they're
     * files and as {@code Tree}s when they're folders. The tree depth is {@link #UNLIMITED_TREE_DEPTH}.
     *
     * @param context the context to the content store
     * @param url     the url of the folder
     * @return the folder as a tree
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the folder the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Tree getTree(Context context, String url) throws InvalidContextException, PathNotFoundException,
            XmlFileParseException,
            XmlMergeException, ItemProcessingException, StoreException;

    /**
     * Returns the folder at the given url as a tree. The tree children are returned as {@link Item}s when they're
     * files and as {@code Tree}s when they're folders. The tree depth is {@link #UNLIMITED_TREE_DEPTH}.
     *
     * @param context        the context to the content store
     * @param cachingOptions the caching options for any caching operation done inside this service call
     * @param url            the url of the folder
     * @return the folder as a tree
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the folder the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Tree getTree(Context context, CachingOptions cachingOptions, String url) throws InvalidContextException,
            PathNotFoundException,
            XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException;

    /**
     * Returns the folder at the given url as a tree. The tree children are returned as {@link Item}s when they're
     * files and as {@code Tree}s when they're folders.
     *
     * @param context the context to the content store
     * @param url     the url of the folder
     * @param depth   the number of levels the tree should have. Use #UNLIMITED_TREE_DEPTH when you want the tree to
     *                include
     *                all files/folder until the bottom of the folder hierarchy
     * @return the folder as a tree
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the folder the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Tree getTree(Context context, String url, int depth) throws InvalidContextException, PathNotFoundException,
            XmlFileParseException,
            XmlMergeException, ItemProcessingException, StoreException;

    /**
     * Returns the folder at the given url as a tree. The tree children are returned as {@link Item}s when they're
     * files and as {@code Tree}s when they're folders.
     *
     * @param context        the context to the content store
     * @param cachingOptions the caching options for any caching operation done inside this service call
     * @param url            the url of the folder
     * @param depth          the number of levels the tree should have. Use #UNLIMITED_TREE_DEPTH when you want the
     *                       tree to include
     *                       all files/folder until the bottom of the folder hierarchy
     * @return the folder as a tree
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the folder the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Tree getTree(Context context, CachingOptions cachingOptions, String url, int depth) throws InvalidContextException,
            PathNotFoundException, XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException;

    /**
     * Returns the folder at the given url as a tree. The tree children are returned as {@link Item}s when they're
     * files and as {@code Tree}s when they're folders. The tree depth is {@link #UNLIMITED_TREE_DEPTH}.
     *
     * @param context the context to the content store
     * @param url     the url of the folder
     * @param filter  an {@link ItemFilter} to filter out undesired items in the tree
     * @return the folder as a tree
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the folder the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Tree getTree(Context context, String url, ItemFilter filter) throws InvalidContextException, PathNotFoundException,
            XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException;

    /**
     * Returns the folder at the given url as a tree. The tree children are returned as {@link Item}s when they're
     * files and as {@code Tree}s when they're folders. The tree depth is {@link #UNLIMITED_TREE_DEPTH}.
     *
     * @param context        the context to the content store
     * @param cachingOptions the caching options for any caching operation done inside this service call
     * @param url            the url of the folder
     * @param filter         an {@link ItemFilter} to filter out undesired items in the tree
     * @return the folder as a tree
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the folder the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Tree getTree(Context context, CachingOptions cachingOptions, String url,
                 ItemFilter filter) throws InvalidContextException,
            PathNotFoundException, XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException;

    /**
     * Returns the folder at the given url as a tree. The tree children are returned as {@link Item}s when they're
     * files and as {@code Tree}s when they're folders.
     *
     * @param context the context to the content store
     * @param url     the url of the folder
     * @param depth   the number of levels the tree should have. Use #UNLIMITED_TREE_DEPTH when you want the tree to
     *                include
     *                all files/folder until the bottom of the folder hierarchy
     * @param filter  an {@link ItemFilter} to filter out undesired items in the tree
     * @return the folder as a tree
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the folder the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Tree getTree(Context context, String url, int depth, ItemFilter filter) throws InvalidContextException,
            PathNotFoundException,
            XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException;

    /**
     * Returns the folder at the given url as a tree. The tree children are returned as {@link Item}s when they're
     * files and as {@code Tree}s when they're folders.
     *
     * @param context        the context to the content store
     * @param cachingOptions the caching options for any caching operation done inside this service call
     * @param url            the url of the folder
     * @param depth          the number of levels the tree should have. Use #UNLIMITED_TREE_DEPTH when you want the
     *                       tree to include
     *                       all files/folder until the bottom of the folder hierarchy
     * @param filter         an {@link ItemFilter} to filter out undesired items in the tree
     * @return the folder as a tree
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the folder the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Tree getTree(Context context, CachingOptions cachingOptions, String url, int depth,
                 ItemFilter filter) throws InvalidContextException,
            PathNotFoundException, XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException;

    /**
     * Returns the folder at the given url as a tree. The tree children are returned as {@link Item}s when they're
     * files and as {@code Tree}s when they're folders. The tree depth is {@link #UNLIMITED_TREE_DEPTH}.
     *
     * @param context   the context to the content store
     * @param url       the url of the folder
     * @param processor additional {@link ItemProcessor} for the tree's items
     * @return the folder as a tree
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the folder the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Tree getTree(Context context, String url, ItemProcessor processor) throws InvalidContextException,
            PathNotFoundException,
            XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException;

    /**
     * Returns the folder at the given url as a tree. The tree children are returned as {@link Item}s when they're
     * files and as {@code Tree}s when they're folders. The tree depth is {@link #UNLIMITED_TREE_DEPTH}.
     *
     * @param context        the context to the content store
     * @param cachingOptions the caching options for any caching operation done inside this service call
     * @param url            the url of the folder
     * @param processor      additional {@link ItemProcessor} for the tree's items
     * @return the folder as a tree
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the folder the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Tree getTree(Context context, CachingOptions cachingOptions, String url, ItemProcessor processor) throws
            InvalidContextException,
            PathNotFoundException, XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException;

    /**
     * Returns the folder at the given url as a tree. The tree children are returned as {@link Item}s when they're
     * files and as {@code Tree}s when they're folders.
     *
     * @param context   the context to the content store
     * @param url       the url of the folder
     * @param depth     the number of levels the tree should have. Use #UNLIMITED_TREE_DEPTH when you want the tree
     *                  to include
     *                  all files/folder until the bottom of the folder hierarchy
     * @param processor additional {@link ItemProcessor} for the tree's items
     * @return the folder as a tree
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the folder the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Tree getTree(Context context, String url, int depth, ItemProcessor processor) throws InvalidContextException,
            PathNotFoundException,
            XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException;

    /**
     * Returns the folder at the given url as a tree. The tree children are returned as {@link Item}s when they're
     * files and as {@code Tree}s when they're folders.
     *
     * @param context        the context to the content store
     * @param cachingOptions the caching options for any caching operation done inside this service call
     * @param url            the url of the folder
     * @param depth          the number of levels the tree should have. Use #UNLIMITED_TREE_DEPTH when you want the
     *                       tree to include
     *                       all files/folder until the bottom of the folder hierarchy
     * @param processor      additional {@link ItemProcessor} for the tree's items
     * @return the folder as a tree
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the folder the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Tree getTree(Context context, CachingOptions cachingOptions, String url, int depth, ItemProcessor processor)
            throws InvalidContextException, PathNotFoundException, XmlFileParseException, XmlMergeException,
            ItemProcessingException,
            StoreException;

    /**
     * Returns the folder at the given url as a tree. The tree children are returned as {@link Item}s when they're
     * files and as {@code Tree}s when they're folders. The tree depth is {@link #UNLIMITED_TREE_DEPTH}.
     *
     * @param context   the context to the content store
     * @param url       the url of the folder
     * @param filter    an {@link ItemFilter} to filter out undesired items in the tree
     * @param processor additional {@link ItemProcessor} for the tree's items
     * @return the folder as a tree
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the folder the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Tree getTree(Context context, String url, ItemFilter filter, ItemProcessor processor) throws
            InvalidContextException,
            PathNotFoundException, XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException;

    /**
     * Returns the folder at the given url as a tree. The tree children are returned as {@link Item}s when they're
     * files and as {@code Tree}s when they're folders. The tree depth is {@link #UNLIMITED_TREE_DEPTH}.
     *
     * @param context        the context to the content store
     * @param cachingOptions the caching options for any caching operation done inside this service call
     * @param url            the url of the folder
     * @param filter         an {@link ItemFilter} to filter out undesired items in the tree
     * @param processor      additional {@link ItemProcessor} for the tree's items
     * @return the folder as a tree
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the folder the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Tree getTree(Context context, CachingOptions cachingOptions, String url, ItemFilter filter, ItemProcessor processor)
            throws InvalidContextException, PathNotFoundException, XmlFileParseException, XmlMergeException,
            ItemProcessingException,
            StoreException;

    /**
     * Returns the folder at the given url as a tree. The tree children are returned as {@link Item}s when they're
     * files and as {@code Tree}s when they're folders. The tree depth is {@link #UNLIMITED_TREE_DEPTH}.
     *
     * @param context   the context to the content store
     * @param url       the url of the folder
     * @param depth     the number of levels the tree should have. Use #UNLIMITED_TREE_DEPTH when you want the tree
     *                  to include
     *                  all files/folder until the bottom of the folder hierarchy
     * @param filter    an {@link ItemFilter} to filter out undesired items in the tree
     * @param processor additional {@link ItemProcessor} for the tree's items
     * @return the folder as a tree
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the folder the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Tree getTree(Context context, String url, int depth, ItemFilter filter, ItemProcessor processor) throws
            InvalidContextException,
            PathNotFoundException, XmlFileParseException, XmlMergeException, ItemProcessingException, StoreException;

    /**
     * Returns the folder at the given url as a tree. The tree children are returned as {@link Item}s when they're
     * files and as {@code Tree}s when they're folders. The tree depth is {@link #UNLIMITED_TREE_DEPTH}.
     *
     * @param context        the context to the content store
     * @param cachingOptions the caching options for any caching operation done inside this service call
     * @param url            the url of the folder
     * @param depth          the number of levels the tree should have. Use #UNLIMITED_TREE_DEPTH when you want the
     *                       tree to include
     *                       all files/folder until the bottom of the folder hierarchy
     * @param filter         an {@link ItemFilter} to filter out undesired items in the tree
     * @param processor      additional {@link ItemProcessor} for the tree's items
     * @return the folder as a tree
     * @throws InvalidContextException if the context is invalid
     * @throws PathNotFoundException   if the folder the url points to can't be found
     * @throws XmlFileParseException   if there was an error while parsing a children's XML descriptor
     * @throws XmlMergeException       if there was an error while attempting to do a merge of XML descriptors
     * @throws ItemProcessingException if there was an error while processing an item
     * @throws StoreException          if an error occurred while accessing the content store
     */
    Tree getTree(Context context, CachingOptions cachingOptions, String url, int depth, ItemFilter filter, ItemProcessor processor)
            throws InvalidContextException, PathNotFoundException, XmlFileParseException, XmlMergeException, ItemProcessingException,
            StoreException;

}
