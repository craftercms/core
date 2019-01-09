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

import freemarker.template.Configuration;
import org.craftercms.core.exception.TemplateException;
import org.craftercms.core.util.template.CompiledTemplate;
import org.craftercms.core.util.template.TemplateCompiler;
import org.craftercms.core.util.template.impl.IdentifiableStringTemplateSource;
import org.craftercms.core.util.template.impl.freemarker.ConcurrentStringTemplateLoader.StringTemplateSource;
import org.springframework.beans.factory.annotation.Required;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class FreeMarkerStringTemplateCompiler implements TemplateCompiler<IdentifiableStringTemplateSource> {

    private Configuration templateConfiguration;
    private ConcurrentStringTemplateLoader templateLoader;

    public FreeMarkerStringTemplateCompiler() {
        templateLoader = new ConcurrentStringTemplateLoader();

        templateConfiguration = new Configuration(Configuration.VERSION_2_3_23);
        templateConfiguration.setTemplateLoader(templateLoader);
        // Don't wait to check whether a template was updated
        templateConfiguration.setTemplateUpdateDelayMilliseconds(0);
    }

    @Required
    public void setTemplateConfiguration(Configuration templateConfiguration) {
        this.templateConfiguration = templateConfiguration;
    }

    @Required
    public void setTemplateLoader(ConcurrentStringTemplateLoader templateLoader) {
        this.templateLoader = templateLoader;
    }

    public CompiledTemplate compile(IdentifiableStringTemplateSource templateSource) throws TemplateException {
        // Replace '/' with any other char because Freemarker messes up IDs with '/' because it thinks they're paths.
        String id = templateSource.getId().replace('/', ';');
        String source = templateSource.getSource();

        StringTemplateSource currentTemplateSource = (StringTemplateSource)templateLoader.findTemplateSource(id);

        if (currentTemplateSource == null || !currentTemplateSource.getSource().equals(source)) {
            templateLoader.putTemplateSource(id, source);
        }

        try {
            return new FreeMarkerCompiledTemplate(templateConfiguration.getTemplate(id));
        } catch (Exception e) {
            throw new TemplateException("Unable to compile Freemarker template:\n" + source, e);
        }
    }

}
