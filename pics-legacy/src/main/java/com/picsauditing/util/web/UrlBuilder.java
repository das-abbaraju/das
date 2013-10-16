package com.picsauditing.util.web;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.util.Strings;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.views.util.UrlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.TreeMap;

public class UrlBuilder {
	private static final Logger LOG = LoggerFactory.getLogger(UrlBuilder.class);

	private UrlHelper urlHelper;
	private HttpServletRequest request;
	private HttpServletResponse response;

	private String action;
	private String method;
	private Map<String, Object> parameters = new TreeMap<>();

	public String build() {
		String url = Strings.EMPTY_STRING;

		try {
			url = urlHelper().buildUrl(action, request(), response(), parameters);
		} catch (Exception exception) {
			LOG.error("Error trying to get url generation for {} and method {}\n{}",
					new Object[]{action, method, exception});
		}

		return url;
	}

	private HttpServletRequest request() {
		if (request == null) {
			request = ServletActionContext.getRequest();
		}

		return request;
	}

	private HttpServletResponse response() {
		if (response == null) {
			response = ServletActionContext.getResponse();
		}

		return response;
	}

	public UrlBuilder action(String action) {
		this.action = action;

		return this;
	}

	public UrlBuilder method(String method) {
		this.method = method;

		return this;
	}

	public UrlBuilder addParameters(String key, Object value) {
		parameters.put(key, value);

		return this;
	}

	private UrlHelper urlHelper() {
		if (urlHelper == null) {
			urlHelper = ActionContext.getContext().getContainer().getInstance(UrlHelper.class);
		}

		return urlHelper;
	}
}