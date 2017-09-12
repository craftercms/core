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
package org.craftercms.core.util.spring;

import java.util.Map;
import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Picks up any bean of a specific type defined in the Spring application context, and puts it in a registry, with the key or ID as the
 * name of the context bean except the a prefix, e.g. if a bean is named "crafter.contentStoreAdapter.filesystem", and the prefix is
 * "crafter.contentStoreAdapter", the ID of the bean in the registry would be "filesystem".
 *
 * @author Alfonso Vásquez
 */
public abstract class AbstractBeanIdBasedRegistry<T> implements BeanPostProcessor {

    private static final Log logger = LogFactory.getLog(AbstractBeanIdBasedRegistry.class);

    private Map<String, T> registry;

    @PostConstruct
    public void init() {
        registry = createRegistry();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (getRegistryType().isAssignableFrom(bean.getClass())) {
            if (beanName.startsWith(getBeanNameIdPrefix())) {
                String id = beanName.substring(getBeanNameIdPrefix().length());
                register(id, (T)bean);

                if (logger.isDebugEnabled()) {
                    logger.debug("Bean '" + id + "' of type " + getRegistryType().getName() + " added to registry");
                }
            }
        }

        return bean;
    }

    public T get(String id) {
        return registry.get(id);
    }

    protected void register(String id, T obj) {
        registry.put(id, obj);
    }

    protected abstract Class<T> getRegistryType();

    protected abstract String getBeanNameIdPrefix();

    protected abstract Map<String, T> createRegistry();

}
