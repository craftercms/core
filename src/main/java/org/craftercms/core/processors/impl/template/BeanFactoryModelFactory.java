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

import org.craftercms.core.service.Item;
import org.dom4j.Node;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * {@link NodeTemplateModelFactory} that always returns the {@link BeanFactory} of the Spring application context
 * where this factory is configured. Very useful in conjunction with the
 * {@link org.craftercms.core.util.template.impl.spel.SpELStringTemplateCompiler}, since you can use SpEL in
 * the node template just as you would use SpEl in a Spring XML configuration file.
 *
 * @author Alfonso Vásquez
 */
public class BeanFactoryModelFactory implements NodeTemplateModelFactory, BeanFactoryAware {

    /**
     * The {@code BeanFactory} of the current Spring application context.
     */
    private BeanFactory beanFactory;

    /**
     * Sets the {@code BeanFactory} of the current Spring application context.
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * Returns always the {@link BeanFactory} of the current Spring application context as the model.
     */
    @Override
    public Object getModel(Item item, Node node, String template) {
        return beanFactory;
    }

}
