package com.picsauditing.actions;

import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.User;

public class PicsActionSupport extends ActionSupport {
	protected Permissions permissions = null;
	protected boolean autoLogin = false;
	@Autowired
	protected String message;

	protected boolean getPermissions(OpPerms opPerms, OpType opType) throws Exception {
		permissions = (Permissions) ActionContext.getContext().getSession().get("permissions");
		if (permissions == null) {
			permissions = new Permissions();
			if (this.autoLogin) {
				// Auto Login tallred
				User user = new User();
				user.setFromDB("941"); // tallred
				permissions.login(user);
				ActionContext.getContext().getSession().put("permissions", permissions);
			}
		}

		if (!permissions.loginRequired(ServletActionContext.getResponse(), ServletActionContext.getRequest())) {
			return false;
		}

		permissions.tryPermission(opPerms, opType);

		return true;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
