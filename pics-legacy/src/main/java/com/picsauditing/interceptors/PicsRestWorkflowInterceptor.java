package com.picsauditing.interceptors;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.rest.RestWorkflowInterceptor;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.inject.Inject;


public class PicsRestWorkflowInterceptor extends RestWorkflowInterceptor {
	
	private static final long serialVersionUID = 3177056726863243986L;
	
	private final String inputResultName = Action.INPUT;
	private String postMethodName = "create";
	private String editMethodName = "edit";
	private String newMethodName = "editNew";
	private String putMethodName = "update";

	@Override
	@Inject(required = false, value = "struts.mapper.postMethodName")
	public void setPostMethodName(String postMethodName) {
		this.postMethodName = postMethodName;
	}

	@Override
	@Inject(required = false, value = "struts.mapper.editMethodName")
	public void setEditMethodName(String editMethodName) {
		this.editMethodName = editMethodName;
	}

	@Override
	@Inject(required = false, value = "struts.mapper.newMethodName")
	public void setNewMethodName(String newMethodName) {
		this.newMethodName = newMethodName;
	}

	@Override
	@Inject(required = false, value = "struts.mapper.putMethodName")
	public void setPutMethodName(String putMethodName) {
		this.putMethodName = putMethodName;
	}

	@Override
	protected String doIntercept(ActionInvocation invocation) throws Exception {
		Object action = invocation.getAction();

		if (action instanceof ValidationAware) {
			ValidationAware validationAwareAction = (ValidationAware) action;

			if (validationAwareAction.hasErrors()) {
				ActionMapping mapping = (ActionMapping) ActionContext.getContext().get(ServletActionContext.ACTION_MAPPING);
				String viewName = inputResultName;
				if (postMethodName.equals(mapping.getMethod())) {
					viewName = newMethodName;
				} else if (putMethodName.equals(mapping.getMethod())) {
					viewName = editMethodName;
				}

				return viewName;
			}
		}

		return invocation.invoke();
	}
}
