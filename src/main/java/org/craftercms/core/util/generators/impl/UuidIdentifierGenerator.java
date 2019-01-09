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
package org.craftercms.core.util.generators.impl;

import java.util.UUID;

import org.craftercms.core.util.generators.IdentifierGenerator;

/**
 * Generator for UUID identifiers.
 *
 * @author Alfonso Vásquez
 */
public class UuidIdentifierGenerator implements IdentifierGenerator {

    /**
     * Generates a UUID identifier. UUIDs are unique between any calls to any instances.
     *
     * @return the UUID, as string
     */
    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }

}
