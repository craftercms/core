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

import org.craftercms.core.url.impl.AddDebugParamUrlTransformer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.craftercms.core.url.impl.AddDebugParamUrlTransformer.DEFAULT_DEBUG_URL_PARAM;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class AddDebugParamUrlTransformerTest {

    private static final String URL = "/index.html";
    private static final String URL_WITH_PARAM = "/index.html?param=value";

    private AddDebugParamUrlTransformer transformer;

    @Before
    public void setUp() throws Exception {
        setUpTestTransformer();
    }

    @Test
    public void testTransformer() throws Exception {
        String transformedUrl = transformer.transformUrl(null, null, URL);
        assertEquals(URL + "?" + DEFAULT_DEBUG_URL_PARAM +  "=true", transformedUrl);

        transformedUrl = transformer.transformUrl(null, null, URL_WITH_PARAM);
        assertEquals(URL_WITH_PARAM + "&" + DEFAULT_DEBUG_URL_PARAM + "=true", transformedUrl);
    }

    private void setUpTestTransformer() {
        transformer = new AddDebugParamUrlTransformer();
    }

}
