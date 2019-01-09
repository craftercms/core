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
package org.craftercms.core.xml.mergers.impl.cues;

import java.util.Map;

import org.craftercms.core.exception.XmlMergeException;
import org.dom4j.Element;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public interface MergeCue {

    int getPriority();

    Element merge(Element parent, Element child, Map<String, String> params) throws XmlMergeException;

}
