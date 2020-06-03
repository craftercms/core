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
package org.craftercms.core.url.impl;

import org.craftercms.core.url.impl.ReplacePatternFirstUrlTransformer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class ReplacePatternFirstUrlTransformerTest {

    private static final String URL = "/xml/index.xml";
    private static final String TRANSFORMED_URL = "/html/index.xml";

    private ReplacePatternFirstUrlTransformer transformer;

    @Before
    public void setUp() throws Exception {
        setUpTestTransformer();
    }

    @Test
    public void testTransformer() throws Exception {
        String transformedUrl = transformer.transformUrl(null, null, URL);
        assertEquals(TRANSFORMED_URL, transformedUrl);
    }

    private void setUpTestTransformer() {
        transformer = new ReplacePatternFirstUrlTransformer();
        transformer.setPatternToReplace("xml");
        transformer.setReplacement("html");
    }

}
