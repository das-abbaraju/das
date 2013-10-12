package com.picsauditing.strutsutil.url;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.views.util.UrlHelper;

import com.opensymphony.xwork2.inject.Inject;

public class UrlHelperFacade implements UrlHelper {

	private String prefixActionMapperList;

	@Inject(value = StrutsConstants.PREFIX_BASED_MAPPER_CONFIGURATION)
	public void setPrefixBasedActionMappers(String prefixActionMapperList) {
		this.prefixActionMapperList = prefixActionMapperList;
	}

	@Override
	public String buildUrl(String action, HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> params) {
		return getUrlHelper(action, prefixActionMapperList).buildUrl(action, request, response, params);
	}

	@Override
	public String buildUrl(String action, HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> params, String scheme, boolean includeContext, boolean encodeResult) {
		return getUrlHelper(action, prefixActionMapperList).buildUrl(action, request, response, params, scheme,
				includeContext, encodeResult);
	}

	@Override
	public String buildUrl(String action, HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> params, String scheme, boolean includeContext, boolean encodeResult,
			boolean forceAddSchemeHostAndPort) {
		return getUrlHelper(action, prefixActionMapperList).buildUrl(action, request, response, params, scheme,
				includeContext, encodeResult, forceAddSchemeHostAndPort);
	}

	@Override
	public String buildUrl(String action, HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> params, String scheme, boolean includeContext, boolean encodeResult,
			boolean forceAddSchemeHostAndPort, boolean escapeAmp) {
		return getUrlHelper(action, prefixActionMapperList).buildUrl(action, request, response, params, scheme,
				includeContext, encodeResult, forceAddSchemeHostAndPort, escapeAmp);
	}

	@Override
	public void buildParametersString(Map<String, Object> params, StringBuilder link, String paramSeparator) {
		getUrlHelper(null, null).buildParametersString(params, link, paramSeparator);
	}

	@Override
	public Map<String, Object> parseQueryString(String queryString, boolean forceValueArray) {
		return Collections.emptyMap();
	}

	private UrlHelper getUrlHelper(String action, String prefixActionMapperList) {
		return UrlHelperFactory.getUrlHelper(action, prefixActionMapperList);
	}

}
