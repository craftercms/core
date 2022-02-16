/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
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
 * Root exception for all exceptions defined in Crafter.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public class CrafterException extends RuntimeException {

    private static final long serialVersionUID = 8922403836288820982L;

    public CrafterException() {
        super();
    }

    public CrafterException(String message, Throwable cause) {
        super(message, cause);
    }

    public CrafterException(String message) {
        super(message);
    }

    public CrafterException(Throwable cause) {
        super(cause);
    }

}
