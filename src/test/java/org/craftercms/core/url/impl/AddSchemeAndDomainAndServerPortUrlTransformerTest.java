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

import javax.servlet.http.HttpServletRequest;

import org.craftercms.commons.http.RequestContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.assertEquals;

/**
 * @author avasquez
 */
public class AddSchemeAndDomainAndServerPortUrlTransformerTest {

    private AddSchemeAndDomainAndServerPortUrlTransformer transformer;
    private MockHttpServletRequest request;

    @Before
    public void setUp() throws Exception {
        setUpTestTransformer();
        setUpTestRequest();
    }

    @After
    public void tearDown() throws Exception {
        removeCurrentRequest();
    }

    @Test
    public void testTransformUrl() throws Exception {
        request.setScheme("http");
        request.setServerName("craftercms.org");
        request.setServerPort(80);

        setCurrentRequest(request);

        String url = transformer.transformUrl(null, null, "/test");
        assertEquals("http://craftercms.org/test", url);

        request.setServerPort(8080);

        url = transformer.transformUrl(null, null, "/test");
        assertEquals("http://craftercms.org:8080/test", url);

        request.setScheme("https");
        request.setServerPort(443);

        url = transformer.transformUrl(null, null, "/test");
        assertEquals("https://craftercms.org/test", url);

        request.setServerPort(8443);

        url = transformer.transformUrl(null, null, "/test");
        assertEquals("https://craftercms.org:8443/test", url);

        removeCurrentRequest();
    }

    @Test
    public void testTransformUrlForceHttps() throws Exception {
        transformer.setForceHttps(true);

        request.setScheme("http");
        request.setServerName("craftercms.org");
        request.setServerPort(80);

        String url = transformer.transformUrl(null, null, "/test");
        assertEquals("https://craftercms.org/test", url);

        transformer.setHttpsPort(8443);

        url = transformer.transformUrl(null, null, "/test");
        assertEquals("https://craftercms.org:8443/test", url);
    }

    private void setUpTestTransformer() {
        transformer = new AddSchemeAndDomainAndServerPortUrlTransformer();
    }

    private void setUpTestRequest() {
        request = new MockHttpServletRequest();

        setCurrentRequest(request);
    }

    private void setCurrentRequest(HttpServletRequest request) {
        RequestContext.setCurrent(new RequestContext(request, null, null));
    }

    private void removeCurrentRequest() {
        RequestContext.clear();
    }

}
