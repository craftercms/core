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
package org.craftercms.core.util.template.impl.freemarker;

import static org.junit.Assert.*;

import org.craftercms.core.util.template.impl.freemarker.FreeMarkerStringTemplateCompiler;
import org.junit.Before;
import org.junit.Test;
import org.craftercms.core.util.template.impl.IdentifiableStringTemplateSource;

import java.io.StringWriter;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class FreemarkerStringTemplateCompilerTest {

    private static final Person model = new Person("John", "Doe");

    private static final String TEMPLATE = "Hello ${firstName} ${lastName}!";
    private static final String PROCESSED_TEMPLATE = "Hello " + model.getFirstName() + " " + model.getLastName() + "!";

    private FreeMarkerStringTemplateCompiler compiler;

    @Before
    public void setUp() throws Exception {
        setUpTestCompiler();
    }

    @Test
    public void testCompiler() throws Exception {
        StringWriter output = new StringWriter();

        compiler.compile(new IdentifiableStringTemplateSource("template", TEMPLATE)).process(model, output);
        assertEquals(PROCESSED_TEMPLATE, output.toString());
    }

    private void setUpTestCompiler() {
        compiler = new FreeMarkerStringTemplateCompiler();
    }

    public static class Person {

        private String firstName;
        private String lastName;

        private Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

    }

}
