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
package org.craftercms.core.util.xml.marshalling.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.apache.commons.lang3.ArrayUtils;
import org.craftercms.core.service.Item;
import org.craftercms.core.service.Tree;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.xstream.XStreamMarshaller;

import java.io.IOException;
import java.io.Writer;

/**
 * Extension of {@link org.springframework.oxm.xstream.XStreamMarshaller} that:
 * <p/>
 * <ol>
 * <li>Provides correct marshalling/unmarshalling support for Crafter objects.</li>
 * <li>Adds a {@code unsupportedClasses} property to exclude any unwanted classes from being
 * marshalled/unmarshalled</li>
 * </ol>
 *
 * @author Alfonso Vásquez
 */
public class CrafterXStreamMarshaller extends XStreamMarshaller {

    public static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    public static final String ITEM_CLASS_ALIAS = "item";
    public static final String TREE_CLASS_ALIAS = "tree";
    public static final String DOCUMENT_CLASS_ALIAS = "document";

    protected Class[] unsupportedClasses;
    protected boolean suppressXmlDeclaration;

    public CrafterXStreamMarshaller() {
        suppressXmlDeclaration = false;
    }

    public void setUnsupportedClasses(Class[] unsupportedClasses) {
        this.unsupportedClasses = unsupportedClasses;
    }

    public void setSuppressXmlDeclaration(boolean suppressXmlDeclaration) {
        this.suppressXmlDeclaration = suppressXmlDeclaration;
    }

    @Override
    protected void customizeXStream(XStream xstream) {
        xstream.alias(ITEM_CLASS_ALIAS, Item.class);
        xstream.alias(TREE_CLASS_ALIAS, Tree.class);
        xstream.aliasType(DOCUMENT_CLASS_ALIAS, Document.class);

        xstream.registerConverter(Dom4jDocumentConverter.INSTANCE);
    }

    /**
     * Returns true if the specified class:
     * <p/>
     * <ol>
     * <li></li>
     * <li>Is NOT the same, a subclass or subinterface of one of the unsupported classes.</li>
     * <li>Is the the same, a subclass or subinterface of one of the supported classes.</li>
     * </ol>
     * <p/>
     * Also returns true if there aren't any unsupported or supported classes.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean supports(Class clazz) {
        if (ArrayUtils.isNotEmpty(unsupportedClasses)) {
            for (Class unsupportedClass : unsupportedClasses) {
                if (unsupportedClass.isAssignableFrom(clazz)) {
                    return false;
                }
            }
        }

        return super.supports(clazz);
    }

    /**
     * Just as super(), but instead of a {@link com.thoughtworks.xstream.io.xml.CompactWriter} creates a {@link
     * EscapingCompactWriter}.
     * Also if the object graph is a Dom4j document, the document is written directly instead of using XStream.
     */
    @Override
    public void marshalWriter(Object graph, Writer writer, DataHolder dataHolder) throws XmlMappingException,
            IOException {
        if (graph instanceof Document) {
            OutputFormat outputFormat = OutputFormat.createCompactFormat();
            outputFormat.setSuppressDeclaration(suppressXmlDeclaration);

            XMLWriter xmlWriter = new XMLWriter(writer, outputFormat);
            try {
                xmlWriter.write((Document)graph);
            } finally {
                try {
                    xmlWriter.flush();
                } catch (Exception ex) {
                    logger.debug("Could not flush XMLWriter", ex);
                }
            }
        } else {
            if (!suppressXmlDeclaration) {
                writer.write(XML_DECLARATION);
            }

            HierarchicalStreamWriter streamWriter = new EscapingCompactWriter(writer);
            try {
                getXStream().marshal(graph, streamWriter, dataHolder);
            } catch (Exception ex) {
                throw convertXStreamException(ex, true);
            } finally {
                try {
                    streamWriter.flush();
                } catch (Exception ex) {
                    logger.debug("Could not flush HierarchicalStreamWriter", ex);
                }
            }
        }
    }

}
