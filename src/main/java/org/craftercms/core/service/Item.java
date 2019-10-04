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

import org.apache.commons.collections4.CollectionUtils;
import org.craftercms.core.util.XmlUtils;
import org.craftercms.core.util.cache.impl.AbstractCachingAwareObject;
import org.dom4j.Document;

import java.util.*;

/**
 * Represents an item of a content store. Content store items can be separated into 3 main categories:
 * <p/>
 * <ol>
 * <li>
 * <b>Standalone descriptors</b>: XML files that contain metadata and content for web pages. These type of
 * descriptors are in themselves items, so the item url and the descriptor url should be the same.
 * </li>
 * <li>
 * <b>Regular files</b>: Regular non-descriptor files (like documents, images, templates, scripts, etc)
 * </li>
 * <li>
 * <b>Folders</b>: Plain old folders/directories.
 * </li>
 * </ol>
 * <p/>
 * <p>Both regular files and folders can have their own metadata files or descriptors. So we can also say that
 * there are two types of descriptors: standalone descriptors and (regular file and folder) metadata files.</p>
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public class Item extends AbstractCachingAwareObject {

    /**
     * The name of the item (basically the file name).
     */
    protected String name;
    /**
     * The url or path of the item in the content store.
     */
    protected String url;
    /**
     * The url or path of the item's descriptor in the content store. If the item is a standalone descriptor, the
     * {@code descriptorUrl} will be the same as the {@code url}.
     */
    protected String descriptorUrl;
    /**
     * The DOM (Document Object Model) of the item's descriptor.
     */
    protected Document descriptorDom;
    /**
     * The properties of the item, which most of the time are metadata information on the item.
     */
    protected Map<String, Object> properties;
    /**
     * Flag that indicates if the item is a folder or not.
     */
    protected boolean isFolder;

    /**
     * Default no-arg constructor.
     */
    public Item() {
    }

    /**
     * Copy constructor. Performs a deep copy (calls {@link #Item(Item, boolean)} with true).
     */
    public Item(Item item) {
        this(item, true);
    }

    /**
     * Copy constructor. Performs a deep copy depending on the value of the {@code deepCopy} flag. In a deep copy, the
     * {@code descriptorDom} and {@code properties} are cloned.
     */
    public Item(Item item, boolean deepCopy) {
        super(item);

        name = item.name;
        url = item.url;
        descriptorUrl = item.descriptorUrl;
        isFolder = item.isFolder;

        if (deepCopy) {
            descriptorDom = item.descriptorDom != null? (Document)item.descriptorDom.clone(): null;
            properties = item.properties != null? new HashMap<>(item.properties): null;
        } else {
            descriptorDom = item.descriptorDom;
            properties = item.properties;
        }
    }

    /**
     * Returns name of the item (basically the file name).
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the item (basically the file name).
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the url or path of the item in the content store.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the url or path of the item in the content store.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Returns the url or path of the item's descriptor in the content store. If the item is a standalone descriptor,
     * the {@code descriptorUrl} will be the same as the {@code url}.
     */
    public String getDescriptorUrl() {
        return descriptorUrl;
    }

    /**
     * Sets the url or path of the item's descriptor in the content store. If the item is a standalone descriptor,
     * the {@code descriptorUrl} should be set to the same value as the {@code url}.
     */
    public void setDescriptorUrl(String descriptorUrl) {
        this.descriptorUrl = descriptorUrl;
    }

    /**
     * Returns the DOM (Document Object Model) of the item's descriptor.
     */
    public Document getDescriptorDom() {
        return descriptorDom;
    }

    /**
     * Sets the DOM (Document Object Model) of the item's descriptor.
     */
    public void setDescriptorDom(Document descriptorDom) {
        this.descriptorDom = descriptorDom;
    }

    /**
     * Returns the properties of the item, which most of the time are metadata information on the item.
     */
    public Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * Sets properties of the item, which most of the time are metadata information on the item.
     */
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    /**
     * Returns the property value for the specified key.
     */
    public Object getProperty(String key) {
        if (properties == null) {
            return null;
        }

        return properties.get(key);
    }

    /**
     * Adds the specified property value, associated to the specified key.
     */
    public void setProperty(String key, Object value) {
        if (properties == null) {
            properties = new HashMap<>();
        }

        properties.put(key, value);
    }

    /**
     * Queries a single descriptor node text value. First looks in the properties, if not found it executes the XPath
     * query.
     */
    public String queryDescriptorValue(String xpathQuery) {
        if (descriptorDom != null) {
            String value = (String)getProperty(xpathQuery);
            if (value == null) {
                value = XmlUtils.selectSingleNodeValue(descriptorDom, xpathQuery);
            }

            return value;
        } else {
            return null;
        }
    }

    /**
     * Queries multiple descriptor node text values. First looks in the properties, if not found it executes the XPath
     * query.
     */
    @SuppressWarnings("unchecked")
    public List<String> queryDescriptorValues(String xpathQuery) {
        if (descriptorDom != null) {
            List<String> value = (List<String>)getProperty(xpathQuery);
            if (CollectionUtils.isEmpty(value)) {
                value = XmlUtils.selectNodeValues(descriptorDom, xpathQuery);
            }

            return value;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Returns true if the item is a folder.
     */
    public boolean isFolder() {
        return this.isFolder;
    }

    /**
     * Sets whether the item is a folder or not.
     */
    public void setFolder(boolean folder) {
        this.isFolder = folder;
    }

    /**
     * Returns true if the specified {@code Item}'s and this instance's {@code name}, {@code url},
     * {@code descriptorUrl} and
     * {@code isFolder} are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Item item = (Item)o;

        if (!Objects.equals(name, item.name)) {
            return false;
        }
        if (!Objects.equals(url, item.url)) {
            return false;
        }
        if (!Objects.equals(descriptorUrl, item.descriptorUrl)) {
            return false;
        }
        if (isFolder != item.isFolder) {
            return false;
        }

        return true;
    }

    /**
     * Returns the hash code, which is the combination of the hash code of {@code name}, {@code url},
     * {@code descriptorUrl} and
     * {@code folder}.
     */
    @Override
    public int hashCode() {
        int result = name != null? name.hashCode(): 0;
        result = 31 * result + (url != null? url.hashCode(): 0);
        result = 31 * result + (descriptorUrl != null? descriptorUrl.hashCode(): 0);
        result = 31 * result + (isFolder? 1: 0);
        return result;
    }

    @Override
    public String toString() {
        return "Item[" +
            "name='" + name + '\'' +
            ", url='" + url + '\'' +
            ", descriptorUrl='" + descriptorUrl + '\'' +
            ", properties=" + properties +
            ", folder=" + isFolder +
            ']';
    }

}
