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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

/**
 * Folder {@link Item} that also contains it's children.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public class Tree extends Item {

    /**
     * The folder's children. Children can be both {@link Item}s or other {@link Tree}s.
     */
    protected List<Item> children;

    /**
     * Default no-arg constructor. Sets {@code folder} to true, since a tree is always a folder.
     */
    public Tree() {
        isFolder = true;
    }

    /**
     * Copy constructor that takes an item and calls the {@link Item#Item(Item)}. Sets {@code folder} to true,
     * since a tree is always a folder.
     */
    public Tree(Item item) {
        super(item);

        isFolder = true;
    }

    /**
     * Copy constructor that takes an item and calls the {@link Item#Item(Item, boolean)}. Sets {@code folder} to true,
     * since a tree is always a folder.
     */
    public Tree(Item item, boolean deepCopy) {
        super(item, deepCopy);

        isFolder = true;
    }

    /**
     * Copy constructor that takes another tree. Performs a deep copy (calls {@link Item#Item(Item, boolean)} with
     * true).
     */
    public Tree(Tree tree) {
        this(tree, true);
    }

    /**
     * Copy constructor that takes another tree. Performs a deep copy depending on the value of the {@code deepCopy}
     * flag. In a deep copy, a deep copy of each child is done (by calling {@link Item#Item(Item, boolean)} and
     * {@link #Tree(Tree, boolean)}).
     */
    public Tree(Tree tree, boolean deepCopy) {
        super(tree, deepCopy);

        if (deepCopy) {
            if (CollectionUtils.isNotEmpty(tree.children)) {
                children = new ArrayList<Item>(tree.children.size());
                for (Item child : tree.children) {
                    if (child instanceof Tree) {
                        children.add(new Tree((Tree)child, deepCopy));
                    } else {
                        children.add(new Item(child, deepCopy));
                    }
                }
            } else {
                children = new ArrayList<Item>();
            }
        } else {
            children = tree.children;
        }
    }

    /**
     * Overrides {@link Item#setFolder(boolean)}, by checking that the flag is never set to false, since a tree
     * is always a folder.
     *
     * @throws IllegalArgumentException if the method was called with false (the folder flag should never being set
     *                                  to false).
     */
    @Override
    public void setFolder(boolean folder) {
        if (!folder) {
            throw new IllegalArgumentException("A tree must always be a folder");
        }

        super.setFolder(folder);
    }

    /**
     * Returns the tree's children. Children can be both {@link Item}s or other {@link Tree}s.
     */
    public List<Item> getChildren() {
        return children;
    }

    /**
     * Sets the tree's children. Children can be both {@link Item}s or other {@link Tree}s.
     *
     * @param children
     */
    public void setChildren(List<Item> children) {
        this.children = children;
    }

    /**
     * Adds a child. The child can be both {@link Item}s or another {@link Tree}s.
     */
    public void addChild(Item child) {
        if (children == null) {
            children = new ArrayList<>();
        }

        children.add(child);
    }

    /**
     * Removes a child.
     */
    public boolean removeChild(Item child) {
        if (children != null) {
            return children.remove(child);
        } else {
            return false;
        }
    }

    /**
     * Returns true if {@link Item#equals(Object)} returns true and if the specified {@code Tree}'s and this instance's
     * {@code children} are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        Tree tree = (Tree)o;

        if (children != null? !children.equals(tree.children): tree.children != null) {
            return false;
        }

        return true;
    }

    /**
     * Returns the hash code, which is a combination of {@link Item#hashCode()} and the list of children's hash code.
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (children != null? children.hashCode(): 0);
        return result;
    }

    @Override
    public String toString() {
        return "Tree[" +
            "name='" + name + '\'' +
            ", url='" + url + '\'' +
            ", descriptorUrl='" + descriptorUrl + '\'' +
            ", properties=" + properties +
            ", children=" + children +
            ']';
    }

}
