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
package org.craftercms.core.exception;

/**
 * Thrown when two descriptor DOMs can't be merged correctly into a single DOM.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public class XmlMergeException extends XmlException {

    private static final long serialVersionUID = 8924660634344041072L;

    public XmlMergeException() {
    }

    public XmlMergeException(String message, Throwable cause) {
        super(message, cause);
    }

    public XmlMergeException(String message) {
        super(message);
    }

    public XmlMergeException(Throwable cause) {
        super(cause);
    }

}
