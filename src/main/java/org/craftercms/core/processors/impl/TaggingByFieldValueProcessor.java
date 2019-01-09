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

package org.craftercms.core.processors.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craftercms.core.exception.ItemProcessingException;
import org.craftercms.core.processors.ItemProcessor;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.service.Item;
import org.craftercms.core.util.XmlUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Required;

import java.util.Map;

/**
 * {@link ItemProcessor} that adds a new tag or field to items that have a certain tag/field and value.
 * @author joseross
 */
public class TaggingByFieldValueProcessor extends AbstractTaggingProcessor {

    /**
     * The name of the existing field to query.
     */
    protected String sourceField;

    /**
     * Map of values, the keys can be regular expressions.
     */
    protected Map<String, String> valueMapping;

    @Required
    public void setSourceField(String sourceField) {
        this.sourceField = sourceField;
    }

    @Required
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Required
    public void setValueMapping(Map<String, String> valueMapping) {
        this.valueMapping = valueMapping;
    }

    @Override
    protected String getTagValues(Item item) {
        String value = defaultValue;
        Document document = item.getDescriptorDom();
        if (document != null) {
            String sourceValue = XmlUtils.selectSingleNodeValue(document.getRootElement(), sourceField);
            if (StringUtils.isNotEmpty(sourceValue)) {
                for (Map.Entry<String, String> entry : valueMapping.entrySet()) {
                    if (sourceValue.matches(entry.getKey())) {
                        value = entry.getValue();
                        break;
                    }
                }
            }
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaggingByFieldValueProcessor that = (TaggingByFieldValueProcessor) o;

        if (sourceField != null ? !sourceField.equals(that.sourceField) : that.sourceField != null) return false;
        if (newField != null ? !newField.equals(that.newField) : that.newField != null) return false;
        if (defaultValue != null ? !defaultValue.equals(that.defaultValue) : that.defaultValue != null) return false;
        return valueMapping != null ? valueMapping.equals(that.valueMapping) : that.valueMapping == null;

    }

    @Override
    public int hashCode() {
        int result = sourceField != null ? sourceField.hashCode() : 0;
        result = 31 * result + (newField != null ? newField.hashCode() : 0);
        result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
        result = 31 * result + (valueMapping != null ? valueMapping.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TaggingByFieldValueProcessor{" +
            "sourceField='" + sourceField + '\'' +
            ", newField='" + newField + '\'' +
            ", defaultValue='" + defaultValue + '\'' +
            ", valueMapping=" + valueMapping +
            '}';
    }

}
