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
 * Thrown to indicate that a specified scope is not valid, that is, it is not registered in the cache.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public class InvalidScopeException extends CacheException {

    private static final long serialVersionUID = -1829926648999823411L;

    public InvalidScopeException() {
    }

    public InvalidScopeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidScopeException(String message) {
        super(message);
    }

    public InvalidScopeException(Throwable cause) {
        super(cause);
    }

}
