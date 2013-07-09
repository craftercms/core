/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
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
package org.craftercms.crafter.core.url.impl;

import org.craftercms.core.url.impl.ExtractRequestAttributesUrlTransformer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.craftercms.core.util.HttpServletUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.HashMap;
import java.util.Map;

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
        assertEquals("1", HttpServletUtils.getAttribute("hotel", HttpServletUtils.SCOPE_REQUEST));
        assertEquals("42", HttpServletUtils.getAttribute("booking", HttpServletUtils.SCOPE_REQUEST));
    }

    private void setUpTestTransformer() {
        transformer = new ExtractRequestAttributesUrlTransformer();
        transformer.setUriTemplate(URI_TEMPLATE);
    }

    private void setUpTestRequestAttributes() {
        RequestContextHolder.setRequestAttributes(new StubRequestAtrributes());
    }

    private void tearDownTestRequestAttributes() {
        RequestContextHolder.resetRequestAttributes();
    }

    private static class StubRequestAtrributes implements RequestAttributes {

        private Map<String, Object> attributes;

        private StubRequestAtrributes() {
            attributes = new HashMap<String, Object>();
        }

        @Override
        public Object getAttribute(String name, int scope) {
            return attributes.get(name);
        }

        @Override
        public void setAttribute(String name, Object value, int scope) {
            attributes.put(name, value);
        }

        @Override
        public void removeAttribute(String name, int scope) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public String[] getAttributeNames(int scope) {
            return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void registerDestructionCallback(String name, Runnable callback, int scope) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Object resolveReference(String key) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public String getSessionId() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Object getSessionMutex() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

    }

}
