package com.picsauditing.interceptors;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.picsauditing.strutsutil.url.RestUrlFinder;

public class PicsRestInterceptor extends AbstractInterceptor {

	private static final long serialVersionUID = 3853838783164282788L;

	private String prefixActionMapperList;

	@Inject(value = StrutsConstants.PREFIX_BASED_MAPPER_CONFIGURATION)
	public void setPrefixBasedActionMappers(String prefixActionMapperList) {
		this.prefixActionMapperList = prefixActionMapperList;
	}

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		ActionMapping mapping = ServletActionContext.getActionMapping();
		if (ServletActionContext.getActionMapping() != null
				&& RestUrlFinder.isRestActionMapping(mapping, prefixActionMapperList)) {
			ActionContext.getContext().getParameters().putAll(ServletActionContext.getActionMapping().getParams());
		}

		return invocation.invoke();
	}
}
