/*
 * Copyright (C) 2007-2020 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.core.util;

/**
 * Utility methods for exceptions.
 *
 * @author Alfonso Vásquez
 */
public class ExceptionUtils extends org.apache.commons.lang3.exception.ExceptionUtils {

    @SuppressWarnings("unchecked")
    public static <T> T getThrowableOfType(Throwable throwable, Class<T> type) {
        if (throwable == null || type == null) {
            return null;
        }

        Throwable[] throwables = getThrowables(throwable);
        for (Throwable throwableInChain : throwables) {
            if (type.isAssignableFrom(throwableInChain.getClass())) {
                return (T)throwableInChain;
            }
        }

        return null;
    }

}
