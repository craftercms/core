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

import java.io.IOException;
import java.io.Writer;

import freemarker.template.Template;
import org.craftercms.core.exception.TemplateException;
import org.craftercms.core.util.template.CompiledTemplate;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class FreeMarkerCompiledTemplate implements CompiledTemplate {

    private Template template;

    public FreeMarkerCompiledTemplate(Template template) {
        this.template = template;
    }

    public void process(Object model, Writer output) throws TemplateException {
        try {
            template.process(model, output);
        } catch (IOException e) {
            throw new TemplateException("An I/O error occurred while writing to output", e);
        } catch (Exception e) {
            throw new TemplateException("Unable to process Freemarker template:\n" + template, e);
        }
    }

}
