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
 * Thrown when a URL can't be correctly transformed to a different URL.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 * @see org.craftercms.core.url.UrlTransformationEngine
 */
public class UrlTransformationException extends CrafterException {

    private static final long serialVersionUID = 1987858998547665933L;

    public UrlTransformationException() {
    }

    public UrlTransformationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UrlTransformationException(String message) {
        super(message);
    }

    public UrlTransformationException(Throwable cause) {
        super(cause);
    }

}
