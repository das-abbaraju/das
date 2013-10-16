package com.picsauditing.employeeguard.controllers;

import com.picsauditing.breadcrumb.Breadcrumb;
import com.picsauditing.breadcrumb.BreadcrumbCache;
import com.picsauditing.breadcrumb.BreadcrumbPathBuilder;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.util.Strings;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@SuppressWarnings("serial")
public class BreadcrumbAction extends PicsRestActionSupport {
	private static final Logger LOG = LoggerFactory.getLogger(BreadcrumbAction.class);

	private String action;
	private String method;
	private String displayName;

	private List<Breadcrumb> breadcrumbs;

	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public List<Breadcrumb> getBreadcrumbs() throws Exception {
		if (breadcrumbs == null) {
			String requestUri;

			try {
				String namespace = ServletActionContext.getContext().getActionInvocation().getProxy().getNamespace();
				requestUri = namespace + "/" + action;

				if (Strings.isNotEmpty(method) && !"index".equals(method) && !requestUri.contains(method)) {
					requestUri += "/" + method;
				}
			} catch (Exception exception) {
				LOG.error("Could not build URI from action context", exception);
				requestUri = getRequestURI();
			}

			breadcrumbs = BreadcrumbPathBuilder.getBreadcrumbs(requestUri);

			BreadcrumbCache.getInstance().clear();
		}

		return breadcrumbs;
	}
}