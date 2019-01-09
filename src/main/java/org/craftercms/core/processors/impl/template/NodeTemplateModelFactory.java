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
package org.craftercms.core.processors.impl.template;

import org.craftercms.core.service.Item;
import org.dom4j.Node;

/**
 * Factory that returns models for DOM node templates.
 *
 * @author Alfonso Vásquez
 */
public interface NodeTemplateModelFactory {

    /**
     * Returns the model for a given template of a given node.
     *
     * @param item     the {@link Item} whose descriptor contains the specified node
     * @param node     the DOM node whose content is the specified template
     * @param template the actual template
     * @return the model to be used for processing the specified template
     */
    Object getModel(Item item, Node node, String template);

}
