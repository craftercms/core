package org.craftercms.core.processors.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craftercms.core.exception.ItemProcessingException;
import org.craftercms.core.processors.ItemProcessor;
import org.craftercms.core.service.CachingOptions;
import org.craftercms.core.service.Context;
import org.craftercms.core.service.Item;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Required;


/**
 * Base {@link ItemProcessor} to add a new field to documents.
 * @author joseross
 */
public abstract class AbstractTaggingProcessor implements ItemProcessor {

    private static final Log logger = LogFactory.getLog(AbstractTaggingProcessor.class);

    /**
     * Name of the new field to add.
     */
    protected String newField;


    /**
     * Optional default value for the new field.
     */
    protected String defaultValue;

    @Required
    public void setNewField(String newField) {
        this.newField = newField;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * If this method returns null or an empty string the item will not be tagged.
     * @param item
     * @return values to use for tagging the item
     */
    protected abstract String getTagValues(Item item);

    /**
     * Tags the item adding the new field with the specified values.
     * @param item
     * @param values
     */
    protected void addNewField(Item item, String values) {
        if(logger.isDebugEnabled()) {
            logger.debug("Tagging item with field: " + newField + " and value: " + values);
        }
        Document document = item.getDescriptorDom();
        if(document != null) {
            Element root = document.getRootElement();
            if(root != null) {
                for(String value : values.split(",")) {
                    Element newElement = root.addElement(newField);
                    newElement.setText(value);
                }
            }
        }
    }

    @Override
    public Item process(Context context, CachingOptions cachingOptions, Item item) throws ItemProcessingException {
        if(logger.isDebugEnabled()) {
            logger.debug("Processing item: " + item);
        }
        String values = getTagValues(item);
        if(StringUtils.isNotEmpty(values)) {
            if(logger.isDebugEnabled()) {
                logger.debug("Item will be tagged");
            }
            addNewField(item, values);
        }
        return item;
    }

}
