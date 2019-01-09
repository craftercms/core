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
package org.craftercms.core.processors;

import org.craftercms.core.exception.CrafterException;
import org.craftercms.core.service.Item;

/**
 * Resolves the {@link ItemProcessor} to use for a given {@link Item}.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 */
public interface ItemProcessorResolver {

    /**
     * Returns a {@link ItemProcessor} for a given {@link org.craftercms.core.service.Item}.
     *
     * @throws org.craftercms.core.exception.CrafterException
     *          if an error occurs while resolving the processor
     */
    ItemProcessor getProcessor(Item item) throws CrafterException;

}
