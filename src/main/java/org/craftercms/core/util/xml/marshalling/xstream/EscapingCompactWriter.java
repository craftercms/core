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

import java.io.Writer;

import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.naming.NameCoder;

/**
 * Extension of {@link com.thoughtworks.xstream.io.xml.CompactWriter} that enables setting on/off XML escaping.
 *
 * @author Alfonso Vásquez
 */
public class EscapingCompactWriter extends com.thoughtworks.xstream.io.xml.CompactWriter {

    protected boolean escapeXml;

    public EscapingCompactWriter(Writer writer) {
        super(writer);
    }

    public EscapingCompactWriter(Writer writer, int mode) {
        super(writer, mode);
    }

    public EscapingCompactWriter(Writer writer, NameCoder nameCoder) {
        super(writer, nameCoder);
    }

    public EscapingCompactWriter(Writer writer, int mode, NameCoder nameCoder) {
        super(writer, mode, nameCoder);
    }

    public void setEscapeXml(boolean escapeXml) {
        this.escapeXml = escapeXml;
    }

    @Override
    protected void writeText(QuickWriter writer, String text) {
        if (escapeXml) {
            super.writeText(writer, text);
        } else {
            writer.write(text);
        }
    }

    @Override
    protected void writeAttributeValue(QuickWriter writer, String text) {
        if (escapeXml) {
            super.writeAttributeValue(writer, text);
        } else {
            writer.write(text);
        }
    }

}
