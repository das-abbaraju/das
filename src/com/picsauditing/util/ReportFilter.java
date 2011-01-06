package com.picsauditing.util;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.struts2.ServletActionContext;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.Permissions;

@SuppressWarnings("serial")
public class ReportFilter implements Serializable {
	protected String destinationAction = "";
	protected boolean ajax = false;
	protected boolean allowCollapsed = true;
	protected boolean allowMailMerge = false;
	protected String customAPI;

	public boolean isAllowMailMerge() {
		return allowMailMerge;
	}

	public void setAllowMailMerge(boolean allowMailMerge) {
		this.allowMailMerge = allowMailMerge;
	}

	public boolean isAllowCollapsed() {
		return allowCollapsed;
	}

	public void setAllowCollapsed(boolean allowCollapsed) {
		this.allowCollapsed = allowCollapsed;
	}

	public String getDestinationAction() {
		if (destinationAction.equals(""))
			return ServletActionContext.getActionMapping().getName();
		return destinationAction;
	}

	public void setDestinationAction(String destinationAction) {
		this.destinationAction = destinationAction;
	}

	public boolean isAjax() {
		return ajax;
	}

	public void setAjax(boolean ajax) {
		this.ajax = ajax;
	}

	public void setPermissions(Permissions permissions) {

	}
	public Map<String, String> getYearList(){
		Map<String, String> map = new LinkedHashMap<String, String>();
		String.valueOf(DateBean.getCurrentYear());
		for(int i=DateBean.getCurrentYear()-1; i>=2001; i--){
			map.put(String.valueOf(i), String.valueOf(i));
		}
		return map;
	}

	public String getCustomAPI() {
		return customAPI;
	}

	public void setCustomAPI(String customAPI) {
		this.customAPI = customAPI;
	}

}
