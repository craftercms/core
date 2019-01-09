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
 * Thrown when a template related error occurs.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 * @see org.craftercms.core.util.template.TemplateCompiler
 */
public class TemplateException extends CrafterException {

    private static final long serialVersionUID = -3571190479982716681L;

    public TemplateException() {
    }

    public TemplateException(String message, Throwable cause) {
        super(message, cause);
    }

    public TemplateException(String message) {
        super(message);
    }

    public TemplateException(Throwable cause) {
        super(cause);
    }

}
