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

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.dom4j.Document;

/**
 * XStream converter to marshal Dom4j {@link Document} (unmarshalling is unsupported).
 *
 * @author Alfonso Vásquez
 */
public class Dom4jDocumentConverter implements Converter {

    public static final Dom4jDocumentConverter INSTANCE = new Dom4jDocumentConverter();

    private Dom4jDocumentConverter() {
    }

    @Override
    public boolean canConvert(Class type) {
        return Document.class.isAssignableFrom(type);
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Document document = (Document)source;

        EscapingCompactWriter escapingCompactWriter = (EscapingCompactWriter)writer.underlyingWriter();
        escapingCompactWriter.setEscapeXml(false);
        escapingCompactWriter.setValue(document.getRootElement().asXML());
        escapingCompactWriter.setEscapeXml(true);
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        throw new UnsupportedOperationException();
    }

}
