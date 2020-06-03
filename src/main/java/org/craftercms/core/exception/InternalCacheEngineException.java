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
 * Thrown when an error occurs in the underlying cache implementation used.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public class InternalCacheEngineException extends CacheException {

    private static final long serialVersionUID = -1829926648009823411L;

    public InternalCacheEngineException() {
    }

    public InternalCacheEngineException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalCacheEngineException(String message) {
        super(message);
    }

    public InternalCacheEngineException(Throwable cause) {
        super(cause);
    }

}
