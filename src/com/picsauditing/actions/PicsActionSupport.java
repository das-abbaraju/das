package com.picsauditing.actions;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
//import com.picsauditing.PICS.PermissionsBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;

public class PicsActionSupport extends ActionSupport
{
	protected Permissions permissions = null;
	//protected PermissionsBean pBean = null;
	
	protected boolean getPermissions(OpPerms opPerms, OpType opType) throws Exception
	{
		permissions = (Permissions) ActionContext.getContext().getSession().get("permissions");
		if (permissions == null)
			permissions = new Permissions();
		
		if ( !permissions.loginRequired(ServletActionContext.getResponse(), ServletActionContext.getRequest() ) ) 
		{
			return false;
		}

		//pBean = (PermissionsBean) ActionContext.getContext().getSession().get("pBean");
		//pBean.setPermissions(permissions);
		
		permissions.tryPermission(opPerms, opType);
		
		return true;
	}

	/*
	 
	public PermissionsBean getPBean() {
		return pBean;
	}
	public void setPBean(PermissionsBean bean) {
		pBean = bean;
	}
	*/
}
