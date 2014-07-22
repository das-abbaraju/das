package com.picsauditing.securitysession.web;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.securitysession.Permissions;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.ServletActionContext;

import java.util.Map;

public class StrutsSessionInfoProvider implements SessionInfoProvider {

	@Override
	public int getUserId() {
		return getPermissions().getUserId();
	}

	@Override
	public int getAccountId() {
		return getPermissions().getAccountId();
	}

	@Override
	public int getId() {
		Map<String, Object> params = getRequestParams();
		if (MapUtils.isEmpty(params) || !params.containsKey(REST_ID_PARAM_KEY)) {
			return 0;
		}

		return NumberUtils.toInt((String) params.get(REST_ID_PARAM_KEY));
	}

	@Override
	public Permissions getPermissions() {
		if (ActionContext.getContext() == null || ActionContext.getContext().getSession() == null) {
			return Permissions.EMPTY_PERMISSIONS;
		}

		return (Permissions) ActionContext.getContext().getSession()
				.get(Permissions.SESSION_PERMISSIONS_COOKIE_KEY);
	}

	private Map<String, Object> getRequestParams() {
		return ActionContext.getContext().getParameters();
	}

	@Override
	public NameSpace getNamespace() {
		if (ServletActionContext.getActionMapping() == null) {
			return NameSpace.UNKNOWN;
		}

		String namespace = ServletActionContext.getActionMapping().getNamespace();

		if (Strings.isEmpty(namespace)) {
			return NameSpace.UNKNOWN;
		} else if (namespace.startsWith(EMPLOYEEGUARD_NAMESPACE)) {
			return NameSpace.EMPLOYEEGUARD;
		} else {
			return NameSpace.PICSORG;
		}
	}

	@Override
	public Map<String, Object> getSession() {
		return ActionContext.getContext().getSession();
	}

	@Override
	public void setSession(final Map<String, Object> session) {
		ActionContext.getContext().setSession(session);
	}

	@Override
	public void putInSession(final String key, final Object value) {
		ActionContext.getContext().getSession().put(key, value);
	}

	@Override
	public String getURI() {
		return ServletActionContext.getRequest().getRequestURI();
	}

	@Override
	public String getReferer() {
		return ServletActionContext.getRequest().getHeader("Referer");
	}
}
