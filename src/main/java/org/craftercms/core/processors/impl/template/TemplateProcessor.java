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
package org.craftercms.core.processors.impl.template;

import java.io.StringWriter;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craftercms.core.exception.ItemProcessingException;
import org.craftercms.core.exception.TemplateException;
import org.craftercms.core.processors.ItemProcessor;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.service.Item;
import org.craftercms.core.util.template.CompiledTemplate;
import org.craftercms.core.util.template.TemplateCompiler;
import org.craftercms.core.util.template.impl.IdentifiableStringTemplateSource;
import org.craftercms.core.util.xml.NodeScanner;
import org.dom4j.Document;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Required;

/**
 * {@link ItemProcessor} that processes the content of certain XML nodes in item descriptors as templates. Template
 * engines that can be used are adapted through the {@link TemplateCompiler} interface. Current implementations of this
 * interface include an adapter for FreeMarker templates and another one for SpEL (Spring Expression Language)
 * templates.
 *
 * @author Sumer Jabri
 * @author Alfonso Vásquez
 * @see TemplateCompiler
 * @see org.craftercms.core.util.template.impl.freemarker.FreeMarkerStringTemplateCompiler
 * @see org.craftercms.core.util.template.impl.spel.SpELStringTemplateCompiler
 * @see <a href="http://freemarker.sourceforge.net/">FreeMarker</a>
 * @see <a href="http://static.springsource.org/spring/docs/3.0.x/reference/expressions.html>Spring Expression
 * Language (SpEL)</a>
 */
public class TemplateProcessor implements ItemProcessor {

    private static final Log logger = LogFactory.getLog(TemplateProcessor.class);

    /**
     * {@code NodeScanner} for template nodes.
     */
    protected NodeScanner templateNodeScanner;
    /**
     * Template compiler. It basically compiles the template provided in an element and then processes it by using
     * a model obtained through the {@code modelFactory}.
     */
    protected TemplateCompiler<IdentifiableStringTemplateSource> templateCompiler;
    /**
     * Factory that provides the models for the templates.
     */
    protected NodeTemplateModelFactory modelFactory;

    /**
     * Sets the {@code NodeScanner} for template nodes.
     */
    @Required
    public void setTemplateNodeScanner(NodeScanner templateNodeScanner) {
        this.templateNodeScanner = templateNodeScanner;
    }

    /**
     * Sets the template compiler. It basically compiles the template provided in an element and then processes it by
     * using a model obtained through the {@code modelFactory}.
     */
    @Required
    public void setTemplateCompiler(TemplateCompiler<IdentifiableStringTemplateSource> templateCompiler) {
        this.templateCompiler = templateCompiler;
    }

    /**
     * Sets the factory that provides the models for the templates.
     */
    @Required
    public void setModelFactory(NodeTemplateModelFactory modelFactory) {
        this.modelFactory = modelFactory;
    }

    /**
     * Processes the content of certain nodes (found by the {@code NodeScanner} in the item's descriptor as templates,
     * by compiling the node text templates through the {@code templateCompiler} and then processing the compiled
     * template with a model returned by {@code modelFactory}.
     *
     * @throws ItemProcessingException if an error occurred while processing a template
     */
    public Item process(Context context, CachingOptions cachingOptions, Item item) throws ItemProcessingException {
        String descriptorUrl = item.getDescriptorUrl();
        Document descriptorDom = item.getDescriptorDom();

        if (descriptorDom != null) {
            List<Node> templateNodes = templateNodeScanner.scan(descriptorDom);
            if (CollectionUtils.isNotEmpty(templateNodes)) {
                for (Node templateNode : templateNodes) {
                    String templateNodePath = templateNode.getUniquePath();

                    if (logger.isDebugEnabled()) {
                        logger.debug("Template found in " + descriptorUrl + " at " + templateNodePath);
                    }

                    String templateId = templateNodePath + "@" + descriptorUrl;
                    String template = templateNode.getText();
                    IdentifiableStringTemplateSource templateSource = new IdentifiableStringTemplateSource(templateId,
                                                                                                           template);

                    Object model = modelFactory.getModel(item, templateNode, template);
                    StringWriter output = new StringWriter();

                    try {
                        CompiledTemplate compiledTemplate = templateCompiler.compile(templateSource);
                        compiledTemplate.process(model, output);
                    } catch (TemplateException e) {
                        throw new ItemProcessingException("Unable to process the template " + templateId, e);
                    }

                    templateNode.setText(output.toString());
                }
            }
        }

        return item;
    }

    /**
     * Returns true if the specified {@code TemplateProcessor}'s and this instance's fields are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TemplateProcessor that = (TemplateProcessor)o;

        if (!modelFactory.equals(that.modelFactory)) {
            return false;
        }
        if (!templateCompiler.equals(that.templateCompiler)) {
            return false;
        }
        if (!templateNodeScanner.equals(that.templateNodeScanner)) {
            return false;
        }

        return true;
    }

    /**
     * Returns the hash code for this instance, which is basically the combination of the hash code of each field.
     * As with any other {@link ItemProcessor}, this method is defined because any processor which is passed in the
     * method call of a {@link org.craftercms.core.service.ContentStoreService} can be used as part of a
     * key for caching.
     */
    @Override
    public int hashCode() {
        int result = templateNodeScanner.hashCode();
        result = 31 * result + templateCompiler.hashCode();
        result = 31 * result + modelFactory.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "TemplateProcessor[" +
               "modelFactory=" + modelFactory +
               ", templateNodeScanner=" + templateNodeScanner +
               ", templateCompiler=" + templateCompiler +
               ']';
    }

}
