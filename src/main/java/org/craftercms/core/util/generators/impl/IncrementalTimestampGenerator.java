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
package org.craftercms.core.util.generators.impl;

import java.util.concurrent.atomic.AtomicLong;

import org.craftercms.core.util.generators.TimestampGenerator;

/**
 * Implementation of {@link org.craftercms.core.util.generators.TimestampGenerator}
 * that returns increments of a counter as timestamps.
 *
 * @author Alfonso Vásquez
 */
public class IncrementalTimestampGenerator implements TimestampGenerator {

    private AtomicLong counter;

    public IncrementalTimestampGenerator() {
        counter = new AtomicLong(0);
    }

    /**
     * Returns increments of a counter as timestamps.
     *
     * @return the timestamp
     */
    public long generate() {
        return counter.getAndIncrement();
    }
}
