/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
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
package org.craftercms.core.util.spring.mvc;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import org.apache.commons.collections.CollectionUtils;
import org.craftercms.core.util.JsonUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.view.AbstractView;

/**
 * Class description goes HERE
 *
 * @author Alfonso Vásquez
 */
public class GsonView extends AbstractView {

    public static final String DEFAULT_CONTENT_TYPE = "application/json";
    public static final String DEFAULT_CHARACTER_ENCODING = "UTF-8";

    public static final String JSON_ANTI_HIJACKING_PREFIX = "{} &&";

    public static final String PRAGMA_HEADER_NAME = "Pragma";
    public static final String CACHE_CONTROL_HEADER_NAME = "Cache-Control";
    public static final String EXPIRES_HEADER_NAME = "Expires";

    public static final String DISABLED_CACHING_PRAGMA_HEADER_VALUE = "no-cache";
    public static final String DISABLED_CACHING_CACHE_CONTROL_HEADER_VALUE = "no-cache, no-store, max-age=0";
    public static final long DISABLED_CACHING_EXPIRES_HEADER_VALUE = 1L;

    private Gson gson;
    private boolean prefixJson;
    private boolean disableCaching;
    private Set<String> renderedAttributes;
    private boolean renderSingleAttributeAsRootObject;

    public GsonView() {
        gson = JsonUtils.getDefaultGsonBuilder().create();

        setContentType(DEFAULT_CONTENT_TYPE);
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    /**
     * Indicates whether the JSON output by this view should be prefixed with "{@code {} &&}". Default is false.
     * <p/>
     * <p>Prefixing the JSON string in this manner is used to help prevent JSON Hijacking. The prefix renders the
     * string syntactically invalid as a script so that it cannot be hijacked. This prefix does not affect the
     * evaluation of JSON, but if JSON validation is performed on the string, the prefix would need to be ignored.</p>
     */
    public void setPrefixJson(boolean prefixJson) {
        this.prefixJson = prefixJson;
    }

    /**
     * Tells the client to disable caching of the generated JSON. Default is false.
     */
    public void setDisableCaching(boolean disableCaching) {
        this.disableCaching = disableCaching;
    }

    public void setRenderedAttributes(Set<String> renderedAttributes) {
        this.renderedAttributes = renderedAttributes;
    }

    public void setRenderSingleAttributeAsRootObject(boolean renderSingleAttributeAsRootObject) {
        this.renderSingleAttributeAsRootObject = renderSingleAttributeAsRootObject;
    }

    @Override
    protected void prepareResponse(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType(getContentType());
        response.setCharacterEncoding(DEFAULT_CHARACTER_ENCODING);
        if ( disableCaching ) {
            response.addHeader(PRAGMA_HEADER_NAME, DISABLED_CACHING_PRAGMA_HEADER_VALUE);
            response.addHeader(CACHE_CONTROL_HEADER_NAME, DISABLED_CACHING_CACHE_CONTROL_HEADER_VALUE);
            response.addDateHeader(EXPIRES_HEADER_NAME, DISABLED_CACHING_EXPIRES_HEADER_VALUE);
        }
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        model = filterModel(model);

        Writer output = response.getWriter();

        if ( prefixJson ) {
            output.write(JSON_ANTI_HIJACKING_PREFIX);
        }

        if ( renderSingleAttributeAsRootObject && model.size() == 1 ) {
            gson.toJson(model.values().iterator().next(), output);
        } else {
            gson.toJson(model, output);
        }
    }

    /**
     * Filters out undesired attributes from the given model.
     * <p/>
     * <p>Default implementation removes {@link BindingResult} instances and entries not included in the {@link
     * #setRenderedAttributes(java.util.Set)} property.</p>
     *
     * @param model the model, as passed on to {@link #renderMergedOutputModel}
     * @return the model with only the attributes to render
     */
    protected Map<String, Object> filterModel(Map<String, Object> model) {
        Map<String, Object> filteredModel = new HashMap<String, Object>(model.size());
        Set<String> renderedAttributes = CollectionUtils.isNotEmpty(this.renderedAttributes) ? this.renderedAttributes
                : model.keySet();

        for (Map.Entry<String, Object> attribute : model.entrySet()) {
            if ( !(attribute.getValue() instanceof BindingResult) && renderedAttributes.contains(attribute.getKey()) ) {
                filteredModel.put(attribute.getKey(), attribute.getValue());
            }
        }

        return filteredModel;
    }

}
