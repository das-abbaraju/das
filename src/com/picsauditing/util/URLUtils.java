package com.picsauditing.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.mapper.DefaultActionMapper;
import org.apache.struts2.views.util.DefaultUrlHelper;

public class URLUtils implements StreamContentProvider {
	private static final String URL_SEPARATOR = "/";

	private DefaultUrlHelper urlHelper;
	private DefaultActionMapper actionMapper;
	private HttpServletRequest request;
	private HttpServletResponse response;

	private String namespace;

	public URLUtils() {
		urlHelper = new DefaultUrlHelper();
		actionMapper = new DefaultActionMapper();
	}

	public String getResponseFrom(String uri) throws IOException {
		return contentOf(uri);
	}

	private String contentOf(String uri) throws IOException {

		InputStream inputStream = openResponseFrom(uri);
		StringBuffer buffer = new StringBuffer();
		int nextByte;
		while ((nextByte = inputStream.read()) > -1) {
			buffer.append((char) nextByte);
		}
		inputStream.close();
		return buffer.toString();
	}

	public static String getProtocol(HttpServletRequest request) {
		try {
			URL url = new URL(request.getRequestURL().toString());
			return url.getProtocol();
		} catch (Exception e) {
		}

		return null;
	}

	public InputStream openResponseFrom(String uri) throws IOException {
		return new URL(uri).openStream();
	}

	public String getActionUrl(String action) {
		return getActionUrl(action, null, null, false);
	}

	public String getActionUrl(String action, String method) {
		return getActionUrl(action, method, null, false);
	}

	public String getActionUrl(String action, Map<String, Object> parameters) {
		return getActionUrl(action, null, parameters, false);
	}

	/**
	 * Generated URI with encoded parameters.
	 *
	 * @param action
	 * @param parameters
	 *            must be in key, value pairs to be placed into a
	 *            <code>&lt;String, Object&gt;</code> map.
	 */
	public String getActionUrl(String action, Object... parameters) {
		if (parameters != null && parameters.length % 2 == 0) {
			Map<String, Object> parameterMap = new HashMap<String, Object>();

			for (int i = 0; i < parameters.length; i = i + 2) {
				parameterMap.put(parameters[i].toString(), parameters[i + 1]);
			}

			return getActionUrl(action, parameterMap);
		}

		return null;
	}

	public String getActionUrl(String action, String method, Map<String, Object> parameters) {
		return getActionUrl(action, method, parameters, false);
	}

	/**
	 * Generated URI with encoded parameters.
	 *
	 * @param action
	 *            action name
	 * @param method
	 *            method name, use null if not needed
	 * @param parameters
	 *            &lt;String, Object&gt; map of parameter name, parameter value
	 * @param useFullUrl
	 *            whether to display something like
	 *            "https://www.picsorganizer.com" beforehand
	 * @return
	 *         "/Action!method.action?param1key=param1value&amp;param2key=param2value"
	 */
	public String getActionUrl(String action, String method, Map<String, Object> parameters, boolean useFullUrl) {
		ActionMapping mapping = new ActionMapping(action, getNamespace(), method, parameters);
		// TODO Find out if we need to override these defaults
		String scheme = null;
		boolean includeContext = true;
		boolean encodeResults = true;
		boolean escapeAmp = false;

		String actionUri = actionMapper.getUriFromActionMapping(mapping);
		return urlHelper.buildUrl(actionUri, getRequest(), getResponse(), parameters, scheme, includeContext,
				encodeResults, useFullUrl, escapeAmp);
	}

	private String getNamespace() {
		if (namespace == null) {
			namespace = ServletActionContext.getActionMapping().getNamespace();
		}

		return namespace;
	}

	private HttpServletRequest getRequest() {
		if (request == null) {
			request = ServletActionContext.getRequest();
		}

		return request;
	}

	private HttpServletResponse getResponse() {
		if (response == null) {
			response = ServletActionContext.getResponse();
		}

		return response;
	}

	/**
	 * Returns the name of the Action method in the URL. If the request is null,
	 * it will return empty string.
	 *
	 * @param request
	 * @return https://www.picsorganizer.com/ManageReports!ownedBy.action will
	 *         return "ManageReports"
	 */
	public static String getActionMethodNameFromRequest(HttpServletRequest request) {
		if (request == null || request.getRequestURL() == null) {
			return Strings.EMPTY_STRING;
		}

		String url = request.getRequestURL().toString();
		int indexAction = url.lastIndexOf(".action");
		int indexMethodSeparator = url.lastIndexOf("!");

		if (indexAction > indexMethodSeparator && (indexAction > -1 && indexMethodSeparator > -1)) {
			return url.substring(indexMethodSeparator + 1, indexAction);
		} else if (indexMethodSeparator == -1 && indexAction > -1) {
			return "execute";
		}

		return Strings.EMPTY_STRING;
	}

	/**
	 * Returns the name of the Action in the URL. If the request is null,
	 * it will return empty string.
	 *
	 * @param request
	 * @return https://www.picsorganizer.com/ManageReports!ownedBy.action will
	 *         return "ownedBy"
	 */
	public static String getActionNameFromRequest(HttpServletRequest request) {
		if (request == null || request.getRequestURL() == null) {
			return Strings.EMPTY_STRING;
		}

		String url = request.getRequestURL().toString();
		int indexLastUrlSeparator = url.lastIndexOf(URL_SEPARATOR);
		int indexAction = url.lastIndexOf(".action");
		int indexMethodSeparator = url.lastIndexOf("!");

		if (indexLastUrlSeparator > -1) {
			if (indexAction > -1 && indexMethodSeparator < 0) {
				return url.substring(indexLastUrlSeparator + 1, indexAction);
			} else if (indexMethodSeparator < indexAction && indexMethodSeparator > 0) {
				return url.substring(indexLastUrlSeparator + 1, indexMethodSeparator);
			}
		}

		return Strings.EMPTY_STRING;
	}
}
