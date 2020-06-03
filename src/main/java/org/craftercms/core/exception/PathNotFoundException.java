/*
 * Copyright (C) 2007-2020 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
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
 * Thrown to indicate that a resource in a content store couldn't be reached (its path was not found).
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public class PathNotFoundException extends CrafterException {

    private static final long serialVersionUID = 7672434363103060508L;

    public PathNotFoundException() {
    }

    public PathNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PathNotFoundException(String message) {
        super(message);
    }

    public PathNotFoundException(Throwable cause) {
        super(cause);
    }

}
