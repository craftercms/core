/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
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
package org.craftercms.core.processors.impl;

import org.craftercms.core.service.ContentStoreService;
import org.dom4j.Element;

/**
 * Extension of {@link IncludeDescriptorsProcessor} that prevents pages from being included when
 * {@code disablePageInclusion} is true.
 *
 * @author avasquez
 */
public class PageAwareIncludeDescriptorsProcessor extends IncludeDescriptorsProcessor {

    protected boolean disablePageInclusion;
    protected String pagesPathPattern;

    public PageAwareIncludeDescriptorsProcessor(String includeElementXPathQuery, String disabledIncludeNodeXPathQuery) {
        super(includeElementXPathQuery, disabledIncludeNodeXPathQuery);
        disablePageInclusion = true;
    }

    public void setPagesPathPattern(String pagesPathPattern) {
        this.pagesPathPattern = pagesPathPattern;
    }

    public void setDisablePageInclusion(boolean disablePageInclusion) {
        this.disablePageInclusion = disablePageInclusion;
    }

    @Override
    protected boolean isIncludeDisabled(Element includeElement) {
        return super.isIncludeDisabled(includeElement) ||
                (disablePageInclusion && includeElement.getTextTrim().matches(pagesPathPattern));
    }

}
