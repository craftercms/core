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
package org.craftercms.core.util.template.impl.spel;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.craftercms.core.util.template.impl.spel.SpELStringTemplateCompiler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.craftercms.core.util.template.impl.IdentifiableStringTemplateSource;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.StringWriter;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/contexts/SpElStringTemplateCompilerTest.xml")
public class SpELStringTemplateCompilerTest {


    private static final String TEMPLATE = "Hello #{person.firstName} #{person.lastName}! Current OS name is " +
            "#{systemProperties['os.name']}. Current PATH env variable is #{systemEnvironment['PATH']}.";
    private static final String PROCESSED_TEMPLATE = "Hello %s %s! Current OS name is %s. Current PATH env variable " +
            "is %s.";

    @Autowired
    private SpELStringTemplateCompiler compiler;
    @Autowired
    private Person person;
    @Autowired
    private BeanFactory beanFactory;

    @Test
    public void testCompiler() throws Exception {
        StringWriter output = new StringWriter();

        compiler.compile(new IdentifiableStringTemplateSource("template", TEMPLATE)).process(beanFactory, output);

        String expected = String.format(PROCESSED_TEMPLATE, person.getFirstName(), person.getLastName(), System
                .getProperty("os.name"), System.getenv("PATH"));
        assertEquals(expected, output.toString());
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
