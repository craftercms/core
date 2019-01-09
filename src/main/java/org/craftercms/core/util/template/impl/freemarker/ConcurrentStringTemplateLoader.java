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
package org.craftercms.core.util.template.impl.freemarker;

import java.io.Reader;
import java.io.StringReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import freemarker.cache.TemplateLoader;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class ConcurrentStringTemplateLoader implements TemplateLoader {

    private final Map<String, StringTemplateSource> templates;

    public ConcurrentStringTemplateLoader() {
        this.templates = new ConcurrentHashMap<String, StringTemplateSource>();
    }

    public boolean hasTemplateSource(String name) {
        return templates.containsKey(name);
    }

    public void putTemplateSource(String name, String templateSource) {
        putTemplateSource(name, templateSource, System.currentTimeMillis());
    }

    public void putTemplateSource(String name, String templateSource, long lastModified) {
        templates.put(name, new StringTemplateSource(name, templateSource, lastModified));
    }

    public void removeTemplateSource(String name) {
        templates.remove(name);
    }

    public void closeTemplateSource(Object templateSource) {
    }

    public Object findTemplateSource(String name) {
        return templates.get(name);
    }

    public long getLastModified(Object templateSource) {
        return ((StringTemplateSource)templateSource).lastModified;
    }

    public Reader getReader(Object templateSource, String encoding) {
        return new StringReader(((StringTemplateSource)templateSource).source);
    }

    public static class StringTemplateSource {
        private final String name;
        private final String source;
        private final long lastModified;

        public StringTemplateSource(String name, String source, long lastModified) {
            if (name == null) {
                throw new IllegalArgumentException("name == null");
            }
            if (source == null) {
                throw new IllegalArgumentException("source == null");
            }
            if (lastModified < -1L) {
                throw new IllegalArgumentException("lastModified < -1L");
            }
            this.name = name;
            this.source = source;
            this.lastModified = lastModified;
        }

        public String getName() {
            return name;
        }

        public String getSource() {
            return source;
        }

        public long getLastModified() {
            return lastModified;
        }

        public boolean equals(Object obj) {
            if (obj instanceof StringTemplateSource) {
                return name.equals(((StringTemplateSource)obj).name);
            }
            return false;
        }

        public int hashCode() {
            return name.hashCode();
        }

    }

}
