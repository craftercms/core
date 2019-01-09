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
package org.craftercms.core.util.cache;

/**
 * Objects that are generally cached implement this method so that they can provide information about their caching
 * to methods higher in the stack trace.
 *
 * @author Alfonso Vásquez
 */
public interface CachingAwareObject {

    String getScope();

    void setScope(String scope);

    Object getKey();

    void setKey(Object key);

    Long getCachingTime();

    void setCachingTime(Long cachingTime);

}
