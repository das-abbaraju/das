package com.picsauditing.actions;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.PICS.PermissionsBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;

public class PicsActionSupport extends ActionSupport
{

	protected Permissions permissions = null;
	protected PermissionsBean pBean = null;

	protected String orderBy = null;
	protected int showPage = 1;
	protected String startsWith = null;

	
	protected boolean getPermissions(OpPerms opPerms, OpType opType) throws Exception
	{
		permissions = (Permissions) ActionContext.getContext().getSession().get("permissions");
		
		if ( !permissions.loginRequired(ServletActionContext.getResponse(), ServletActionContext.getRequest() ) ) 
		{
			return true;	
		}

		pBean = (PermissionsBean) ActionContext.getContext().getSession().get("pBean");
		pBean.setPermissions(permissions);
		
		permissions.tryPermission(opPerms, opType);
		
		return true;
	}

	public PermissionsBean getPBean() {
		return pBean;
	}
	public void setPBean(PermissionsBean bean) {
		pBean = bean;
	}

	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public int getShowPage() {
		return showPage;
	}
	public void setShowPage(int showPage) {
		this.showPage = showPage;
	}

	public String getStartsWith() {
		return startsWith;
	}
	public void setStartsWith(String startsWith) {
		this.startsWith = startsWith;
	}
}
