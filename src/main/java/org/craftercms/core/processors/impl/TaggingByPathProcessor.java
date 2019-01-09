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

import org.craftercms.core.processors.ItemProcessor;
import org.craftercms.core.service.Item;

import java.util.Map;

/**
 * {@link ItemProcessor} that adds a new tag or field to items on specific paths.
 * @author joseross
 */
public class TaggingByPathProcessor extends AbstractTaggingProcessor {

    /**
     * Map of paths & values, values can be multiple (separated by commas).
     */
    protected Map<String, String> pathMapping;

    public void setPathMapping(Map<String, String> pathMapping) {
        this.pathMapping = pathMapping;
    }

    @Override
    protected String getTagValues(Item item) {
        String value = null;
        String path = item.getUrl();
        for(Map.Entry<String, String> entry : pathMapping.entrySet()) {
            if (path.matches(entry.getKey())) {
                value = entry.getValue();
            }
        }
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaggingByPathProcessor that = (TaggingByPathProcessor) o;

        if (newField != null ? !newField.equals(that.newField) : that.newField != null) return false;
        return pathMapping != null ? pathMapping.equals(that.pathMapping) : that.pathMapping == null;
    }

    @Override
    public int hashCode() {
        int result = newField != null ? newField.hashCode() : 0;
        result = 31 * result + (pathMapping != null ? pathMapping.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TaggingByPathProcessor{" +
            "newField='" + newField + '\'' +
            ", pathMapping=" + pathMapping +
            '}';
    }

}
