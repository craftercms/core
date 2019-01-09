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
 * Thrown when an error occurred while a {@link org.craftercms.core.processors.ItemProcessor} was processing
 * an {@link org.craftercms.core.service.Item}.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 * @see org.craftercms.core.processors.ItemProcessor
 * @see org.craftercms.core.service.Item
 */
public class ItemProcessingException extends CrafterException {

    private static final long serialVersionUID = -7494746631220427670L;

    public ItemProcessingException() {
    }

    public ItemProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItemProcessingException(String message) {
        super(message);
    }

    public ItemProcessingException(Throwable cause) {
        super(cause);
    }

}
