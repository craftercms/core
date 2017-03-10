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

import java.util.Map;

/**
 * {@link ItemProcessor} that adds a new field based on the document path.
 * @author joseross
 */
public class ByPathFieldAddingProcessor implements ItemProcessor {

    private static final Log logger = LogFactory.getLog(ByPathFieldAddingProcessor.class);

    /**
     * The name of the new field to add.
     */
    protected String fieldName;

    /**
     * Map of paths & values, values can be multiple (separated by commas).
     */
    protected Map<String, String> pathMapping;

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setPathMapping(Map<String, String> pathMapping) {
        this.pathMapping = pathMapping;
    }

    protected void addField(Document document, String values) {
        if(document != null) {
            if(logger.isDebugEnabled()) {
                logger.debug("Adding field '" + fieldName + "' with value: " + values);
            }
            Element root = document.getRootElement();
            for(String value : values.split(",")) {
                Element newElement = root.addElement(fieldName);
                newElement.setText(value);
            }
        }
    }

    @Override
    public Item process(Context context, CachingOptions cachingOptions, Item item) throws ItemProcessingException {
        if(logger.isDebugEnabled()) {
            logger.debug("Processing item: " + item);
        }
        for(String path : pathMapping.keySet()) {
            if(item.getUrl().matches(path)) {
                if(logger.isDebugEnabled()) {
                    logger.debug("Item matches path: " + path);
                }
                addField(item.getDescriptorDom(), pathMapping.get(path));
                break;
            }
        }
        return item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ByPathFieldAddingProcessor that = (ByPathFieldAddingProcessor) o;

        if (fieldName != null ? !fieldName.equals(that.fieldName) : that.fieldName != null) return false;
        return pathMapping != null ? pathMapping.equals(that.pathMapping) : that.pathMapping == null;
    }

    @Override
    public int hashCode() {
        int result = fieldName != null ? fieldName.hashCode() : 0;
        result = 31 * result + (pathMapping != null ? pathMapping.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ByPathFieldAddingProcessor{" +
            "fieldName='" + fieldName + '\'' +
            ", pathMapping=" + pathMapping +
            '}';
    }

}
