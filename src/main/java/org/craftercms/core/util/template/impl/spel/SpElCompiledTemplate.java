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

import java.io.IOException;
import java.io.Writer;

import org.craftercms.core.exception.TemplateException;
import org.craftercms.core.util.template.CompiledTemplate;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class SpElCompiledTemplate implements CompiledTemplate {

    private Expression expression;
    private EvaluationContext evaluationContext;

    public SpElCompiledTemplate(Expression expression, EvaluationContext evaluationContext) {
        this.expression = expression;
        this.evaluationContext = evaluationContext;
    }

    @Override
    public void process(Object model, Writer output) throws TemplateException {
        try {
            String result = expression.getValue(evaluationContext, model, String.class);

            output.write(result);
            output.flush();
        } catch (IOException e) {
            throw new TemplateException("An I/O error occurred while writing to output", e);
        } catch (Exception e) {
            throw new TemplateException("Unable to process SpEL template:\n" + expression.getExpressionString(), e);
        }
    }

}
