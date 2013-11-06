package com.picsauditing.strutsutil.url;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.picsauditing.strutsutil.actionmapper.ConfigLocator;
import com.picsauditing.strutsutil.actionmapper.delegate.ActionMapperDelegate;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.MapUtils;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.views.util.DefaultUrlHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PicsRestUrlHelper extends DefaultUrlHelper {

    private String idParameterName = "id";

    private ConfigLocator configLocator = new ConfigLocator();
    private ActionMapperDelegate actionMapperDelegate = new ActionMapperDelegate();

    @Inject(required = false, value = StrutsConstants.STRUTS_ID_PARAMETER_NAME)
    public void setIdParameterName(String idParameterName) {
        this.idParameterName = idParameterName;
    }

    @Override
    public String buildUrl(String action, HttpServletRequest request, HttpServletResponse response,
                           Map<String, Object> params) {
        return buildUrl(action, request, response, params, null, true, true);
    }

    @Override
    public String buildUrl(String action, HttpServletRequest request, HttpServletResponse response,
                           Map<String, Object> params, String scheme, boolean includeContext, boolean encodeResult) {
        return buildUrl(action, request, response, params, scheme, includeContext, encodeResult, false);
    }

    @Override
    public String buildUrl(String action, HttpServletRequest request, HttpServletResponse response,
                           Map<String, Object> params, String scheme, boolean includeContext, boolean encodeResult,
                           boolean forceAddSchemeHostAndPort) {
        return buildUrl(action, request, response, params, scheme, includeContext, encodeResult,
                forceAddSchemeHostAndPort, true);
    }

    @Override
    public String buildUrl(String action, HttpServletRequest request, HttpServletResponse response,
                           Map<String, Object> params, String scheme, boolean includeContext, boolean encodeResult,
                           boolean forceAddSchemeHostAndPort, boolean escapeAmp) {
        return super.buildUrl(action, request, response, params, scheme, includeContext, encodeResult,
                forceAddSchemeHostAndPort, escapeAmp);
    }

    @Override
    public void buildParametersString(Map<String, Object> params, StringBuilder link, String paramSeparator) {
        ConfigurationManager configurationManager = Dispatcher.getInstance().getConfigurationManager();
        ActionMapping actionMapping = new ActionMapping();
        actionMapperDelegate.parseNameAndNamespace(link.toString(), actionMapping, configurationManager);

        PackageConfig packageConfig = configLocator.findPackageConfigForNamespace(configurationManager, actionMapping);
        ActionConfig actionConfig = configLocator.findActionConfig(packageConfig, actionMapping);

        if (actionConfig != null) {
            Map<String, Object> mapWithoutRestMappedParams = removeRestMappedParams(params, actionConfig.getName());
            super.buildParametersString(mapWithoutRestMappedParams, link, paramSeparator);
        } else {
            Map<String, Object> mapWithoutIdParam = removeIdParameterFromParamsMap(params);
            super.buildParametersString(mapWithoutIdParam, link, paramSeparator);
        }
    }

    private Map<String, Object> removeRestMappedParams(Map<String, Object> params, String actionName) {
        if (MapUtils.isEmpty(params)) {
            return Collections.emptyMap();
        }

        Map<String, Object> mapWithoutRestParams = new HashMap<>();
        for (String paramName : params.keySet()) {
            if (!actionName.contains("{" + paramName + "}") && !paramName.equals(idParameterName)) {
                mapWithoutRestParams.put(paramName, params.get(paramName));
            }
        }

        return mapWithoutRestParams;
    }

    private Map<String, Object> removeIdParameterFromParamsMap(Map<String, Object> params) {
        if (MapUtils.isEmpty(params)) {
            return Collections.emptyMap();
        }

        Map<String, Object> mapWithoutIdParam = new HashMap<>();
        if (params.containsKey(idParameterName)) {
            mapWithoutIdParam = buildNewMapWithoutIdParam(params, mapWithoutIdParam);
        } else {
            mapWithoutIdParam.putAll(params);
        }

        return mapWithoutIdParam;
    }

    private Map<String, Object> buildNewMapWithoutIdParam(Map<String, Object> params,
                                                          Map<String, Object> mapWithoutIdParam) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            if (Strings.isNotEmpty(key) && !key.equals(idParameterName)) {
                mapWithoutIdParam.put(key, entry.getValue());
            }
        }

        return mapWithoutIdParam;
    }

    @Override
    public Map<String, Object> parseQueryString(String queryString, boolean forceValueArray) {
        if (Strings.isEmpty(queryString)) {
            return Collections.emptyMap();
        }

        return super.parseQueryString(queryString, forceValueArray);
    }
}
