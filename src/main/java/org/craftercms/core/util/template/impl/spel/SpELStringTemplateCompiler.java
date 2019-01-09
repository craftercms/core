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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;

import org.craftercms.core.exception.TemplateException;
import org.craftercms.core.util.template.CompiledTemplate;
import org.craftercms.core.util.template.TemplateCompiler;
import org.craftercms.core.util.template.impl.IdentifiableStringTemplateSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.expression.BeanFactoryAccessor;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.convert.ConversionService;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;
import org.springframework.expression.spel.support.StandardTypeLocator;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class SpELStringTemplateCompiler implements TemplateCompiler<IdentifiableStringTemplateSource>,
    BeanFactoryAware {

    private ExpressionParser parser;
    private ParserContext parserContext;
    private EvaluationContext evalContext;
    private ConfigurableBeanFactory beanFactory;

    private Map<String, Expression> expressionCache;

    public SpELStringTemplateCompiler() {
        parser = new SpelExpressionParser();
        parserContext = new TemplateParserContext();
        expressionCache = new ConcurrentHashMap<String, Expression>();
    }

    public void setParserContext(ParserContext parserContext) {
        this.parserContext = parserContext;
    }

    public void setEvalContext(EvaluationContext evalContext) {
        this.evalContext = evalContext;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof ConfigurableBeanFactory)) {
            throw new IllegalArgumentException("beanFactory should be of type ConfigurableBeanFactory");
        }

        this.beanFactory = (ConfigurableBeanFactory)beanFactory;
    }

    @PostConstruct
    public void init() {
        if (evalContext == null) {
            evalContext = new StandardEvaluationContext();
        }

        if (evalContext instanceof StandardEvaluationContext) {
            StandardEvaluationContext standardEvalContext = (StandardEvaluationContext)evalContext;
            // PropertyAccessor used when the model is a BeanFactory.
            standardEvalContext.addPropertyAccessor(new BeanFactoryAccessor());
            if (beanFactory != null) {
                if (standardEvalContext.getBeanResolver() == null) {
                    standardEvalContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
                }
                if (standardEvalContext.getTypeLocator() == null) {
                    standardEvalContext.setTypeLocator(new StandardTypeLocator(beanFactory.getBeanClassLoader()));
                }
                if (standardEvalContext.getTypeConverter() == null) {
                    ConversionService conversionService = beanFactory.getConversionService();
                    if (conversionService != null) {
                        standardEvalContext.setTypeConverter(new StandardTypeConverter(conversionService));
                    }
                }
            }
        }
    }

    @Override
    public CompiledTemplate compile(IdentifiableStringTemplateSource templateSource) throws TemplateException {
        String id = templateSource.getId();
        String source = templateSource.getSource();

        try {
            Expression expression = expressionCache.get(id);
            if (expression == null || !expression.getExpressionString().equals(source)) {
                expression = parser.parseExpression(source, parserContext);
                expressionCache.put(id, expression);
            }

            return new SpElCompiledTemplate(expression, evalContext);
        } catch (Exception e) {
            throw new TemplateException("Unable to compile SpEL template:\n" + source, e);
        }
    }

}
