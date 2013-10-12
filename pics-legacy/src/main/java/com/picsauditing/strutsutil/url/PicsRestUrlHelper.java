package com.picsauditing.strutsutil.url;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.views.util.DefaultUrlHelper;

import com.opensymphony.xwork2.inject.Inject;
import com.picsauditing.util.Strings;

public class PicsRestUrlHelper extends DefaultUrlHelper {

	private String idParameterName = "id";

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
		Map<String, Object> mapWithoutIdParam = removeIdParameterFromParamsMap(params);
		super.buildParametersString(mapWithoutIdParam, link, paramSeparator);
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
