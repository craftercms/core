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
package org.craftercms.core.url.impl;

import org.craftercms.core.url.impl.LongToShortUrlTransformer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class LongToShortUrlTransformerTest {

    private static final String LONG_URL = "/001_folder/002_subfolder/003_index.html/";
    private static final String SHORT_URL = "/folder/subfolder/index.html/";

    private LongToShortUrlTransformer transformer;

    @Before
    public void setUp() throws Exception {
        setUpTestTransformer();
    }

    @Test
    public void testTransformer() throws Exception {
        String transformedUrl = transformer.transformUrl(null, null, LONG_URL);
        assertEquals(SHORT_URL, transformedUrl);
    }

    private void setUpTestTransformer() {
        transformer = new LongToShortUrlTransformer();
    }

}
