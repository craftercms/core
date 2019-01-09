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

import org.craftercms.commons.http.RequestContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.assertEquals;

/**
 * @author Alfonso Vásquez
 */
public class ExtractRequestAttributesUrlTransformerTest {

    private static final String URI_TEMPLATE = "/site/website/hotels/{hotel}/bookings/{booking}/index.xml";
    private static final String URL = "/site/website/hotels/1/bookings/42/index.xml";
    private static final String TRANSFORMED_URL = "/site/website/hotels/bookings/index.xml";

    private ExtractRequestAttributesUrlTransformer transformer;

    @Before
    public void setUp() throws Exception {
        setUpTestRequestAttributes();
        setUpTestTransformer();
    }

    @After
    public void tearDown() throws Exception {
        tearDownTestRequestAttributes();
    }

    @Test
    public void testTransformUrl() throws Exception {
        String transformedUrl = transformer.transformUrl(null, null, URL);
        assertEquals(TRANSFORMED_URL, transformedUrl);
        assertEquals("1", RequestContext.getCurrent().getRequest().getAttribute("hotel"));
        assertEquals("42", RequestContext.getCurrent().getRequest().getAttribute("booking"));
    }

    private void setUpTestTransformer() {
        transformer = new ExtractRequestAttributesUrlTransformer();
        transformer.setUriTemplate(URI_TEMPLATE);
    }

    private void setUpTestRequestAttributes() {
        RequestContext.setCurrent(new RequestContext(new MockHttpServletRequest(), null, null));
    }

    private void tearDownTestRequestAttributes() {
        RequestContext.clear();
    }

}
