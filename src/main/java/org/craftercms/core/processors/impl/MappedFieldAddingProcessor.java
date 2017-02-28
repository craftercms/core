package org.craftercms.core.processors.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craftercms.core.exception.ItemProcessingException;
import org.craftercms.core.processors.ItemProcessor;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.service.Item;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Required;

import java.util.Map;

/**
 * {@link ItemProcessor} to add a new field with values mapped from an existing one. Can be used to label documents.
 * @author joseross
 */
public class MappedFieldAddingProcessor implements ItemProcessor {

    private static final Log logger = LogFactory.getLog(MappedFieldAddingProcessor.class);

    /**
     * The name of the existing field to query.
     */
    protected String sourceField;

    /**
     * The name of the new field to add.
     */
    protected String mappedField;

    /**
     * The default value to use.
     */
    protected String defaultValue;

    /**
     * Map of values, the keys can be regular expressions.
     */
    protected Map<String, String> valueMapping;

    @Required
    public void setSourceField(String sourceField) {
        this.sourceField = sourceField;
    }

    @Required
    public void setMappedField(String mappedField) {
        this.mappedField = mappedField;
    }

    @Required
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Required
    public void setValueMapping(Map<String, String> valueMapping) {
        this.valueMapping = valueMapping;
    }

    protected void addMappedField(Document document, String mappedValue) {
        Element root = document.getRootElement();
        Element mappedElement = root.addElement(mappedField);
        mappedElement.setText(mappedValue);
    }

    public Item process(Context context, CachingOptions cachingOptions, Item item) throws ItemProcessingException {
        Document document = item.getDescriptorDom();
        if(document != null) {
            if(logger.isDebugEnabled()) {
                logger.debug("Processing item " + item);
            }
            Node sourceNode = document.selectSingleNode(sourceField);
            if(sourceNode != null) {
                String originalValue = sourceNode.getStringValue();
                String mappedValue = defaultValue;
                if(logger.isDebugEnabled()) {
                    logger.debug("Found field " + sourceField + " with value " + originalValue);
                }
                for(String key : valueMapping.keySet()) {
                    if(originalValue.matches(key)) {
                        mappedValue = valueMapping.get(key);
                        break;
                    }
                }
                if(logger.isDebugEnabled()) {
                    logger.debug("Adding field " + mappedField + " with value " + mappedValue);
                }
                addMappedField(document, mappedValue);
            }
        }

        return item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MappedFieldAddingProcessor that = (MappedFieldAddingProcessor) o;

        if (sourceField != null ? !sourceField.equals(that.sourceField) : that.sourceField != null) return false;
        if (mappedField != null ? !mappedField.equals(that.mappedField) : that.mappedField != null) return false;
        if (defaultValue != null ? !defaultValue.equals(that.defaultValue) : that.defaultValue != null) return false;
        return valueMapping != null ? valueMapping.equals(that.valueMapping) : that.valueMapping == null;

    }

    @Override
    public int hashCode() {
        int result = sourceField != null ? sourceField.hashCode() : 0;
        result = 31 * result + (mappedField != null ? mappedField.hashCode() : 0);
        result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
        result = 31 * result + (valueMapping != null ? valueMapping.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MappedFieldAddingProcessor{" +
            "sourceField='" + sourceField + '\'' +
            ", mappedField='" + mappedField + '\'' +
            ", defaultValue='" + defaultValue + '\'' +
            ", valueMapping=" + valueMapping +
            '}';
    }

}
