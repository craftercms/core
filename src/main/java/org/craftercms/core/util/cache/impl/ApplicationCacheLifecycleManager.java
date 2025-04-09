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
package org.craftercms.core.util.cache.impl;

import org.craftercms.core.events.ContextCreatedEvent;
import org.craftercms.core.events.ContextDestroyedEvent;
import org.craftercms.core.events.ContextEvent;
import org.craftercms.core.service.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

/**
 * Event listener that adds an application cache scope whenever a context is created and removes it when the context
 * is destroyed.
 *
 * @author avasquez
 * @since 4.3.1
 */
public class ApplicationCacheLifecycleManager implements ApplicationListener<ContextEvent> {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationCacheLifecycleManager.class);

	protected CacheService appCacheService;

	public ApplicationCacheLifecycleManager(CacheService appCacheService) {
		this.appCacheService = appCacheService;
	}

	@Override
	public synchronized void onApplicationEvent(ContextEvent event) {
		var context = event.getContext();

		if (event instanceof ContextCreatedEvent) {
			if (!appCacheService.hasScope(context)) {
				logger.info("Adding application cache scope for context: {}", context);
				appCacheService.addScope(context);
			}
		} else if (event instanceof ContextDestroyedEvent) {
			logger.info("Removing application cache scope for context: {}", context);
			appCacheService.removeScope(context);
		}
	}

	@Override
	public boolean supportsAsyncExecution() {
		return false;
	}

}
