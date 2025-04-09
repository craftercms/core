/*
 * Copyright (C) 2007-2025 Crafter Software Corporation. All Rights Reserved.
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
package org.craftercms.core.events;

import org.craftercms.core.service.Context;

/**
 * Event triggered when a {@link Context} is destroyed.
 *
 * @author avasquez
 * @since 4.3.1
 */
public class ContextDestroyedEvent extends ContextEvent {

	public ContextDestroyedEvent(Context context) {
		super(context);
	}

}
